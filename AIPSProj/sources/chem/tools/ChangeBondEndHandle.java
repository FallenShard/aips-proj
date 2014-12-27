/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.tools;

import CH.ifa.draw.framework.Connector;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.standard.ChangeConnectionHandle;
import java.awt.Point;

/**
 *
 * @author FallenShard
 */
public class ChangeBondEndHandle extends ChangeConnectionHandle
{

    /**
     * Constructs the connection handle.
     */
    public ChangeBondEndHandle(Figure owner)
    {
        super(owner);
    }

    /**
     * Gets the end figure of a connection.
     */
    protected Connector target()
    {
        return fConnection.end();
    }

    /**
     * Disconnects the end figure.
     */
    protected void disconnect()
    {
        fConnection.disconnectEnd();
    }

    /**
     * Sets the end of the connection.
     */
    protected void connect(Connector c)
    {
        fConnection.connectEnd(c);
    }

    /**
     * Sets the end point of the connection.
     */
    protected void setPoint(int x, int y)
    {
        fConnection.endPoint(x, y);
    }

    /**
     * Returns the end point of the connection.
     */
    public Point locate()
    {
        return fConnection.endPoint();
    }
}
