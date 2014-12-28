/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.framework.Figure;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author FallenShard
 */
public class CarbonFigure extends AtomFigure
{    
    public CarbonFigure()
    {
        super();

        // Set nucleus color
        m_nucleus.setAttribute("FillColor", Color.DARK_GRAY);

        // Set name text attributes
        m_name.setAttribute("TextColor", Color.WHITE);
        m_name.setText("C");
        Rectangle r = m_name.displayBox();
        m_name.basicDisplayBox(new Point(60 - r.width / 2, 60 - r.height / 2), null);
        
        // Set valence text attributes
        m_lastOrbitEls = 4;
        m_lastOrbitMaxEls = 8;
        m_valence.setAttribute("TextColor", Color.WHITE);
        updateValenceText();
        
        // Electrons
        double angle = 2 * Math.PI / m_lastOrbitEls;
        for (int i = 0; i < m_lastOrbitEls; i++)
        {
            double c = Math.cos(angle * i);
            double s = -Math.sin(angle * i);
            int dX = (int)(55 * c + 60);
            int dY = (int)(55 * s + 60);

            Figure el = new ElectronFigure(new Point(dX, dY), 5, this);
            el.setAttribute("Angle", angle * i);
            m_electrons.add(el);
        }

        for (Figure fig : m_electrons)
            super.add(fig);
    }
    
    @Override
    public String getAtomName()
    {
        return "C";
    }
}
