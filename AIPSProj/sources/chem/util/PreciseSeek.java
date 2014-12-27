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
public class PreciseSeek implements SeekStrategy, Serializable
{
    @Override
    public Figure getConnectableFigure(int x, int y, Vector<Figure> figures)
    {
        Figure result = null;
        for (Figure figure : figures)
        {
            if (figure.canConnect() && figure.containsPoint(x, y))
            {
                result = figure;
                break;
            }
        }
        
        return result;
    }
}
