/*
 * Copyright (C) 2018 FireHound
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fh.settings.fragments.hwbutton;

import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;
import com.fh.settings.R;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.util.fh.FhUtils;

import lineageos.providers.LineageSettings;

import org.lineageos.internal.util.PowerMenuConstants;

import static org.lineageos.internal.util.PowerMenuConstants.*;

public class PowerButton extends SettingsPreferenceFragment implements
          Preference.OnPreferenceChangeListener {

    final static String TAG = "PowerMenuActions";
    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";

    private ListPreference mTorchPowerButton;
    private CheckBoxPreference mScreenshotPref;
    private CheckBoxPreference mOnTheGoPref;
    private CheckBoxPreference mAirplanePref;
    private CheckBoxPreference mUsersPref;
    //private CheckBoxPreference mBugReportPref;

    Context mContext;
    private ArrayList<String> mLocalUserConfig = new ArrayList<String>();
    private String[] mAllActions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.power_button_settings);
        mContext = getActivity().getApplicationContext();
        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        mAllActions = PowerMenuConstants.getAllActions();

        for (String action : mAllActions) {
            if (action.equals(GLOBAL_ACTION_KEY_SCREENSHOT)) {
                mScreenshotPref = (CheckBoxPreference) findPreference(GLOBAL_ACTION_KEY_SCREENSHOT);
            } else if (action.equals(GLOBAL_ACTION_KEY_ONTHEGO)) {
                mOnTheGoPref = (CheckBoxPreference) findPreference(GLOBAL_ACTION_KEY_ONTHEGO);
            } else if (action.equals(GLOBAL_ACTION_KEY_AIRPLANE)) {
                mAirplanePref = (CheckBoxPreference) findPreference(GLOBAL_ACTION_KEY_AIRPLANE);
            } else if (action.equals(GLOBAL_ACTION_KEY_USERS)) {
                mUsersPref = (CheckBoxPreference) findPreference(GLOBAL_ACTION_KEY_USERS);
//            } else if (action.equals(GLOBAL_ACTION_KEY_BUGREPORT)) {
//                mBugReportPref = (CheckBoxPreference) findPreference(GLOBAL_ACTION_KEY_BUGREPORT);
            }
        }

        if (!FhUtils.deviceHasFlashlight(getContext())) {
            Preference toRemove = prefScreen.findPreference(TORCH_POWER_BUTTON_GESTURE);
            if (toRemove != null) {
                prefScreen.removePreference(toRemove);
            }
        } else {
            mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
            int mTorchPowerButtonValue = Settings.Secure.getInt(resolver,
                    Settings.Secure.TORCH_POWER_BUTTON_GESTURE, 0);
            mTorchPowerButton.setValue(Integer.toString(mTorchPowerButtonValue));
            mTorchPowerButton.setSummary(mTorchPowerButton.getEntry());
            mTorchPowerButton.setOnPreferenceChangeListener(this);
        }

        getUserConfig();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mScreenshotPref != null) {
            mScreenshotPref.setChecked(settingsArrayContains(GLOBAL_ACTION_KEY_SCREENSHOT));
        }

        if (mOnTheGoPref != null) {
            mOnTheGoPref.setChecked(settingsArrayContains(GLOBAL_ACTION_KEY_ONTHEGO));
        }

        if (mAirplanePref != null) {
            mAirplanePref.setChecked(settingsArrayContains(GLOBAL_ACTION_KEY_AIRPLANE));
        }

        if (mUsersPref != null) {
            if (!UserHandle.MU_ENABLED || !UserManager.supportsMultipleUsers()) {
                getPreferenceScreen().removePreference(findPreference(GLOBAL_ACTION_KEY_USERS));
                mUsersPref = null;
            } else {
                List<UserInfo> users = ((UserManager) mContext.getSystemService(
                        Context.USER_SERVICE)).getUsers();
                boolean enabled = (users.size() > 1);
                mUsersPref.setChecked(settingsArrayContains(GLOBAL_ACTION_KEY_USERS) && enabled);
                mUsersPref.setEnabled(enabled);
            }
        }

/*
        if (mBugReportPref != null) {
            mBugReportPref.setChecked(settingsArrayContains(GLOBAL_ACTION_KEY_BUGREPORT));
        }
        updatePreferences();
*/
    }

