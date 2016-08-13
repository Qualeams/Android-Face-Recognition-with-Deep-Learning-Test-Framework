/* Copyright 2016 Michael Sladoje and Mike Schälchli. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package ch.zhaw.facerecognition.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import ch.zhaw.facerecognitionlibrary.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    AlertDialog.Builder adb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        adb = new AlertDialog.Builder(this);


        adb.setTitle("Reset all settings?");


        adb.setIcon(android.R.drawable.ic_dialog_alert);


        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, true);

                Toast.makeText(getApplicationContext(), "Settings have been set to default.", Toast.LENGTH_SHORT).show();
            } });


        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            } });

        Preference button = (Preference)findPreference("key_default_button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                adb.show();
                return true;
            }
        });
    }
}
