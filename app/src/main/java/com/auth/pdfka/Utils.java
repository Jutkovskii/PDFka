package com.auth.pdfka;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//вспомогательные функции
public class Utils {

    //подготовка битмапа для отправки через интент
    public static Uri packBitmapToIntent(Activity activity,Bitmap bitmap){
        Uri uri=null;

        try {
            File resultImg = File.createTempFile("result",".png", activity.getCacheDir());

            FileOutputStream imgOutputStream = new FileOutputStream(resultImg);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imgOutputStream);
            uri=Uri.fromFile(resultImg);
            imgOutputStream.flush();
            imgOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    //получение битмапа от интента
    public static Bitmap getBitmapFromIntent(Activity activity){
        try {
            return MediaStore.Images.Media.getBitmap(activity.getContentResolver(),activity.getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
