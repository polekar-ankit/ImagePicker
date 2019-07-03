package com.gipl.gallary.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.gipl.gallary.models.Album;
import com.gipl.imagepicker.R;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by MyInnos on 03-11-2016.
 */
public class CustomAlbumSelectAdapter extends CustomGenericAdapter<Album> {

    public CustomAlbumSelectAdapter(Activity activity, Context context, ArrayList<Album> albums) {
        super(activity, context, albums);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_view_item_album_select, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_album_image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_album_name);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;

        viewHolder.textView.setText(arrayList.get(position).name);

        if (arrayList.get(position).name.equals("Take Photo")) {

       /*     RequestOptions requestOptions = new RequestOptions()
                    .dontAnimate()
                    .override(200, 200)
                    .placeholder(R.color.colorAccent)
                    .priority(Priority.IMMEDIATE);

            Glide.with(context)
                    .load()
                    .apply(requestOptions)
                    .into(viewHolder.imageView);*/
                viewHolder.imageView.setImageURI(Uri.parse(arrayList.get(position).cover));


        } else {
            final Uri uri = Uri.fromFile(new File(arrayList.get(position).cover));
            /*RequestOptions requestOptions = new RequestOptions()
                    .dontAnimate()
                    .override(200, 200)
                    .placeholder(R.color.colorAccent)
                    .priority(Priority.IMMEDIATE);

            Glide.with(context)
                    .load(uri)
                    .apply(requestOptions)
                    .into(viewHolder.imageView);*/
            viewHolder.imageView.setImageURI(uri);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