/*
    @Override
    public void onResume() {
        super.onResume();
        updatePreferences();
    }
*/

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean value;

        if (preference == mScreenshotPref) {
            value = mScreenshotPref.isChecked();
            updateUserConfig(value, GLOBAL_ACTION_KEY_SCREENSHOT);

        } else if (preference == mOnTheGoPref) {
            value = mOnTheGoPref.isChecked();
            updateUserConfig(value, GLOBAL_ACTION_KEY_ONTHEGO);

        } else if (preference == mAirplanePref) {
            value = mAirplanePref.isChecked();
            updateUserConfig(value, GLOBAL_ACTION_KEY_AIRPLANE);

        } else if (preference == mUsersPref) {
            value = mUsersPref.isChecked();
            updateUserConfig(value, GLOBAL_ACTION_KEY_USERS);

/*
        } else if (preference == mBugReportPref) {
            value = mBugReportPref.isChecked();
            updateUserConfig(value, GLOBAL_ACTION_KEY_BUGREPORT);
*/

        } else {
            return super.onPreferenceTreeClick(preference);
        }
        return true;
    }

    private boolean settingsArrayContains(String preference) {
        return mLocalUserConfig.contains(preference);
    }

    private void updateUserConfig(boolean enabled, String action) {
        if (enabled) {
            if (!settingsArrayContains(action)) {
                mLocalUserConfig.add(action);
            }
        } else {
            if (settingsArrayContains(action)) {
                mLocalUserConfig.remove(action);
            }
        }
        saveUserConfig();
    }

/*
    private void updatePreferences() {
        boolean bugreport = Settings.Global.getInt(getContentResolver(),
                Settings.Global.BUGREPORT_IN_POWER_MENU, 0) != 0;
        if (mBugReportPref != null) {
            mBugReportPref.setEnabled(bugreport);
            if (bugreport) {
                mBugReportPref.setSummary(null);
            } else {
                mBugReportPref.setSummary(R.string.power_menu_bug_report_disabled);
            }
        }
    }
*/

    private void getUserConfig() {
        mLocalUserConfig.clear();
        String[] defaultActions;
        String savedActions = LineageSettings.Secure.getStringForUser(mContext.getContentResolver(),
                LineageSettings.Secure.POWER_MENU_ACTIONS, UserHandle.USER_CURRENT);

        if (savedActions == null) {
            defaultActions = mContext.getResources().getStringArray(
                    com.android.internal.R.array.config_globalActionsList);
            for (String action : defaultActions) {
                mLocalUserConfig.add(action);
            }
        } else {
            for (String action : savedActions.split("\\|")) {
                mLocalUserConfig.add(action);
            }
        }
    }

    private void saveUserConfig() {
        StringBuilder s = new StringBuilder();

        ArrayList<String> setactions = new ArrayList<String>();
        for (String action : mAllActions) {
            if (settingsArrayContains(action)) {
                setactions.add(action);
            } else {
                continue;
            }
        }

        for (int i = 0; i < setactions.size(); i++) {
            s.append(setactions.get(i).toString());
            if (i != setactions.size() - 1) {
                s.append("|");
            }
        }

        LineageSettings.Secure.putStringForUser(getContentResolver(),
                LineageSettings.Secure.POWER_MENU_ACTIONS, s.toString(), UserHandle.USER_CURRENT);
        updatePowerMenuDialog();
    }

    private void updatePowerMenuDialog() {
        Intent u = new Intent();
        u.setAction(lineageos.content.Intent.ACTION_UPDATE_POWER_MENU);
        mContext.sendBroadcastAsUser(u, UserHandle.ALL);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mTorchPowerButton) {
            int mTorchPowerButtonValue = Integer.valueOf((String) newValue);
            int index = mTorchPowerButton.findIndexOfValue((String) newValue);
            mTorchPowerButton.setSummary(
                    mTorchPowerButton.getEntries()[index]);
            Settings.Secure.putInt(resolver, Settings.Secure.TORCH_POWER_BUTTON_GESTURE,
                    mTorchPowerButtonValue);
            if (mTorchPowerButtonValue == 1) {
                //if doubletap for torch is enabled, switch off double tap for camera
                Settings.Secure.putInt(resolver, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
                        1);
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
