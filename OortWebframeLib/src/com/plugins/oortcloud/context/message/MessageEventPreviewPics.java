package com.plugins.oortcloud.context.message;

import java.util.ArrayList;
import java.util.List;

public class MessageEventPreviewPics {

    public MessageEventPreviewPics(List pics, int index) {
        this.pics = (ArrayList<String>) pics;
        this.index = index;
    }

    public ArrayList<String> pics;
    public int index;

}



