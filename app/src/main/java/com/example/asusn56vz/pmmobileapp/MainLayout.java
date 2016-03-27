package com.example.asusn56vz.pmmobileapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.math.BigDecimal;

public class MainLayout extends LinearLayout {

    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    private static final int SLIDING_DURATION = 500;
    private static final int QUERY_INTERVAL = 16;
    int mainLayoutWidth;
    int menu_toggle;
    int getting_data = 0;
    View menu;
    private View content;
    private static int menuLeftMargin = 15;

    protected enum MenuState {
        HIDING, HIDDEN, SHOWING, SHOWN,
    }

    ;

    private int contentXOffset;
    private MenuState currentMenuState = MenuState.HIDDEN;
    private Scroller menuScroller = new Scroller(this.getContext(),
            new EaseInInterpolator());
    private Runnable menuRunnable = new MenuRunnable();
    private Handler menuHandler = new Handler();
    int prevX = 0;
    boolean isDragging = false;
    int lastDiffX = 0;

    public MainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mainLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        menuLeftMargin = mainLayoutWidth * 10 / 100;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        super.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return MainLayout.this.onTouch(view, motionEvent);
            }
        });
        menu = this.getChildAt(0);
        content = this.getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if(currentMenuState == MenuState.SHOWING || currentMenuState == MenuState.SHOWN || currentMenuState == MenuState.HIDING){
            menu_toggle = 1;
        }
        if (changed) {
            Log.w("enter", "here");
            LayoutParams contentLayoutParams = (LayoutParams) content
                    .getLayoutParams();
            contentLayoutParams.height = this.getHeight();
            contentLayoutParams.width = this.getWidth();
            LayoutParams menuLayoutParams = (LayoutParams) menu
                    .getLayoutParams();
            menuLayoutParams.height = this.getHeight();
            menuLayoutParams.width = this.getWidth() - menuLeftMargin;
            Log.w("height",String.valueOf(menuLayoutParams.height));
        }

        if (menu_toggle == 0) {
            menu.layout(left + menuLeftMargin, top, right, bottom);
            content.layout(left - contentXOffset, top, right - contentXOffset,
                    bottom);
        } else if (menu_toggle == 1){
            menu.layout(left + menuLeftMargin, top, right, bottom);
        }


    }

    public MenuState getCurrentMenuState(){
        return currentMenuState;
    }

    public void toggleMenu() {
        menu_toggle = 0;
        if (currentMenuState == MenuState.HIDING)
            return;
        if (currentMenuState == MenuState.SHOWING) {
            menu_toggle = 1;
            return;
        }

        switch (currentMenuState) {
            case HIDDEN:
                currentMenuState = MenuState.SHOWING;
                menu.setVisibility(View.VISIBLE);
                menuScroller.startScroll(contentXOffset, 0, -menu.getLayoutParams().width, 0,
                        SLIDING_DURATION);
                break;
            case SHOWN:
                currentMenuState = MenuState.HIDING;
                //menu.setVisibility(View.GONE);
                menuScroller.startScroll(contentXOffset, 0, -contentXOffset, 0,
                        SLIDING_DURATION);
                break;
            default:
                break;
        }
        menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
        this.invalidate();
    }

    protected class MenuRunnable implements Runnable {
        @Override
        public void run() {
            boolean isScrolling = menuScroller.computeScrollOffset();
            adjustContentPosition(isScrolling);
        }
    }

    private void adjustContentPosition(boolean isScrolling) {
        int scrollerXOffset = menuScroller.getCurrX();

        content.offsetLeftAndRight(scrollerXOffset - contentXOffset);

        contentXOffset = scrollerXOffset;
        this.invalidate();
        if (isScrolling)
            menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
        else
            this.onMenuSlidingComplete();
    }

    private void onMenuSlidingComplete() {
        switch (currentMenuState) {
            case SHOWING:
                currentMenuState = MenuState.SHOWN;
                break;
            case HIDING:
                currentMenuState = MenuState.HIDDEN;
                menu.setVisibility(View.GONE);
                break;
            default:
                return;
        }
    }

    protected class EaseInInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float t) {
            return (float) Math.pow(t - 1, 5) + 1;
        }

    }

    public boolean isMenuShown() {
        return currentMenuState == MenuState.SHOWN;
    }


    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                return true;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1) {
                        if (currentMenuState == MenuState.SHOWN && getting_data == 0) {
                            toggleMenu();
                        }
                    }

                    // Right to left swipe action
                    else {
                        if (currentMenuState == MenuState.HIDDEN && getting_data == 0) {
                            toggleMenu();
                        }
                    }

                } else {
                    // consider as something else - a screen tap for example
                }
                return true;
            default:
                break;
        }
        return false;
    }

}
