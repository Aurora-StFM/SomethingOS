/*
 * SPDX-FileCopyrightText: 2026 The SomethingOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.somethingos.fontpicker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.fonts.FontFamilyUpdateRequest;
import android.graphics.fonts.FontFileUpdateRequest;
import android.graphics.fonts.FontManager;
import android.graphics.fonts.FontStyle;
import android.graphics.fonts.FontVariationAxis;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.text.FontConfig;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Switches the system UI font between Geist and Roboto.
 *
 * <p>SystemUI, Settings and the launcher ask for LineageOS's {@code google-sans…} and
 * {@code variable-…} font families by name. Our overlays fork points them at Geist at build
 * time. To switch fonts, this class uses Android's updatable system fonts: it installs the
 * chosen font (with its fs-verity signature) and redefines every one of those families to use
 * it, keeping each family's weights. It also switches the Roboto font overlay, which covers the
 * framework's default UI font.
 *
 * <p>Calls here talk to system services and read files, so run them off the main thread.
 */
final class FontSwitcher {

    private static final String TAG = "FontPicker";

    /** Families redefined on a switch: everything LineageOS defines with Google Sans Flex. */
    private static final String[] FAMILY_PREFIXES = {"google-sans", "variable-"};

    /** Where the build installs the font signatures (vendor/somethingos/fonts/signatures). */
    private static final String SIGNATURE_DIR = "/product/etc/somethingos/fonts/";

    /** SystemUI's theme setting, which decides which category overlays are enabled. */
    private static final String THEME_SETTING =
            Settings.Secure.THEME_CUSTOMIZATION_OVERLAY_PACKAGES;
    private static final String FONT_CATEGORY = "android.theme.customization.font";
    private static final String TIMESTAMP_FIELD = "_applied_timestamp";

    /** Returned by {@link #apply} when the font list has none of the families to redefine. */
    static final int RESULT_NO_FAMILIES = -20001;

    enum SystemFont {
        GEIST("Geist-Regular", "/product/fonts/Geist-VF.ttf", null),
        ROBOTO("Roboto-Regular", "/system/fonts/Roboto-Regular.ttf",
                "org.somethingos.overlay.font.roboto");

        /** The PostScript name inside the font file; family definitions refer to fonts by it. */
        final String postScriptName;
        /** The copy of the font already on the device, which is what gets installed. */
        final String path;
        /** Font overlay to enable, or null for the default (Geist, from FontGeistOverlay). */
        final String overlayPackage;

        SystemFont(String postScriptName, String path, String overlayPackage) {
            this.postScriptName = postScriptName;
            this.path = path;
            this.overlayPackage = overlayPackage;
        }

        String signaturePath() {
            return SIGNATURE_DIR + new File(path).getName() + ".sig";
        }
    }

    private final Context mContext;
    private final FontManager mFontManager;

    FontSwitcher(Context context) {
        mContext = context;
        mFontManager = context.getSystemService(FontManager.class);
    }

    /** The font currently used by the redefined families. */
    SystemFont getCurrent() {
        for (FontConfig.NamedFamilyList list : mFontManager.getFontConfig().getNamedFamilyLists()) {
            if (!list.getName().equals("google-sans")) continue;
            for (FontConfig.FontFamily family : list.getFamilies()) {
                for (FontConfig.Font font : family.getFontList()) {
                    if (SystemFont.ROBOTO.postScriptName.equals(font.getPostScriptName())) {
                        return SystemFont.ROBOTO;
                    }
                }
            }
        }
        return SystemFont.GEIST;
    }

