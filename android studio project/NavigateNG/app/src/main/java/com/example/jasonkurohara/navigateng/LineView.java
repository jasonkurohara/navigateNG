package com.example.jasonkurohara.navigateng;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.jar.Attributes;

public class LineView extends View{
    private Paint paint = new Paint();
    private int startX = 0, startY = 0, endX = 0, endY = 0;
    int radius = 30;
  //  Bitmap gBall;


    public LineView(Context context){

        super(context);
      //  gBall = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

    }

    public LineView(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {

       // canvas.drawBitmap(gBall, (canvas.getWidth()/2),0,paint);

        paint.setColor(Color.parseColor("#414df4"));
     //   paint.setStrokeWidth(20);
   //     canvas.drawLine(pointA.x,pointA.y, pointB.x, pointB.y,paint);
    //    canvas.drawCircle();
        super.onDraw(canvas);

      //  int x = getWidth();
    //    int y = getHeight();
    //    int radius = 30;
        paint.setStyle(Paint.Style.FILL);
     //   paint.setColor(Color.parseColor("#414df4"));
        canvas.drawCircle(startX,startY,radius,paint);
        canvas.drawCircle(endX,endY,radius,paint);
    }

    public void setStartX(int pointX){
        startX = pointX;
    }

    public void setStartY(int pointY){
        startY = pointY;
    }

    public void setEndX(int pointX){
        endX = pointX;
    }

    public void setEndY(int pointY){
        endY = pointY;
    }

    public void draw(){
        invalidate();
        requestLayout();
    }
}

