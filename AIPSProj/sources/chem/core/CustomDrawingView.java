/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.core;

import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.standard.StandardDrawingView;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 * @author FallenShard
 */
public class CustomDrawingView extends StandardDrawingView
{
    private UserStatus m_status;
    
    CustomDrawingView(DrawingEditor editor, UserStatus userStatus, int width, int height)
    {
        super(editor, width, height);
        
        m_status = userStatus;
    }
    
    public void mousePressed(MouseEvent e) {
        if (m_status.getUserStatus() != UserStatus.CH4_VIEWER)
            super.mousePressed(e);
    }

    public void mouseDragged(MouseEvent e) {
        if (m_status.getUserStatus() != UserStatus.CH4_VIEWER)
            super.mouseDragged(e);
    }

    public void mouseMoved(MouseEvent e) {
        if (m_status.getUserStatus() != UserStatus.CH4_VIEWER)
            super.mouseMoved(e);
    }


    public void mouseReleased(MouseEvent e) {
        if (m_status.getUserStatus() != UserStatus.CH4_VIEWER)
            super.mouseReleased(e);
    }
}
