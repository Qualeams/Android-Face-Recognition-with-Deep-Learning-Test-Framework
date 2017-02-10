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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognition.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String training = intent.getStringExtra("training");
        if (training != null && !training.isEmpty()){
            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
            intent.removeExtra("training");
        }

        double accuracy = intent.getDoubleExtra("accuracy", 0);
        if (accuracy != 0){
            Toast.makeText(getApplicationContext(), "The accuracy was " + accuracy * 100 + " %", Toast.LENGTH_LONG).show();
            intent.removeExtra("accuracy");
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Button callSettings = (Button)findViewById(R.id.button_settings);
        callSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SettingsActivity.class));
            }
        });

        Button callAddPerson = (Button)findViewById(R.id.button_addPerson);
        callAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddPersonActivity.class));
            }
        });

        FileHelper fh = new FileHelper();

        Button callDetectionTest = (Button)findViewById(R.id.button_detection_test);
        if(fh.getDetectionTestList().length == 0) callDetectionTest.setEnabled(false);
        callDetectionTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DetectionTestActivity.class));
            }
        });

        Button callDetectionView = (Button)findViewById(R.id.button_detection_view);
        callDetectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DetectionActivity.class));
            }
        });

        Button callRecognition = (Button)findViewById(R.id.button_recognition_view);
        if(!((new File(fh.DATA_PATH)).exists())) callRecognition.setEnabled(false);
        callRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RecognitionActivity.class));
            }
        });


        Button callTraining = (Button)findViewById(R.id.button_recognition_training);
        if(fh.getTrainingList().length == 0) callTraining.setEnabled(false);
        callTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), TrainingActivity.class));
            }
        });

        Button callTest = (Button)findViewById(R.id.button_recognition_test);
        if(fh.getTestList().length == 0 || !((new File(fh.DATA_PATH)).exists())) callTest.setEnabled(false);
        callTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), TestActivity.class));
            }
        });

        Button callAbout = (Button)findViewById(R.id.button_about);
        callAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://github.com/Qualeams/Android-Face-Recognition-with-Deep-Learning-Test-Framework";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    Log.e(getClass().getName(), null, e);
                    Toast.makeText(getApplicationContext(), "No browser found on the device to open the url", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
