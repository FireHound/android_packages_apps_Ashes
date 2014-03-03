/*
 * Copyright (C) 2017 FireHound ROMs
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

package com.fh.settings.fragments.animation;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;

import com.fh.settings.R;

import java.util.ArrayList;
import java.util.List;

public class AnimationSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "AnimationSettings";
    private static final String KEY_TOAST_ANIMATION = "toast_animation";

    private ListPreference mToastAnimation;
    Toast mToast;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.animation_settings);

    final ContentResolver resolver = getActivity().getContentResolver();

        mToastAnimation = (ListPreference) findPreference(KEY_TOAST_ANIMATION);
        int toastanimation = Settings.System.getInt(resolver,
                Settings.System.TOAST_ANIMATION, 1);
        mToastAnimation.setValue(String.valueOf(toastanimation));
        mToastAnimation.setSummary(mToastAnimation.getEntry());
        mToastAnimation.setOnPreferenceChangeListener(this);

        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mToastAnimation) {
            int value = Integer.parseInt((String) newValue);
            int index = mToastAnimation.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.TOAST_ANIMATION, value);
            mToastAnimation.setSummary(mToastAnimation.getEntries()[index]);
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), R.string.toast_animation_test,
                    Toast.LENGTH_SHORT);
            mToast.show();
            return true;
            }
            return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.FH_SETTINGS;
    }
}
