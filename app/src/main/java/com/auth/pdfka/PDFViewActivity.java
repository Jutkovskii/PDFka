package com.auth.pdfka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class PDFViewActivity extends AppCompatActivity {
    PdfRenderer pdfRenderer;
    ParcelFileDescriptor descriptor;
PDFAdapter pdfAdapter;
    SharedPreferences sPref;
    Uri uri;
    File currentPDF;
    String filename;
    PinchRecyclerView pdfView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_view);

        Intent intent = getIntent();
         uri = intent.getData();//получение uri файла для отображения

        filename=uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
        currentPDF  =getFileFromUri(uri);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(filename);

        ConstraintLayout pdfLayout = findViewById(R.id.pdfLayout);
        pdfView = new PinchRecyclerView(this);

        ViewGroup.LayoutParams params = pdfLayout.getLayoutParams();
        pdfView.setLayoutParams(params);

        pdfLayout.addView(pdfView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        pdfView.setLayoutManager(llm);
        init(0);
       // registerForContextMenu(pdfView);
        pdfView.scrollToPosition(load(uri));

    }

    void init(int pos){
        try {
            descriptor = ParcelFileDescriptor.open(currentPDF, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(descriptor);
            pdfAdapter=new PDFAdapter(pdfRenderer, this);
            pdfView.setAdapter(pdfAdapter);
            // pdfView.setBackgroundColor(Color.RED);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //получение файла из uri
    File getFileFromUri(Uri uri){
        File tempFile=null;
        try {

            InputStream tempInputStream = getContentResolver().openInputStream(uri);
             tempFile = File.createTempFile("temp",".pdf");
            OutputStream tempOutputStream = new FileOutputStream(tempFile);

            int EOF = -1;
            int DEFAULT_BUFFER_SIZE = 1024 * 4;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while (EOF != tempInputStream.read(buffer)) {
                tempOutputStream.write(buffer);
            }
            tempOutputStream.flush();
            uri = Uri.fromFile(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
return tempFile;

    }

    //сохранение информации о файле (номер последней открытой страницы)
    void save(Uri uri,int pos){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("Uri", uri.toString());
        ed.putInt("page", pos);
        ed.commit();
    }

    //если файл ранее был открыт на какой-то странице, скролл до неё
    int load(Uri uri){
        sPref = getPreferences(MODE_PRIVATE);
        String qwe =sPref.getString("Uri", "");
        if(uri.toString().equals(qwe))
            return sPref.getInt("page",0);
        else
            return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        save(uri,pdfAdapter.page);
    }

    //обработка нажатий контекстного меню
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            if (item.getOrder() == 3) {//удаление страницы
                int page = item.getItemId();
                pdfAdapter.deletePage(page);
            }

            if (item.getOrder() == 6) {//редактирование страницы
                int page = item.getItemId();

                PdfRenderer.Page tempPage = null;
                try {
                    tempPage = new PdfRenderer(descriptor).openPage(page);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //получение битмапа выбранной страницы и отправка на редактирование
                Bitmap pageBitmap = Bitmap.createBitmap(tempPage.getWidth(), tempPage.getHeight(), Bitmap.Config.ARGB_8888);
                tempPage.render(pageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                Intent intent = new Intent(this, WorkActivity.class);
                intent.setData(Utils.packBitmapToIntent(PDFViewActivity.this, pageBitmap));
                startActivityForResult(intent,page);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return super.onContextItemSelected(item);
    }

    //обработка результатов редактирования
    //возврат измененной страницы и отображение её вместо изначальной
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //changedBMP= Utils.getBitmapFromIntent(PDFViewActivity.this);
        Bitmap changedBMP=null;
        try {
            changedBMP = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        init(requestCode);
        pdfView.scrollToPosition(requestCode);
        Map<Integer,Bitmap> changedPage = new HashMap<>();
        changedPage.put(requestCode,changedBMP);
     pdfAdapter.changedPages.add(changedPage);

    }

    @Override
    public void onBackPressed() {
        if (pdfAdapter.toShow.size()<pdfRenderer.getPageCount())
        pdfAdapter.undo();
        else
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       // if (item.getItemId()==R.menu.menu)
            saveNewPDF();
        return true;// super.onOptionsItemSelected(item);
    }

    void saveNewPDF(){
        PdfDocument document = new PdfDocument();
        String root= Environment.getExternalStorageDirectory().getAbsolutePath();
        root+="/PDFka/";
        new File(root).mkdir();
        root+=filename;
        File pdfResult = new File(root);

        try {

            FileOutputStream pdfStream=new FileOutputStream(pdfResult.getAbsolutePath());
            PdfRenderer pdfRenderer;
            ParcelFileDescriptor descriptor;
            descriptor = ParcelFileDescriptor.open(currentPDF, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(descriptor);
            for(int i=0;i<pdfAdapter.toShow.size();i++)
            {

                PdfRenderer.Page inputPage =pdfRenderer.openPage(pdfAdapter.toShow.get(i));
                Bitmap tempBitmap = Bitmap.createBitmap(inputPage.getWidth() , inputPage.getHeight(), Bitmap.Config.ARGB_8888);
                inputPage.render(tempBitmap,null,null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                inputPage.close();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(inputPage.getWidth(),inputPage.getHeight(),i).create();
                PdfDocument.Page outputPage = document.startPage(pageInfo);

                Canvas canvas=outputPage.getCanvas();


                Paint paint = new Paint();
                canvas.drawBitmap(tempBitmap,0,0,paint);
                document.finishPage(outputPage);

            }
            document.writeTo(pdfStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        document.close();
    }
}