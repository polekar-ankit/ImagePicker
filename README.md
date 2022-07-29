# ImagePicker
[![](https://jitpack.io/v/polekar-ankit/ImagePicker.svg)](https://jitpack.io/#polekar-ankit/ImagePicker).

## now we are migrating our library to kotlin.
Easy to use and configurable library to Pick an image from the Gallery or Capture image using Camera.

It contains source code, help and one sample app to test the functionality.
To simplify the image pick/capture option we have created ImagePicker library. I hope it will be useful to all.

Features:
* Pick Gallery Image
* Capture Camera Image
* Retrieve Image Result as File Path as String or Uri object and Bitmap too.
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
        implementation 'com.github.polekar-ankit:ImagePicker:3.5.1'
   }
```

### Example
To view complete source code for the sample, refer repository.

Create object of imagePickerDialog
```
imagePickerDialog = new ImagePickerDialog();
```

In order to use this image picker, you need to set its configuration using PickerConfiguration class.
```
PickerConfiguration pickerConfiguration = PickerConfiguration.build()
```

To open image picker dialog
```
// Open custom dialog with icons.
imagePickerDialog = imagePickerDialog.show();
//below method is remove and it is replace ImagePickerDialog.show()
<!--  ImagePickerDialog.display(getSupportFragmentManager(), pickerConfiguration.setSetCustomDialog(true)); -->
```
.setImagePickerErrorListener(this::setError)//Listen error

//listen image list
.setImageListResult(imageResults -> {
     int count = imageResults != null ? imageResults.size() : 0;
     setImagesList(imageResults);
     Toast.makeText(requireContext(), "Found image list with " + count + " images Successfully added", Toast.LENGTH_SHORT).show();
 })

//listen single image
.setImagePickerResult(imageResult -> setImage(imageResult.getsImagePath(), imageResult.getImageBitmap()));
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
#### 7.setPickerDialogListener(pickerDialogListener: IPickerDialogListener?):
        use this listener  to track cancel event of dialog 
#### 8.setImagePickerResult(imagePickerResult: IImageResult?):
        use this listener to get single image
#### 9. setImagePickerErrorListener(iImagePickerErrorListener: IImagePickerErrorListener?)
        this method will listener error thrown by library while picking image
#### NOTE : OUR OLD  ImagePicker.IImagePickerResult HAS BEEN REMOVE AND SEPARATED IN TO ABOVE THREE LISTENER ALSO WE HAVE CHANGE PACKAGE NAME



### ScreenShot	 	 
<img src="https://github.com/polekar-ankit/ImagePicker/blob/master/screenshot/device-2020-04-22-183237.png" alt="alt text" width="200" height="400"> <img src="https://github.com/polekar-ankit/ImagePicker/blob/master/screenshot/device-2020-04-22-183322.png" alt="alt text" width="200" height="400">

Default Dialog &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; Customised Dialog

## Authors
* **Ankit Polekar**
* **Suyash Raikar**
