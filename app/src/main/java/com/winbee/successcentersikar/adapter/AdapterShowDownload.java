package com.winbee.successcentersikar.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.winbee.successcentersikar.R;

import java.io.File;
import java.util.ArrayList;

public class AdapterShowDownload extends RecyclerView.Adapter<AdapterShowDownload.ViewHolder> {
    Context ctx;
    ArrayList<File> fileArrayList;
    OnVideoClickListner onVideoClickListner;

    public AdapterShowDownload(Context ctx, ArrayList<File> fileArrayList, OnVideoClickListner onVideoClickListner) {
        this.ctx = ctx;
        this.fileArrayList = fileArrayList;
        this.onVideoClickListner = onVideoClickListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_download, parent, false);
        return new ViewHolder(view, onVideoClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = Uri.fromFile(fileArrayList.get(position));

        Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        holder.videoImage.setImageBitmap(bitmap2);
        String fullname = fileArrayList.get(position).getName();
        String title = fullname.substring(0,
                fileArrayList.get(position).getName().length() - 4);
        holder.titleVideo.setText(title);

        holder.deleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 1/28/21 delete video here

                new AlertDialog.Builder(ctx)
                        .setTitle("Delete offline video")
                        .setMessage("Are you sure you want to delete this video ?")

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                File file = new File(String.valueOf(fileArrayList.get(position)));
                                try{
                                    file.delete();

                                }catch (Exception e){
                                    Log.d("TAG", "onClick: "+e.getMessage());
                                }

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoImage, deleteVideo;
        TextView titleVideo;
        OnVideoClickListner onVideoClickListner;

        public ViewHolder(@NonNull View itemView, OnVideoClickListner onVideoClickListner) {
            super(itemView);
            videoImage = itemView.findViewById(R.id.videoImage);
            deleteVideo = itemView.findViewById(R.id.deleteVideo);
            titleVideo = itemView.findViewById(R.id.titleVideo);
            this.onVideoClickListner = onVideoClickListner;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVideoClickListner.onVideoClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface OnVideoClickListner {
        void onVideoClicked(int position);
    }

}
