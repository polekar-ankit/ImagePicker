package com.gipl.imagepicker.listener;

import com.gipl.imagepicker.exceptions.ImageErrors;

public interface IImagePickerError {
   public void onError(ImageErrors imageErrors);
}
