/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.figures.GroupFigure;
import CH.ifa.draw.figures.RectangleFigure;
import CH.ifa.draw.figures.TextFigure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Handle;
import CH.ifa.draw.standard.CompositeFigure;
import CH.ifa.draw.standard.RelativeLocator;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public class AtomFigure extends CompositeFigure
{
    protected EllipseFigure m_nucleus = null;
    
    protected TextFigure m_name = null;
    
    protected Vector<EllipseFigure> m_electrons = new Vector<>();

    public AtomFigure()
    {
        super();
    }


    @Override
    public void basicDisplayBox(Point origin, Point corner)
    {
        Rectangle r = displayBox();

        basicMoveBy(origin.x - r.width / 2, origin.y - r.height / 2);
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
        
        for (EllipseFigure fig : m_electrons)
        {
            handles.add(new AngularHandle(fig, m_nucleus, RelativeLocator.center(), 15));
        }

        return handles;
    }
    
    public EllipseFigure getConnectableElectron()
    {
        EllipseFigure result = null;
        for (EllipseFigure m_electron : m_electrons)
        {
            if (m_electron.canConnect())
            {
                result = m_electron;
                break;
            }
        }
        
        return result;
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
