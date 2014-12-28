/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.figures.ChopEllipseConnector;
import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.figures.TextFigure;
import CH.ifa.draw.framework.Connector;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Handle;
import CH.ifa.draw.standard.CompositeFigure;
import CH.ifa.draw.standard.RelativeLocator;
import chem.anim.Animatable;
import chem.figures.persist.AtomModel;
import chem.util.SeekStrategy;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public abstract class AtomFigure extends CompositeFigure implements Animatable
{
    protected AtomModel m_model = new AtomModel();
    
    protected EllipseFigure m_orbit = null;
    protected EllipseFigure m_nucleus = null;
    
    protected TextFigure m_name = null;
    protected TextFigure m_valence = null;
    protected int m_lastOrbitEls;
    protected int m_lastOrbitMaxEls;
    
    protected Color m_orbitColor = Color.WHITE;
    
    public static final int MAX_BONDS = 3;
    
    protected Vector<Figure> m_electrons = new Vector<>();
    
    public List<ElectronFigure> getElectrons() {
        ArrayList<ElectronFigure> electrons = new ArrayList<>();
        
        for (Figure f : m_electrons)
        {
            electrons.add((ElectronFigure)f);
        }
        
        return electrons;
    }
    
    // For positioning
    private int lastX = 0;
    private int lastY = 0;
    
    protected Map<ChemicalBond, AtomFigure> m_bonds = new HashMap<>();

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
        
        // This is the valence number
        m_valence = new TextFigure();
        m_valence.setFont(new Font("Calibri", Font.BOLD, 12));
        
        super.add(m_orbit);
        super.add(m_nucleus);
        super.add(m_name);
        super.add(m_valence);
    }
    
    public abstract String getAtomName();

    @Override
    public void basicDisplayBox(Point origin, Point corner)
    {
        Rectangle r = displayBox();

        int currX = corner.x - r.width / 2;
        int currY = corner.y - r.height / 2;
        
        int deltaX = currX - lastX;
        int deltaY = currY - lastY;        
        
        basicMoveBy(deltaX, deltaY);
        lastX = currX;
        lastY = currY;
    }
    
    @Override
    public boolean canConnect()
    {
        return m_lastOrbitEls < m_lastOrbitMaxEls;
    }
    
    @Override
    public Vector<Handle> handles()
    {
        Vector<Handle> handles = new Vector<>();
        
        for (Figure fig : m_electrons)
        {
            handles.add(new AngularHandle(fig, m_nucleus, RelativeLocator.center(), 15));
        }

        return handles;
    }
    
    public Figure getConnectableElectron(int x, int y, SeekStrategy seekStrategy)
    {        
        return seekStrategy.getConnectableFigure(x, y, m_electrons);
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
        
        for (Map.Entry<ChemicalBond, AtomFigure> entry : m_bonds.entrySet())
        {
            ChemicalBond bond = entry.getKey();
            bond.setAttribute(name, value);
            bond.startFigure().setAttribute(name, value);
            bond.endFigure().setAttribute(name, value);
        }
    }
    
    @Override
    public void draw(Graphics g)
    {   
        FigureEnumeration k = figures();
        while (k.hasMoreElements())
            k.nextFigure().draw(g);
    }
    
    protected void increaseValence(ChemicalBond bond, AtomFigure figure)
    {
        if (m_lastOrbitEls < m_lastOrbitMaxEls)
        {
            m_lastOrbitEls++;
            m_bonds.put(bond, figure);
        }

        updateValenceText();
    }
    
    protected void decreaseValence(ChemicalBond bond)
    {
        m_lastOrbitEls--;
        
        m_bonds.remove(bond);

        updateValenceText();
    }
    
    protected void updateValenceText()
    {
        m_valence.setText("" + m_lastOrbitEls + " (" + m_lastOrbitMaxEls + ")");
        
        if (m_lastOrbitEls == m_lastOrbitMaxEls)
        {
            m_orbitColor = Color.GREEN;
        }
        else
        {
            m_orbitColor = Color.WHITE;
        }
        
        m_orbit.setAttribute("FrameColor", m_orbitColor);
        Rectangle valR = m_valence.displayBox();
        
        Rectangle r = m_nucleus.displayBox();
        m_valence.basicDisplayBox(new Point(r.x + r.width / 2 - valR.width / 2, r.y + r.height / 2 - valR.height / 2 - m_name.displayBox().height / 2 - 5), null);
    }
    
    public int bondsWith(AtomFigure atom)
    {
        int occurences = 0;
        for (Map.Entry<ChemicalBond, AtomFigure> entry : m_bonds.entrySet())
        {
            if (entry.getValue() == atom)
                occurences++;
        }
        
        return occurences;
    }
    
    @Override
    public void animationStep(float timeDelta)
    {
//        for (Figure electron : m_electrons)
//            ((Animatable)electron).animationStep(timeDelta);
    }
    
    public AtomModel getModel()
    {
        Rectangle r = displayBox();
        AtomModel m = new AtomModel(getAtomName(), r.x, r.y, -1);
        
        return m;
    }
}
