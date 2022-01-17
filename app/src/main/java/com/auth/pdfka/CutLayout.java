package com.auth.pdfka;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class CutLayout extends FrameLayout //implements View.OnDragListener, View.OnTouchListener
{
    Paint borderPaint;
    View selected_item = null;
    LayoutParams params;
    CutFrameView cutView;
BorderPointerFactory borderPointerFactory;
    ImageView leftTopPoint, rightBottomPoint;
    public CutLayout(@NonNull Context context) {
        super(context);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        cutView  = new CutFrameView(context);
        params.leftMargin=50;
        params.topMargin=250;
        params.rightMargin=150;
        params.bottomMargin=450;
        params.width=400;
        params.height=700;
        cutView.setLayoutParams(params);
        GradientDrawable drawableBorder = new GradientDrawable();
        drawableBorder.setAlpha(125);
        drawableBorder .setStroke(10, Color.BLACK);
        cutView.setBackground(drawableBorder);
        addView(cutView);
        borderPointerFactory = new BorderPointerFactory(context);

        leftTopPoint = borderPointerFactory.getBorderPointer(params.leftMargin- BorderPointerFactory.sdvig,params.topMargin- BorderPointerFactory.sdvig);
        rightBottomPoint = borderPointerFactory.getBorderPointer(params.leftMargin+params.width- BorderPointerFactory.sdvig,params.topMargin+params.height- BorderPointerFactory.sdvig);
        int x=leftTopPoint.getHeight();
        addView(rightBottomPoint);
        addView(leftTopPoint);

    }


    //@Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        return false;
    }

    //@Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            selected_item = view;
            return true;
        }
        return false;
    }
}
