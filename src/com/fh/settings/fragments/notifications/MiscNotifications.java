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
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import com.fh.settings.R;

public class MiscNotifications extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "NotificationsSettings";
    private static final String DISABLE_IMMERSIVE_MESSAGE = "disable_immersive_message";
    private SwitchPreference mDisableIM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.misc_notifications);

    ContentResolver resolver = getActivity().getContentResolver();

    mDisableIM = (SwitchPreference) findPreference(DISABLE_IMMERSIVE_MESSAGE);
        mDisableIM.setOnPreferenceChangeListener(this);
        int DisableIM = Settings.System.getInt(getContentResolver(),
                DISABLE_IMMERSIVE_MESSAGE, 0);
        mDisableIM.setChecked(DisableIM != 0);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mDisableIM) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), DISABLE_IMMERSIVE_MESSAGE,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.FH_SETTINGS;
    }
}
