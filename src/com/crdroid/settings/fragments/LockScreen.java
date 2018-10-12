/*
 * Copyright (C) 2016-2018 crDroid Android Project
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
package com.crdroid.settings.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.crdroid.Utils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.crdroid.settings.R;
import com.crdroid.settings.preferences.CustomSeekBarPreference;

import java.util.List;
import java.util.ArrayList;

import lineageos.providers.LineageSettings;

public class LockScreen extends SettingsPreferenceFragment
            implements Preference.OnPreferenceChangeListener, Indexable  {

    public static final String TAG = "LockScreen";

    private static final String FACE_UNLOCK_PREF = "face_auto_unlock";
    private static final String FACE_UNLOCK_PACKAGE = "com.android.facelock";
    private static final String LOCKSCREEN_GESTURES_CATEGORY = "lockscreen_gestures_category";
    private static final String FP_SUCCESS_VIBRATE = "fp_success_vibrate";

    private FingerprintManager mFingerprintManager;

    private Preference mFaceUnlock;
    private Preference mFingerprintVib;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.crdroid_settings_lockscreen);

        PreferenceCategory gestCategory = (PreferenceCategory) findPreference(LOCKSCREEN_GESTURES_CATEGORY);

        mFaceUnlock = (Preference) findPreference(FACE_UNLOCK_PREF);

        if (!Utils.isPackageInstalled(getActivity(), FACE_UNLOCK_PACKAGE)) {
            mFaceUnlock.setEnabled(false);
            mFaceUnlock.setSummary(getActivity().getString(
                    R.string.face_auto_unlock_not_available));
        }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (Preference) findPreference(FP_SUCCESS_VIBRATE);

        if (mFingerprintManager == null || !mFingerprintManager.isHardwareDetected()) {
            gestCategory.removePreference(mFingerprintVib);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.Global.putInt(resolver,
                Settings.Global.LOCKSCREEN_ENABLE_POWER_MENU, 1);
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.FACE_AUTO_UNLOCK, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.DOUBLE_TAP_SLEEP_LOCKSCREEN, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.FP_SUCCESS_VIBRATE, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.LOCKSCREEN_BATTERY_INFO, 1, UserHandle.USER_CURRENT);
        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.LOCKSCREEN_VISUALIZER_ENABLED, 1, UserHandle.USER_CURRENT);
        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.LOCKSCREEN_MEDIA_METADATA, 1, UserHandle.USER_CURRENT);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CRDROID_SETTINGS;
    }

    /**
     * For search
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.crdroid_settings_lockscreen;
                    result.add(sir);

                    return result;
                }
            };
}
