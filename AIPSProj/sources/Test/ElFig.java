/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.figures.GroupFigure;
import CH.ifa.draw.figures.TextFigure;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author FallenShard
 */
public class ElFig extends GroupFigure
{
    EllipseFigure el = null;
    TextFigure text = null;
    
    public ElFig()
    {   
        setAttribute("FillColor", Color.GREEN);
        
        el = new EllipseFigure(new Point(0, 0), new Point(15, 15));
        text = new TextFigure();
        text.setText("-");
        Font f = new Font("Times New Roman", Font.PLAIN, 30);
        text.setFont(f);
        text.basicDisplayBox(new Point(0, 0), new Point(15, 15));
        
        super.add(el);
        super.add(text);
    }
    
    @Override
    public boolean canConnect()
    {
        return true;
    }

    @Override
    public void basicDisplayBox(Point origin, Point corner)
    {
        Rectangle r = displayBox();
        
        basicMoveBy((int)origin.getX()-r.width/2, (int)origin.getY()-r.height/2);
    }
}
