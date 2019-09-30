package com.gipl.gallary.activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gipl.gallary.adapter.CustomImageSelectAdapter;
import com.gipl.gallary.helpers.ConstantsCustomGallery;
import com.gipl.gallary.models.Image;
import com.gipl.imagepicker.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by MyInnos on 03-11-2016.
 */
public class ImageSelectActivity extends HelperActivity {
    private final String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};
    MutableLiveData<ArrayList<Image>> imageMutableLiveData = new MutableLiveData<>();
    //    private ContentObserver observer;
    MutableLiveData<Message> messageMutableLiveData = new MutableLiveData<>();
    ExecutorService executors;
    //    private ArrayList<Image> images;
    private String album;
    private TextView errorDisplay, tvProfile, tvAdd, tvSelectCount;
    private LinearLayout liFinish;
    private ProgressBar loader;
    //    private int countSelected;
    private RecyclerView gridView;
    //    private Handler handler;
    private CustomImageSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        setView(findViewById(R.id.layout_image_select));

        tvProfile = findViewById(R.id.tvProfile);
        tvAdd = findViewById(R.id.tvAdd);
        tvSelectCount = findViewById(R.id.tvSelectCount);
        tvProfile.setText(R.string.image_view);
        liFinish = findViewById(R.id.liFinish);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        album = intent.getStringExtra(ConstantsCustomGallery.INTENT_EXTRA_ALBUM);

        errorDisplay = findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);

        loader = findViewById(R.id.loader);
        gridView = findViewById(R.id.grid_view_image_select);
        gridView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new CustomImageSelectAdapter();
        gridView.setAdapter(adapter);

        imageMutableLiveData.observe(this, new Observer<ArrayList<Image>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Image> images) {
                adapter.addItems(images);
            }
        });
        messageMutableLiveData.observe(this, this::processMessage);
        adapter.setiItemClickListener(image -> {
            tvSelectCount.setText(String.format("%d %s", adapter.getCountSelected(), getString(R.string.selected)));
            tvSelectCount.setVisibility(View.VISIBLE);
            tvAdd.setVisibility(View.VISIBLE);
            tvProfile.setVisibility(View.GONE);

            if (adapter.getCountSelected() == 0) {
                //actionMode.finish();
                tvSelectCount.setVisibility(View.GONE);
                tvAdd.setVisibility(View.GONE);
                tvProfile.setVisibility(View.VISIBLE);
            }
        });


        liFinish.setOnClickListener(v -> {
            if (tvSelectCount.getVisibility() == View.VISIBLE) {
                adapter.deselectAll();
            } else {
                finish();
                //overridePendingTransition(ac, abc_fade_out);
            }
        });

        tvAdd.setOnClickListener(v -> sendIntent());
    }

    private void processMessage(Message message) {
        switch (message.what) {
            case ConstantsCustomGallery.PERMISSION_GRANTED: {
                loadImages();
                break;
            }

            case ConstantsCustomGallery.FETCH_STARTED: {
                loader.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.VISIBLE);
                break;
            }

            case ConstantsCustomGallery.FETCH_COMPLETED: {
                        /*
                        If adapter is null, this implies that the loaded images will be shown
                        for the first time, hence send FETCH_COMPLETED message.
                        However, if adapter has been initialised, this thread was run either
                        due to the activity being restarted or content being changed.
                         */
                loader.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);

                            /*
                            Some selected images may have been deleted
                            hence update action mode title
                             */

                adapter.setCountSelected(message.arg1);
                if (adapter.getCountSelected() > 0) {
                    //actionMode.setTitle(countSelected + " " + getString(R.string.selected));
                    tvSelectCount.setText(String.format("%d %s", adapter.getCountSelected(), getString(R.string.selected)));
                    tvSelectCount.setVisibility(View.VISIBLE);
                    tvAdd.setVisibility(View.VISIBLE);
                } else
                    tvAdd.setVisibility(View.GONE);

                tvProfile.setVisibility(View.GONE);


                break;
            }

            case ConstantsCustomGallery.ERROR: {
                loader.setVisibility(View.GONE);
                errorDisplay.setVisibility(View.VISIBLE);
                break;
            }

        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onStart() {
        super.onStart();
        loadImages();
//        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);

        checkPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        executors.shutdown();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        orientationBasedUI(newConfig.orientation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            default: {
                return false;
            }
        }
    }

    private void sendIntent() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES, adapter.getSelected());
        setResult(RESULT_OK, intent);
        finish();
        //overridePendingTransition(abc_fade_in, abc_fade_out);
    }

    private void loadImages() {
        executors = Executors.newSingleThreadExecutor();
        executors.submit(new ImageLoaderRunnable());
    }


    private void sendMessage(int what) {
        sendMessage(what, 0);
    }

    private void sendMessage(int what, int arg1) {

        Message message = new Message();
        message.what = what;
        message.arg1 = arg1;
        messageMutableLiveData.postValue(message);
    }

    @Override
    protected void permissionGranted() {
        sendMessage(ConstantsCustomGallery.PERMISSION_GRANTED);
    }

    @Override
    protected void hideViews() {
        loader.setVisibility(View.GONE);
        gridView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (adapter.getCountSelected() > 0) {
            tvProfile.setVisibility(View.VISIBLE);
            tvAdd.setVisibility(View.GONE);
            tvSelectCount.setVisibility(View.GONE);
            adapter.deselectAll();
        } else {
            super.onBackPressed();
            //overridePendingTransition(abc_fade_in, abc_fade_out);
//            finish();
        }

    }

    private class ImageLoaderRunnable implements Runnable {
        @Override
        public void run() {

            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            /*
            If the adapter is null, this is first time this activity's view is
            being shown, hence send FETCH_STARTED message to show progress bar
            while images are loaded from phone
             */
            sendMessage(ConstantsCustomGallery.FETCH_STARTED);

            File file;
            HashSet<Long> selectedImages = new HashSet<>();
            if (adapter.getItemCount() > 0) {
                Image image;
                for (int i = 0, l = adapter.getItemCount(); i < l; i++) {
                    image = adapter.getImages().get(i);
                    file = new File(image.path);
                    if (file.exists() && image.isSelected) {
                        selectedImages.add(image.id);
                    }
                }
            }

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{album}, MediaStore.Images.Media.DATE_ADDED);
            if (cursor == null) {
                sendMessage(ConstantsCustomGallery.ERROR);
                return;
            }

            /*
            In case this runnable is executed to onChange calling loadImages,
            using countSelected variable can result in a race condition. To avoid that,
            tempCountSelected keeps track of number of selected images. On handling
            FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
             */
            int tempCountSelected = 0;

            if (cursor.moveToLast()) {
                ArrayList<Image> images = new ArrayList<>();
                do {
                    if (executors.isShutdown()) {
                        return;
                    }

                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    boolean isSelected = selectedImages.contains(id);
                    if (isSelected) {
                        tempCountSelected++;
                    }

                    file = null;
                    try {
                        file = new File(path);
                    } catch (Exception e) {
                        Log.d("Exception : ", e.toString());
                    }

                    if (file.exists()) {
                        images.add(new Image(id, name, path, isSelected));
                    }
                    if (images.size()==20){
                        imageMutableLiveData.postValue(images);
                    }
                } while (cursor.moveToPrevious());

                imageMutableLiveData.postValue(images);
            }
            cursor.close();
            sendMessage(ConstantsCustomGallery.FETCH_COMPLETED, tempCountSelected);
        }
    }
}
