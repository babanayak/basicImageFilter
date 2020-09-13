package com.example.android.basicfilterapp.Interface;

public interface EditImageFragemntListener {
    void OnBrightnessChanged(int Brightness);
    void OnContrastChanged(float Contrast);
    void OnSaturationChanged(float Saturation);
    void onEditStarted();
    void onEditCompleted();
}
