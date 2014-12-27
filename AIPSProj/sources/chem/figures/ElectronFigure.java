/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.framework.Figure;
import chem.anim.Animatable;
import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author FallenShard
 */
public class ElectronFigure extends EllipseFigure implements Animatable
{
    AtomFigure m_parent = null;
    
    Figure m_otherElectron = null;
    
    ChemicalBond m_covalentBond = null;
    
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
}
