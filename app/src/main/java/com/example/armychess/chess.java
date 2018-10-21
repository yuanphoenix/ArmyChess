package com.example.armychess;

import android.graphics.Point;
/**
 *
 *
 * 棋子类，继承自Point
 */

public class chess extends Point {
    private int weight;

    public chess(int x,int y)
    {
        super(x,y);
        this.x  =x;
        this.y =y;
    }
    public chess(int x,int y,int z)
    {
        super(x,y);
        this.x=x;
        this.y=y;
        this.weight=z;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setX(int x)
    {
        this.x=x;
    }
    public void setY(int y)
    {
        this.y=y;
    }
    public int getWeight()
    {
        return weight;
    }
    public int getX()
    {
        return  x;
    }
    public  int getY()
    {
        return  y;
    }
}
