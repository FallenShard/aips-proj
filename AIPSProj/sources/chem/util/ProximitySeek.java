/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.util;

import CH.ifa.draw.framework.Figure;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public class ProximitySeek implements SeekStrategy, Serializable
{
    @Override
    public Figure getConnectableFigure(int x, int y, Vector<Figure> figures)
    {
        Figure result = null;
        int minDist = 99999;
        for (Figure figure : figures)
        {
            int dX = Math.abs(figure.center().x - x);
            int dY = Math.abs(figure.center().y - y);
            int distSq = dX * dX + dY * dY;
            
            if (distSq < minDist && figure.canConnect())
            {
                minDist = distSq;
                result = figure;
            }
        }
        
        return result;
    }

}
