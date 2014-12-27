/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.tools;

import chem.figures.AtomFigure;
import CH.ifa.draw.framework.ConnectionFigure;
import CH.ifa.draw.framework.Connector;
import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.standard.AbstractTool;
import CH.ifa.draw.util.Geom;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

/**
 *
 * @author FallenShard
 */
public class CovalentBondTool extends AbstractTool
{
    /**
     * Connector on the starting figure, a free electron
     */
    private Connector   m_startConnector;
    
    /**
     * Connector on the ending figure, a free electron
     */
    private Connector   m_endConnector;
    
    /**
     * NO IDEA
     */
    private Connector   m_connectorTarget = null;

    /**
     * Figure that is the "target" of current mouse position
     */
    private Figure m_target = null;

    /**
     * Created connection figure between the electrons
     */
    private ConnectionFigure  m_connection;

    /**
     * Prototype for the connection figure
     */
    private final ConnectionFigure  m_connPrototype;


    public CovalentBondTool(DrawingView view, ConnectionFigure prototype)
    {
        super(view);
        m_connPrototype = prototype;
    }

    /**
     * Handles mouse move events in the drawing view.
     */
    @Override
    public void mouseMove(MouseEvent e, int x, int y)
    {
        trackConnectors(e, x, y);
    }

    /**
     * Manipulates connections in a context dependent way. If the
     * mouse down hits a figure start a new connection.
     */
    public void mouseDown(MouseEvent e, int x, int y)
    {
        // Pull mouse position from the event
        int ex = e.getX();
        int ey = e.getY();
        
        // Find the figure which is the starting point for connector
        m_target = findConnectionStart(ex, ey, drawing());
        
        // If it's not null, let's attempt to create a connector from it
        if (m_target != null)
        {
            m_startConnector = findConnector(ex, ey, m_target);
            
            // If that went successful, mark the point of the mouse down as start and end
            // ending point will change on mouseDrag, no worries
            if (m_startConnector != null)
            {
                Point p = new Point(ex, ey);
                m_connection = createConnection();
                m_connection.startPoint(p.x, p.y);
                m_connection.endPoint(p.x, p.y);
                
                // Add it to the view as well, so that it gets updated/drawn/whatever
                view().add(m_connection);
            }
        }
    }

    /**
     * Adjust the created connection or split segment.
     * This event is received when the connection is being edited
     */
    public void mouseDrag(MouseEvent e, int x, int y)
    {
        // Get the point where the mouse clicked
        Point p = new Point(e.getX(), e.getY());
        
        // If the connection is not null, track the connectors positions
        if (m_connection != null)
        {
            // So we track here
            trackConnectors(e, x, y);
            
            // If we have a connector target (to end the connection)
            if (m_connectorTarget != null)
            {
                // Change current point into the center of rectangular displayBox of the target
                p = Geom.center(m_connectorTarget.displayBox());
            }
            
            // Change the connection end point
            m_connection.endPoint(p.x, p.y);
        }
    }

    /**
     * Connects the figures if the mouse is released over another connectable electron.
     */
    public void mouseUp(MouseEvent e, int x, int y)
    {
        // The ending figure that will accept the connection
        Figure endFigure = null;
        
        // If we have acquired a starting connector
        if (m_startConnector != null)
        {
            // Let's assume this function uses m_startConnector as a non-null
            endFigure = findTarget(e.getX(), e.getY(), drawing());
        }
            
        // If we acquired a target
        if (endFigure != null)
        {
            m_endConnector = findConnector(e.getX(), e.getY(), endFigure);
            if (m_endConnector != null)
            {
                m_connection.connectStart(m_startConnector);
                m_connection.connectEnd(m_endConnector);
                m_connection.updateConnection();
            }
        }
        else if (m_connection != null)
        {
            // If we started a connection, but did not find ending point, remove it from view
            view().remove(m_connection);
        }

        // Reset all state
        m_connection = null;
        m_startConnector = null;
        m_endConnector = null;
        editor().toolDone();
    }

