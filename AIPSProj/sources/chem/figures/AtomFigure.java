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
import chem.figures.persist.Persistable;
import chem.figures.persist.PersistableFigure;
import chem.util.Const;
import chem.util.Dim;
import chem.util.SeekStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public abstract class AtomFigure extends CompositeFigure implements Animatable, PersistableFigure
{
    public static final int MAX_BONDS = 3;
    
    // Used for persistence in database
    protected AtomModel m_model = new AtomModel();
    
    // Figures that comprise the atom
    protected EllipseFigure m_orbit = null;
    protected EllipseFigure m_nucleus = null;
    
    protected TextFigure m_name = null;
    protected TextFigure m_valence = null;
    
    protected Vector<Figure> m_electrons = new Vector<>();
    
    // Orbit parameters
    protected int m_lastOrbitEls;
    protected int m_lastOrbitMaxEls;
    
    // Quickfix for orbit, will make a new figure later
    protected Color m_orbitColor = Const.ORBIT_DEFAULT;

    // For positioning bug
    private int lastX = 0;
    private int lastY = 0;
    
    // Used in graph processing
    protected Map<ChemicalBond, AtomFigure> m_bonds = new HashMap<>();

    public AtomFigure()
    {
        super();
        
        // Create nucleus here
        m_nucleus = new EllipseFigure(new Point(Dim.NUCLEUS_OFFSET_X, Dim.NUCLEUS_OFFSET_Y), 
                                      new Point(Dim.NUCLEUS_OFFSET_X + Dim.NUCLEUS_SIZE, Dim.NUCLEUS_OFFSET_Y + Dim.NUCLEUS_SIZE));
        
        // Create a halo-like orbit
        m_orbit = new EllipseFigure(new Point(Dim.ORBIT_OFFSET_X, Dim.ORBIT_OFFSET_Y), 
                                    new Point(Dim.ORBIT_OFFSET_X + Dim.ORBIT_SIZE, Dim.ORBIT_OFFSET_Y + Dim.ORBIT_SIZE));
        m_orbit.setAttribute("FillColor", Const.TRANSPARENT);
        m_orbit.setAttribute("FrameColor", m_orbitColor);
        
        // This is the central text, atom's name
        m_name = new TextFigure();
        m_name.setFont(new Font("Calibri", Font.BOLD, Dim.NUCLEUS_FONT_SIZE));
        
        // This is the valence number
        m_valence = new TextFigure();
        m_valence.setFont(new Font("Calibri", Font.BOLD, Dim.VALENCE_FONT_SIZE));
        
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
            handles.add(new AngularHandle(fig, m_nucleus, RelativeLocator.center(), Dim.ELECTRON_OFFSET));
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
        
        if (name.equalsIgnoreCase("FrameColor") && (Color)(value) == Const.IDLE_BORDER)
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
        m_valence.basicDisplayBox(new Point(r.x + r.width / 2 - valR.width / 2, 
                                            r.y + r.height / 2 - valR.height / 2 - m_name.displayBox().height / 2 - Dim.ELECTRON_SIZE), null);
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
    
    public Vector<ElectronFigure> getElectrons()
    {
        Vector<ElectronFigure> electrons = new Vector<>();
        
        for (Figure f : m_electrons)
        {
            electrons.add((ElectronFigure)f);
        }
        
        return electrons;
    }
    
    public void setElectrons(Vector<Figure> electrons)
    {    
        super.removeAll(m_electrons);
        
        m_electrons.clear();
        m_electrons = electrons;
        
        for (Figure el : m_electrons)
            super.add(el);
    }
    
    @Override
    public AtomModel getModel()
    {
        m_model.setType(getAtomName());
        m_model.setX(displayBox().x);
        m_model.setY(displayBox().y);
        return m_model;
    }

    @Override
    public void setModel(Persistable model)
    {
        m_model = (AtomModel)model;
    }

    @Override
    public void saveToDatabase(Session session, int documentId)
    {
        getModel();

        m_model.save(session, documentId);
        
        for (Figure electron : m_electrons)
        {
            ((PersistableFigure)electron).saveToDatabase(session, documentId);
        }
    }
    
    @Override
    public void saveToDatabaseAs(Session session, int documentId)
    {
        getModel();

        m_model.saveAs(session, documentId);
        
        for (Figure electron : m_electrons)
        {
            ((PersistableFigure)electron).saveToDatabaseAs(session, documentId);
        }
    }
    
    @Override
    public void deleteFromDatabase(Session session)
    {
        
    }
    
    @Override
    public void appendJson(StringBuilder packedJson, ObjectMapper mapper)
    {
        try 
        {
            getModel();
            packedJson.append(mapper.writeValueAsString(m_model));
            
            for (Figure electron : m_electrons)
            {
                ((PersistableFigure)electron).appendJson(packedJson, mapper);
            }
            
            packedJson.append("$");
        } 
        catch (JsonProcessingException ex)
        {
            Logger.getLogger(AtomFigure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
