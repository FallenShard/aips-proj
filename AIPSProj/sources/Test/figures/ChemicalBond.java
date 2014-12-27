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
            
            m_start.setCovalentBond(this, end);
            m_end.setCovalentBond(this, start);
        }
    }
    
    @Override
    public void handleDisconnect(Figure start, Figure end)
    {
        if (start instanceof ElectronFigure && end instanceof ElectronFigure)
        {
            if (m_start != null)
                m_start.setCovalentBond(null, null);
            m_start = null;
            
            if (m_end != null)
                m_end.setCovalentBond(null, null);
            m_end = null;
        }
    }
}
