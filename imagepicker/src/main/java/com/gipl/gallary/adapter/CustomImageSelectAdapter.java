package com.gipl.gallary.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.gipl.gallary.helpers.ConstantsCustomGallery;
import com.gipl.gallary.models.Image;
import com.gipl.imagepicker.R;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by MyInnos on 03-11-2016.
 */
public class CustomImageSelectAdapter extends RecyclerView.Adapter<CustomImageSelectAdapter.ViewHolder> {
    protected int size;
    private ArrayList<Image> images = new ArrayList<>();
    private int countSelected;
    private IItemClickListener iItemClickListener;

    public ArrayList<Image> getImages() {
        return images;
    }

    public int getCountSelected() {
        return countSelected;
    }

    public void setCountSelected(int countSelected) {
        this.countSelected = countSelected;
    }

    public void addItems(ArrayList<Image> images) {
        this.images = images;
    }

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_view_image_select, viewGroup, false);
        return new ViewHolder(view);
    }



    public ArrayList<Image> getSelected() {
        ArrayList<Image> selectedImages = new ArrayList<>();
        for (int i = 0, l = images.size(); i < l; i++) {
            if (images.get(i).isSelected) {
                selectedImages.add(images.get(i));
            }
        }
        return selectedImages;
    }
    public void setLayoutParams(int size) {
        this.size = size;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
//        viewHolder.imageView.getLayoutParams().width = size;
//        viewHolder.imageView.getLayoutParams().height = size;
//
//        viewHolder.view.getLayoutParams().width = size;
//        viewHolder.view.getLayoutParams().height = size;

        if (images.get(position).isSelected) {
            viewHolder.view.setAlpha(0.5f);
            viewHolder.container.setForeground(viewHolder.imageView.getContext().getResources().getDrawable(R.drawable.ic_done_white));

        } else {
            viewHolder.view.setAlpha(0.0f);
            viewHolder.container.setForeground(null);
        }

        Uri uri = Uri.fromFile(new File(images.get(position).path));
//
//        viewHolder.imageView.setImageURI(uri);

        RequestOptions requestOptions = new RequestOptions()
                .dontAnimate()
                .override(200, 200)
                .placeholder(R.color.colorAccent)
                .priority(Priority.IMMEDIATE);

        Glide.with(viewHolder.imageView.getContext())
                .load(uri)
                .apply(requestOptions)
                .into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!images.get(position).isSelected && countSelected >= ConstantsCustomGallery.limit) {
                    Toast.makeText(
                            v.getContext(),
                            String.format(v.getContext().getString(R.string.limit_exceeded), ConstantsCustomGallery.limit),
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                images.get(position).isSelected = !images.get(position).isSelected;
                if (images.get(position).isSelected) {
                    countSelected++;
                } else {
                    countSelected--;
                }
                notifyItemChanged(position);
                if (iItemClickListener != null) {
                    iItemClickListener.onItemClick(images.get(position));
                }
            }
        });
    }

    public void deselectAll() {

        for (int i = 0, l = images.size(); i < l; i++) {
            images.get(i).isSelected = false;
        }
        countSelected = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void addItem(Image image) {
        images.add(image);
        notifyItemChanged(getItemCount()-1);
    }

    public interface IItemClickListener {
        void onItemClick(Image image);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        ImageView imageView;
        FrameLayout container;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_image_select);
            view = itemView.findViewById(R.id.view_alpha);
            container = itemView.findViewById(R.id.container);
        }
    }

}
