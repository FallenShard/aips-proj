/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.framework.Figure;
import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author FallenShard
 */
public class ElectronFigure extends EllipseFigure
{
    AtomFigure m_parent = null;
    
    Figure m_otherElectron = null;
    
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
    
    public void setCovalentBond(Figure electron)
    {
        m_otherElectron = electron;
    }
}
