# ImagePicker
Easy to use and configurable library to Pick an image from the Gallery or Capture image using Camera.

It contains source code, help and one sample app to test the functionality.
To simplify the image pick/capture option we have created ImagePicker library. I hope it will be useful to all.

Features:
* Pick Gallary Image
* Capture Camera Image
* Retrive Image Result as File Path as String or Uri object and Bitmap too.
* Handle Runtime Permission for Camera and Storage

### Installation
Add following line to gradle file.
```
allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
   }
   dependencies {
        implementation 'com.github.polekar-ankit:ImagePicker:v3.5.1'
   }
```

### Example
To view complete source code for the sample, refer repository.

In order to use this image picker, you need to set its configuration using PickerConfiguration class.
```
final PickerConfiguration pickerConfiguration = new ImagePickerDialog(this)
```

To open image picker dialog
```
// Open custom dialog with icons.
imagePickerDialog = ImagePickerDialog.display(getSupportFragmentManager(), pickerConfiguration.setSetCustomDialog(true));

// Open default dialog.
imagePickerDialog = ImagePickerDialog.display(getSupportFragmentManager(), pickerConfiguration.setSetCustomDialog(false));
```

In order to get and process selected image, use setImagePickerResult() method of PickerConfiguration class and override methods of PickerResult class.
```
@Override
public void onReceiveImageList(ArrayList<ImageResult> imageResults) {
        super.onReceiveImageList(imageResults);
        int count =  imageResults.size();
}
```
also
## properties of PickerConfiguration

#### 1.setTextColor(int colorCode)
#### 2.setIconColor(int colorCode)
#### 3.setBackGroundColor(int colorCode)
#### 4.setIsDialogCancelable(boolean isCancelable): 
        use to set cancelable property of  image picker dialog 
#### 5.enableMultiSelect(boolean enableMultiSelect)
#### 6.setMultiSelectImageCount(int multiSelectImageCount):
        if enableMultiSelect property set true then set multi select count
#### 7.setPickerDialogListener(ImagePicker.IPickerDialogListener pickerDialogListener):
        use this listener  to track cancel event of dialog 
#### 8.setImagePickerResult(ImagePicker.IImagePickerResult imagePickerResult):
	use this listener to get image selection result in following method 
         a)onImageGet : get image selection result if enableMultiSelect is false 
         b)onError: return error thrown by image picker dialog 
         c)onReceiveImageList: get list of images if enableMultiSelect is true  
### ScreenShot	 	 
<img src="https://github.com/polekar-ankit/ImagePicker/blob/master/screenshot/device-2020-04-22-183237.png" alt="alt text" width="200" height="400"> <img src="https://github.com/polekar-ankit/ImagePicker/blob/master/screenshot/device-2020-04-22-183322.png" alt="alt text" width="200" height="400">

Default Dialog &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; Customised Dialog

## Authors
* **Ankit Polekar**
* **Suyash Raikar**
