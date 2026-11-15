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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.somethingos.fontpicker.FontSwitcher.SystemFont;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The System font screen: a Roboto and a Geist preview card, with Cancel and Apply.
 *
 * <p>Tapping a card only selects it; Apply installs the font and restarts the UI.
 */
public class FontPickerActivity extends Activity {

    private static final String TAG = "FontPicker";

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private FontSwitcher mSwitcher;
    private SystemFont mCurrent;
    private SystemFont mSelected;

    private View mRobotoCard;
    private View mGeistCard;
    private Button mApplyButton;
    private TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_picker);

        mSwitcher = new FontSwitcher(this);
        mRobotoCard = findViewById(R.id.card_roboto);
        mGeistCard = findViewById(R.id.card_geist);
        mApplyButton = findViewById(R.id.apply);
        mStatus = findViewById(R.id.status);

        mRobotoCard.setOnClickListener(v -> select(SystemFont.ROBOTO));
        mGeistCard.setOnClickListener(v -> select(SystemFont.GEIST));
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

    private void select(SystemFont font) {
        mSelected = font;
        mRobotoCard.setSelected(font == SystemFont.ROBOTO);
        mGeistCard.setSelected(font == SystemFont.GEIST);
        // Nothing to apply if it's already the system font.
        mApplyButton.setEnabled(font != mCurrent);
    }

    private void apply() {
        SystemFont font = mSelected;
        setControlsEnabled(false);
        mStatus.setText(R.string.applying);
        mExecutor.execute(() -> {
            int result;
            try {
                result = mSwitcher.apply(font);
            } catch (Exception e) {
                Log.e(TAG, "Couldn't apply " + font, e);
                result = FontManager.RESULT_ERROR_FAILED_UPDATE_CONFIG;
            }
            if (result == FontManager.RESULT_SUCCESS) {
                mSwitcher.restartUi();
                // This screen still uses the old font, so close it for good.
                mMainHandler.post(() -> {
                    finish();
                    Process.killProcess(Process.myPid());
                });
            } else {
                final int error = result;
                mMainHandler.post(() -> {
                    mStatus.setText(getString(R.string.error, error));
                    setControlsEnabled(true);
                    select(mSelected);
                });
            }
        });
    }

    private void setControlsEnabled(boolean enabled) {
        mRobotoCard.setEnabled(enabled);
        mGeistCard.setEnabled(enabled);
        mApplyButton.setEnabled(enabled);
    }
}
