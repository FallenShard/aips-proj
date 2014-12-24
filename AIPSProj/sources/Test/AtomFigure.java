/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.framework.Handle;
import CH.ifa.draw.standard.RelativeLocator;
import CH.ifa.draw.util.Animatable;
import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

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
        int widthSize;
        if (width > 0) 
            widthSize = Math.min(120, Math.max(width, height));
        else
            widthSize = Math.max(-120, Math.min(width, height));
        
        corner = new Point(origin.x + widthSize, origin.y + widthSize);
        
        int absWidth = Math.abs(width);
        if (absWidth > 80)
            setAttribute("FillColor", new Color(255,   0, 0));
        else if (absWidth > 40)
            setAttribute("FillColor", new Color(255, 127, 0));
        else
            setAttribute("FillColor", new Color(255, 255, 0));
        
        super.basicDisplayBox(origin, corner);
    }
    
    @Override
    public Vector<Handle> handles()
    {
        Vector<Handle> handles = new Vector<>();
        handles.add(new AtomHandle(this, RelativeLocator.southWest(), 0, 120));
        handles.add(new AtomHandle(this, RelativeLocator.southEast(), 0, 120));
        handles.add(new AtomHandle(this, RelativeLocator.northWest(), 0, 120));
        handles.add(new AtomHandle(this, RelativeLocator.northEast(), 0, 120));
        return handles;
    }
}
