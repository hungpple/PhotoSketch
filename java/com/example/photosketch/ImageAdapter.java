package com.example.photosketch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{

    private List<Image> imageList;
    private onClickListener mOnClickListener;

    public ImageAdapter(List<Image> imageList, onClickListener onClickListener) {
        this.imageList = imageList;
        this.mOnClickListener = onClickListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView image;
        public ImageView select;

        onClickListener onClickListener;

        public MyViewHolder(View view, onClickListener onClickListener) {
            super(view);

            image = view.findViewById(R.id.image);
            select = view.findViewById(R.id.select);

            this.onClickListener = onClickListener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickListener.onClick(getAdapterPosition());
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_layout, parent, false);
        return new MyViewHolder(itemView, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picasso.get().load(imageList.get(position).getImageResourceID()).fit().centerInside().into(holder.image);
        holder.select.setVisibility(View.INVISIBLE);

        if(imageList.get(position).isSelect()){
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.INVISIBLE);
        }
    }

    public void addTickToSelectedImage(int position) {
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface onClickListener {
        void onClick(int position);
    }


}