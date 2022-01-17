package com.auth.pdfka;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FileManagerSupport {

    private static ArrayList<String> criteria;
    protected static String[] supportedFormats = {"pdf","jpg","png","bmp", "webp"};
    private static FileManagerSupport fileManagerSupport;
    protected Map<String,Integer> icons;
    private FileManagerSupport(){
        criteria = new ArrayList<String>();

        icons=new HashMap<String, Integer>();
        icons.put("",R.raw.dir);
        icons.put(supportedFormats[0],R.raw.pdf);
        for(int i=1;i<5;i++)
        icons.put(supportedFormats[i],R.raw.img);

    }


    public static FileManagerSupport getFileManagerSupport(){
        if (fileManagerSupport == null) fileManagerSupport=new FileManagerSupport();
        return  fileManagerSupport;
    }
    public  void setCriteria(String [] Criteria){
        criteria.clear();
       for(String crit: Criteria)
                criteria.add(crit);
    }
    public void setCriteria(String Criteria){
        criteria.clear();
        if(Criteria.isEmpty())
            setCriteria();
        else
            criteria.add(Criteria);
    }
    public  void setCriteria(){
        criteria.clear();
      /*  for(String crit: supportedFormats)
            criteria.add(crit);*/
    }
    public int getIcon(File file){
        if(file.isDirectory())
            return icons.get("");
        else {
            for(String format: supportedFormats)
                if(file.getName().endsWith("."+format))
                    return icons.get(format);
                return R.raw.file;
        }
    }
    public ArrayList<File> filter(ArrayList<File> contentFilesList) {

        contentFilesList.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(o1.isDirectory()&&o2.isFile()) return -1;
                if(o2.isDirectory()&&o1.isFile()) return 1;
                return o1.compareTo(o2);
            }
        });
        ArrayList<File> result = new ArrayList<>();
        for (File file : contentFilesList) {
            if (file.isFile()) {
                String[] filenameParts = file.getName().split("\\.");
                String suffix = filenameParts[filenameParts.length - 1];
                if(criteria.isEmpty())
                    result.add(file);
                else
                    if (criteria.contains(suffix))
                        result.add(file);
            }
            else
                result.add(file);
        }

        return result;
    }
}
