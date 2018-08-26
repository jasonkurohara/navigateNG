package com.example.jasonkurohara.navigateng;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class DrawPath2 extends View {
    private Paint paint = new Paint();
    private ArrayList<PointF> points = new ArrayList<PointF>();
    private ArrayList<Vertex> path = new ArrayList<Vertex>();

    public DrawPath2(Context context){

        super(context);
        //  gBall = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

    }

    public DrawPath2(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
    }

    public DrawPath2(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {

        //   canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        paint.setColor(Color.parseColor("#414df4"));
        paint.setStrokeWidth(20);

        for(int i = 0; i < points.size()-1; i++){
            canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y, paint);
        }

        super.onDraw(canvas);
    }
    public void draw(){
        invalidate();
        requestLayout();
    }

    public void setPointPath(ArrayList<Vertex> fullpath){
        path.clear();
        for(Vertex vert: fullpath){
            path.add(vert);
        }
 //       path = fullpath;
        points.clear();
        for(Vertex vert: path){
            PointF pointA = new PointF(vert.getX()*160+225, vert.getY()*160+830);
            points.add(pointA);
        }
    }

    public void clearCanvas(){

    }
}
