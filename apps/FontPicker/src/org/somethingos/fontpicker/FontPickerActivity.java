/*
 * SPDX-FileCopyrightText: 2026 The SomethingOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.somethingos.fontpicker;

import android.app.Activity;
import android.graphics.fonts.FontManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.somethingos.fontpicker.FontSwitcher.SystemFont;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Default font dialog: a Roboto and a Geist option, with Cancel and Apply, shown over
 * Settings > Display > Font and display size.
 *
 * <p>Tapping an option only selects it; Apply installs the font and restarts the UI.
 */
public class FontPickerActivity extends Activity {

    private static final String TAG = "FontPicker";

    /** Gap between the dialog and the screen edges, and its widest size (for tablets). */
    private static final int SIDE_MARGIN_DP = 24;
    private static final int MAX_WIDTH_DP = 440;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private FontSwitcher mSwitcher;
    private SystemFont mCurrent;
    private SystemFont mSelected;

    private View mRobotoOption;
    private View mGeistOption;
    private View mApplyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_picker);
        sizeWindow();

        mSwitcher = new FontSwitcher(this);
        mRobotoOption = findViewById(R.id.option_roboto);
        mGeistOption = findViewById(R.id.option_geist);
        mApplyButton = findViewById(R.id.apply);

        mRobotoOption.setOnClickListener(v -> select(SystemFont.ROBOTO));
        mGeistOption.setOnClickListener(v -> select(SystemFont.GEIST));
        findViewById(R.id.cancel).setOnClickListener(v -> finish());
        mApplyButton.setOnClickListener(v -> apply());

        // Show the font in use; asking the font service is quick but not instant.
        setControlsEnabled(false);
        mExecutor.execute(() -> {
            SystemFont current = mSwitcher.getCurrent();
            mMainHandler.post(() -> {
                mCurrent = current;
                setControlsEnabled(true);
                select(current);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExecutor.shutdown();
    }

    /** Full width minus a margin on phones, capped on large screens. */
    private void sizeWindow() {
        float density = getResources().getDisplayMetrics().density;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int width = Math.min(screenWidth - Math.round(2 * SIDE_MARGIN_DP * density),
                Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_WIDTH_DP,
                        getResources().getDisplayMetrics())));
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void select(SystemFont font) {
        mSelected = font;
        // Selecting an option also selects its card and label, which change their look.
        mRobotoOption.setSelected(font == SystemFont.ROBOTO);
        mGeistOption.setSelected(font == SystemFont.GEIST);
        // Nothing to apply if it's already the system font.
        setApplyEnabled(font != mCurrent);
    }

    private void apply() {
        SystemFont font = mSelected;
        setControlsEnabled(false);
        mExecutor.execute(() -> {
            int result;
            try {
                result = mSwitcher.apply(font);
            } catch (Exception e) {
                Log.e(TAG, "Couldn't apply " + font, e);
                result = FontManager.RESULT_ERROR_FAILED_UPDATE_CONFIG;
            }
            if (result == FontManager.RESULT_SUCCESS) {
                // Restarts Settings (reopening this page) and SystemUI with the new font.
                mSwitcher.restartUi();
                // This dialog still uses the old font, so close it for good.
                mMainHandler.post(() -> {
                    finish();
                    Process.killProcess(Process.myPid());
                });
            } else {
                final int error = result;
                mMainHandler.post(() -> {
                    Toast.makeText(this, getString(R.string.error, error), Toast.LENGTH_LONG)
                            .show();
                    setControlsEnabled(true);
                    select(mSelected);
                });
            }
        });
    }

    private void setControlsEnabled(boolean enabled) {
        mRobotoOption.setEnabled(enabled);
        mGeistOption.setEnabled(enabled);
        setApplyEnabled(enabled);
    }

    private void setApplyEnabled(boolean enabled) {
        mApplyButton.setEnabled(enabled);
        mApplyButton.setAlpha(enabled ? 1f : 0.4f);
    }
}
