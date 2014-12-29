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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 *
 * @author FallenShard
 */
public class AtomAreaTracker extends AbstractTool
{
    // Selected group of atoms
    private Rectangle m_selectGroup;

    public AtomAreaTracker(DrawingView view)
    {
        super(view);
    }

    @Override
    public void mouseDown(MouseEvent e, int x, int y)
    {
        // use event coordinates to supress any kind of
        // transformations like constraining points to a grid
        super.mouseDown(e, e.getX(), e.getY());
        rubberBand(fAnchorX, fAnchorY, fAnchorX, fAnchorY);
    }

    @Override
    public void mouseDrag(MouseEvent e, int x, int y)
    {
        super.mouseDrag(e, x, y);
        eraseRubberBand();
        rubberBand(fAnchorX, fAnchorY, x, y);
    }

    @Override
    public void mouseUp(MouseEvent e, int x, int y)
    {
        super.mouseUp(e, x, y);
        eraseRubberBand();
        selectGroup(e.isShiftDown());
    }

    private void rubberBand(int x1, int y1, int x2, int y2)
    {
        m_selectGroup = new Rectangle(new Point(x1, y1));
        m_selectGroup.add(new Point(x2, y2));
        drawXORRect(m_selectGroup);
    }

    private void eraseRubberBand()
    {
        drawXORRect(m_selectGroup);
    }

    private void drawXORRect(Rectangle r)
    {
        Graphics g = view().getGraphics();
        g.setXORMode(view().getBackground());
        g.setColor(Const.IDLE_BORDER);
        g.drawRect(r.x, r.y, r.width, r.height);
    }

    private void selectGroup(boolean toggle)
    {
        FigureEnumeration k = drawing().figuresReverse();
        while (k.hasMoreElements())
        {
            Figure figure = k.nextFigure();
            Rectangle r2 = figure.displayBox();
            if (m_selectGroup.contains(r2.x, r2.y) && m_selectGroup.contains(r2.x+r2.width, r2.y+r2.height))
            {
                if (toggle)
                {
                    view().toggleSelection(figure);
                    if (view().selection().contains(figure))
                        figure.setAttribute("FrameColor", Const.SELECTION_BORDER);
                    else
                        figure.setAttribute("FrameColor", Const.IDLE_BORDER);
                }  
                else
                {
                    view().addToSelection(figure);
                    figure.setAttribute("FrameColor", Const.SELECTION_BORDER);
                } 
            }
        }
    }
}
