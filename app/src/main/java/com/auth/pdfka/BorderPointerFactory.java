package com.auth.pdfka;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class BorderPointerFactory extends androidx.appcompat.widget.AppCompatImageView {

    public static int sdvig=0;
    Context context;
    FrameLayout.LayoutParams layoutParams;
    public BorderPointerFactory(@NonNull Context context) {
        super(context);
        this.context=context;
        ImageView borderPointer=new ImageView(context);
        borderPointer.setImageResource(R.drawable.circle);
        sdvig=borderPointer.getDrawable().getMinimumHeight()/2;

    }

    public ImageView getBorderPointer(int x,int y){
        ImageView borderPointer=new ImageView(context);
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        borderPointer.setLayoutParams(layoutParams);
        borderPointer.setImageResource(R.drawable.circle);
        borderPointer.setImageAlpha(100);
        borderPointer.setX(x);
        borderPointer.setY(y);
        return  borderPointer;
    }
}
