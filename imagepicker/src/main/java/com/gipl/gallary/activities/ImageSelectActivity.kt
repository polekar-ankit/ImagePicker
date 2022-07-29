package com.gipl.gallary.activities

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.os.Process
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gipl.gallary.adapter.CustomImageSelectAdapter
import com.gipl.gallary.helpers.ConstantsCustomGallery
import com.gipl.gallary.models.Image
import com.gipl.imagepicker.R
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Ankit on 03-11-2016.
 */
class ImageSelectActivity : HelperActivity() {
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA
    )
    var imageMutableLiveData = MutableLiveData<ArrayList<Image>>()

    private var messageMutableLiveData = MutableLiveData<Message>()
    var executors: ExecutorService? = null
    
    private var album: String? = null
    private var errorDisplay: TextView? = null
    private var tvProfile: TextView? = null
    private var tvAdd: TextView? = null
    private var tvSelectCount: TextView? = null
    private var liFinish: LinearLayout? = null
    private var loader: ProgressBar? = null

    //    private int countSelected;
    private var gridView: RecyclerView? = null

    //    private Handler handler;
    private var adapter: CustomImageSelectAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_select)
        setView(findViewById(R.id.layout_image_select))
        tvProfile = findViewById(R.id.tvProfile)
        tvAdd = findViewById(R.id.tvAdd)
        tvSelectCount = findViewById(R.id.tvSelectCount)
        tvProfile?.setText(R.string.image_view)
        liFinish = findViewById(R.id.liFinish)
        val intent = intent
        if (intent == null) {
            finish()
        }
        album = intent?.getStringExtra(ConstantsCustomGallery.INTENT_EXTRA_ALBUM)
        errorDisplay = findViewById(R.id.text_view_error)
        errorDisplay?.visibility = View.INVISIBLE
        loader = findViewById(R.id.loader)
        gridView = findViewById(R.id.grid_view_image_select)
        gridView?.layoutManager = GridLayoutManager(this, 3)
        adapter = CustomImageSelectAdapter()
        gridView?.adapter = adapter
        checkPermission()
        imageMutableLiveData.observe(this) {
            adapter?.addItems(it)
        }
        messageMutableLiveData.observe(this) { message: Message -> processMessage(message) }
        adapter?.setiItemClickListener(object : CustomImageSelectAdapter.IItemClickListener {
            override fun onItemClick(image: Image?) {
                tvSelectCount?.text = String.format(
                    "%d %s",
                    adapter?.countSelected,
                    getString(R.string.selected)
                )
                tvSelectCount?.setVisibility(View.VISIBLE)
                tvAdd?.setVisibility(View.VISIBLE)
                tvProfile?.setVisibility(View.GONE)
                if (adapter?.countSelected == 0) {
                    tvSelectCount?.visibility = View.GONE
                    tvAdd?.visibility = View.GONE
                    tvProfile?.visibility = View.VISIBLE
                }
            }

        })
        liFinish?.setOnClickListener { v: View? ->
            if (tvSelectCount?.visibility == View.VISIBLE) {
                adapter?.deselectAll()
                finish()
                tvSelectCount?.text = ""
            } else {
                finish()
                //overridePendingTransition(ac, abc_fade_out);
            }
        }
        tvAdd?.setOnClickListener { sendIntent() }
    }

    private fun processMessage(message: Message) {
        when (message.what) {
            ConstantsCustomGallery.PERMISSION_GRANTED -> {
                loadImages()
            }
            ConstantsCustomGallery.FETCH_STARTED -> {
                adapter?.images?.clear()
                loader?.visibility = View.VISIBLE
                gridView?.visibility = View.VISIBLE
            }
            ConstantsCustomGallery.FETCH_COMPLETED -> {

                /*
                        If adapter is null, this implies that the loaded images will be shown
                        for the first time, hence send FETCH_COMPLETED message.
                        However, if adapter has been initialised, this thread was run either
                        due to the activity being restarted or content being changed.
                         */loader?.visibility = View.GONE
                gridView?.visibility = View.VISIBLE

                /*
                            Some selected images may have been deleted
                            hence update action mode title
                             */

                //adapter.setCountSelected(message.arg1);
                if ((adapter?.countSelected ?: 0) > 0) {
                    //actionMode.setTitle(countSelected + " " + getString(R.string.selected));
                    tvSelectCount?.text =
                        String.format(
                            "%d %s",
                            adapter?.countSelected,
                            getString(R.string.selected)
                        )
                    tvSelectCount?.visibility = View.VISIBLE
                    tvAdd?.visibility = View.VISIBLE
                } else tvAdd?.visibility = View.GONE
                tvProfile?.visibility = View.GONE
            }
            ConstantsCustomGallery.ERROR -> {
                loader?.visibility = View.GONE
                errorDisplay?.visibility = View.VISIBLE
            }
        }
    }


    override fun onStop() {
        super.onStop()
        executors?.shutdown()
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

    private fun sendIntent() {
        val intent = Intent()
        intent.putParcelableArrayListExtra(
            ConstantsCustomGallery.INTENT_EXTRA_IMAGES,
            adapter?.selected
        )
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun loadImages() {
        executors = Executors.newSingleThreadExecutor()
        executors?.submit(ImageLoaderRunnable())
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

    override fun onBackPressed() {
        if ((adapter?.countSelected ?: 0) > 0) {
            tvProfile?.visibility = View.VISIBLE
            tvSelectCount?.text = ""
            tvAdd?.visibility = View.GONE
            tvSelectCount?.visibility = View.GONE
            adapter?.deselectAll()
            super.onBackPressed()
        } else {
            super.onBackPressed()
            //overridePendingTransition(abc_fade_in, abc_fade_out);
//            finish();
        }
    }

    private inner class ImageLoaderRunnable : Runnable {
        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            /*
            If the adapter is null, this is first time this activity's view is
            being shown, hence send FETCH_STARTED message to show progress bar
            while images are loaded from phone
             */sendMessage(ConstantsCustomGallery.FETCH_STARTED)
            var file: File?
            val selectedImages = HashSet<Long>()
            if ((adapter?.itemCount ?: 0) > 0) {
                var image: Image?
                var i = 0
                val l = adapter?.itemCount ?: 0
                while (i < l) {
                    image = adapter?.images?.get(i)
                    file = image?.path?.let { File(it) }
                    if (file?.exists() == true && image?.isSelected == true) {
                        image.id.let { selectedImages.add(it) }
                    }
                    i++
                }
            }
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?",
                arrayOf(album),
                MediaStore.Images.Media.DATE_ADDED
            )
            if (cursor == null) {
                sendMessage(ConstantsCustomGallery.ERROR)
                return
            }
            var tempCountSelected = 0
            var lastSendPos = 0
            var counter = 0
            if (cursor.moveToLast()) {
                val images = ArrayList<Image>()
                do {
                    if (executors?.isShutdown == true) {
                        return
                    }
                    val idIdex = cursor.getColumnIndex(projection[0])
                    val nameIdex = cursor.getColumnIndex(projection[1])
                    val pathIdex = cursor.getColumnIndex(projection[2])
                    val id = cursor.getLong(idIdex)
                    val name = cursor.getString(nameIdex)
                    val path = cursor.getString(pathIdex)
                    val isSelected = selectedImages.contains(id)
                    if (isSelected) {
                        tempCountSelected++
                    }
                    file = null
                    try {
                        file = File(path)
                    } catch (e: Exception) {
                        Log.d("Exception : ", e.toString())
                    }
                    if (file?.exists() == true) {
                        images.add(Image(id, name, path, isSelected))
                        counter++
                    }

                    if (counter == 20) {
                        val lastPos = images.size
                        val sendArr = ArrayList(images.subList(lastSendPos, lastPos))
                        imageMutableLiveData.postValue(sendArr)
                        SystemClock.sleep(500)
                        counter = 0
                        lastSendPos = lastPos
                    } else if (images.size == cursor.count) {
                        val lastPos = images.size
                        val sendArr = ArrayList(images.subList(lastSendPos, lastPos))
                        imageMutableLiveData.postValue(sendArr)
                    }
                } while (cursor.moveToPrevious())

            }
            cursor.close()
            sendMessage(ConstantsCustomGallery.FETCH_COMPLETED, tempCountSelected)
        }
    }
}