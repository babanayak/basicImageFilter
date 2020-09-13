package com.example.android.basicfilterapp.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class NonswipeableviewPager extends ViewPager {
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public NonswipeableviewPager(@NonNull Context context) {
        super(context);
        setMyscroller();
    }

    private void setMyscroller() {
        try {
            Class<?> viewpager=ViewPager.class;
            Field scroller=viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this,new MyScroller(getContext()));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public NonswipeableviewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setMyscroller();
    }

    private class MyScroller extends Scroller{
        public MyScroller(Context context) {
            super(context,new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 400);
        }
    }
}
