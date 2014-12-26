/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import CH.ifa.draw.figures.EllipseFigure;
import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author FallenShard
 */
public class HoleFigure extends EllipseFigure
{
    
    public HoleFigure(Point center, int radius)
    {   
        setAttribute("FillColor", new Color(0, 0, 0, 0));

        basicDisplayBox(new Point(center.x - radius, center.y - radius), new Point(center.x + radius, center.y + radius));
    }
    
    @Override
    public boolean canConnect()
    {
        return true;
    }
}
