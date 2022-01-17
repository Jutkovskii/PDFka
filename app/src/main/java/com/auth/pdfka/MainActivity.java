package com.auth.pdfka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,FileManagerActivity.class);
        intent.putExtra("criteria", "pdf");
        ImageView imageView = findViewById(R.id.imageView2);
        InputStream bm = getResources().openRawResource(R.raw.temp);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(bm);

        Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
        imageView.setImageBitmap(bitmap);
        startActivity(intent);
//setListener(imageView);

    }
    int xPos=0,yPos;
    void setListener(View view){

        view.setOnTouchListener(new View.OnTouchListener() {
            float mx=0, my=0;
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {

                float curX, curY;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mx = event.getX();
                        my = event.getY();
                        //pdfView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        curX = event.getX();
                        curY = event.getY();
                        view.scrollBy((int) (mx - curX), (int) (my - curY));
                        xPos = (int) ( mx -curX);
                        //yPos = (int) ( curY);
                        mx = curX;
                        my = curY;
                        break;
                    case MotionEvent.ACTION_UP:
                        curX = event.getX();
                        curY = event.getY();
                        view.scrollBy((int) (mx - curX), (int) (my - curY));
                        xPos = (int) ( mx -curX);
                        // yPos = (int) ( curY);
                        // pdfView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        break;
                }

                return true;
            }
        });
    }

}
