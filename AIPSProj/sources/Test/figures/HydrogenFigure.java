/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.figures.TextFigure;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author FallenShard
 */
public class HydrogenFigure extends AtomFigure
{
    int m_numElectrons = 2;
    
    
    public HydrogenFigure()
    {
        super();
        
        m_nucleus = new EllipseFigure(new Point(20, 20), new Point(100,100));
        m_nucleus.setAttribute("FillColor", Color.BLUE);
        
        m_name = new TextFigure();
        m_name.setFont(new Font("Calibri", Font.BOLD, 30));
        m_name.setText("H");
        m_name.setAttribute("TextColor", Color.WHITE);
        Rectangle r = m_name.displayBox();
        m_name.basicDisplayBox(new Point(60 - r.width / 2, 60 - r.height / 2), null);
        
        double angle = 2 * Math.PI / m_numElectrons;
        for (int i = 0; i < m_numElectrons; i++)
        {
            double c = Math.cos(angle * i + Math.PI / 2);
            double s = -Math.sin(angle * i + Math.PI / 2);
            int dX = (int)(55 * c + 60);
            int dY = (int)(55 * s + 60);
            
            EllipseFigure electron = new ElectronFigure(new Point(dX, dY), 5);
            m_electrons.add(electron);
            
        }
               
        super.add(m_nucleus);
        super.add(m_name);
        
        for (EllipseFigure fig : m_electrons)
            super.add(fig);
    }
}
