package com.auth.pdfka;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

public class CutFrameView extends androidx.appcompat.widget.AppCompatImageView  {
    public RectF rectF;
    Paint paint;
    public CutFrameView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
