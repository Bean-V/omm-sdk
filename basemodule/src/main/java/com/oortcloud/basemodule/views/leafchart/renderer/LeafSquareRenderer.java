package com.oortcloud.basemodule.views.leafchart.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.oortcloud.basemodule.views.leafchart.bean.Axis;
import com.oortcloud.basemodule.views.leafchart.bean.PointValue;
import com.oortcloud.basemodule.views.leafchart.bean.Square;
import com.oortcloud.basemodule.views.leafchart.support.LeafUtil;

import java.util.List;

/**
 * Created by chenliu on 2017/1/10.<br/>
 * 描述：
 * </br>
 */

public class LeafSquareRenderer extends AbsRenderer {

    public LeafSquareRenderer(Context context, View view) {
        super(context, view);
    }

    public void drawSquares(Canvas canvas, Square square, Axis axisX) {
        if (square != null) {
            //1.画直方图边界
            linePaint.setColor(square.getBorderColor());
            if(!square.isFill()){
                linePaint.setStrokeWidth(LeafUtil.dp2px(mContext, square.getBorderWidth()));
                linePaint.setStyle(Paint.Style.STROKE);
            }
            List<PointValue> values = square.getValues();
            float width = LeafUtil.dp2px(mContext, square.getWidth());
            for (PointValue point : values) {
                RectF rectF = new RectF(point.getOriginX() - width / 2,
                        point.getOriginY(), point.getOriginX() + width / 2, axisX.getStartY());

                canvas.drawRect(rectF, linePaint);
            }
        }
    }


}
