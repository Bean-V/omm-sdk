package com.sentaroh.android.upantool;

import java.util.List;
public class ItemPickManager {

    public static ImagePickFinish itemPickFinsh;

    public interface ImagePickFinish {

        public void imagePickFinsh(int code, List uris,List<String> paths);
    }
}


