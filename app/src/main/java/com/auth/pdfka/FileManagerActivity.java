package com.auth.pdfka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FileManagerActivity extends AppCompatActivity {

    private FileManagerSupport fileManagerSupport;
    ItemListAdapter itemListAdapter;
    String name = "name", image = "image";
    ListView itemListView;
    ArrayList<File> contentFilesList;
    String prevDirPath;
    String rootDirPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
//проверка прав доступа к файловой системе
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        } else {
        init();
        }
    }
//инициализация
    public void init(){
        itemListView=findViewById(R.id.itemList);
        itemListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        fileManagerSupport = FileManagerSupport.getFileManagerSupport();
        fileManagerSupport.setCriteria(getIntent().getStringExtra("criteria"));

        getFolderContent(Environment.getExternalStorageDirectory());
        rootDirPath = Environment.getExternalStorageDirectory().getParent();//получение корневой доступной папки
        prevDirPath = rootDirPath;
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (contentFilesList.get(position).isDirectory())
                    getFolderContent(contentFilesList.get(position));
                else
                    openFileInActivity(contentFilesList.get(position));
            }
        });
    }
    //запрос прав на доступ к файловой системе
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==0)
        init();
        else
        { this.finishActivity(0);}

    }
//отработка возврата
    @Override
    public void onBackPressed() {

     if(!prevDirPath.equals(rootDirPath))//нельзя выйти дальше корня
            getFolderContent(new File(prevDirPath));
    }
//получение содержимого папки
    void getFolderContent(File folder){
        prevDirPath= folder.getParentFile().getAbsolutePath();//получение пути до предыдущей папки
        contentFilesList = new ArrayList<>(Arrays.asList(folder.listFiles()));//получение содержимого папки
       contentFilesList=fileManagerSupport.filter(contentFilesList);//фильтрация списка согласно критерию

        ArrayList<Map<String,Object>>contentDataList= new ArrayList<Map<String,Object>>(contentFilesList.size());//создание списка пар <тип,содержимое>
        Map<String,Object> contentData;//пары <тип, содержимое>, где тип "name" - имя файла, тип "image" - отображаемое изображение
        for(File file: contentFilesList){
            contentData = new HashMap<String, Object>();
            contentData.put(name,file.getName());//в раздел name сохраняется имя файла
            contentData.put(image,fileManagerSupport.getIcon(file));//в раздел image сохраняется ID изображения файла
            contentDataList.add(contentData);
        }
        String[] fromFilesList={name,image};
        int[] toItemList={R.id.itemName,R.id.itemIcon};
        itemListAdapter = new ItemListAdapter(this,contentDataList,R.layout.item_file_manager,fromFilesList,toItemList);
        itemListView.setAdapter(itemListAdapter);
        registerForContextMenu(itemListView);
    }
//передача файла в активность отображения
    void openFileInActivity(File file){

        Uri uri=null;
        try {
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
        }
        catch(Exception e)
        {
            Log.d(TAG, "onCreate: " + e.getMessage());
        }

       // Intent shareIntent = new Intent(Intent.ACTION_VIEW);
        Intent shareIntent = new Intent(this, PDFViewActivity.class);
        shareIntent.setDataAndType(uri, this.getContentResolver().getType(uri))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //startActivity(Intent.createChooser(shareIntent, "getString"));
        startActivity(shareIntent);



    }
//адаптер отображения списка файлов
    class ItemListAdapter extends SimpleAdapter {

        public ItemListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public void setViewText(TextView v, String text) {
            super.setViewText(v, text);
        }

        @Override
        public void setViewImage(ImageView v, int value) {
            super.setViewImage(v, value);
        }
    }
}