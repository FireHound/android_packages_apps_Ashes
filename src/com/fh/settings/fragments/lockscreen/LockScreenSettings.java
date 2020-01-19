/*
 * Copyright (C) 2019 FireHound
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fh.settings.fragments.lockscreen;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import java.util.List;
import java.util.ArrayList;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String FP_SUCCESS_VIBRATE = "lockscreen_fingerprint";
    private static final String KEY_AUTOCOLOR = "lockscreen_visualizer_autocolor";
    private static final String KEY_LAVALAMP = "lockscreen_lavalamp_enabled";

    private Preference mFingerprintVib;
    private FingerprintManager mFingerprintManager;
    private SwitchPreference mAutoColor;
    private SwitchPreference mLavaLamp;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        final PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (Preference) findPreference(FP_SUCCESS_VIBRATE);

        if (mFingerprintManager == null || !mFingerprintManager.isHardwareDetected()) {
            prefScreen.removePreference(mFingerprintVib);
        }

        boolean mLavaLampEnabled = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.LOCKSCREEN_LAVALAMP_ENABLED, 1,
                UserHandle.USER_CURRENT) != 0;

        mAutoColor = (SwitchPreference) findPreference(KEY_AUTOCOLOR);
        mAutoColor.setEnabled(!mLavaLampEnabled);

        if (mLavaLampEnabled) {
            mAutoColor.setSummary(getActivity().getString(
                    R.string.lockscreen_autocolor_lavalamp));
        } else {
            mAutoColor.setSummary(getActivity().getString(
                    R.string.lockscreen_autocolor_summary));
        }

        mLavaLamp = (SwitchPreference) findPreference(KEY_LAVALAMP);
        mLavaLamp.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mLavaLamp) {
            boolean mLavaLampEnabled = (Boolean) newValue;
            mAutoColor.setEnabled(!mLavaLampEnabled);

            if (mLavaLampEnabled) {
                mAutoColor.setSummary(getActivity().getString(
                        R.string.lockscreen_autocolor_lavalamp));
            } else {
                mAutoColor.setSummary(getActivity().getString(
                        R.string.lockscreen_autocolor_summary));
            }

            return true;
        }

        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.FH_SETTINGS;
    }
}
