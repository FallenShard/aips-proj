/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.figures.GroupFigure;
import CH.ifa.draw.figures.RectangleFigure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Handle;
import CH.ifa.draw.standard.CompositeFigure;
import CH.ifa.draw.standard.RelativeLocator;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public class AtomFigure extends CompositeFigure
{
    public EllipseFigure r1 = null;
    public EllipseFigure e1 = null;
    public EllipseFigure e2 = null;

    public AtomFigure()
    {
        super();
        
        r1 = new EllipseFigure(new Point(20, 20), new Point(100,100));
        e1 = new EllipseFigure(new Point(55, 0), new Point(65, 10));
        e2 = new EllipseFigure(new Point(55, 110), new Point(65, 120));
        
        r1.setAttribute("FillColor", Color.RED);
        e1.setAttribute("FillColor", Color.YELLOW);
        e2.setAttribute("FillColor", Color.BLUE);
        
        super.add(r1);
        super.add(e1);
        super.add(e2);
    }


    @Override
    public void basicDisplayBox(Point origin, Point corner)
    {
        Rectangle r = displayBox();

        basicMoveBy(corner.x - r.width / 2, corner.y - r.height / 2);
    }
    
    @Override
    public boolean canConnect()
    {
        return true;
    }
    
    @Override
    public Vector<Handle> handles()
    {
        Vector<Handle> handles = new Vector<>();
        handles.add(new AngularHandle(e1, r1, RelativeLocator.center(), 15));
        handles.add(new AngularHandle(e2, r1, RelativeLocator.center(), 15));
        
        return handles;
    }

    @Override
    public Rectangle displayBox()
    {    
        FigureEnumeration k = figures();
        Rectangle r = k.nextFigure().displayBox();

        while (k.hasMoreElements())
            r.add(k.nextFigure().displayBox());
        return r;
    }
}
