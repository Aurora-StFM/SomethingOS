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
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;
import android.widget.Toast;

import org.somethingos.fontpicker.FontSwitcher.SystemFont;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Default font dialog: a Roboto and a Geist option, with Cancel and Apply, shown over
 * Settings > Display > Font and display size.
 *
 * <p>Tapping an option only selects it. Apply installs the font, then the dialog offers a
 * restart: SystemUI (status bar, Quick Settings, lock screen) only switches after one, as no
 * other app may restart it. Later restarts just Settings and the launcher.
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
    private View mOptions;
    private View mRestartMessage;
    private TextView mCancelButton;
    private TextView mApplyButton;

    /** True once the font is applied and the dialog is asking whether to restart. */
    private boolean mAskingToRestart;
    /** True while the font is being applied. */
    private boolean mApplying;

    /** Saved across a rebuild of this activity. */
    private static final String STATE_ASK_TO_RESTART = "ask_to_restart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_picker);
        sizeWindow();

        mSwitcher = new FontSwitcher(this);
        mRobotoOption = findViewById(R.id.option_roboto);
        mGeistOption = findViewById(R.id.option_geist);
        mOptions = findViewById(R.id.options);
        mRestartMessage = findViewById(R.id.restart_message);
        mCancelButton = findViewById(R.id.cancel);
        mApplyButton = findViewById(R.id.apply);

        mRobotoOption.setOnClickListener(v -> select(SystemFont.ROBOTO));
        mGeistOption.setOnClickListener(v -> select(SystemFont.GEIST));
        mCancelButton.setOnClickListener(v -> {
            if (mAskingToRestart) {
                restartAppsAndClose();
            } else {
                finish();
            }
        });
        mApplyButton.setOnClickListener(v -> {
            if (mAskingToRestart) {
                mSwitcher.reboot();
            } else {
                apply();
            }
        });

        // Rebuilt after Apply (the Roboto overlay changes resources): keep asking. A rebuild
        // while applying also means it worked, as the overlay is switched last.
        if (savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_ASK_TO_RESTART, false)) {
            askToRestart();
            return;
        }

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_ASK_TO_RESTART, mAskingToRestart || mApplying);
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
        mApplying = true;
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
                mMainHandler.post(() -> {
                    mApplying = false;
                    askToRestart();
                });
            } else {
                final int error = result;
                mMainHandler.post(() -> {
                    mApplying = false;
                    Toast.makeText(this, getString(R.string.error, error), Toast.LENGTH_LONG)
                            .show();
                    setControlsEnabled(true);
                    select(mSelected);
                });
            }
        });
    }

    /** Swaps the options for the restart question, with Later / Restart. */
    private void askToRestart() {
        mAskingToRestart = true;
        // Tapping outside would skip both choices, leaving Settings in the old font.
        setFinishOnTouchOutside(false);
        mOptions.setVisibility(View.GONE);
        mRestartMessage.setVisibility(View.VISIBLE);
        mCancelButton.setText(R.string.later);
        mApplyButton.setText(R.string.restart);
        mCancelButton.setEnabled(true);
        setApplyEnabled(true);
        // Back now means "later", not "forget it".
        getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT, this::restartAppsAndClose);
    }


    /** Restarts Settings and the launcher in the new font and closes this dialog for good. */
    private void restartAppsAndClose() {
        mSwitcher.restartApps();
        // This dialog still uses the old font, so end its process too.
        finish();
        Process.killProcess(Process.myPid());
    }

    private void setControlsEnabled(boolean enabled) {
        mCancelButton.setEnabled(enabled);
        mRobotoOption.setEnabled(enabled);
        mGeistOption.setEnabled(enabled);
        setApplyEnabled(enabled);
    }

    private void setApplyEnabled(boolean enabled) {
        mApplyButton.setEnabled(enabled);
        mApplyButton.setAlpha(enabled ? 1f : 0.4f);
    }
}
