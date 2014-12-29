/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.framework.Figure;
import chem.util.Const;
import chem.util.Dim;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author FallenShard
 */
public class OxygenFigure extends AtomFigure
{
    public OxygenFigure()
    {
        super();
        
        // Set nucleus color
        m_nucleus.setAttribute("FillColor", Const.OXYGEN_FILL);
        
        // Set name text attributes
        m_name.setAttribute("TextColor", Const.DARK_TEXT);
        m_name.setText("O");
        Rectangle r = m_name.displayBox();
        m_name.basicDisplayBox(new Point(Dim.ATOM_RADIUS - r.width / 2, Dim.ATOM_RADIUS - r.height / 2), null);
        
        // Set valence text attributes
        m_lastOrbitEls = 6;
        m_lastOrbitMaxEls = 8;
        m_valence.setAttribute("TextColor", Const.DARK_TEXT);
        updateValenceText();
        
        double angle = 2 * Math.PI / m_lastOrbitEls;
        for (int i = 0; i < m_lastOrbitEls; i++)
        {
            double c = Math.cos(angle * i);
            double s = -Math.sin(angle * i);
            int dX = (int)((Dim.ATOM_RADIUS - Dim.ELECTRON_RADIUS) * c + Dim.ATOM_RADIUS);
            int dY = (int)((Dim.ATOM_RADIUS - Dim.ELECTRON_RADIUS) * s + Dim.ATOM_RADIUS);
            
            Figure el = new ElectronFigure(new Point(dX, dY), Dim.ELECTRON_RADIUS, this, i);
            el.setAttribute("Angle", angle * i);
            m_electrons.add(el);
        }

        for (Figure fig : m_electrons)
            super.add(fig);
    }
    
    @Override
    public String getAtomName()
    {
        return "O";
    }
}
