/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.Locator;
import CH.ifa.draw.standard.LocatorHandle;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author FallenShard
 */
public class AngularHandle extends LocatorHandle
{
    private Figure m_centerFig;
    private int surfaceOffset;
    
    public AngularHandle(Figure owner, Figure centerFig, Locator l, int offset)
    {
        super(owner, l);
        
        m_centerFig = centerFig;
        surfaceOffset = offset;
    }
    
    @Override
    public void invokeStep(int x, int y, int anchorX, int anchorY, DrawingView view)
    {
        Rectangle bounds = owner().displayBox();
        Point rotCenter = m_centerFig.center();
        
        int radius = m_centerFig.displayBox().width / 2 + surfaceOffset;
        
        int dX = x - rotCenter.x;
        int dY = y - rotCenter.y;
        
        double angle = Math.atan2((double)dY, (double)dX);
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        int newX = (int)(rotCenter.x + radius * c + 0.5);
        int newY = (int)(rotCenter.y + radius * s + 0.5);
        
        Point origin = new Point(newX - bounds.width / 2, newY - bounds.height / 2);
        Point corner = new Point(newX + bounds.width / 2, newY + bounds.height / 2);
        
        owner().displayBox(origin, corner);
    }
    
    @Override
    public void draw(Graphics g)
    {
        
    }
}
