package com.gipl.gallary.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gipl.gallary.adapter.AlbumAdapter
import com.gipl.gallary.helpers.ConstantsCustomGallery
import com.gipl.gallary.models.Album
import com.gipl.imagepicker.R
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Ankit on 03-11-2016.
 */
class AlbumSelectActivity : HelperActivity(), AlbumAdapter.IItemClickListener {
    private val projection = arrayOf(
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATA
    )
    var messageMutableLiveData = MutableLiveData<Message>()
    var service: ExecutorService? = null
    private val albumsLiveData = MutableLiveData<ArrayList<Album>>()
    private var errorDisplay: TextView? = null
    private var tvProfile: TextView? = null
    private var liFinish: LinearLayout? = null
    private var loader: ProgressBar? = null
    private var gridView: RecyclerView? = null
    private var adapter: AlbumAdapter? = null
    private val actionBar: ActionBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_select)
        setView(findViewById(R.id.layout_album_select))
        val intent = intent
        if (intent == null) {
            finish()
        }
        if (intent != null) {
            ConstantsCustomGallery.limit = intent.getIntExtra(
                ConstantsCustomGallery.INTENT_EXTRA_LIMIT,
                ConstantsCustomGallery.DEFAULT_LIMIT
            )
        }
        errorDisplay = findViewById(R.id.text_view_error)
        errorDisplay?.visibility = View.INVISIBLE
        tvProfile = findViewById(R.id.tvProfile)
        tvProfile?.setText(R.string.album_view)
        liFinish = findViewById(R.id.liFinish)
        loader = findViewById(R.id.loader)
        gridView = findViewById(R.id.grid_view_album_select)
        albumsLiveData.observe(this) { albums: ArrayList<Album> -> processAlbumsList(albums) }
        messageMutableLiveData.observe(this) { msg: Message -> processMessage(msg) }
        liFinish?.setOnClickListener { finish() }
    }

    private fun processMessage(msg: Message) {
        when (msg.what) {
            ConstantsCustomGallery.PERMISSION_GRANTED -> {
                loadAlbums()
            }
            ConstantsCustomGallery.FETCH_STARTED -> {
                loader?.visibility = View.VISIBLE
                gridView?.visibility = View.INVISIBLE
            }
            ConstantsCustomGallery.FETCH_COMPLETED -> {
                loader?.visibility = View.GONE
                gridView?.visibility = View.VISIBLE
                service?.shutdownNow()
            }
            ConstantsCustomGallery.ERROR -> {
                service?.shutdownNow()
                loader?.visibility = View.GONE
                errorDisplay?.visibility = View.VISIBLE
            }
        }
    }

    private fun processAlbumsList(albums: ArrayList<Album>) {
        if (adapter == null) {
            adapter = AlbumAdapter()
            val windowManager = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val size = metrics.widthPixels / 2
            adapter?.setSize(size)
            val gridLayoutManager = GridLayoutManager(this, 2)
            gridView?.layoutManager = gridLayoutManager
            gridView?.adapter = adapter
            adapter?.setItemClickListener(this)
            adapter?.addItem(albums)
            loader?.visibility = View.GONE
            gridView?.visibility = View.VISIBLE
            //            orientationBasedUI(getResources().getConfiguration().orientation);
        } else {
            adapter?.addItem(albums)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        loadAlbums()


//        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);
        checkPermission()
    }

    override fun onStop() {
        super.onStop()
        service?.shutdownNow()
        //        getContentResolver().unregisterContentObserver(observer);
//        observer = null;
    }

    override fun onDestroy() {
        super.onDestroy()
        actionBar?.setHomeAsUpIndicator(null)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //        orientationBasedUI(newConfig.orientation);
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
        //overridePendingTransition(abc_fade_in, abc_fade_out);
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                false
            }
        }
    }

    private fun loadAlbums() {
        if (adapter != null) adapter?.clear()
        if (service != null && service?.isShutdown == false) service?.shutdownNow()
        service = Executors.newSingleThreadExecutor()
        service?.submit(AlbumLoaderRunnable())
    }

    override fun onItemClick(album: Album?) {
        if (adapter != null) {
            val intent1 = Intent(applicationContext, ImageSelectActivity::class.java)
            intent1.putExtra(ConstantsCustomGallery.INTENT_EXTRA_ALBUM, album?.name)
            startActivityForResult(intent1, ConstantsCustomGallery.REQUEST_CODE)
        }
    }

    private fun sendMessage(what: Int, arg1: Int = 0) {
        val message = Message()
        message.what = what
        message.arg1 = arg1
        messageMutableLiveData.postValue(message)
    }

    override fun permissionGranted() {
        sendMessage(ConstantsCustomGallery.PERMISSION_GRANTED)
    }

    override fun hideViews() {
        loader?.visibility = View.GONE
        gridView?.visibility = View.INVISIBLE
    }

    private inner class AlbumLoaderRunnable : Runnable {
        override fun run() {
            if (adapter == null) {
                sendMessage(ConstantsCustomGallery.FETCH_STARTED)
            }
            val cursor = applicationContext.contentResolver
                .query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Images.Media.DATE_MODIFIED
                )
            if (cursor == null) {
                sendMessage(ConstantsCustomGallery.ERROR)
                return
            }
            val temp = ArrayList<Album>(cursor.count)
            val albumSet = HashSet<Long>()
            var file: File
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return
                    }
                    val albumId = cursor.getLong(cursor.getColumnIndex(projection[0]))
                    val album = cursor.getString(cursor.getColumnIndex(projection[1]))
                    val image = cursor.getString(cursor.getColumnIndex(projection[2]))
                    if (!albumSet.contains(albumId)) {
                        /*
                        It may happen that some image file paths are still present in cache,
                        though image file does not exist. These last as long as media
                        scanner is not run again. To avoid get such image file paths, check
                        if image file exists.
                         */
                        file = File(image)
                        if (file.exists()) {
                            temp.add(Album(album, image))
                            albumSet.add(albumId)
                        }
                    }
                } while (cursor.moveToPrevious())
            }
            cursor.close()
            albumsLiveData.postValue(temp)
            sendMessage(ConstantsCustomGallery.FETCH_COMPLETED)
        }
    }
}