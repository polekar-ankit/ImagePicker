package com.gipl.gallary.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.gipl.gallary.models.Album;
import com.gipl.imagepicker.R;

import java.util.ArrayList;


/**
 * Created by MyInnos on 03-11-2016.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    protected int size;
    private IItemClickListener iItemClickListener;
    private ArrayList<Album> arrayList = new ArrayList<>();
    private RequestOptions requestOptions = new RequestOptions()
            .dontAnimate()
            .override(size, size)
            .placeholder(R.color.colorAccent)
            .priority(Priority.IMMEDIATE);

    public void setItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_view_item_album_select, viewGroup, false);
        return new ViewHolder(view);
    }

    public void clear() {
        arrayList.clear();
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<Album> arrayList) {
        this.arrayList.addAll(arrayList);
    }

    public Album getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;

        viewHolder.textView.setText(arrayList.get(position).name);

        Glide.with(viewHolder.itemView.getContext())
                .load(arrayList.get(position).cover)
                .apply(requestOptions)
                .into(viewHolder.imageView);

//        viewHolder.imageView.setImageURI(Uri.parse(arrayList.get(position).cover));

        viewHolder.itemView.setOnClickListener(v -> {
            if (iItemClickListener != null)
                iItemClickListener.onItemClick(arrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface IItemClickListener {
        void onItemClick(Album album);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_album_image);
            textView = itemView.findViewById(R.id.text_view_album_name);
        }
    }
}
