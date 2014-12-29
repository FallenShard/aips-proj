/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.framework.Figure;
import chem.anim.Animatable;
import chem.db.HibernateUtil;
import chem.figures.persist.AtomModel;
import chem.figures.persist.ConnectableFigure;
import chem.figures.persist.ElectronModel;
import chem.figures.persist.Persistable;
import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ElectronFigure extends EllipseFigure implements Animatable, Persistable, ConnectableFigure
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    //Used for database
    ElectronModel m_model = new ElectronModel();
    
    @Override
    public int getId()
    {
        return m_model.getId();
    }

    @Override
    public void setModel()
    {
        m_model.setAngle(m_angle);
        m_model.setAtomId(m_parent.getId());
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
    public void setRelations()
    {
        if (m_covalentBond != null)
            m_model.setBondId(m_covalentBond.getId());
        if (m_otherElectron != null)
        {
            ElectronFigure otherElectron = (ElectronFigure)m_otherElectron;
            m_model.setOtherElectronId(otherElectron.getId());
        }
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(m_model);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    AtomFigure m_parent = null;
    
    //Changed type from Figure
    Figure m_otherElectron = null;
    
    ChemicalBond m_covalentBond = null;
    
    //Used for database
    double m_angle;

    public void setAngle(double m_angle) {
        this.m_angle = m_angle;
    }
    
    public ElectronFigure()
    {
        setAttribute("FillColor", Color.GREEN);
        
        basicDisplayBox(new Point(0, 0), new Point(10, 10));
    }
    
    public ElectronFigure(Point center, int radius, AtomFigure parent)
    {   
        setAttribute("FillColor", Color.GREEN);
        
        m_parent = parent;
        
        basicDisplayBox(new Point(center.x - radius, center.y - radius), new Point(center.x + radius, center.y + radius));
    }
    
    @Override
    public boolean canConnect()
    {
        return m_otherElectron == null;
    }
    
    public void setCovalentBond(ChemicalBond bond, Figure electron)
    {
        m_covalentBond = bond;
        m_otherElectron = electron;
        
        if (bond == null)
            m_parent.decreaseValence(bond);
        else
            m_parent.increaseValence(bond, ((ElectronFigure)electron).getParent());
    }
    
    public AtomFigure getParent()
    {
        return m_parent;
    }
    
    public Figure getConnectedElectron()
    {
        return m_otherElectron;
    }
    
    public Figure getCovalentBond()
    {
        return m_covalentBond;
    }

    @Override
    public void animationStep(float timeDelta)
    {
        moveBy((int) (100 * timeDelta + 0.5), 0);
    }

    //Used for database
    public ElectronModel getModel() {
        ElectronModel em = new ElectronModel(m_angle);
        return em;
    }
}