    @Override
    public void deactivate()
    {
        super.deactivate();
        if (m_target != null)
            m_target.connectorVisibility(false);
    }

    /**
     * Creates the ConnectionFigure. By default the figure prototype is
     * cloned.
     * @return Returns the connection prototype that was specified in the constructor
     */
    protected ConnectionFigure createConnection()
    {
        return (ConnectionFigure)m_connPrototype.clone();
    }

    /**
     * Finds a connectable figure source, with the given coordinates and drawing
     */
    protected Figure findSource(int x, int y, Drawing drawing)
    {
        return findConnectableElectron(x, y, drawing);
    }

    /**
     * Finds a connectable figure target, with the given coordinates and drawing
     */
    protected Figure findTarget(int x, int y, Drawing drawing)
    {
        Figure target = findConnectableElectron(x, y, drawing);
        Figure start = m_startConnector.owner();

        if (target != null && m_connection != null)
        {
            // Assure that target can connect and that it is not self-included in composite
            if (target.canConnect() && !target.includes(start))
            {
                // Also, make sure that two electrons can be connected by this connection
                if (m_connection.canConnect(start, target))
                {
                    return target;
                }
            }
        }

        return null;
    }

    /**
     * Gets the currently created connection
     */
    protected ConnectionFigure createdFigure()
    {
        return m_connection;
    }

    /**
     * This stuff tracks connectors, it gets called on mouseMove and mouseDrag
     * @param e mouseEvent that came from 
     * @param x mouseCoord x
     * @param y mouseCoord y
     */
    protected void trackConnectors(MouseEvent e, int x, int y)
    {
        // Figure that gets affected by tracking
        Figure trackedFigure;

        if (m_startConnector == null)
        {
            trackedFigure = findSource(x, y, drawing());
        }
        else
        {
            trackedFigure = findTarget(x, y, drawing());
        }

        // Track the figure containing the mouse
        if (trackedFigure != m_target)
        {
            // If target is source, change connector
            if (m_target != null)
                m_target.connectorVisibility(false);
            
            m_target = trackedFigure;
            if (m_target != null)
                m_target.connectorVisibility(true);
        }

        // Newly formed connector
        Connector newConnector = null;
        if (trackedFigure != null)
            newConnector = findConnector(e.getX(), e.getY(), trackedFigure);
        if (newConnector != m_connectorTarget)
            m_connectorTarget = newConnector;

        view().checkDamage();
    }

    // Returns a connector provided by the figure, relative to x and y
    private Connector findConnector(int x, int y, Figure f)
    {
        return f.connectorAt(x, y);
    }

    /**
     * Finds a figure that will provide the starting connector
     */
    protected Figure findConnectionStart(int x, int y, Drawing drawing)
    {
        Figure target = findConnectableElectron(x, y, drawing);
        return target;
    }

    /**
     * This is in fact, the heart of the class, it finds a connectable figure at x, y coords
     * @param x usually mouseX
     * @param y usually mouseY
     * @param drawing
     * @return 
     */
    private Figure findConnectableElectron(int x, int y, Drawing drawing)
    {
        // Obtain an enumaration of figures from drawing - a bunch of atoms and connections
        FigureEnumeration figures = drawing.figuresReverse();
        while (figures.hasMoreElements())
        {
            Figure fig = figures.nextFigure();

            // Only atoms can connect (or molecules)
            if (fig.canConnect() && fig.containsPoint(x, y))
            {
                // If the figure is an atom, find a free electron
                if (fig instanceof AtomFigure)
                {
                    AtomFigure atom = (AtomFigure)fig;
                    
                    return atom.getConnectableElectron(x, y);
                }
            }
        }
        return null;
    }

    protected Connector getStartConnector()
    {
        return m_startConnector;
    }

    protected Connector getEndConnector()
    {
        return m_endConnector;
    }

    protected Connector getTarget()
    {
        return m_connectorTarget;
    }
}
