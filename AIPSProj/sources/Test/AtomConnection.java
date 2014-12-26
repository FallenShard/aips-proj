/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.figures.LineConnection;
import CH.ifa.draw.framework.Figure;

/**
 *
 * @author FallenShard
 */
public class AtomConnection extends LineConnection
{
    AtomConnection()
    {
        super();
        setStartDecoration(null);
        setEndDecoration(null);
    }
    
    @Override
    public boolean canConnect(Figure start, Figure end)
    {
        return start instanceof EllipseFigure && end instanceof EllipseFigure;
    }
}
