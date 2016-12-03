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

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.PreferencesHelper;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognition.R;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "Test";
    TextView progress;
    Thread thread;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        progress = (TextView) findViewById(R.id.progressText);
        progress.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onResume()
    {
        super.onResume();

        final Handler handler = new Handler(Looper.getMainLooper());
        thread = new Thread(new Runnable() {
            public void run() {
                if(!Thread.currentThread().isInterrupted()){
                    PreProcessorFactory ppF = new PreProcessorFactory(getApplicationContext());
                    PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
                    String algorithm = preferencesHelper.getClassificationMethod();

                    FileHelper fileHelper = new FileHelper();
                    fileHelper.createDataFolderIfNotExsiting();
                    final File[] persons = fileHelper.getTestList();
                    if (persons.length > 0) {
                        Recognition rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(), Recognition.RECOGNITION, algorithm);
                        // total and matches are used to calculate the accuracy afterwards
                        int total = 0;
                        int total_reference = 0;
                        int total_deviation = 0;
                        int matches = 0;
                        int matches_reference = 0;
                        int matches_deviation = 0;
                        List<String> results = new ArrayList<>();
                        results.add("Set;Expected Name;Expected File;Result");
                        Date time_start = new Date();
                        for (File person : persons) {
                            if (person.isDirectory()){
                                File[] folders = person.listFiles();
                                for (File folder : folders) {
                                    if (folder.isDirectory()){
                                        File[] files = folder.listFiles();
                                        int counter = 1;
                                        for (File file : files) {
                                            if (FileHelper.isFileAnImage(file)){
                                                Date time_preprocessing_start = new Date();
                                                Mat imgRgba = Imgcodecs.imread(file.getAbsolutePath());
                                                Imgproc.cvtColor(imgRgba, imgRgba, Imgproc.COLOR_BGRA2RGBA);

                                                List<Mat> images = ppF.getProcessedImage(imgRgba, PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                                if (images == null || images.size() > 1) {
                                                    // More than 1 face detected --> cannot use this file for training
                                                    Date time_preprocessing_end = new Date();
                                                    // Subtract time of preprocessing
                                                    time_start.setTime(time_start.getTime() + (time_preprocessing_end.getTime() - time_preprocessing_start.getTime()));
                                                    continue;
                                                } else {
                                                    imgRgba = images.get(0);
                                                }
                                                if (imgRgba.empty()) {
                                                    Date time_preprocessing_end = new Date();
                                                    // Subtract time of preprocessing
                                                    time_start.setTime(time_start.getTime() + (time_preprocessing_end.getTime() - time_preprocessing_start.getTime()));
                                                    continue;
                                                }
                                                // The last token is the name --> Folder name = Person name
                                                String[] tokens = file.getParentFile().getParent().split("/");
                                                final String name = tokens[tokens.length - 1];
                                                tokens = file.getParent().split("/");
                                                final String folderName = tokens[tokens.length - 1];

//                                              fileHelper.saveCroppedImage(imgRgb, ppF, file, name, total);

                                                total++;
                                                if (folderName.equals("reference")) {
                                                    total_reference++;
                                                } else if (folderName.equals("deviation")) {
                                                    total_deviation++;
                                                }

                                                String name_recognized = rec.recognize(imgRgba, name);
                                                results.add(folderName + ";" + name + ";" + file.getName() + ";" + name_recognized);

                                                if (name.equals(name_recognized)) {
                                                    matches++;
                                                    if (folderName.equals("reference")) {
                                                        matches_reference++;
                                                    } else if (folderName.equals("deviation")) {
                                                        matches_deviation++;
                                                    }
                                                }
                                                // Update screen to show the progress
                                                final int counterPost = counter;
                                                final int filesLength = files.length;
                                                progress.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progress.append("Image " + counterPost + " of " + filesLength + " from " + name + "\n");
                                                    }
                                                });
                                                counter++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Date time_end = new Date();
                        long duration = time_end.getTime() - time_start.getTime();
                        int durationPerImage = (int) duration / total;
                        double accuracy = (double) matches / (double) total;
                        double accuracy_reference = (double) matches_reference / (double) total_reference;
                        double accuracy_deviation = (double) matches_deviation / (double) total_deviation;
                        double robustness = accuracy_deviation / accuracy_reference;
                        Map<String, ?> printMap = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll();
                        fileHelper.saveResultsToFile(printMap, accuracy, accuracy_reference, accuracy_deviation, robustness, durationPerImage, results);
                        rec.saveTestData();

                        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("accuracy", accuracy);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thread.interrupt();
    }
}
