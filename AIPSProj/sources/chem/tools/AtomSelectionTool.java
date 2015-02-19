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
import CH.ifa.draw.standard.HandleTracker;
import CH.ifa.draw.standard.SelectionTool;
import chem.util.Const;
import java.awt.event.MouseEvent;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author FallenShard
 */
public class AtomSelectionTool extends SelectionTool
{
    private Tool m_currentTool = null;
    
    private BlockingQueue<Boolean> m_updateQueue = null;

    public AtomSelectionTool(DrawingView view, BlockingQueue<Boolean> updateQueue)
    {
        super(view);
        
        m_updateQueue = updateQueue;
    }

    /**
     * Handles mouse down events and starts the corresponding tracker.
     */
    @Override
    public void mouseDown(MouseEvent e, int x, int y)
    {
        // on Windows NT: AWT generates additional mouse down events
        // when the left button is down && right button is clicked.
        // To avoid dead locks we ignore such events
        if (m_currentTool != null)
            return;

        view().freezeView();

        // Check if a handle was manipulated first
        Handle handle = view().findHandle(e.getX(), e.getY());
        if (handle != null)
        {
            m_currentTool = createHandleTracker(view(), handle);
        }
        else
        {
            // Otherwise, a figure might have been selected
            Figure selected = drawing().findFigure(e.getX(), e.getY());

            if (selected != null)
            {
                m_currentTool = createDragTracker(view(), selected);
            }
            else 
            {
                if (!e.isShiftDown())
                {
                    for (Object f : view().selection())
                        ((Figure)f).setAttribute("FrameColor", Const.IDLE_BORDER);
                    
                    view().clearSelection();
                }
                m_currentTool = createAreaTracker(view());
            }
        }
        m_currentTool.mouseDown(e, x, y);
    }

    /**
     * Handles mouse drag events. The events are forwarded to the
     * current tracker.
     */
    public void mouseDrag(MouseEvent e, int x, int y) {
        if (m_currentTool != null) // JDK1.1 doesn't guarantee mouseDown, mouseDrag, mouseUp
            m_currentTool.mouseDrag(e, x, y);
    }

    /**
     * Handles mouse up events. The events are forwarded to the
     * current tracker.
     */
    public void mouseUp(MouseEvent e, int x, int y) {
        view().unfreezeView();
        if (m_currentTool != null) // JDK1.1 doesn't guarantee mouseDown, mouseDrag, mouseUp
            m_currentTool.mouseUp(e, x, y);
        m_currentTool = null;
    }

    /**
     * Factory method to create a Handle tracker. It is used to track a handle.
     */
    @Override
    protected Tool createHandleTracker(DrawingView view, Handle handle) {
        return new HandleTracker(view, handle);
    }

    /**
     * Factory method to create a Drag tracker. It is used to drag a figure.
     */
    @Override
    protected Tool createDragTracker(DrawingView view, Figure f) {
        return new AtomDragTracker(view, f);
    }

    /**
     * Factory method to create an area tracker. It is used to select an
     * area.
     */
    @Override
    protected Tool createAreaTracker(DrawingView view) {
        return new AtomAreaTracker(view);
    }
    
    @Override
    public void activate()
    {
        super.activate();
        
        try
        {
            if (m_updateQueue != null)
            {
                m_updateQueue.clear();
                m_updateQueue.put(true);
            }
        } 
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void deactivate()
    {
        try
        {
            if (m_updateQueue != null)
            {
                m_updateQueue.clear();
                m_updateQueue.put(false);
            }
        } 
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
                
        super.deactivate();
        
        for (Object f : view().selection())
                        ((Figure)f).setAttribute("FrameColor", Const.IDLE_BORDER);
        view().clearSelection();
    }
    
    public void setUpdateQueue(BlockingQueue<Boolean> queue)
    {
        m_updateQueue = queue;
    }
}
