package com.tap.gallary.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.tap.gallary.models.Album
import com.tap.imagepicker.R

/**
 * currently not used in application will be deleted in feature
 */
class CustomAlbumSelectAdapter(activity: Activity?, context: Context?, albums: ArrayList<Album>) :
    CustomGenericAdapter<Album>(activity, context, albums) {
    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
//        val convertView = convertView
        val viewHolder: ViewHolder = convertView.tag as ViewHolder
        viewHolder.imageView?.layoutParams?.width = size
        viewHolder.imageView?.layoutParams?.height = size
        viewHolder.textView?.text = arrayList[position].name
        val requestOptions = RequestOptions()
            .dontAnimate()
            .override(200, 200)
            .placeholder(R.color.colorAccent)
            .priority(Priority.IMMEDIATE)
        viewHolder.imageView?.context?.let {
            Glide.with(it)
                .load(arrayList[position].cover)
                .apply(requestOptions)
                .into(viewHolder.imageView!!)
        }
        viewHolder.imageView!!.setImageURI(Uri.parse(arrayList[position].cover))

        return convertView
    }

    private class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
    }
}