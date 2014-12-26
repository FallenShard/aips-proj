/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test;

import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.standard.ChopBoxConnector;
import CH.ifa.draw.util.Geom;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author FallenShard
 */
public class AtomConnector extends ChopBoxConnector {

    /*
     * Serialization support.
     */
    private static final long serialVersionUID = -3165091511154766610L;

    public AtomConnector() {
    }

    public AtomConnector(Figure owner) {
        super(owner);
    }

    protected Point chop(Figure target, Point from) {
        Rectangle r = target.displayBox();
        
        double angle = Geom.pointToAngle(r, from);
        
        r.x -= 15;
        r.y -= 15;
        r.height += 15;
        r.width += 15;
        
	    return Geom.ovalAngleToPoint(r, angle);
    }
}
