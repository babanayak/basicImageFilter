package com.example.android.basicfilterapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.android.basicfilterapp.Interface.EditImageFragemntListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private EditImageFragemntListener listener;
    SeekBar seekbar_brightness,seekbar_contrast,seekbar_saturation;

    public void setListener(EditImageFragemntListener listener) {
        this.listener = listener;
    }

    public EditImageFragment() {
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
        View itemView= inflater.inflate(R.layout.fragment_edit_image, container, false);
        seekbar_brightness=(SeekBar)itemView.findViewById(R.id.seekbar_brightness);
        seekbar_contrast=(SeekBar)itemView.findViewById(R.id.seekbar_constraint);
        seekbar_saturation=(SeekBar)itemView.findViewById(R.id.seekbar_saturation);
        seekbar_brightness.setMax(200);
        seekbar_brightness.setProgress(100);
        seekbar_contrast.setMax(30);
        seekbar_contrast.setProgress(10);
        seekbar_saturation.setMax(30);
        seekbar_saturation.setProgress(10);
        seekbar_brightness.setOnSeekBarChangeListener(this);
        seekbar_contrast.setOnSeekBarChangeListener(this);
        seekbar_saturation.setOnSeekBarChangeListener(this);
        return itemView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if(listener!=null){
            if(seekBar.getId()==R.id.seekbar_brightness){
                listener.OnBrightnessChanged(progress-100);
            }
            else if(seekBar.getId()==R.id.seekbar_constraint){
                progress+=10;
                float value=.10f*progress;
                listener.OnContrastChanged(value);
            }
            else if(seekBar.getId()==R.id.seekbar_saturation){
                float value=.10f*progress;
                listener.OnSaturationChanged(value);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
     if(listener!=null){
         listener.onEditStarted();
     }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    if(listener!=null){
        listener.onEditCompleted();
    }
    }
    public void resetcontrols(){
        seekbar_brightness.setProgress(100);
        seekbar_contrast.setProgress(0);
        seekbar_saturation.setProgress(10);

    }
}
