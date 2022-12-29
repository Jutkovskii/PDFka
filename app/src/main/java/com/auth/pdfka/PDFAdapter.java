package com.auth.pdfka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.ViewHolder> implements View.OnClickListener {
    PdfRenderer pdfRenderer;
    LayoutInflater inflater;
    Context context;
    public PdfRenderer.Page curPage;//текущая страница
    public int page = 0;
    public int prevPage=0;
    public int deletedPage=0;
ArrayList<Integer> toRemove;//список номеров страниц для удаления
ArrayList<Integer> toShow;//список номкеров страниц для отображения
    ArrayList<Integer> deletedPagesList;//список номеров удаленных страниц
    Map<Integer,Integer>deletedPages;//тэги удаленных страниц
ArrayList<Map<Integer, Bitmap>> changedPages;//измененные страницы
    PDFAdapter(PdfRenderer pdfRenderer, Context context) {
        this.pdfRenderer = pdfRenderer;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
toRemove = new ArrayList<>();
        toShow = new ArrayList<>();
        deletedPagesList = new ArrayList<>();
        deletedPages = new HashMap<>();
        for(int i=0;i<pdfRenderer.getPageCount();i++)
            toShow.add(i);
changedPages=new ArrayList<>();
    }

    ViewHolder viewHolder;
public View localView;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        localView = inflater.inflate(R.layout.current_page, parent, false);
        ViewHolder holder = new ViewHolder(localView);

        return holder;
}

//элемент стал отображаемым
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        prevPage=page;
        page = holder.getAdapterPosition();
        holder.flagView.setTag(page);

    }
//элемент вышел из поля видимости
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        deletedPage=holder.getPosition();
    }

    //отображение списка страниц
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        viewHolder=holder;

        holder.flagView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("Выберите действие");
                contextMenu.add(1, position, 3, "Удалить");//groupId, itemId, order, title
                contextMenu.add(4, position, 6, "Редактировать");
            }
        });
             Bitmap pageBitmap;
        try {
            //int sdvig=position;
           holder.itemView.setTag(position);
             if (curPage != null)
                curPage.close();
             int temp = toShow.get(position);//проверка, надо ли отображать
            curPage = pdfRenderer.openPage(temp);
            int imageWidth = curPage.getWidth();
            int imageHeight = curPage.getHeight();
            int displayWidth = context.getResources().getDisplayMetrics().widthPixels;
            int displayHeight = context.getResources().getDisplayMetrics().heightPixels;
            int bitmapWidth, bitmapHeight;
            //проверка ориентации страницы
            if (imageWidth > imageHeight) {
                bitmapWidth = displayWidth;
                bitmapHeight = imageHeight;
            } else {
                bitmapWidth = imageWidth;
                bitmapHeight = imageHeight;
            }

            int rotate=2;

            pageBitmap = Bitmap.createBitmap(bitmapWidth * rotate, bitmapHeight * rotate, Bitmap.Config.ARGB_8888);

            curPage.render(pageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
               for(Map <Integer,Bitmap> tmp: changedPages)//проверка, изменена ли страница
                    if(tmp.containsKey(position)) {
                        pageBitmap=Bitmap.createScaledBitmap(tmp.get(position),bitmapWidth,bitmapHeight,false);
                    }

            holder.flagView.setImageBitmap(pageBitmap);

        } catch (Exception e) {

        }


    }

    @Override
    public int getItemCount() {
        int size = toShow.size();
        return size;
    }

    @Override
    public void onClick(View view) {
        int tag = (int) view.getTag();

        deletePage( tag);
    }
//удаление странцы
    //страница вносится в список удаленных и ее отображение игнорируется
    public void deletePage(int tag){
        int removePage=-1;
        removePage=tag;
        deletedPagesList.add(toShow.get(tag));
        deletedPages.put(toShow.get(tag),tag);
        if(removePage!=-1)
        {
            notifyItemRemoved(removePage);
            toRemove.add(removePage);
            for(int i=removePage;i<toShow.size()-1;i++)
                toShow.set(i,toShow.get(i+1));
            toShow.remove(toShow.size()-1);
            notifyDataSetChanged();
            Toast toast = Toast.makeText(context,"page "+page+" tag "+tag +" prevPage "+prevPage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
//отмена удаления страницы
    public void undo(){
        int currentList=deletedPagesList.get(deletedPagesList.size()-1);
        int currentTag=deletedPages.get(currentList);

   toShow.add(0);
        for(int i=toShow.size()-1;i>currentTag;i--){
            int prev=toShow.get(i-1);
            toShow.set(i,prev);
        }
        toShow.set(currentTag,currentList);
        deletedPages.remove(currentList);
        deletedPagesList.remove(deletedPagesList.size()-1);
        notifyItemInserted(currentTag);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView flagView;
        public CardView cardView;
        ViewHolder(View view) {
            super(view);
cardView = (CardView) view.findViewById(R.id.cardView);
            flagView = (ImageView) view.findViewById(R.id.imageView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);


        }

    }


}
