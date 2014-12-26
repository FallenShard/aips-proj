/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import CH.ifa.draw.figures.ChopEllipseConnector;
import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.figures.TextFigure;
import CH.ifa.draw.framework.Connector;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Handle;
import CH.ifa.draw.standard.CompositeFigure;
import CH.ifa.draw.standard.RelativeLocator;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public class AtomFigure extends CompositeFigure
{
    protected EllipseFigure m_nucleus = null;
    
    protected TextFigure m_name = null;
    
    protected EllipseFigure m_orbit = null;
    
    protected Vector<EllipseFigure> m_electrons = new Vector<>();

    public AtomFigure()
    {
        super();
        
        m_nucleus = new EllipseFigure(new Point(20, 20), new Point(100,100));
        m_orbit = new EllipseFigure(new Point (5, 5), new Point(115, 115));
        
        m_name = new TextFigure();
        m_name.setFont(new Font("Calibri", Font.BOLD, 30));
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
    
    public EllipseFigure getConnectableElectron(int x, int y)
    {
        EllipseFigure result = null;
        for (EllipseFigure m_electron : m_electrons)
        {
            if (m_electron.canConnect() && m_electron.containsPoint(x, y))
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
    
    @Override
    public Connector connectorAt(int x, int y)
    {
        return new ChopEllipseConnector(this);
    }
    
    @Override
    public void setAttribute(String name, Object value)
    {
        FigureEnumeration k = figures();
        while (k.hasMoreElements())
            k.nextFigure().setAttribute(name, value);
    }
    
    @Override
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2));
        
        FigureEnumeration k = figures();
        while (k.hasMoreElements())
            k.nextFigure().draw(g);
    }
}
