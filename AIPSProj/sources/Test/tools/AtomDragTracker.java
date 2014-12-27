/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.tools;

import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.standard.AbstractTool;
import java.awt.Color;
import java.awt.event.MouseEvent;

/**
 *
 * @author FallenShard
 */
public class AtomDragTracker extends AbstractTool
{
    // Selected figure
    private Figure  m_selectedFigure;
    
    // previous mouse position
    private int     m_lastX, m_lastY;      
    private boolean m_moved = false;

    public AtomDragTracker(DrawingView view, Figure selection)
    {
        super(view);
        m_selectedFigure = selection;
    }

    @Override
    public void mouseDown(MouseEvent e, int x, int y)
    {
        super.mouseDown(e, x, y);
        m_lastX = x;
        m_lastY = y;

        // If shift is pressed, it's a chain selection, otherwise 
        if (e.isShiftDown())
        {
           view().toggleSelection(m_selectedFigure);
           m_selectedFigure.setAttribute("FrameColor", Color.BLACK);
           m_selectedFigure = null;
        } 
        else if (!view().selection().contains(m_selectedFigure))
        {
            view().clearSelection();
            view().addToSelection(m_selectedFigure);
            
            m_selectedFigure.setAttribute("FrameColor", Color.RED);
        }
    }

    @Override
    public void mouseDrag(MouseEvent e, int x, int y)
    {
        super.mouseDrag(e, x, y);
        m_moved = (Math.abs(x - fAnchorX) > 4) || (Math.abs(y - fAnchorY) > 4);

        if (m_moved)
        {
            FigureEnumeration figures = view().selectionElements();
            while (figures.hasMoreElements())
                figures.nextFigure().moveBy(x - m_lastX, y - m_lastY);
        }
        m_lastX = x;
        m_lastY = y;
    }
    
    @Override
    public void deactivate()
    {
        super.deactivate();
        if (m_selectedFigure != null)
        {
            m_selectedFigure.setAttribute("FrameColor", Color.BLACK);
            m_selectedFigure = null;
        }
    }
}
