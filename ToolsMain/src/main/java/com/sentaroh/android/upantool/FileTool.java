package com.sentaroh.android.upantool;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.contact.ContactUtil;
import com.sentaroh.android.upantool.contact.FileUtil;
import com.sentaroh.android.upantool.contact.MyContact;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.utils.Platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

//import me.jahnen.libaums.core.fs.UsbFile;


public class FileTool {


    public  static  String MIGRATIONNAME = "/一键备份";


    public interface ProgressListener {
        void onProgress(int var1);
    }

//    public static int getListIconOfFile(boolean isFolder, String fileName){
//
//        return 0;
//    }

    public static String getFileSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + "KB";
        } else if (size < 1024 * 1024 * 1024){
            return (size / 1024 / 1024) + "MB";
        }else{
            return String.format("%.2f",size/1024.0/1024.0/1024.0) + "GB";
        }
    }

    public static int getResIdFromFileName(boolean isFolder, String fileName) {

        if (isFolder) {
            //文件夹
            return R.mipmap.ic_fm_filetype_folder_s;
        } else {
            fileName = fileName.toLowerCase();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                //图片文件
                return R.mipmap.ic_fm_list_item_pic;
            } else if (fileName.endsWith(".txt")) {
                //TXT 文件
                return R.mipmap.ic_fm_filetype_txt_s;
            } else if (fileName.endsWith(".pdf")) {
                //PDF 文件
                return R.mipmap.ic_fm_filetype_pdf_s;
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                return R.mipmap.ic_fm_filetype_xls_s;
            } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
                return R.mipmap.ic_fm_filetype_ppt_s;
            } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                return R.mipmap.ic_fm_filetype_doc_s;
            } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi")) {
                //视频文件
                return R.mipmap.ic_fm_list_item_video;
            } else {
                //未知文件
                return R.mipmap.ic_fm_filetype_other_s;
            }
       }
   }

    public static Boolean isVideo(String fileName){
        if((fileName.toLowerCase().endsWith(".mp4") || fileName.toLowerCase().endsWith(".avi"))){
            return true;
        }
        return false;

    }


    public static Boolean isAudio(String fileName){
        if((fileName.toLowerCase().endsWith(".mp3") || fileName.toLowerCase().endsWith(".wav") || fileName.toLowerCase().endsWith(".aac") || fileName.toLowerCase().endsWith(".m4a") || fileName.toLowerCase().endsWith(".mid") || fileName.toLowerCase().endsWith(".ogg") || fileName.toLowerCase().endsWith(".xmf"))){
            return true;
        }
        return false;

    }
    public static int getResIdFromFileNameBig(boolean isFolder, String fileName) {

        if (isFolder) {
            //文件夹
            return R.mipmap.ic_fm_filetype_folder;
        } else {
            fileName = fileName.toLowerCase();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg") || fileName.endsWith(".webp")) {
                //图片文件
                return R.mipmap.ic_fm_list_item_pic;
            } else if (fileName.endsWith(".txt")) {
                //TXT 文件
                return R.mipmap.ic_fm_filetype_txt;
            } else if (fileName.endsWith(".pdf")) {
                //PDF 文件
                return R.mipmap.ic_fm_filetype_pdf;
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                return R.mipmap.ic_fm_filetype_xls;
            } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
                return R.mipmap.ic_fm_filetype_ppt;
            } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                return R.mipmap.ic_fm_filetype_doc;
            } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi")) {
                //视频文件
                return R.mipmap.ic_fm_list_item_video;
            } else {
                //未知文件
                return R.mipmap.ic_fm_filetype_other;
            }
        }
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }           }
        return filename;
    }   /*  * Java文件操作 获取不带扩展名的文件名  */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    //mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".jpg");
   // mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".mp4");
    public static List<File> getSpecificTypeOfFile(Context context, String[] extension) {
        List<String> fileUrls = new ArrayList<>();

        //从外存中获取
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名


        //Set set = MediaStore.getExternalVolumeNames(context);
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //构造筛选语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver = context.getContentResolver();
        //获取游标
        Cursor cursor = resolver.query(fileUri, projection, selection, null, sortOrder);
        if (cursor == null)
            return null;
        //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
        if (cursor.moveToLast()) {
            do {
                //输出文件的完整路径
                String data = cursor.getString(0);
                String [] arrv = data.split("/");
                if(arrv.length < 9 && !data.contains("/.")) {
                    Log.d("tag", data);
                    fileUrls.add(data);

//                    if(fileUrls.size() > 200){
//                        break;
//                    }
                }

            } while (cursor.moveToPrevious());
        }
        cursor.close();
        List<File> rets = new ArrayList<>();
        for (int i = 0; i < fileUrls.size(); i++) {
            File file = new File(fileUrls.get(i));
            rets.add(file);
        }
        Log.d("ccccccc", "getSpecificTypeOfFile: " + rets.size());
        return rets;
    }


    public static List<File> getSpecificTypeOfFile_(Context context, String[] extension) {
        List<String> fileUrls = new ArrayList<>();

        //从外存中获取
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名


        //Set set = MediaStore.getExternalVolumeNames(context);
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //构造筛选语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver = context.getContentResolver();
        //获取游标
        Cursor cursor = resolver.query(fileUri, projection, selection, null, sortOrder);
        if (cursor == null)
            return null;
        //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
        if (cursor.moveToLast()) {
            do {
                //输出文件的完整路径
                String data = cursor.getString(0);
                String [] arrv = data.split("/");
                if(arrv.length < 9 && !data.contains("/.")) {
                    Log.d("tag", data);
                    fileUrls.add(data);


                }

            } while (cursor.moveToPrevious());
        }
        cursor.close();
        List<File> rets = new ArrayList<>();
        for (int i = 0; i < fileUrls.size(); i++) {
            File file = new File(fileUrls.get(i));
            rets.add(file);
        }
        Log.d("ccccccc", "getSpecificTypeOfFile: " + rets.size());
        return rets;
    }



    public static List getPic(Context context){
        String [] arr = {".jpg",".png",".jpeg"};
        List<File> files = getSpecificTypeOfFile(context,arr);
        List datas = new ArrayList();
        HashMap<String,List> map = new HashMap<String,List>();
        for(int i= 0;i < files.size();i++){
            File f = files.get(i);
            String path = f.getAbsolutePath();
            String name = f.getName();
            String dir = path.split("/" + name)[0];
            String[] dirs = dir.split("/");
            String key  = dirs[dirs.length - 1];
            List list = map.get(key);
            if(list == null){
                list = new ArrayList<>();
                map.put(key,list);

                HashMap<String,List> dataMap = new HashMap<String,List>();
                dataMap.put(key,list);
                datas.add(dataMap);
            }
            if(f.isFile() && f.exists()) {
                list.add(f);
            }

        }

        return datas;

    }


    public static List getPic_(Context context){
        String [] arr = {".jpg",".png",".jpeg",".bmp",".webp",".heic",".heif"};
        List<File> files = getSpecificTypeOfFile(context,arr);
        return files;

    }

    public static boolean isSelfEncryptFile(String filePath){
        if((filePath.contains("[加密") || (filePath.contains("[Encrypt"))) && filePath.contains("].zip")){
            return true;
        }
        return false;
    }
    public static boolean isPicture(String filePath){


        if(filePath.length() < 7){
            return false;
        }

        String [] arr = {".jpg",".png",".jpeg",".bmp",".webp",".heic",".heif"};

        String suf = filePath.substring(filePath.length() - 6);
        String[] strs = suf.split("\\.");
        if(strs.length > 0) {
            suf = "." + strs[strs.length - 1].toLowerCase();
        }


        List ls = Arrays.asList(arr);
        if(ls.contains(suf)){
            return true;
        }
        return false;

    }



    public static List getVideos(Context context){
        String [] arr = {".mp4",".avi",".rmvb",".mov",".mpeg"};
        List<File> files = getSpecificTypeOfFile(context,arr);

        List datas = new ArrayList();
        HashMap<String,List> map = new HashMap<String,List>();
        for(int i= 0;i < files.size();i++){
            File f = files.get(i);
            String path = f.getAbsolutePath();
            String name = f.getName();
            String dir = path.split("/" + name)[0];
            String[] dirs = dir.split("/");
            String key  = dirs[dirs.length - 1];
            List list = map.get(key);
            if(list == null){
                list = new ArrayList<>();
                map.put(key,list);
                HashMap<String,List> dataMap = new HashMap<String,List>();
                dataMap.put(key,list);
                datas.add(dataMap);
            }
            if(f.isFile() && f.exists()) {
                list.add(f);
            }

        }

        return datas;

    }

    public static List getVideos_(Context context){
        String [] arr = {".mp4",".avi","rmvb",".mov",".mpeg",".webm"};
        List<File> files = getSpecificTypeOfFile(context,arr);
        return files;

    }



    public static List getAudios(Context context){

        String [] arr = {".aac",".m4a",".mp3","mid","ogg","xmf","wav"};
        List<File> files = getSpecificTypeOfFile(context,arr);

        List datas = new ArrayList();
        HashMap<String,List> map = new HashMap<String,List>();
        for(int i= 0;i < files.size();i++){
            File f = files.get(i);
//            String path = f.getAbsolutePath();
//            String name = f.getName();
//            String dir = path.split("/" + name)[0];
//            String[] dirs = dir.split("/");
//            String key  = dirs[dirs.length - 1];
//            List list = map.get(key);
//            if(list == null){
//                list = new ArrayList<>();
//                map.put(key,list);
//                HashMap<String,List> dataMap = new HashMap<String,List>();
//                dataMap.put(key,list);
//                datas.add(dataMap);
//            }
            if(f.isFile() && f.exists()) {
                if(f.canRead() && f.canWrite()) {
                    datas.add(f);
                }
            }

        }

        return datas;

    }
    public static List getAudios_(Context context){

        String [] arr = {".aac",".m4a",".mp3","mid","ogg","xmf","wav"};
        List<File> files = getSpecificTypeOfFile(context,arr);
        return files;

    }



    public static List getDoc_(Context context){

        String [] arr = {".html",".txt",".pdf",".doc",".docx",".ppt",".pptx",".xls","xmls"};
        List<File> files = getSpecificTypeOfFile(context,arr);
        return files;

    }





    public static List<File> getAllFileInDir(File dir) {
        if (dir == null || !dir.isDirectory()) return null;
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
//                (str.indexOf("ABC")!=-1
                //if (file.getName().toUpperCase().contains(fileName.toUpperCase())) {

                if(file.isFile() && file.exists()) {
                    list.add(file);
                }
                //}
                if (file.isDirectory()) {
                    list.addAll(getAllFileInDir(file));
                }
            }
        }
        return list;
    }



    public static List<SafFile3> getAllFileInDir_(SafFile3 dir) {
        if (dir == null || !dir.isDirectory()) return null;
        List<SafFile3> list = new ArrayList<>();
        SafFile3[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (SafFile3 file : files) {
//                (str.indexOf("ABC")!=-1
                //if (file.getName().toUpperCase().contains(fileName.toUpperCase())) {

                if(file.isFile() && file.exists()) {
                    list.add(file);
                }
                //}
                if (file.isDirectory()) {
                    list.addAll(getAllFileInDir_(file));
                }
            }
        }
        return list;
    }


    public static List getWeixi__(Context context){
        String [] arr = {".mp4",".avi","rmvb"};
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File download = new File(root + "/Android/data/com.tencent.mm/MicroMsg/Download");

        File pic = new File(root + "/Pictures/WeiXin");

        if(pic == null || pic.exists()) {
            if(pic.listFiles() == null) {
                pic = new File(root + "/Pictures/WeChat");
            }
        }

        File dd = new File(root + "/Download/WeiXin");
        if(dd == null || dd.exists()) {
            if(dd.listFiles() == null) {
                dd = new File(root + "/Download/WeChat");
            }
        }
        List<File> list = new ArrayList<>();

        if(pic.listFiles() != null) {
            if(pic.listFiles().length > 0) {
                list.addAll(getAllFileInDir(pic));
            }
        }

        if(download.listFiles() != null) {
            if(download.listFiles().length > 0) {
                list.addAll(getAllFileInDir(download));
            }
        }

        if(dd.listFiles() != null) {
            if(dd.listFiles().length > 0) {
                list.addAll(getAllFileInDir(dd));
            }
        }

        List datas = new ArrayList();
        HashMap<String,List> map = new HashMap<String,List>();
        for (File file : list) {
            String fileName = file.getName();
            fileName = fileName.toLowerCase();
            String picKey = "Pictures";
            String videoKey = "Videos";
            String textKey = "Docs";
            String otherKey = "Others";

            String[] nameTypes = fileName.split("\\.");
            if(nameTypes.length > 1) {
                String nameType = nameTypes[nameTypes.length - 1].toLowerCase();
                String[] pics = {"png", "jpg","jpeg","webp"};
                String[] videos = {"mp4", "avi", "rmvb"};
                String[] texts = {"pdf", "txt", "doc", "xls", "docx", "xlsx", "ppt", "pptx","log"};

                if (Arrays.asList(pics).contains(nameType)){
                    List mlist = map.get(picKey);
                    if(mlist == null){
                        mlist = new ArrayList<>();
                        map.put(picKey,mlist);

                        HashMap<String,List> dataMap = new HashMap<String,List>();
                        dataMap.put(picKey,mlist);
                        datas.add(dataMap);
                    }
                    mlist.add(file);
                }else  if (Arrays.asList(videos).contains(nameType)){
                    List mlist = map.get(videoKey);
                    if(mlist == null){
                        mlist = new ArrayList<>();
                        map.put(videoKey,mlist);
                        HashMap<String,List> dataMap = new HashMap<String,List>();
                        dataMap.put(videoKey,mlist);
                        datas.add(dataMap);
                    }
                    mlist.add(file);
                }else if (Arrays.asList(texts).contains(nameType)){
                    List mlist = map.get(textKey);
                    if(mlist == null){
                        mlist = new ArrayList<>();
                        map.put(textKey,mlist);
                        HashMap<String,List> dataMap = new HashMap<String,List>();
                        dataMap.put(textKey,mlist);
                        datas.add(dataMap);
                    }
                    mlist.add(file);
                }else{
                    List mlist = map.get(otherKey);
                    if(mlist == null){
                        mlist = new ArrayList<>();
                        map.put(otherKey,mlist);

                        HashMap<String,List> dataMap = new HashMap<String,List>();
                        dataMap.put(otherKey,mlist);
                        datas.add(dataMap);
                    }
                    mlist.add(file);
                }

            }

        }
        return datas;

    }



    public static List getWeixi(Context context){
        String [] arr = {".mp4",".avi","rmvb"};
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File download = new File(root + "/Android/data/com.tencent.mm/MicroMsg/Download");

        File pic = new File(root + "/Pictures/WeiXin");

        if(pic == null || pic.exists()) {
            if(pic.listFiles() == null) {
                pic = new File(root + "/Pictures/WeChat");
            }
        }

        File dd = new File(root + "/Download/WeiXin");
        if(dd == null || dd.exists()) {
            if(dd.listFiles() == null) {
                dd = new File(root + "/Download/WeChat");
            }
        }
        List<File> list = new ArrayList<>();

        if(pic.listFiles() != null) {
            if(pic.listFiles().length > 0) {
                list.addAll(getAllFileInDir(pic));
            }
        }

        if(download.listFiles() != null) {
            if(download.listFiles().length > 0) {
                list.addAll(getAllFileInDir(download));
            }
        }

        if(dd.listFiles() != null) {
            if(dd.listFiles().length > 0) {
                list.addAll(getAllFileInDir(dd));
            }
        }

        List datas = new ArrayList();
        HashMap<String,List> map = new HashMap<String,List>();
        for (File file : list) {
            String fileName = file.getName();
            fileName = fileName.toLowerCase();
            String picKey = "图片";
            String videoKey = "视频";
            String textKey = "文档";
            String otherKey = "其他";

            String[] nameTypes = fileName.split("\\.");
            if(nameTypes.length > 1) {
                String nameType = nameTypes[nameTypes.length - 1];
                String[] pics = {"png", "jpg","jpeg"};
                String[] videos = {"mp4", "avi", "rmvb"};
                String[] texts = {"pdf", "txt", "doc", "xls", "docx", "xlsx", "ppt", "pptx","log"};

                if (Arrays.asList(pics).contains(nameType)){
                    List mlist = map.get(picKey);
                    if(mlist == null){
                        mlist = new ArrayList<>();
                        map.put(picKey,mlist);

                        HashMap<String,List> dataMap = new HashMap<String,List>();
                        dataMap.put(picKey,mlist);
                        datas.add(dataMap);
                    }
                    mlist.add(file);
                }else  if (Arrays.asList(videos).contains(nameType)){
                    List mlist = map.get(videoKey);
                    if(mlist == null){
                        mlist = new ArrayList<>();
                        map.put(videoKey,mlist);
                        HashMap<String,List> dataMap = new HashMap<String,List>();
                        dataMap.put(videoKey,mlist);
                        datas.add(dataMap);
                    }
                    mlist.add(file);
                }else if (Arrays.asList(texts).contains(nameType)){
                    List mlist = map.get(textKey);
                    if(mlist == null){
                        mlist = new ArrayList<>();
                        map.put(textKey,mlist);
                        HashMap<String,List> dataMap = new HashMap<String,List>();
                        dataMap.put(textKey,mlist);
                        datas.add(dataMap);
                    }
                    mlist.add(file);
                }else{
                    List mlist = map.get(otherKey);
                    if(mlist == null){
                        mlist = new ArrayList<>();
                        map.put(otherKey,mlist);

                        HashMap<String,List> dataMap = new HashMap<String,List>();
                        dataMap.put(otherKey,mlist);
                        datas.add(dataMap);
                    }
                    mlist.add(file);
                }

            }

        }
        return datas;

    }


    public static List getWeixi_(Context context){
        String [] arr = {".mp4",".avi","rmvb"};
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File download = new File(root + "/Android/data/com.tencent.mm/MicroMsg/Download");

        File pic = new File(root + "/Pictures/WeiXin");

        File dd = new File(root + "/Download/WeiXin");
        List<File> list = new ArrayList<>();

        if(pic.listFiles() != null) {
            if(pic.listFiles().length > 0) {
                list.addAll(getAllFileInDir(pic));
            }
        }

        if(download.listFiles() != null) {
            if(download.listFiles().length > 0) {
                list.addAll(getAllFileInDir(download));
            }
        }

        if(dd.listFiles() != null) {
            if(dd.listFiles().length > 0) {
                list.addAll(getAllFileInDir(dd));
            }
        }

        return list;

    }

    public static void copyLocalFolder(File fi, SafFile3 usbFile, UsbHelper usbHelper, CopyRes res) throws IOException {



        //UsbHelper.syncCopyInternalToExternal(fi.getAbsolutePath(), (GlobalWorkArea.getGlobalParameters(null).safMgr.getSdcardRootPath() + usbFile.getPath() + "/" + fi.getName()));

        usbHelper.moveCopyInternalToExternal(fi.getAbsolutePath().substring(Environment.getExternalStorageDirectory().getPath().length()),usbFile.getPath().substring(usbHelper.getUsbRootPath().length()) + "/" + fi.getName());

    }

    public static void copyFolder(File fi, File usbFile, UsbHelper usbHelper, CopyRes res) throws IOException {


        //UsbHelper.syncCopyInternalToInternal(fi.getAbsolutePath(),usbFile.getAbsolutePath());

    }


    public static void copyUsbFolder(SafFile3 usbFile, String path, UsbHelper usbHelper, CopyRes res) throws IOException {



        usbHelper.moveCopyExternalToInternal(usbFile.getPath().substring(usbHelper.getUsbRootPath().length()),path.substring(Environment.getExternalStorageDirectory().getPath().length()) + "/" + usbFile.getName());




    }


    public static void copyWithPath(String from,String to){

        UsbHelper.getInstance().syncCopyLocalToLocal(from,to);

    }



    /**    * 获取目录下所有文件(按时间排序)    *     * @param path    * @return    */
    public static List<File> listfilesortbymodifytime(String path) {
        List<File> list = getfiles(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newfile) {
                    boolean lInValid = (file == null || !file.exists());
                    boolean rInValid = (newfile == null || !newfile.exists());
                    boolean bothInValid = lInValid && rInValid;
                    if (bothInValid) {
                        return 0;
                    }

                    if (lInValid) {
                        return -1;
                    }

                    if (rInValid) {
                        return 1;
                    }



                    if (file.lastModified() > newfile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newfile.lastModified()) {
                        return 0;
                    } else {        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**    *     * 获取目录下所有文件    *     * @param realpath    * @param files    * @return    */

    public static List<File> getfiles(String realpath, List<File> files) {
        File realfile = new File(realpath);
        if (realfile.isDirectory()) {
            File[] subfiles = realfile.listFiles();
            for (File file : subfiles) {
//                if (file.isDirectory()) {
//                    getfiles(file.getAbsolutePath(), files);
//                } else {

                if(!(file.getName().startsWith(".")))
                    files.add(file);
//                }
            }
        }
        return files;
    }





    /**    * 获取目录下所有文件(按时间排序)    *     * @param path    * @return    */
    public static List<SafFile3> listUsbfilesortbymodifytime(SafFile3 dirUsbFile) throws IOException {
        List<SafFile3> list = getUsbfiles(dirUsbFile, new ArrayList<SafFile3>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<SafFile3>() {
                public int compare(SafFile3 file, SafFile3 newfile) {
                    if (file.lastModified() > newfile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newfile.lastModified()) {
                        return 0;
                    } else {        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**    *     * 获取目录下所有文件    *     * @param realpath    * @param files    * @return    */
    public static List<SafFile3> getUsbfiles(SafFile3 dirUsbFile, List<SafFile3> files) throws IOException {
        SafFile3 realfile = dirUsbFile;
        if (realfile.isDirectory()) {
            SafFile3[] subfiles =  new SafFile3[0];//realfile.listFiles();
            for (SafFile3 file : subfiles) {
                if(!(file.getName().startsWith(".")))
                    files.add(file);
//                }
            }
        }
        return files;
    }

    /**    * 获取目录下所有文件(按时间排序)    *     * @param path    * @return    */
    public static List<SafFile3> listUsbfilesortbymodifytime_list(List<SafFile3> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<SafFile3>() {
                public int compare(SafFile3 file, SafFile3 newfile) {
                    if (file.lastModified() > newfile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newfile.lastModified()) {
                        return 0;
                    } else {        return 1;
                    }
                }
            });
        }
        return list;
    }




    public static void openFile_u(String fileName,Intent intent,Uri uri){


        //File file = new File(filePath);
        //if(!file.exists()) return;
        /* 取得扩展名 */
        String end=fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")||end.equals("aac")){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            intent.setDataAndType(uri, "audio/*");
        }else if(end.equals("3gp")||end.equals("mp4")){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            intent.setDataAndType(uri, "video/*");
        }else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp")){
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "image/*");
        }else if(fileName.contains(".apk")){
            intent.setDataAndType(uri,"application/vnd.android.package-archive");
        }else if(end.equals("ppt") || end.equals("pptx")){
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        }else if(end.equals("xls") || end.equals("xlsx")){
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.ms-excel");

        }else if(end.equals("doc") || end.equals("docx")){
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/msword");
        }else if(end.equals("pdf")){
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/pdf");
        }else if(end.equals("chm")){
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/x-chm");
        }else if(end.equals("txt") || end.equals("log")){
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "text/plain");
        }else{

            intent.setDataAndType(uri,"*/*");
        }
    }

    public static Intent openFile(String filePath,Context context){


        File file = new File(filePath);
        if(!file.exists()) return null;
        /* 取得扩展名 */
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")||end.equals("aac")){
            return getAudioFileIntent(filePath,context);
        }else if(end.equals("3gp")||end.equals("mp4")){
            return getVideoFileIntent(filePath,context);
        }else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp")){
            return getImageFileIntent(filePath,context);
        }else if(file.getName().contains(".apk")){
            return getApkFileIntent(filePath,context);
        }else if(end.equals("ppt") || end.equals("pptx")){
            return getPptFileIntent(filePath,context);
        }else if(end.equals("xls") || end.equals("xlsx")){
            return getExcelFileIntent(filePath,context);
        }else if(end.equals("doc") || end.equals("docx")){
            return getWordFileIntent(filePath,context);
        }else if(end.equals("pdf")){
            return getPdfFileIntent(filePath,context);
        }else if(end.equals("chm")){
            return getChmFileIntent(filePath,context);
        }else if(end.equals("txt") || end.equals("log")){
            return getTextFileIntent(filePath,false,context);
        }else{
            return getAllIntent(filePath,context);
        }
    }

    //Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent( String param,Context context) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));//.processName
        }else{
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri,"*/*");
        return intent;
    }
    //Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent( String param ,Context context) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));//.processName
        }else{
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        return intent;
    }

    //Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent( String param,Context context) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);


        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));//.processName
        }else{
             uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent( String param ,Context context){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);

        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context, context.getApplicationInfo().processName + ".provider",new File(param));
        }else{
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent( String param ){

        Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent( String param,Context context) {
        Log.d("ddffffff", "getImageFileIntent: "+"2222");
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));
        }else{
            uri = Uri.fromFile(new File(param));
        }

        String s = String.valueOf(uri);
        File f = new File(String.valueOf(uri));
        if(f.exists()){
            Log.d("dd", "getImageFileIntent: "+"2222");

            s = String.valueOf(uri);
        }
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent( String param ,Context context){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));
        }else{
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent( String param ,Context context){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));
        }else{
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent( String param ,Context context){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));
        }else{
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent( String param ,Context context){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));
        }else{
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent( String param, boolean paramBoolean,Context context){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同


            String res = context.getApplicationInfo().processName + ".provider";
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));
            intent.setDataAndType(uri, "text/plain");
        }else{
            if (paramBoolean){
                Uri uri1 = Uri.parse(param );
                intent.setDataAndType(uri1, "text/plain");
            }else{
                Uri uri2 = Uri.fromFile(new File(param ));
                intent.setDataAndType(uri2, "text/plain");
            }
        }

        return intent;
    }
    //Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent( String param ,Context context){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //记得修改com.xxx.fileprovider与androidmanifest相同
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(param));
        }else{
            uri = Uri.fromFile(new File(param));
        }
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }


    public static Uri getUirFromPath(Context context,String path){
        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(context,context.getApplicationInfo().processName + ".provider",new File(path));
         }else{
            uri = Uri.fromFile(new File(path));
        }
        return uri;
    }

    public static Intent openFile_(String filePath,Context context){


        File file = new File(filePath);
        if(!file.exists()) return null;
        /* 取得扩展名 */
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")||end.equals("aac")){
            return getAudioFileIntent(filePath,context);
        }else if(end.equals("3gp")||end.equals("mp4")){
            return getVideoFileIntent(filePath,context);
        }else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp")){
            return getImageFileIntent(filePath,context);
        }else if(end.equals("apk")){
            return getApkFileIntent(filePath,context);
        }else if(end.equals("ppt") || end.equals("pptx")){
            return getPptFileIntent(filePath,context);
        }else if(end.equals("xls") || end.equals("xlsx")){
            return getExcelFileIntent(filePath,context);
        }else if(end.equals("doc") || end.equals("docx")){
            return getWordFileIntent(filePath,context);
        }else if(end.equals("pdf")){
            return getPdfFileIntent(filePath,context);
        }else if(end.equals("chm")){
            return getChmFileIntent(filePath,context);
        }else if(end.equals("txt") || end.equals("log")){
            return getTextFileIntent(filePath,false,context);
        }else{
            return getAllIntent(filePath,context);
        }
    }


    public static void deleteFile(File file) {


        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            deleteFileSafely(file);
        }
        deleteFileSafely(file);
    }

    public static boolean deleteFileSafely(File file) {
        //file.delete();
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return true;
    }



    public static void deleteUsbFile(SafFile3 file) throws IOException {

        if (file.isDirectory()) {
            SafFile3[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                LogHelper.getInstance().d("blank file");
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteUsbFile(childFiles[i]);
            }
        }else {
            file.delete();
            LogHelper.getInstance().d("file.delete()");
        }
    }



    public interface Callback {
        public void callback(int i);


    }

    public static void deleteUsbFile_(SafFile3 file, Callback callback) throws IOException {

        if (file.isDirectory()) {
            SafFile3[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                LogHelper.getInstance().d("blank file");
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteUsbFile_(childFiles[i],callback);
            }
        }else {
            if(callback != null){
                callback.callback(1);
            }

            if(file.exists()) {
                file.delete();
            }


            LogHelper.getInstance().d("file.delete()");
        }
    }

    public static Boolean copyToUPanoRoot(File file ,Context context) {
        //String to_path = GlobalWorkArea.getGlobalParameters(null).safMgr.getSdcardRootPath() + ((file.getPath().equals("/") ? "" : file.getPath()));
        //        usbHelper.moveCopyInternalToExternal(file.getAbsolutePath().substring(localRootPath.length()), usbCurrentPath + "/" + file.getName());
        UsbHelper.getInstance().moveCopyInternalToExternal("/" + file.getAbsolutePath().substring(UsbHelper.getInstance().getSdRootPath().length()),"/" + file.getName());


        return true;
    }


    public static String copyToLocalCacheToPaly(SafFile3 usbFile ,Context context) {
        //String to_path = GlobalWorkArea.getGlobalParameters(null).safMgr.getSdcardRootPath() + ((file.getPath().equals("/") ? "" : file.getPath()));
        //        usbHelper.moveCopyInternalToExternal(file.getAbsolutePath().substring(localRootPath.length()), usbCurrentPath + "/" + file.getName());
        //moveCopyExternalToInternal("/" + file.getAbsolutePath().substring(UsbHelper.getInstance().getSdRootPath().length()),"/" + file.getName());


        String des = "/Cache/yisu/" + usbFile.getName();
        UsbHelper.getInstance().moveCopyExternalToInternal(usbFile.getPath().substring(UsbHelper.getInstance().getUsbRootPath().length()),des);

        return UsbHelper.getInstance().getSdRootPath() + des;
    }



    public static String deleteMediaStore(File file ,Context context){

        String filePath = file.getPath().toLowerCase();

        if(filePath.endsWith(".mp4") || filePath.endsWith(".avi") || filePath.endsWith(".rmvb") || filePath.endsWith(".mpeg") || filePath.endsWith(".mov") || filePath.endsWith(".3gp") || filePath.endsWith(".mkv")  || filePath.endsWith(".ts") || filePath.endsWith(".webm")){

            int res = context.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,

                    MediaStore.Audio.Media.DATA + "= \"" + filePath+"\"",

                    null);

            if (res>0){

                file.delete();

            }else{

                //Log.e(TAG, "删除文件失败");
                file.delete();

                return "删除文件失败:" + file.getAbsolutePath();

            }

        }else if (filePath.endsWith(".jpg")||filePath.endsWith(".png") ||filePath.endsWith(".jpeg")||filePath.endsWith(".gif") || filePath.endsWith(".bmp")
                || filePath.endsWith(".webp")){

            int res = context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

                    MediaStore.Audio.Media.DATA + "= \"" + filePath+"\"",

                    null);

            if (res>0){

                file.delete();

            }else{

                //Log.e(TAG, "删除文件失败");
                file.delete();
                return "删除文件失败:" + file.getAbsolutePath();

            }

        }else{

            file.delete();

        }
        return "删除文件成功";


    }


    static public long getTotalExternalMemorySize() {
        if (isSDCardEnable()) {
//获取SDCard根目录

            File path = Environment.getExternalStorageDirectory();

            StatFs stat = new StatFs(path.getPath());

            long blockSize = stat.getBlockSize();

            long totalBlocks = stat.getBlockCount();

            return totalBlocks * blockSize;

        } else {
            return -1;

        }

    }

    /**

     * 获取SD卡剩余空间

     *

     * @return SD卡剩余空间

     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)

    public static String getFreeSpace() {
        if (!isSDCardEnable()) return "sdcard unable!";

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());;

        long blockSize, availableBlocks;

        availableBlocks = stat.getAvailableBlocksLong();

        blockSize = stat.getBlockSizeLong();

        long size = availableBlocks * blockSize / 1024L;

        return String.valueOf(size);

    }


    public static String getSdSize(){

        if (isSDCardEnable()) {
//获取SDCard根目录

            File path = Environment.getExternalStorageDirectory();

            StatFs stat = new StatFs(path.getPath());

            long blockSize = stat.getBlockSizeLong();

            long totalBlocks = stat.getBlockCountLong();

            long availableBlocks = stat.getAvailableBlocksLong();
            return String.format("%.2fGB/%.2fGB", (totalBlocks * blockSize - availableBlocks * blockSize)/1024.0/1024/1024,totalBlocks * blockSize/1024.0/1024/1024).toString();

        } else {
            return "未读取到SD卡";

        }

    }

    public static String getSdSizePer(){

        if (isSDCardEnable()) {
//获取SDCard根目录

            File path = Environment.getExternalStorageDirectory();

            StatFs stat = new StatFs(path.getPath());

            long blockSize = stat.getBlockSizeLong();

            long totalBlocks = stat.getBlockCountLong();

            long availableBlocks = stat.getAvailableBlocksLong();

            double rate = ((totalBlocks * blockSize - availableBlocks * blockSize)/1024.0/1024/1024)/(totalBlocks * blockSize/1024.0/1024/1024);
            return String.format("%.2f",rate*100).toString()  + '%';

        } else {
            return "未读取到SD卡";

        }

    }

    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

    }


//    public static String getMd5FromFile(SafFile3 file,Context c){
//        if (file == null) {
//            return "";
//        }
//        String value = null;
//        InputStream in = null;
//        try {
////            in = file.getInputStream();
////            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
////                    file.length());
////            MessageDigest md5 = MessageDigest.getInstance("MD5");
////            md5.update(byteBuffer);
////            BigInteger bi = new BigInteger(1, md5.digest());
////            value = bi.toString(16);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != in) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return value;
//    }


    public static String  getFileMD5(SafFile3 file)
    {
        //Create checksum for this file
        //File file = new File("c:/temp/testOut.txt");

        //Use MD5 algorithm
        MessageDigest md5Digest = null;
        try
        {
            md5Digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        //Get the checksum
        String checksum = null;
        try
        {
            checksum = getFileChecksum(md5Digest, file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //see checksum
        //System.out.println(checksum);
        return checksum;
    }

    private static String getFileChecksum(MessageDigest digest, SafFile3 file) throws Exception
    {
        //Get file input stream for reading the file content
        InputStream fis = file.getInputStream();//new FileInputStream(file);

        //Create byte array to read data in chunks


        int size = 3;
        if(file.length() > 1024 * 1024 * 1024){
            size = 20;
        }
        byte[] byteArray = new byte[1024 * 1024 * size];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1)
        {
            digest.update(byteArray, 0, bytesCount);
        }

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    static public String getFileExtention(String fp) {
        String ft="", fn="";
        int sep_pos=fp.lastIndexOf("/");
        if (sep_pos>=0) {
            fn=fp.substring(sep_pos+1);
        } else {
            fn=fp;
        }
        int ext_pos=fn.lastIndexOf(".");
        if (ext_pos >= 0) {
            ft = fn.substring(ext_pos+1).toLowerCase();
        }
        return ft;
    }


    static public File createContactFileAndToUsb(SelectedItemCollection.ProgressListioner progressListioner,Context context) throws IOException {


        List tmp0 = ContactUtil.getAllContacts__(context, new SelectedItemCollection.ProgressListioner() {
            @Override
            public void progress(int progress) {
                if(progressListioner != null){
                    progressListioner.progress(progress);
                }
            }

            @Override
            public void finsh(List<String> filePath) {

            }
        });


        Gson gson = new Gson();
        String json = gson.toJson(tmp0);



        String yisuDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FileUtil.YISU_CACHE;

        File yisuDirFile = new File(yisuDir);
        if (!yisuDirFile.exists()) {
            yisuDirFile.mkdir();
        }


        String fileName = "contacts_backup" + TimeUtil.getCurrentTime("_yyyy-MM-dd_hh_mm_ss")  + ".txt";
        String contactPath = yisuDir + "/" + fileName;

        _write_(contactPath, json);



        return new File(contactPath);

//        if(UsbHelper.getInstance().canCopyToU()) {
//            String usbDesContactPath = UsbHelper.getInstance().getRootFile().getPath() + "/" + "yisu" + "/" + fileName;
//            FileTool.copyWithPath(contactPath, usbDesContactPath);
//        }


    }
    public static File _write_(String filePath , String content) throws IOException {

        String file =  filePath;
        OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");

        oStreamWriter.append(content);
        oStreamWriter.close();

        return new File(file);
    }


    public static void readDataToSysContact(SafFile3 file, FileTool.ProgressListener progressListener,Context context){
        String yisuDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FileUtil.YISU_CACHE;
        File yisuDirFile = new File(yisuDir);
        if(!yisuDirFile.exists()){
            yisuDirFile.mkdir();
        }
        String contactPath = yisuDir+ "/" + file.getName();
        FileTool.copyWithPath(file.getPath(),contactPath);
        List tmp0 = ContactUtil.getAllContacts__(context, new SelectedItemCollection.ProgressListioner() {
            @Override
            public void progress(int progress) {
                if(progressListener != null){
                    progressListener.onProgress(progress);
                }
            }

            @Override
            public void finsh(List<String> filePath) {

            }
        });
        Gson gson = new Gson();
        ArrayList backuplist_ = new ArrayList<>();
        try {
            String json01 = _read_(contactPath);

            Type listType = new TypeToken<List<MyContact>>(){}.getType();
            List  lis = gson.fromJson(json01, listType);
            if(lis != null){
                backuplist_.addAll(lis);
            }

            for(Object o : backuplist_){
                MyContact contact = (MyContact) o;

                boolean found = false;
                for(Object o1 : tmp0){
                    MyContact contact1 = (MyContact) o1;
                    if(contact1.getPhone().equals(contact.getPhone()) && contact1.getName().equals(contact.getName())){
                        found = true;
                        break;
                    }

                }
                if(!found){
                    addContact(contact.getName(),contact.getPhone(),context);
                }

                if(progressListener != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        progressListener.onProgress(backuplist_.indexOf(o) * 100 / backuplist_.size());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addContact(String name, String phoneNumber,Context context) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();

        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        // 联系人的Email地址
        //values.put(Email.DATA, "zhangphil@xxx.com");
        // 电子邮件的类型
        //values.put(Email.TYPE, Email.TYPE_WORK);
        // 向联系人Email URI添加Email数据
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    public static String _read_(String path) throws IOException {

        if (!TextUtils.isEmpty(path)){
            BufferedReader bre=new BufferedReader(new FileReader(new File(path)));
            String str;

            StringBuilder sb = new StringBuilder();

            while ((str = bre.readLine()) != null){
                sb.append(str +"\n");
            }
            if (bre != null){
                bre.close();
            }

            return sb.toString();
        }
        return "";
    }




    public static String getPath_(Context context, Uri uri) {

        String path = null;

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor == null) {

            return null;

        }

        if (cursor.moveToFirst()) {

            try {

                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        cursor.close();

        return path;

    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (Platform.hasKitKat() && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) { // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static File getFileByUri(Uri uri,Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

//    Class<FileProvider> clazz = FileProvider.class;Class<?> simplePathStrategyClazz = clazz.getDeclaredClasses()[0];
//    Method getPathStrategyMethod = clazz.getDeclaredMethod("getPathStrategy", Context.class, String.class);getPathStrategyMethod.setAccessible(true);Object simplePathStrategyObject = getPathStrategyMethod.invoke(null, MainActivity.this, "com.example.songzeceng.myFileProvider");Method method = simplePathStrategyClazz.getDeclaredMethod("getFileForUri", Uri.class);method.setAccessible(true);File destFile = (File) method.invoke(simplePathStrategyObject, uri);Log.i(TAG, "share: file path:" + destFile.getAbsolutePath());


}
