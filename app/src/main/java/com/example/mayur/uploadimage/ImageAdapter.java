package com.example.mayur.uploadimage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {




    private Context mcontext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapter (Context context,List<Upload> uploads){

        mcontext = context;
        mUploads = uploads;




    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(view);



    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);

        holder.textViewName.setText(uploadCurrent.getName());
        Picasso.with(mcontext)
                .load(uploadCurrent.getImageurl())
                .placeholder(R.drawable.ic_launcher_background)
                .fit()
                .centerCrop()
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {

        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,

            View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener {


        public TextView textViewName;
        public ImageView imageView;


        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.txt_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);

            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {
            if(mListener != null){

                int position = getAdapterPosition();

                if(position != RecyclerView.NO_POSITION){

                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem Download = menu.add(Menu.NONE,1,1,"Download");
            MenuItem Delete = menu.add(Menu.NONE,2,2,"Delete Image");

            Download.setOnMenuItemClickListener(this);
            Delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if(mListener != null){

                int position = getAdapterPosition();

                if(position != RecyclerView.NO_POSITION){

                    switch (item.getItemId()){

                        case 1 :
                            mListener.onDelete(position);
                            return true;

                        case 2 :
                            mListener.onDownload(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public  interface OnItemClickListener {

        void onItemClick( int position);

        void onDownload(int position);

        void  onDelete( int position);
    }

    public  void setOnItemClickListener(OnItemClickListener listener){

        mListener = listener;


    }
}