    /**
     * Installs {@code font} and makes it the system font.
     *
     * @return {@link FontManager#RESULT_SUCCESS} or an error code
     */
    int apply(SystemFont font) throws IOException {
        FontConfig config = mFontManager.getFontConfig();
        Map<String, TreeSet<Integer>> families = familyWeights(config);
        if (families.isEmpty()) return RESULT_NO_FAMILIES;

        byte[] signature = Files.readAllBytes(new File(font.signaturePath()).toPath());
        int result;
        try (ParcelFileDescriptor fd = ParcelFileDescriptor.open(
                new File(font.path), ParcelFileDescriptor.MODE_READ_ONLY)) {
            FontFamilyUpdateRequest.Builder request = new FontFamilyUpdateRequest.Builder()
                    .addFontFileUpdateRequest(new FontFileUpdateRequest(fd, signature));
            for (Map.Entry<String, TreeSet<Integer>> family : families.entrySet()) {
                request.addFontFamily(buildFamily(family.getKey(), family.getValue(), font));
            }
            // The config version guards against someone else changing fonts in between.
            result = mFontManager.updateFontFamily(request.build(), config.getConfigVersion());
        }
        if (result != FontManager.RESULT_SUCCESS) {
            Log.e(TAG, "updateFontFamily failed: " + result);
            return result;
        }

        setFontOverlay(font.overlayPackage);
        return FontManager.RESULT_SUCCESS;
    }

    /** Names of the families to redefine, each with the upright weights it defines. */
    private static Map<String, TreeSet<Integer>> familyWeights(FontConfig config) {
        Map<String, TreeSet<Integer>> families = new LinkedHashMap<>();
        for (FontConfig.NamedFamilyList list : config.getNamedFamilyLists()) {
            if (!isRedefined(list.getName())) continue;
            TreeSet<Integer> weights = families.computeIfAbsent(list.getName(),
                    name -> new TreeSet<>());
            for (FontConfig.FontFamily family : list.getFamilies()) {
                for (FontConfig.Font font : family.getFontList()) {
                    // Neither font has an italic; Android slants the upright font instead.
                    if (font.getStyle().getSlant() == FontStyle.FONT_SLANT_UPRIGHT) {
                        weights.add(font.getStyle().getWeight());
                    }
                }
            }
        }
        return families;
    }

    private static boolean isRedefined(String name) {
        for (String prefix : FAMILY_PREFIXES) {
            if (name.startsWith(prefix)) return true;
        }
        return false;
    }

    private static FontFamilyUpdateRequest.FontFamily buildFamily(String name,
            TreeSet<Integer> weights, SystemFont font) {
        if (weights.isEmpty()) weights.add(FontStyle.FONT_WEIGHT_NORMAL);
        List<FontFamilyUpdateRequest.Font> fonts = new ArrayList<>();
        for (int weight : weights) {
            fonts.add(new FontFamilyUpdateRequest.Font.Builder(font.postScriptName,
                    new FontStyle(weight, FontStyle.FONT_SLANT_UPRIGHT))
                    // Both are variable fonts: set the weight axis to match.
                    .setAxes(Collections.singletonList(new FontVariationAxis("wght", weight)))
                    .build());
        }
        return new FontFamilyUpdateRequest.FontFamily.Builder(name, fonts).build();
    }

    /**
     * Enables {@code overlayPackage} as the font overlay, or none if null.
     *
     * <p>SystemUI owns the category overlays and re-applies them from its theme setting, so
     * the overlay is switched there (as Wallpaper &amp; style does) rather than directly.
     */
    private void setFontOverlay(String overlayPackage) {
        String json = Settings.Secure.getString(mContext.getContentResolver(), THEME_SETTING);
        try {
            JSONObject theme = (json == null || json.isEmpty())
                    ? new JSONObject() : new JSONObject(json);
            if (overlayPackage == null) {
                theme.remove(FONT_CATEGORY);
            } else {
                theme.put(FONT_CATEGORY, overlayPackage);
            }
            theme.put(TIMESTAMP_FIELD, System.currentTimeMillis());
            Settings.Secure.putString(mContext.getContentResolver(), THEME_SETTING,
                    theme.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Couldn't parse " + THEME_SETTING + ": " + json, e);
        }
    }

    /**
     * Restarts the apps the user sees most so they load the new font: the launcher, Settings
     * and SystemUI (which Android restarts by itself). Other apps switch when they next start.
     */
    void restartUi() {
        ActivityManager am = mContext.getSystemService(ActivityManager.class);
        Intent home = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
        ResolveInfo launcher = mContext.getPackageManager().resolveActivity(home, 0);
        if (launcher != null && launcher.activityInfo != null) {
            am.forceStopPackage(launcher.activityInfo.packageName);
        }
        am.forceStopPackage("com.android.settings");
        am.forceStopPackage("com.android.systemui");
    }
}
