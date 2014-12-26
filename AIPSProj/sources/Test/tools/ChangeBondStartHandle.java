/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.tools;

import CH.ifa.draw.framework.Connector;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.standard.ChangeConnectionHandle;
import java.awt.Point;

/**
 *
 * @author FallenShard
 */
public class ChangeBondStartHandle extends ChangeConnectionHandle {

    /**
     * Constructs the connection handle for the given start figure.
     */
    public ChangeBondStartHandle(Figure owner) {
        super(owner);
    }

    /**
     * Gets the start figure of a connection.
     */
    protected Connector target() {
        return fConnection.start();
    }

    /**
     * Disconnects the start figure.
     */
    protected void disconnect() {
        fConnection.disconnectStart();
    }

    /**
     * Sets the start of the connection.
     */
    protected void connect(Connector c) {
        fConnection.connectStart(c);
    }

    /**
     * Sets the start point of the connection.
     */
    protected void setPoint(int x, int y) {
        fConnection.startPoint(x, y);
    }

    /**
     * Returns the start point of the connection.
     */
    public Point locate() {
        return fConnection.startPoint();
    }
}
