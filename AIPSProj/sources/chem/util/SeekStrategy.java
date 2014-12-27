/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.util;

import CH.ifa.draw.framework.Figure;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public interface SeekStrategy
{
    public abstract Figure getConnectableFigure(int x, int y, Vector<Figure> figures);
}
