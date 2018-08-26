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

public class DrawPath extends View {
    private Paint paint = new Paint();
    private ArrayList<PointF> points = new ArrayList<PointF>();
    private ArrayList<Vertex> path = new ArrayList<Vertex>();

    public DrawPath(Context context){

        super(context);
        //  gBall = BitmapFactory.decodeResource(getResources(),R.drawable.grid);

    }

    public DrawPath(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
    }

    public DrawPath(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
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
        path = fullpath;
        points.clear();
        for(Vertex vert: path){
            PointF pointA = new PointF(vert.getX()*180+187, vert.getY()*180+550);
            points.add(pointA);
        }
    }

    public void clearCanvas(){

    }
}
