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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognition.R;

public class AddPersonActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        final ToggleButton btnTrainingTest = (ToggleButton)findViewById(R.id.btnTrainingTest);
        final ToggleButton btnReferenceDeviation = (ToggleButton)findViewById(R.id.btnReferenceDeviation);
        final ToggleButton btnTimeManually = (ToggleButton)findViewById(R.id.btnTimeManually);
        btnTrainingTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnTrainingTest.isChecked()){
                    btnReferenceDeviation.setEnabled(true);
                } else {
                    btnReferenceDeviation.setEnabled(false);
                }
            }
        });

        Button btn_Start = (Button)findViewById(R.id.btn_Start);
        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txt_Name = (EditText)findViewById(R.id.txt_Name);
                String name = txt_Name.getText().toString();
                Intent intent = new Intent(v.getContext(), AddPersonPreviewActivity.class);
                intent.putExtra("Name", name);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if(btnTimeManually.isChecked()){
                    intent.putExtra("Method", AddPersonPreviewActivity.MANUALLY);
                } else {
                    intent.putExtra("Method", AddPersonPreviewActivity.TIME);
                }

                if(btnTrainingTest.isChecked()){
                    // Add photos to "Test" folder
                    if(isNameAlreadyUsed(new FileHelper().getTestList(), name)){
                        Toast.makeText(getApplicationContext(), "This name is already used. Please choose another one.", Toast.LENGTH_SHORT).show();
                    } else {
                        intent.putExtra("Folder", "Test");
                        if(btnReferenceDeviation.isChecked()){
                            intent.putExtra("Subfolder", "deviation");
                        } else {
                            intent.putExtra("Subfolder", "reference");
                        }
                        startActivity(intent);
                    }
                } else {
                    // Add photos to "Training" folder

                    if(isNameAlreadyUsed(new FileHelper().getTrainingList(), name)){
                        Toast.makeText(getApplicationContext(), "This name is already used. Please choose another one.", Toast.LENGTH_SHORT).show();
                    } else {
                        intent.putExtra("Folder", "Training");
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private boolean isNameAlreadyUsed(File[] list, String name){
        boolean used = false;
        if(list != null && list.length > 0){
            for(File person : list){
                // The last token is the name --> Folder name = Person name
                String[] tokens = person.getAbsolutePath().split("/");
                final String foldername = tokens[tokens.length-1];
                if(foldername.equals(name)){
                    used = true;
                    break;
                }
            }
        }
        return used;
    }
}
