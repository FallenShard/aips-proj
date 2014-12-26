/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import Test.figures.AtomFigure;
import Test.figures.ElectronFigure;
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
public class CarbonFigure extends AtomFigure
{
    int m_numElectrons = 4;
    
    
    public CarbonFigure()
    {
        super();
        
        m_nucleus = new EllipseFigure(new Point(20, 20), new Point(100,100));
        m_nucleus.setAttribute("FillColor", Color.DARK_GRAY);
        
        m_name = new TextFigure();
        m_name.setFont(new Font("Calibri", Font.BOLD, 30));
        m_name.setAttribute("TextColor", Color.WHITE);
        m_name.setText("C");
        Rectangle r = m_name.displayBox();
        m_name.basicDisplayBox(new Point(60 - r.width / 2, 60 - r.height / 2), null);
        
        double angle = 2 * Math.PI / m_numElectrons;
        for (int i = 0; i < m_numElectrons; i++)
        {
            double c = Math.cos(angle * i);
            double s = -Math.sin(angle * i);
            int dX = (int)(55 * c + 60);
            int dY = (int)(55 * s + 60);

            m_electrons.add(new ElectronFigure(new Point(dX, dY), 5, this));
        }
               
        super.add(m_nucleus);
        super.add(m_name);
        
        for (EllipseFigure fig : m_electrons)
            super.add(fig);
    }
}
