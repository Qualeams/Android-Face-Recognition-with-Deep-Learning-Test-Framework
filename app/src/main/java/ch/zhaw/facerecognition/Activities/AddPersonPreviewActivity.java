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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.io.File;
import java.util.Date;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.Helpers.PreferencesHelper;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognition.R;

public class AddPersonPreviewActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    public static final int TIME = 0;
    public static final int MANUALLY = 1;
    private CustomCameraView preview;
    // The timerDiff defines after how many milliseconds a picture is taken
    private long timerDiff;
    private long lastTime;
    private PreProcessorFactory ppF;
    private FileHelper fh;
    private String folder;
    private String subfolder;
    private String name;
    private int total;
    private int numberOfPictures;
    private int method;
    private ImageButton btn_Capture;
    private boolean capturePressed;
    private boolean front_camera;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person_preview);

        Intent intent = getIntent();
        folder = intent.getStringExtra("Folder");
        if(folder.equals("Test")){
            subfolder = intent.getStringExtra("Subfolder");
        }
        name = intent.getStringExtra("Name");
        method = intent.getIntExtra("Method", 0);
        capturePressed = false;
        if(method == MANUALLY){
            btn_Capture = (ImageButton)findViewById(R.id.btn_Capture);
            btn_Capture.setVisibility(View.VISIBLE);
            btn_Capture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturePressed = true;
                }
            });
        }

        fh = new FileHelper();
        total = 0;
        lastTime = new Date().getTime();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        timerDiff = Integer.valueOf(sharedPrefs.getString("key_timerDiff", "500"));

        preview = (CustomCameraView) findViewById(R.id.AddPersonPreview);
        // Use camera which is selected in settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        front_camera = sharedPref.getBoolean("key_front_camera", true);

        numberOfPictures = Integer.valueOf(sharedPref.getString("key_numberOfPictures", "100"));

        if (front_camera){
            preview.setCameraIndex(1);
        } else {
            preview.setCameraIndex(-1);
        }
        preview.setVisibility(SurfaceView.VISIBLE);
        preview.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();
        Mat imgCopy = new Mat();
        imgRgba.copyTo(imgCopy);
        // Selfie / Mirror mode
        if(front_camera){
            Core.flip(imgRgba,imgRgba,1);
        }

        long time = new Date().getTime();
        if((method == MANUALLY) || (method == TIME) && (lastTime + timerDiff < time)){
            lastTime = time;

            // Check that only 1 face is found. Skip if any or more than 1 are found.
            Mat img = ppF.getCroppedImage(imgCopy);
            if(img != null){
                Rect[] faces = ppF.getFacesForRecognition();
                //Only proceed if 1 face has been detected, ignore if 0 or more than 1 face have been detected
                if((faces != null) && (faces.length == 1)){
                    faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
                    if(((method == MANUALLY) && capturePressed) || (method == TIME)){
                        MatName m = new MatName(name + "_" + total, img);
                        if (folder.equals("Test")) {
                            String wholeFolderPath = fh.TEST_PATH + name + "/" + subfolder;
                            new File(wholeFolderPath).mkdirs();
                            fh.saveMatToImage(m, wholeFolderPath + "/");
                        } else {
                            String wholeFolderPath = fh.TRAINING_PATH + name;
                            new File(wholeFolderPath).mkdirs();
                            fh.saveMatToImage(m, wholeFolderPath + "/");
                        }

                        for(int i = 0; i<faces.length; i++){
                            MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], String.valueOf(total), front_camera);
                        }

                        total++;

                        // Stop after numberOfPictures (settings option)
                        if(total >= numberOfPictures){
                            Intent intent = new Intent(getApplicationContext(), AddPersonActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        capturePressed = false;
                    } else {
                        for(int i = 0; i<faces.length; i++){
                            MatOperation.drawRectangleOnPreview(imgRgba, faces[i], front_camera);
                        }
                    }
                }
            }
        }

        return imgRgba;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ppF = new PreProcessorFactory();
        preview.enableView();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (preview != null)
            preview.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (preview != null)
            preview.disableView();
    }
}
