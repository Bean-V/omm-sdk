package com.oortcloud.appstore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @filename:
 * @function：解决嵌套GridView显示不全的问题
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/3 00:36
 */

/**
 * 自定义GridView，解决嵌套Grideview的显示不完全的问题
 */
public class CustomGridView extends GridView {

    private static final String TAG = "AutoGridView";
    private int numColumnsID;
    private int previousFirstVisible;
    private int numColumns = 1;

    public CustomGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
       // init(attrs);
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(attrs);
    }

    public CustomGridView(Context context) {
        super(context);
    }

//    /**
//     * Sets the numColumns based on the attributeset
//     */
//    private void init(AttributeSet attrs) {
//        // Read numColumns out of the AttributeSet
//        int count = attrs.getAttributeCount();
//        if(count > 0) {
//            for(int i = 0; i < count; i++) {
//                String name = attrs.getAttributeName(i);
//                if(name != null && name.equals("numColumns")) {
//                    // Update columns
//                    this.numColumnsID = attrs.getAttributeResourceValue(i, 1);
//                    updateColumns();
//                    break;
//                }
//            }
//        }
//        Log.d(TAG, "numColumns set to: " + numColumns);
//    }
//
//
//    /**
//     * Reads the amount of columns from the resource file and
//     * updates the "numColumns" variable
//     */
//    private void updateColumns() {
//        this.numColumns = 4;//getContext().getResources().getInteger(numColumnsID);
//    }
//
//    @Override
//    public void setNumColumns(int numColumns) {
//        this.numColumns = numColumns;
//        super.setNumColumns(numColumns);
//
//        Log.d(TAG, "setSelection --> " + previousFirstVisible);
//        setSelection(previousFirstVisible);
//    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

//    @Override
//    protected void onLayout(boolean changed, int leftPos, int topPos, int rightPos, int bottomPos) {
//        super.onLayout(changed, leftPos, topPos, rightPos, bottomPos);
//        setHeights();
//    }
//
//    @Override
//    protected void onConfigurationChanged(Configuration newConfig) {
//        updateColumns();
//        setNumColumns(this.numColumns);
//    }
//
//    @Override
//    protected void onScrollChanged(int newHorizontal, int newVertical, int oldHorizontal, int oldVertical) {
//        // Check if the first visible position has changed due to this scroll
//        int firstVisible = getFirstVisiblePosition();
//        if(previousFirstVisible != firstVisible) {
//            // Update position, and update heights
//            previousFirstVisible = firstVisible;
//            setHeights();
//        }
//
//        super.onScrollChanged(newHorizontal, newVertical, oldHorizontal, oldVertical);
//    }
//
//    /**
//     * Sets the height of each view in a row equal to the height of the tallest view in this row.
//     * @param firstVisible The first visible position (adapter order)
//     */
//
//    private int contentHeight = 0;
//    private void setHeights() {
//        ListAdapter adapter = getAdapter();
//
//        if(adapter != null) {
//            for(int i = 0; i < getChildCount(); i+=numColumns) {
//                // Determine the maximum height for this row
//                int maxHeight = 0;
//                for(int j = i; j < i+numColumns; j++) {
//                    View view = getChildAt(j);
//                    if(view != null && view.getHeight() > maxHeight) {
//                        maxHeight = view.getHeight();
//                    }
//                }
//                //Log.d(TAG, "Max height for row #" + i/numColumns + ": " + maxHeight);
//
//                // Set max height for each element in this row
//                if(maxHeight > 0) {
//                    for(int j = i; j < i+numColumns; j++) {
//                        View view = getChildAt(j);
//                        if(view != null && view.getHeight() != maxHeight) {
//                            view.setMinimumHeight(maxHeight);
//                        }
//                    }
//                }
//            }
//        }
//    }
}