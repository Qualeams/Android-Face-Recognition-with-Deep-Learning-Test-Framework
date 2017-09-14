# User manual

## Table of contents
1. [Introduction](#introduction)
2. [Test results](#testresults)
3. [Manual for using the app](#manualfor)
4. [Settings](#settings)
5. [Add Person](#addperson)
6. [Training](#training1)
7. [Test](#test1)
8. [Recognition](#recognition1) <a name="introduction"></a>

## Introduction

Face Recognition can be used as a test framework for several face recognition methods including the Neural Networks with TensorFlow and Caffe.
It includes following preprocessing algorithms:
- Grayscale
- Crop
- Eye Alignment
- Gamma Correction
- Difference of Gaussians
- Canny-Filter
- Local Binary Pattern
- Histogramm Equalization (can only be used if grayscale is used too)
- Resize

You can choose from the following feature extraction and classification methods:
- Eigenfaces with Nearest Neighbour
- Image Reshaping with Support Vector Machine
- TensorFlow with SVM or KNN
- Caffe with SVM or KNN <a name="testresults"></a>

## Test results 
Results of a test with 6 persons (100 training and 100 test pictures for each person):

 | **Accuracy** | **Performance** 
--- | --- | ---
**Eigenfaces with NN** | 85.29 % | 0.46 s / image
**Image Reshaping with SVM** | 95.29 % | 0.58 s / image
**TensorFlow Inception model with SVM** | 88.57 % | 2.35 s / image
**TensorFlow VGG Face Descriptor model with SVM** | 100 % | 6.27 s / image <a name="manualfor"></a>

## Manual for using the app 

### Requirements on the Android device
- Android 5.0 or higher
- armeabi-v7a CPU architecture and higher
- For best experience in recognition mode rotate the device to left.

### Quick Start
#### Add Person
Add at least 2 persons by using the "Add Person" function (at least 10 images per person).

#### Training
Train the classifier with the images captured before.

#### Recognition
Use the recognition function to classify/identify multiple persons in the camera preview.

### Deep learning with Convolutional Neural Networks
#### TensorFlow
##### Inception5h model
If you want to use the Tensorflow Inception5h model, download it from here:
https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip

Then copy the file "tensorflow_inception_graph.pb" to "/sdcard/Pictures/facerecognition/data/TensorFlow"

Use these default settings for a start:
Number of classes: 1001 (not relevant as we don't use the last layer)
Input Size: 224
Image mean: 128
Output size: 1024
Input layer: input
Output layer: avgpool0
Model file: tensorflow_inception_graph.pb

##### VGG Face Descriptor model
If you want to use the VGG Face Descriptor model, download it from here:
https://drive.google.com/file/d/0B9SVRVIKcFzAdm9rQ3JBOC16dkE/view?usp=sharing

Caution: This model runs only on devices with at least 3 GB or RAM.

Then copy the file "vgg_faces.pb" to "/sdcard/Pictures/facerecognition/data/TensorFlow"

Use these default settings for a start:
Number of classes: 1000 (not relevant as we don't use the last layer)
Input Size: 224
Image mean: 128
Output size: 4096
Input layer: Placeholder
Output layer: fc7/fc7
Model file: vgg_faces.pb

#### Caffe
##### VGG Face Descriptor model
If you want to use the VGG Face Descriptor model, download it from here:
http://www.robots.ox.ac.uk/~vgg/software/vgg_face/src/vgg_face_caffe.tar.gz

Caution: This model runs only on devices with at least 3 GB or RAM.

Then copy the files "VGG_FACE_deploy.prototxt" and "VGG_FACE.caffemodel" to "/sdcard/Pictures/facerecognition/data/caffe"

Use these default settings for a start:
Mean values: 104, 117, 123
Output layer: fc7
Model file: VGG_FACE_deploy.prototxt
Weights file: VGG_FACE.caffemodel <a name="settings"></a>

## Settings 

### General
#### Camera
Choose between front and back camera
#### Default Settings
Reset all settings to default

### Add Person
#### Number of pictures
Only relevant if "TIME" is selected in "Add Person". The capturing will stop after this amount of pictures.
#### Time between photos (in ms)
Only relevant if "TIME" is selected in "Add Person". The capturing will wait with taking the next picture at least that long.

### Preprocessing
The preprocessings will be carried out in the following order

1. Standard Preprocessing
2. Brightness Correction
3. Contours
4. Contrast Adjustment
5. Standard Postprocessing

#### Standard Preprocessing
The standard preprocessings will be carried out in the following order if selected

1. Grayscale
2. Crop (should always be used as it reduces noise by cropping the image to include the face only)
3. Eye Alignment

#### Contours
If you use these preprocessings it's best to use only one of them at once. The order is again given by the numbers. 

#### Contrast Adjustment
The Histogramm Equalization can only be used if the image is in grayscale (therefore if the preprocessing grayscale has been used before).

#### Standard Postprocessing
Resize should always be used as it improves the performance by shrinking the image.

#### gamma (Gamma Correction)
This is the parameter gamma for the Gamma Correction (if preprocessing is selected).

#### sigmas (comma separated - Difference of Gaussians)
These are the parameteres sigma1 and sigma2 for the Difference of Gaussians filter (if preprocessing is selected) which need to be separated by commas (e.g. 0.25, 4)

#### N (Resize to N x N)
This is the size in pixels (N x N) for resizing the image (if preprocessing is selected).

### Algorithms General
#### Feature extraction and classification method
#### Eigenfaces with NN
This is the classical Eigenfaces algorithm. Currently the implementation for storing and loading data is bad. So it will need a while (depending on how many pictures you have) to store the training data. Also the loading will take a while each time you use it. But once loaded, the Eigenfaces algorithm is the fastest (until you reach a critical limit of pictures of a few thousand, then the Image Reshaping with SVM performs better).

##### Image Reshaping with SVM
This is the classical approach by reshaping the image to a vector which is used as a feature vector for the Support Vector Machine. This method has the best performance with many images and also a quiet good accuracy.

#### TensorFlow with SVM or KNN
Here you can use a Convolutional Neural Network (CNN) (TensorFlow model) to extract the feature vectors which are then used by the K-Nearest Neighbor or Support Vector Machine classifiers. The performance is very bad though as mobile devices can't use the GPU for the calculations at the moment. 

##### Caffe with SVM or KNN
The same as with TensorFlow but just using a Caffe model instead. The performance is worse as with TensorFlow (in the tests with the VGG Face Descriptor model it was twice as slow).

#### Classification method for TensorFlow and Caffe
Here you can choose the classifier if you selected TensorFlow or Caffe.

#### K (Number of Nearest Neighbor)
The parameter k for the K-Nearest Neighbor classifier (if K-Nearest Neighbor has been selected before).

#### PCA Threshold (Eigenfaces)
The threshold for the Eigenfaces algorithm (if Eigenfaces has been selected before). It determines how many eigenvectors are used based on the Principal Component Analysis.

#### Train Options (LIBSVM)
You can use different train options available in LIBSVM (if SVM has been selected before). The default is a linear kernel (-t 0) as the images have enough dimensions to give a good accuracy. 

### TensorFlow
#### Number of classes
Number of classes the model has been trained on.

#### Input Size
The input size for the CNN. The image will be resized to this size before entering the CNN. You can still use a smaller size in preprocessing to improve performance but will loose information.

#### Image mean
The image mean for the specific model you use. It is used to normalize the image before entering the CNN.

#### Output size
The output size of the output layer you use.

#### Input layer
The input layer of the model you want to use.

#### Output layer
The output layer of the model you want to use. As TensorFlow isn't used as a classifier but as a feature extractor you shouldn't use the last layer but for example the last pooling layer.

#### Model file
Name of the model file you want to use. You need to copy the file to "/sdcard/Pictures/facerecognition/data/TensorFlow".

### Caffe
#### Mean values (comma separated)
The mean values you want to use for normalization. You need to enter them comma separated (e.g. 104, 117, 123).

#### Output layer
The output layer of the model you want to use. As Caffe isn't used as a classifiert but as a feature extractor you shouldn't use the last layer but for example the fc7 layer.

#### Model file
Name of the model file you want to use. You need to copy the file to "/sdcard/Pictures/facerecognition/data/caffe".

#### Weight file
Name of the weights file you want to use. You need to copy the file to "/sdcard/Pictures/facerecognition/data/caffe".  <a name="addperson"></a>

## Add Person
You can either add persons by copying selfies (only one person per image) to "/sdcard/Pictures/facerecognition/training/NAMEOFTHEPERSON" or "/sdcard/Pictures/facerecognition/test/reference/NAMEOFTHEPERSON" or "/sdcard/Pictures/facerecognition/test/deviation/NAMEOFTHEPERSON".

However it's recommended to use the built in "Add Person" function since it saves only pictures where a face was detected and crops the image accordingly. You can also copy these files to your computer for later reusability and when needed copy them back to the device.
### Name
Each name represents a class. If you want to use the test function, you need to use the exact same name for the same person in training and test.

### Training / Test
Switch between capturing for the training or test function.

### Reference / Deviation
This is only needed if you capture images for the test function. Normally you just take a batch of pictures for testing (reference). But if you want to test some specific deviations (e.g. bad light conditions), then you can capture this specific test set in the deviation folder which will be compared to the reference folder.

### Time / Manually
Time means that the capturing will be carried out automatically according to the settings you have set. If you want to capture the images manually you can do this and decide by yourself when to press the button and when to stop the capturing (by hitting the back button).

### Start
Start capturing

## Training <a name="training1"></a>
Executes the training for the images saved under "/sdcard/Pictures/facerecognition/training" with the algorithms defined in the settings. Saves the training data in the classifier folders (either Eigenfaces, SVM or KNN).

## Test <a name="test1"></a>
Executes the test for the images saved under "/sdcard/Pictures/facerecognition/test" with the algorithms defined in the settings. Shows the accuracy after completion and saves the results under "/sdcard/Pictures/facerecognition/results". The feature vectors are also saved in the classifier folders like the training data for evaluation purposes (e.g. t-SNE visualization).

## Recognition <a name="recognition1"></a>
Live recognition of multiple persons at once. Uses the trained data of the algorithms defined in the settings.
