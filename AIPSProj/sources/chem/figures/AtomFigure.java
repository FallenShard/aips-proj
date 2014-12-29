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
import chem.db.HibernateUtil;
import chem.figures.persist.AtomModel;
import chem.figures.persist.DocumentFigure;
import chem.figures.persist.Persistable;
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
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public abstract class AtomFigure extends CompositeFigure implements Animatable, Persistable, DocumentFigure
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    //Used for database
    protected AtomModel m_model = new AtomModel();
    
    @Override
    public int getId()
    {
        return m_model.getId();
    }

    @Override
    public void setModel()
    {
        m_model.setType(getAtomName());
        m_model.setX(displayBox().x);
        m_model.setY(displayBox().y);
    }
    
    @Override
    public void setDocumentId(int id)
    {
        m_model.setDocumentId(id);
    }
    
    @Override
    public void save()
    {
        setModel();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(m_model);
        session.getTransaction().commit();
        session.close();
    }
    
    @Override
    public void delete()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        //Delete electrons
        String hqlString = "DELETE FROM ELECTRON WHERE ATOM_ID = :atomid";
        Query query = session.createQuery(hqlString);
        query.setParameter("atomid", m_model.getId());
        query.executeUpdate();
        
        //Delete atom
        String hqlString1 = "DELETE FROM ATOM WHERE ID = :id";
        Query query1 = session.createQuery(hqlString1);
        query1.setParameter("id", m_model.getId());
        query1.executeUpdate();
        
        session.close();
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected EllipseFigure m_orbit = null;
    protected EllipseFigure m_nucleus = null;
    
    protected TextFigure m_name = null;
    protected TextFigure m_valence = null;
    protected int m_lastOrbitEls;
    protected int m_lastOrbitMaxEls;
    
    protected Color m_orbitColor = Color.WHITE;
    
    public static final int MAX_BONDS = 3;
    
    protected Vector<Figure> m_electrons = new Vector<>();
    
    //Used for database
    public List<ElectronFigure> getElectrons() {
        ArrayList<ElectronFigure> electrons = new ArrayList<ElectronFigure>();
        
        for (Figure f : m_electrons)
        {
            electrons.add((ElectronFigure)f);
        }
        
        return electrons;
    }
    
    //Used for fixing "THAT CRAP" bellow ^^
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

        // FIX THIS CRAP
        // STILL CRAP, NOW IT STAYS WHERE IT'S CLICKED ^^
        int currX = origin.x - r.width / 2 - lastX;
        int currY = origin.y - r.height / 2 - lastY;
        basicMoveBy(currX, currY);
        lastX = origin.x - r.width / 2;
        lastY = origin.y - r.height / 2;
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
    
//    protected boolean isFullLastOrbit()
//    {
//        return m_lastOrbitEls == m_lastOrbitMaxEls;
//    }
    
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
        AtomModel m = new AtomModel(getAtomName(), r.x, r.y);
        
        return m;
    }
}
