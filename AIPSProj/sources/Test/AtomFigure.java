/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.util.Animatable;
import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author FallenShard
 */
public class AtomFigure extends EllipseFigure
{
    @Override
    public void basicDisplayBox(Point origin, Point corner)
    {
        int width = corner.x - origin.x;
        int height = corner.y - origin.y;
        int size;
        if (width > 0) 
            size = Math.min(120, Math.max(width, height));
        else
            size = Math.max(-120, Math.min(width, height));
        
        corner = new Point(origin.x + size, origin.y + size);
        
        int absWidth = Math.abs(width);
        if (absWidth > 100)
            setAttribute("FillColor", new Color(255, 255, 0));
        else if (absWidth > 80)
            setAttribute("FillColor", new Color(255, 127, 0));
        else
            setAttribute("FillColor", new Color(255,   0, 0));
        
        super.basicDisplayBox(origin, corner);
    }
    
    double time = 0.0;
}
