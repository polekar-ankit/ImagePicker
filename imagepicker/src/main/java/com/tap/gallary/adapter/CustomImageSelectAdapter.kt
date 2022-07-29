package com.tap.gallary.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.tap.imagepicker.R
import android.view.ViewGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Priority
import com.tap.gallary.helpers.ConstantsCustomGallery
import com.tap.gallary.models.Image
import java.util.ArrayList

/**
 * Created by Ankit on 03-11-2016.
 */
class CustomImageSelectAdapter : RecyclerView.Adapter<CustomImageSelectAdapter.ViewHolder>() {
    protected var size = 0
    private val requestOptions = RequestOptions()
        .dontAnimate()
        .override(200, 200)
        .placeholder(R.color.colorAccent)
        .priority(Priority.IMMEDIATE)
    val images = ArrayList<Image>()
    var countSelected = 0
    private var iItemClickListener: IItemClickListener? = null
    fun addItems(newImages: ArrayList<Image>) {
        val pos = images.size - 1
        images.addAll(newImages)
        notifyItemChanged(pos, newImages.size - 1)
    }

    fun setiItemClickListener(iItemClickListener: IItemClickListener) {
        this.iItemClickListener = iItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_view_image_select, viewGroup, false)
        return ViewHolder(view)
    }

    val selected: ArrayList<Image>
        get() {
            val selectedImages = ArrayList<Image>()
            for (i in images.indices) {
                val image = images[i]
                if (image.isSelected) {
                    selectedImages.add(image)
                    Log.d("images", image.name + " " + i)
                }
            }
            return selectedImages
        }

    fun setLayoutParams(size: Int) {
        this.size = size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (size != 0) size = viewHolder.view.layoutParams.width
        viewHolder.view.layoutParams.height = size
        if (images[position].isSelected) {
            viewHolder.view.alpha = 0.5f
            viewHolder.container.foreground =
                viewHolder.imageView.context.resources.getDrawable(R.drawable.ic_done_white)
        } else {
            viewHolder.view.alpha = 0.0f
            viewHolder.container.foreground = null
        }
        Glide.with(viewHolder.imageView.context)
            .load(images[position].path)
            .apply(requestOptions)
            .into(viewHolder.imageView)
        viewHolder.itemView.setOnClickListener { v: View ->
            if (!images[position].isSelected && countSelected >= ConstantsCustomGallery.limit) {
                Toast.makeText(
                    v.context,
                    String.format(
                        v.context.getString(R.string.limit_exceeded),
                        ConstantsCustomGallery.limit
                    ),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            images[position].isSelected = !images[position].isSelected
            if (images[position].isSelected) {
                countSelected++
            } else {
                countSelected--
            }
            notifyItemChanged(position)
            if (iItemClickListener != null) {
                iItemClickListener!!.onItemClick(images[position])
            }
        }
    }

    fun deselectAll() {
        var i = 0
        val l = images.size
        while (i < l) {
            images[i].isSelected = false
            i++
        }
        countSelected = 0
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun addItem(image: Image) {
        images.add(image)
        notifyItemChanged(itemCount - 1)
    }

    interface IItemClickListener {
        fun onItemClick(image: Image?)
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: View
        var imageView: ImageView
        var container: FrameLayout

        init {
            imageView = itemView.findViewById(R.id.image_view_image_select)
            view = itemView.findViewById(R.id.view_alpha)
            container = itemView.findViewById(R.id.container)
        }
    }
}