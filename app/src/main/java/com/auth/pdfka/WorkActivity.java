package com.auth.pdfka;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class WorkActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, View.OnDragListener {
    ConstraintLayout motherLayout;
    FrameLayout imageLayout;
    CutLayout cutLayout;
    Button cutButton, backButton;
    int maxHeight,maxWidth;
    View selected_item = null;
    Bitmap bmp;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        cutLayout = new CutLayout(getApplicationContext());
        motherLayout = findViewById(R.id.motherLayout);
        cutButton=findViewById(R.id.cutButton);
        cutButton.setOnClickListener(this);
        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        imageLayout = findViewById(R.id.imageLayout);

        /*imageLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN||motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    selected_item = view;
                    maxHeight=imageLayout.getHeight();
                    maxWidth=imageLayout.getWidth();
                    double pointX=motionEvent.getX();
                    double pointY = motionEvent.getY();
                    if(pointY<0) pointY=0;
                    if(pointY>maxHeight) pointY=maxHeight;
                    int centerX = cutLayout.params.leftMargin+cutLayout.params.width/2;
                    int centerY = cutLayout.params.topMargin+cutLayout.params.height/2;

                    if(pointX>centerX)
                    {
                        cutLayout.params.gravity= Gravity.LEFT;
                        cutLayout.params.rightMargin=maxWidth-(int)pointX;
                    }
                    else
                    {

                        cutLayout.params.leftMargin=(int)pointX;
                        cutLayout.params.gravity= Gravity.RIGHT;
                    }
                    if(pointY>centerY)
                    {
                        cutLayout.params.gravity= Gravity.TOP;
                        cutLayout.params.bottomMargin=maxHeight-(int)pointY;
                    }
                    else
                    {
                        cutLayout.params.gravity= Gravity.BOTTOM;
                        cutLayout.params.topMargin=(int)pointY;
                    }

                    cutLayout.params.width=maxWidth-cutLayout.params.leftMargin-cutLayout.params.rightMargin;
                    cutLayout.params.height=maxHeight-cutLayout.params.bottomMargin-cutLayout.params.topMargin;
                    cutLayout.cutView.setLayoutParams(cutLayout.params);
                    return true;
                }

                return false;
            }

        });*///под вопросом, может через анонимный класс?
        imageLayout.setOnTouchListener(this);

        imageLayout.addView(cutLayout);
cutLayout.rightBottomPoint.setOnDragListener(this);
cutLayout.leftTopPoint.setOnDragListener(this);
        imageView=findViewById(R.id.imageView);
        bmp= Utils.getBitmapFromIntent(WorkActivity.this);
        imageView.setImageBitmap(bmp);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cutButton:{

                //определение коэффициента масштабирования изображение/рамка
                float ratioX = (float)bmp.getHeight()/(float)cutLayout.getHeight();
                float ratioY = (float)bmp.getWidth()/(float)cutLayout.getWidth();
                Log.d("OLOLOG", "bmp " + bmp.getHeight() +" layout "+cutLayout.getHeight() + " ratioX "+ratioX);
                //определение границ нового изображения
                float left= (cutLayout.leftTopPoint.getX()+cutLayout.leftTopPoint.getHeight()/2);
                float top= (cutLayout.leftTopPoint.getY()+cutLayout.leftTopPoint.getHeight()/2);
                float width= (cutLayout.rightBottomPoint.getX()+cutLayout.leftTopPoint.getHeight()/2-left);
                float height= (cutLayout.rightBottomPoint.getY()+cutLayout.leftTopPoint.getHeight()/2-top);

                //получение нового изображения и сохранение вместо старого
                bmp = Bitmap.createBitmap(bmp,(int)(left*ratioY),(int)(top*ratioX),(int)((width)*ratioY),(int)((height)*ratioX));
                imageView.setImageBitmap(bmp);
                //а надо ли новую активность?
                /*Intent intent = new Intent(this,NewImageActivity.class);
                intent.setData(packBitmapToIntent(result));
                startActivity(intent);*/
                break;
            }
            case R.id.backButton: {
               Intent intent = new Intent();
                intent.setData(Utils.packBitmapToIntent(WorkActivity.this,bmp));
                setResult(1,intent);
                finish();
                /* Intent intent = new Intent(this,NewImageActivity.class);
                intent.setData(Utils.packBitmapToIntent(WorkActivity.this,bmp));
                startActivity(intent);*/
                break;
            }
            default:break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction()==MotionEvent.ACTION_POINTER_DOWN)
            Log.d("OLOLOG", "onTouch: ");
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN||motionEvent.getAction()==MotionEvent.ACTION_MOVE){
            selected_item = view;
            maxHeight=imageLayout.getHeight();
            maxWidth=imageLayout.getWidth();
            double pointX=motionEvent.getX();
            double pointY = motionEvent.getY();
            if(pointY<0) pointY=0;
            if(pointY>maxHeight) pointY=maxHeight;
            int centerX = cutLayout.params.leftMargin+cutLayout.params.width/2;
            int centerY = cutLayout.params.topMargin+cutLayout.params.height/2;

            if(pointX>centerX)
            {
                cutLayout.rightBottomPoint.setX((int)pointX-cutLayout.leftTopPoint.getWidth()/2);
                cutLayout.params.gravity= Gravity.LEFT;
                cutLayout.params.rightMargin=maxWidth-(int)pointX;
            }
            else
            {
                cutLayout.leftTopPoint.setX((int)pointX-cutLayout.leftTopPoint.getWidth()/2);
                cutLayout.params.leftMargin=(int)pointX;
                cutLayout.params.gravity= Gravity.RIGHT;
            }
            if(pointY>centerY)
            {
                cutLayout.rightBottomPoint.setY((int)pointY-cutLayout.leftTopPoint.getHeight()/2);
                cutLayout.params.gravity= Gravity.TOP;
                cutLayout.params.bottomMargin=maxHeight-(int)pointY;
            }
            else
            {
                cutLayout.leftTopPoint.setY((int)pointY-cutLayout.leftTopPoint.getHeight()/2);
                cutLayout.params.gravity= Gravity.BOTTOM;
                cutLayout.params.topMargin=(int)pointY;
            }

            cutLayout.params.width=maxWidth-cutLayout.params.leftMargin-cutLayout.params.rightMargin;
            cutLayout.params.height=maxHeight-cutLayout.params.bottomMargin-cutLayout.params.topMargin;
            cutLayout.cutView.setLayoutParams(cutLayout.params);
            return true;
        }

        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch(event.getAction()) {

            case DragEvent.ACTION_DRAG_ENDED   :
                selected_item.setX( event.getX()-selected_item.getWidth()/2);
                selected_item.setY(event.getY()-selected_item.getHeight()*7/3);
                // if(selected_item.getId()==rightBottomPoint.getId())
                // frame.setY(selected_item.getY());
                // if(selected_item.getId()==rightBottomPoint.getId())
                //redrawFrame((int)leftTopPoint.getX(), (int)leftTopPoint.getY(),(int)rightBottomPoint.getX(),(int) rightBottomPoint.getY());
                //       v.setMinimumWidth((int) event.getX());
//v.setMinimumHeight((int) event.getY());
                Log.d("OLOLOG","X: " +event.getX()+ " Y: " + event.getY() + " Wid: "+v.getWidth()+" Hei: "+v.getHeight() + " corr x: "+ (event.getX()-v.getWidth()/2)+ " corr y: "+(event.getY()-v.getHeight()*7/3));
/*v.setX((int) event.getX()-v.getWidth()/2);
v.setY((int) event.getY()-v.getHeight()*7/3);*/
                // Do nothing
                break;

                  /* case DragEvent.ACTION_DROP:


                       // Do nothing
                       break;*/
            default: break;
        }
        return true;
    }

}