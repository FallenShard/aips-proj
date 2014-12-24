/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test;

import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.Locator;
import CH.ifa.draw.standard.LocatorHandle;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author FallenShard
 */
public class AtomHandle extends LocatorHandle
{
    private final int m_minSize;
    private final int m_maxSize;
    
    public AtomHandle(Figure owner, Locator l, int minSize, int maxSize)
    {
        super(owner, l);
        
        m_minSize = minSize;
        m_maxSize = maxSize;
    }
    
    @Override
    public void invokeStep(int x, int y, int anchorX, int anchorY, DrawingView view)
    {
        Rectangle bounds = owner().displayBox();
        Point center = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        
        int radius = Math.min(m_maxSize / 2, Math.min(Math.abs(x - center.x), Math.abs(y - center.y)));
        
        
        Point origin = new Point(center.x - radius, center.y - radius);
        Point corner = new Point(center.x + radius, center.y + radius);
        
        owner().displayBox(origin, corner);
    }
}
