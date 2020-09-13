package com.example.android.basicfilterapp;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.basicfilterapp.Adapter.ThumbnailAdapter;
import com.example.android.basicfilterapp.Interface.FilterListFragmentListener;
import com.example.android.basicfilterapp.Utils.BitMapUtils;
import com.example.android.basicfilterapp.Utils.SpacesItemDecoration;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersListFragment extends Fragment implements  FilterListFragmentListener{
    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem>thumbnailItems;
    FilterListFragmentListener listener;

    public void setListener(FilterListFragmentListener listener) {
        this.listener = listener;
    }

    public FiltersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_filters_list, container, false);
        thumbnailItems=new ArrayList<>();
        adapter=new ThumbnailAdapter(thumbnailItems,this,getActivity());
        recyclerView=(RecyclerView)view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int space=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);
        displayThumbnail(null);
        return view;
    }

    public void displayThumbnail(final Bitmap bitmap) {
        Runnable r=new Runnable() {
            @Override
            public void run() {
              Bitmap thumbimg;
              if(bitmap==null){
                  thumbimg= BitMapUtils.getBitmapFromAssets(getActivity(),MainActivity.Picture_name,100,100);
              }
              else
                  thumbimg=Bitmap.createScaledBitmap(bitmap,100,100,false);

              if(thumbimg==null)
                  return;
                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();
                ThumbnailItem thumbnailItem=new ThumbnailItem();
                thumbnailItem.image=thumbimg;
                thumbnailItem.filterName="Normal";
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters= FilterPack.getFilterPack(getActivity());
                for(Filter filter:filters){
                    ThumbnailItem tI=new ThumbnailItem();
                    tI.image=thumbimg;
                    tI.filter=filter;
                    tI.filterName=filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }
                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter .notifyDataSetChanged();
                    }
                });

            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {
      if(listener!=null){
          listener.onFilterSelected(filter);
      }
    }
}
