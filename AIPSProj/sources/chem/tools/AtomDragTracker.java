/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.tools;

import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.standard.AbstractTool;
import chem.util.Const;
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

        // If shift is pressed, it's a chain selection
        if (e.isShiftDown())
        {
            view().toggleSelection(m_selectedFigure);
            
            if (view().selection().contains(m_selectedFigure))
                m_selectedFigure.setAttribute("FrameColor", Const.SELECTION_BORDER);
            else
                m_selectedFigure.setAttribute("FrameColor", Const.IDLE_BORDER);
            
            m_selectedFigure = null;
            
            // Enumerate all selected figures and paint them red
            for (Object f : view().selection())
                ((Figure)f).setAttribute("FrameColor", Const.SELECTION_BORDER);
        } 
        else if (!view().selection().contains(m_selectedFigure))
        {
            // Otherwise, user clicked somewhere else, without shift, so nullify selection, but select the new figure
            for (Object f : view().selection())
                ((Figure)f).setAttribute("FrameColor", Const.IDLE_BORDER);
            
            view().clearSelection();
            view().addToSelection(m_selectedFigure);
            
            m_selectedFigure.setAttribute("FrameColor", Const.SELECTION_BORDER);
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
}
