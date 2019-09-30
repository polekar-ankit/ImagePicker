# ImagePicker

This is the repository for Android ImagePicker. It contains source code, help and one sample app to test the functionality. Include it in your Android project to provide an image picker to select an image from phone gallery or to take picture using camera.

### Installing
Add following line to gradle file.
```
allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
   }
   dependencies {
        implementation 'com.github.polekar-ankit:ImagePicker:1.2'
   }
```

### Example
To view complete source code for the sample, refer repository.

In order to use this image picker, you need to set its configuration using PickerConfiguration class.
```
final PickerConfiguration pickerConfiguration = PickerConfiguration.build()
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

## Authors

* **Ankit Polekar**
* **Suyash Raikar**




  
  
