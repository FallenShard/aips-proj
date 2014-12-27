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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public class AtomFigure extends CompositeFigure
{
    protected EllipseFigure m_orbit = null;
    protected EllipseFigure m_nucleus = null;
    
    protected TextFigure m_name = null;
    protected TextFigure m_valence = null;
    protected int m_lastOrbitEls;
    protected int m_lastOrbitMaxEls;
    
    protected int m_valenceDiff;
    
    protected Color m_orbitColor = Color.WHITE;
    
    public static final int MAX_BONDS = 3;
    
    protected Vector<ElectronFigure> m_electrons = new Vector<>();

    public AtomFigure()
    {
        super();
        
        // Create nucleus here
        m_nucleus = new EllipseFigure(new Point(20, 20), new Point(100,100));
        
        // Create a halo-like orbit
        m_orbit = new EllipseFigure(new Point (5, 5), new Point(115, 115));
        m_orbit.setAttribute("FillColor", new Color(0, 0, 0, 0));
        m_orbit.setAttribute("FrameColor", Color.WHITE);
        
        // This is the central text, atom's name
        m_name = new TextFigure();
        m_name.setFont(new Font("Calibri", Font.BOLD, 30));
        
        // This is the valence number (
        m_valence = new TextFigure();
        m_valence.setFont(new Font("Calibri", Font.BOLD, 12));
        
        super.add(m_orbit);
        super.add(m_nucleus);
        super.add(m_name);
        super.add(m_valence);
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
        
        if (name.equalsIgnoreCase("FrameColor") && (Color)(value) == Color.BLACK)
        {
            m_orbit.setAttribute(name, m_orbitColor);
        }
        
        for (ElectronFigure m_electron : m_electrons)
        {
            if (m_electron.getConnectedElectron() != null)
            {
                m_electron.getConnectedElectron().setAttribute(name, value);
                m_electron.getCovalentBond().setAttribute(name, value);
            }
        }   
    }
    
    @Override
    public void draw(Graphics g)
    {   
        FigureEnumeration k = figures();
        while (k.hasMoreElements())
            k.nextFigure().draw(g);
    }
    
    protected void increaseValence()
    {
        if (m_lastOrbitEls < m_lastOrbitMaxEls)
            m_lastOrbitEls++;
        
        updateValenceText();
    }
    
    protected void decreaseValence()
    {
        m_lastOrbitEls--;

        updateValenceText();
    }
    
    protected void updateValenceText()
    {
        m_valence.setText("" + m_lastOrbitEls);
        
        if (m_lastOrbitEls == m_lastOrbitMaxEls)
        {
            m_orbitColor = Color.GREEN;
            m_orbit.setAttribute("FrameColor", m_orbitColor);
            m_orbit.changed();
        }
        else
        {
            m_orbitColor = Color.WHITE;
            m_orbit.setAttribute("FrameColor", m_orbitColor);
            m_orbit.changed();
        }
    }
    
    protected boolean isFullLastOrbit()
    {
        return m_lastOrbitEls == m_lastOrbitMaxEls;
    }
}
