package com.auth.pdfka;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class NewImageActivity extends AppCompatActivity {

    ImageView imageView;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_image);
        imageView = findViewById(R.id.imageView3);
        imageView.setImageBitmap(getBitmapFromIntent());

    }

    Bitmap getBitmapFromIntent()
    {
        try {
            return MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}