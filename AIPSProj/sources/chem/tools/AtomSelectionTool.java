/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.tools;

import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.Handle;
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.DragTracker;
import CH.ifa.draw.standard.HandleTracker;
import CH.ifa.draw.standard.SelectAreaTracker;
import CH.ifa.draw.standard.SelectionTool;
import java.awt.Color;
import java.awt.event.MouseEvent;

/**
 *
 * @author FallenShard
 */
public class AtomSelectionTool extends SelectionTool
{
    private Tool fChild = null;

    public AtomSelectionTool(DrawingView view)
    {
        super(view);
    }

    /**
     * Handles mouse down events and starts the corresponding tracker.
     */
    public void mouseDown(MouseEvent e, int x, int y)
    {
        // on Windows NT: AWT generates additional mouse down events
        // when the left button is down && right button is clicked.
        // To avoid dead locks we ignore such events
        if (fChild != null)
            return;

        view().freezeView();

        // Check if a handle was manipulated first
        Handle handle = view().findHandle(e.getX(), e.getY());
        if (handle != null)
        {
            fChild = createHandleTracker(view(), handle);
        }
        else
        {
            // Otherwise, a figure might have been selected
            Figure selected = drawing().findFigure(e.getX(), e.getY());

            if (selected != null)
            {
                fChild = createDragTracker(view(), selected);
            }
            else 
            {
                if (!e.isShiftDown())
                {
                    for (Object f : view().selection())
                        ((Figure)f).setAttribute("FrameColor", Color.BLACK);
                    
                    view().clearSelection();
                }
                fChild = createAreaTracker(view());
            }
        }
        fChild.mouseDown(e, x, y);
    }

    /**
     * Handles mouse drag events. The events are forwarded to the
     * current tracker.
     */
    public void mouseDrag(MouseEvent e, int x, int y) {
        if (fChild != null) // JDK1.1 doesn't guarantee mouseDown, mouseDrag, mouseUp
            fChild.mouseDrag(e, x, y);
    }

    /**
     * Handles mouse up events. The events are forwarded to the
     * current tracker.
     */
    public void mouseUp(MouseEvent e, int x, int y) {
        view().unfreezeView();
        if (fChild != null) // JDK1.1 doesn't guarantee mouseDown, mouseDrag, mouseUp
            fChild.mouseUp(e, x, y);
        fChild = null;
    }

    /**
     * Factory method to create a Handle tracker. It is used to track a handle.
     */
    protected Tool createHandleTracker(DrawingView view, Handle handle) {
        return new HandleTracker(view, handle);
    }

    /**
     * Factory method to create a Drag tracker. It is used to drag a figure.
     */
    protected Tool createDragTracker(DrawingView view, Figure f) {
        return new AtomDragTracker(view, f);
    }

    /**
     * Factory method to create an area tracker. It is used to select an
     * area.
     */
    protected Tool createAreaTracker(DrawingView view) {
        return new AtomAreaTracker(view);
    }
    
    @Override
    public void deactivate()
    {
        super.deactivate();
        
        for (Object f : view().selection())
                        ((Figure)f).setAttribute("FrameColor", Color.BLACK);
        view().clearSelection();
    }
}
