/*
 * Copyright (C) 2018 FireHound
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

package com.fh.settings.fragments.notifications;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

import android.content.res.Resources;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.fh.settings.preferences.CustomSeekBarPreference;
import com.fh.settings.utils.Utils;

public class FlashlightAlerts extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "FlashlightAlerts";

    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";
    private static final String FLASHLIGHT_ON_CALL_INTERVAL = "flashlight_on_call_interval";

    private ListPreference mFlashlightOnCall;
    private CustomSeekBarPreference mFlashlightOnCallIntervals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context mContext = getActivity().getApplicationContext();
        addPreferencesFromResource(R.xml.flashlight_alerts_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
        int flashlightValue = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
        mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
        mFlashlightOnCall.setOnPreferenceChangeListener(this);

        mFlashlightOnCallIntervals = (CustomSeekBarPreference) findPreference(FLASHLIGHT_ON_CALL_INTERVAL);
        int flashlightOnCallIntervals = Settings.System.getIntForUser(resolver,
                Settings.System.FLASHLIGHT_ON_CALL_INTERVAL, 500, UserHandle.USER_CURRENT);
        mFlashlightOnCallIntervals.setValue(flashlightOnCallIntervals);
        mFlashlightOnCallIntervals.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        Context mContext = getActivity().getApplicationContext();

        if (preference == mFlashlightOnCall) {
            int flashlightValue = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(resolver,
                    Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
            mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
            mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
            return true;
        }

        if (preference == mFlashlightOnCallIntervals) {
            int customDuration = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.FLASHLIGHT_ON_CALL_INTERVAL, customDuration,
                    UserHandle.USER_CURRENT);
            return true;
        }

        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.FH_SETTINGS;
    }
}
