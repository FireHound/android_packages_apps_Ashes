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

package com.fh.settings.fragments.ui;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.PowerManager;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
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

import com.fh.settings.preferences.SystemSettingSwitchPreference;

public class DisplaySettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String ON_POWER_SAVE = "smart_pixels_on_power_save";

    private SystemSettingSwitchPreference mSmartPixelsOnPowerSave;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.ui_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mSmartPixelsOnPowerSave = (SystemSettingSwitchPreference) findPreference(ON_POWER_SAVE);
        updateDependency();
        boolean enableSmartPixels = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_enableSmartPixels);
        Preference SmartPixels = findPreference("fh_smart_pixels");

        if (!enableSmartPixels){
            prefScreen.removePreference(SmartPixels);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        updateDependency();
        return true;
    }

    private void updateDependency() {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean mUseOnPowerSave = (Settings.System.getIntForUser(
                resolver, Settings.System.SMART_PIXELS_ON_POWER_SAVE,
                0, UserHandle.USER_CURRENT) == 1);
        PowerManager pm = (PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);
        if (pm.isPowerSaveMode() && mUseOnPowerSave) {
            mSmartPixelsOnPowerSave.setEnabled(false);
        } else {
            mSmartPixelsOnPowerSave.setEnabled(true);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.FH_SETTINGS;
    }
}
