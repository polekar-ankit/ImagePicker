package com.gipl.gallary.activities;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gipl.gallary.adapter.AlbumAdapter;
import com.gipl.gallary.helpers.ConstantsCustomGallery;
import com.gipl.gallary.models.Album;
import com.gipl.imagepicker.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by MyInnos on 03-11-2016.
 */
public class AlbumSelectActivity extends HelperActivity implements AlbumAdapter.IItemClickListener {
    private final String[] projection = new String[]{
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    MutableLiveData<Message> messageMutableLiveData = new MutableLiveData<>();
    ExecutorService service;
    private MutableLiveData<ArrayList<Album>> albumsLiveData = new MutableLiveData<>();
    private TextView errorDisplay, tvProfile;
    private LinearLayout liFinish;
    private ProgressBar loader;
    private RecyclerView gridView;

    private AlbumAdapter adapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_select);
        setView(findViewById(R.id.layout_album_select));

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        if (intent != null) {
            ConstantsCustomGallery.limit = intent.getIntExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, ConstantsCustomGallery.DEFAULT_LIMIT);
        }

        errorDisplay = findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);

        tvProfile = findViewById(R.id.tvProfile);
        tvProfile.setText(R.string.album_view);
        liFinish = findViewById(R.id.liFinish);

        loader = findViewById(R.id.loader);
        gridView = findViewById(R.id.grid_view_album_select);

        albumsLiveData.observe(this, this::processAlbumsList);
        messageMutableLiveData.observe(this, this::processMessage);


        liFinish.setOnClickListener(v -> {
            finish();
            //overridePendingTransition(abc_fade_in, abc_fade_out);
        });
    }

    private void processMessage(Message msg) {
        switch (msg.what) {
            case ConstantsCustomGallery.PERMISSION_GRANTED: {
                loadAlbums();
                break;
            }

            case ConstantsCustomGallery.FETCH_STARTED: {
                loader.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.INVISIBLE);
                break;
            }

            case ConstantsCustomGallery.FETCH_COMPLETED: {

                loader.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                service.shutdownNow();
                break;
            }

            case ConstantsCustomGallery.ERROR: {
                service.shutdownNow();
                loader.setVisibility(View.GONE);
                errorDisplay.setVisibility(View.VISIBLE);
                break;
            }

        }
    }

    private void processAlbumsList(ArrayList<Album> albums) {
        if (adapter == null) {
            adapter = new AlbumAdapter();

            final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            final DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            int size = metrics.widthPixels / 2;
            adapter.setSize(size);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

            gridView.setLayoutManager(gridLayoutManager);
            gridView.setAdapter(adapter);
            adapter.setItemClickListener(this);
            adapter.addItem(albums);

            loader.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
//            orientationBasedUI(getResources().getConfiguration().orientation);

        } else {
            adapter.addItem(albums);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAlbums();


//        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);

        checkPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();

        service.shutdownNow();
//        getContentResolver().unregisterContentObserver(observer);
//        observer = null;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(null);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        orientationBasedUI(newConfig.orientation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        //overridePendingTransition(abc_fade_in, abc_fade_out);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsCustomGallery.REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
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

    private void sendMessage(int what) {
        sendMessage(what, 0);
    }

    private void loadAlbums() {
        if (adapter != null)
            adapter.clear();
        if (service != null && !service.isShutdown())
            service.shutdownNow();
        service = Executors.newSingleThreadExecutor();
        service.submit(new AlbumLoaderRunnable());
    }

    @Override
    public void onItemClick(Album album) {
        if (adapter != null) {
            if (album.name.equals(R.string.capture_photo)) {
                //HelperClass.displayMessageOnScreen(getApplicationContext(), "HMM!", false);
            } else {

                Intent intent1 = new Intent(getApplicationContext(), ImageSelectActivity.class);
                intent1.putExtra(ConstantsCustomGallery.INTENT_EXTRA_ALBUM, album.name);
                startActivityForResult(intent1, ConstantsCustomGallery.REQUEST_CODE);
            }
        }
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

    private class AlbumLoaderRunnable implements Runnable {
        @Override
        public void run() {

            if (adapter == null) {
                sendMessage(ConstantsCustomGallery.FETCH_STARTED);
            }

            Cursor cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                            null, null, MediaStore.Images.Media.DATE_MODIFIED);
            if (cursor == null) {
                sendMessage(ConstantsCustomGallery.ERROR);
                return;
            }

            ArrayList<Album> temp = new ArrayList<>(cursor.getCount());
            HashSet<Long> albumSet = new HashSet<>();
            File file;
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long albumId = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String album = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String image = cursor.getString(cursor.getColumnIndex(projection[2]));


                    if (!albumSet.contains(albumId)) {
                        /*
                        It may happen that some image file paths are still present in cache,
                        though image file does not exist. These last as long as media
                        scanner is not run again. To avoid get such image file paths, check
                        if image file exists.
                         */
                        file = new File(image);
                        if (file.exists()) {

                            temp.add(new Album(album, image));
                            albumSet.add(albumId);
                        }
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();
            albumsLiveData.postValue(temp);
            sendMessage(ConstantsCustomGallery.FETCH_COMPLETED);
        }
    }
}
