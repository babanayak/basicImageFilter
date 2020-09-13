package com.example.android.basicfilterapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.basicfilterapp.FiltersListFragment;
import com.example.android.basicfilterapp.Interface.FilterListFragmentListener;
import com.example.android.basicfilterapp.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.MyviewHolder>{
    private List<ThumbnailItem>thumbnailItems;
    private FilterListFragmentListener listener;
    private Context context;
    private int selectedIndex=0;


    public ThumbnailAdapter(List<ThumbnailItem> thumbnailItems, FilterListFragmentListener listener, Context context) {
        this.thumbnailItems = thumbnailItems;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.thumbnail_item,viewGroup,false);

        return new MyviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem=thumbnailItems.get(position);
        holder.thumbnail.setImageBitmap(thumbnailItem.image);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterSelected(thumbnailItem.filter);
                selectedIndex=position;
                notifyDataSetChanged();
            }
        });

       holder.filtername.setText(thumbnailItem.filterName);
       if (selectedIndex==position){
           holder.filtername.setTextColor(ContextCompat.getColor(context,R.color.selected_filter));
       }
       else {
           holder.filtername.setTextColor(ContextCompat.getColor(context,R.color.normal_filter));
       }
    }

    @Override
    public int getItemCount() {
        return thumbnailItems.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView filtername;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail=(ImageView)itemView.findViewById(R.id.thumbnail);
            filtername=(TextView)itemView.findViewById(R.id.filternname);
        }
    }
}
