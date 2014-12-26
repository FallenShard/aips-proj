/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import CH.ifa.draw.figures.LineConnection;
import CH.ifa.draw.framework.Figure;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author FallenShard
 */
public class ChemicalConnection extends LineConnection
{
    public ChemicalConnection()
    {
        super();
        setStartDecoration(null);
        setEndDecoration(null);
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(getFrameColor());
        g.setColor(Color.RED);
        Point p1, p2;
        for (int i = 0; i < fPoints.size()-1; i++) {
            p1 = (Point) fPoints.elementAt(i);
            p2 = (Point) fPoints.elementAt(i+1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
    
    @Override
    public boolean canConnect(Figure start, Figure end)
    {
        return true;
//        if (start instanceof ElectronFigure && end instanceof HoleFigure)
//            return true;
//        
//        return start instanceof HoleFigure && end instanceof ElectronFigure;
    }
}
