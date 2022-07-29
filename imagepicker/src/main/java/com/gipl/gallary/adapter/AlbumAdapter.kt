package com.gipl.gallary.adapter

import androidx.recyclerview.widget.RecyclerView
import com.gipl.gallary.models.Album
import com.bumptech.glide.request.RequestOptions
import com.gipl.imagepicker.R
import android.view.ViewGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.app.Activity
import android.view.View
import android.widget.*
import com.bumptech.glide.Priority
import com.gipl.gallary.adapter.CustomGenericAdapter
import com.gipl.gallary.helpers.ConstantsCustomGallery
import java.util.ArrayList

/**
 * Created by Ankit on 03-11-2016.
 */
class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    protected var viewSize = 0
    private var iItemClickListener: IItemClickListener? = null
    private val arrayList = ArrayList<Album>()
    private val requestOptions = RequestOptions()
        .dontAnimate()
        .override(viewSize, viewSize)
        .placeholder(R.color.colorAccent)
        .priority(Priority.IMMEDIATE)

    fun setItemClickListener(iItemClickListener: IItemClickListener?) {
        this.iItemClickListener = iItemClickListener
    }


    fun setAblumGridSize(size: Int) {
        this.viewSize = size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_view_item_album_select, viewGroup, false)
        return ViewHolder(view)
    }

    fun clear() {
        arrayList.clear()
        notifyDataSetChanged()
    }

    fun addItem(arrayList: ArrayList<Album>?) {
        this.arrayList.addAll(arrayList!!)
    }

    fun getItem(position: Int): Album {
        return arrayList[position]
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.imageView.layoutParams.width = viewSize
        viewHolder.imageView.layoutParams.height = viewSize
        viewHolder.textView.text = arrayList[position].name
        Glide.with(viewHolder.itemView.context)
            .load(arrayList[position].cover)
            .apply(requestOptions)
            .into(viewHolder.imageView)

//        viewHolder.imageView.setImageURI(Uri.parse(arrayList.get(position).cover));
        viewHolder.itemView.setOnClickListener { v: View? ->
            if (iItemClickListener != null) iItemClickListener!!.onItemClick(
                arrayList[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface IItemClickListener {
        fun onItemClick(album: Album?)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var textView: TextView

        init {
            imageView = itemView.findViewById(R.id.image_view_album_image)
            textView = itemView.findViewById(R.id.text_view_album_name)
        }
    }
}