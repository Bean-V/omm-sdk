package com.jun.baselibrary.fix.me;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/29 21:31
 * Version 1.0
 * Description：实现热修复
 */
public class FixDexManager {
    private static final String TAG = "FixDexManager";
    private Context mContext;
    private File mFixDir;

    public FixDexManager(Context context) {
        mContext = context;
        //应用可以访问的dex目录
        mFixDir = mContext.getDir("odex",Context.MODE_PRIVATE);

    }

    /**
     * 修复Dex包
     * @param solutePath
     */
    public void fixBug(String solutePath)throws Exception{
        //2、获取修复bug的dex包的Elements元素
        //2.1、移动到应用能够访问的 dex目录下 能够让ClassLoader
        File srcFile = new File(solutePath);//源路径
        if (!srcFile.exists()){
            throw new FileNotFoundException(solutePath );
        }

        File destFile = new File(mFixDir, srcFile.getName());

        if (destFile.exists() && destFile.length() > 0) {
            Log.d(TAG, "patch [" + solutePath + "] has be loaded.");
            return;
        }

        copyFile(srcFile, destFile);
        Log.v("TAG", "destFile-->"+destFile.length());
        //2.2、ClassLoader读取fixDex路径
        List<File> fixDexFiles = new ArrayList<>();
        fixDexFiles.add(destFile);

        fixDexFiles(fixDexFiles);
    }

    private Object getDexElementsByClassLoader(ClassLoader classLoader)throws Exception {
        Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object dexPathList = pathListField.get(classLoader);

        Field dexElementsField = dexPathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        Object dexElements = dexElementsField.get(dexPathList);
        return dexElements;
    }

    public static void copyFile(File src, File dest) throws IOException {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dest).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
    /**
     * 合并两个数组
     *
     * @param arrayLhs
     * @param arrayRhs
     * @return
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }
    /**
     * 、注入 把合并后的数组注入到原来的 applicationElements
     * @param classLoader
     * @param dexElements
     * @throws Exception
     */
    private void injectDexElements(ClassLoader classLoader, Object dexElements)throws Exception {
        Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object dexPathList = pathListField.get(classLoader);

        Field dexElementsField = dexPathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        dexElementsField.set(dexPathList, dexElements);


    }
    //用于加载全部修复包
    public void loadFixDex()throws Exception {
       File[] dexFiles =  mFixDir.listFiles();

       List<File> fixDexFiles = new ArrayList<>();
        for (File dexFile : dexFiles) {
            if (dexFile.getName().endsWith("dex")){
                fixDexFiles.add(dexFile);
            }
        }
        fixDexFiles(fixDexFiles);
    }

    private void fixDexFiles(List<File> fixDexFiles)throws Exception {
        //1、获取运行的Elements
        ClassLoader applicationClassLoader = mContext.getClassLoader();
        Object applicationElements = getDexElementsByClassLoader(applicationClassLoader);


        File optimizedDirectory = new File(mFixDir, "odex");
        if (!optimizedDirectory.exists()){
            optimizedDirectory.mkdirs();
        }

        Log.v("TAG", "mFixDir-->"+mFixDir.getAbsolutePath());
        Log.v("TAG", "optimizedDirectory-->"+optimizedDirectory.getAbsolutePath());

        for (File fixDexFile : fixDexFiles) {
            ClassLoader fixClassLoader = new BaseDexClassLoader(
                    fixDexFile.getAbsolutePath(), //dex路径
                    optimizedDirectory,//解压路径
                    null,//lib so库路径
                    applicationClassLoader //父ClassLoader
            );
            Object fixDexElements = getDexElementsByClassLoader(fixClassLoader);

            //3、把dex包Elements元素插入到运行的Elements元素前面 合并
            applicationElements = combineArray(fixDexElements, applicationElements);
            //3.1、注入 把合并后的数组注入到原来的 applicationElements
            injectDexElements(applicationClassLoader,applicationElements);

        }


    }
}
