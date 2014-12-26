/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import CH.ifa.draw.figures.LineConnection;
import CH.ifa.draw.framework.Figure;
import Test.tools.ChangeBondEndHandle;
import Test.tools.ChangeBondStartHandle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public class ChemicalBond extends LineConnection
{
    private ElectronFigure m_start = null;
    private ElectronFigure m_end = null;
    
    public ChemicalBond()
    {
        super();
        setStartDecoration(null);
        setEndDecoration(null);
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(getFrameColor());
        g.setColor(Color.RED);
        Point p1, p2;
        for (int i = 0; i < fPoints.size()-1; i++) {
            p1 = (Point) fPoints.elementAt(i);
            p2 = (Point) fPoints.elementAt(i+1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
    
    @Override
    public Vector handles()
    {
        Vector handles = new Vector(fPoints.size());
        handles.addElement(new ChangeBondStartHandle(this));
        handles.addElement(new ChangeBondEndHandle(this));
        return handles;
    }
    
    @Override
    public boolean canConnect(Figure start, Figure end)
    {
        if (start instanceof ElectronFigure && end instanceof ElectronFigure)
        {
            ElectronFigure el1 = (ElectronFigure)start;
            ElectronFigure el2 = (ElectronFigure)end;
            
            return el1.getParent() != el2.getParent();
        }
        return false;
    }
    
    @Override
    public void handleConnect(Figure start, Figure end)
    {
        if (start instanceof ElectronFigure && end instanceof ElectronFigure)
        {
            m_start = (ElectronFigure)start;
            m_end = (ElectronFigure)end;
            
            m_start.setCovalentBond(m_end);
            m_end.setCovalentBond(m_start);
        }
    }
    
    @Override
    public void handleDisconnect(Figure start, Figure end)
    {
        if (start instanceof ElectronFigure && end instanceof ElectronFigure)
        {
            if (m_start != null)
                m_start.setCovalentBond(null);
            m_start = null;
            
            if (m_end != null)
                m_end.setCovalentBond(null);
            m_end = null;
        }
    }
}
