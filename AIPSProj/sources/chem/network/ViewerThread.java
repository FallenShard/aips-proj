/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.network;

import CH.ifa.draw.application.DrawApplication;
import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.framework.DrawingView;
import chem.db.JsonLoader;
import chem.util.NanoTimer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zeromq.ZMQ;

/**
 *
 * @author FallenShard
 */
public class ViewerThread extends Thread
{
    private DrawApplication m_app = null;
    private ZMQ.Socket m_subscriber = null;
    private volatile boolean     m_isRunning = false;
    private int m_documentId = -1;
    
    public ViewerThread(ZMQ.Context context, DrawApplication app, int docId)
    {
        m_app = app;
        m_subscriber = context.socket(ZMQ.SUB);
        m_documentId = docId;
    }
    
    @Override
    public void start()
    {
        if (!m_isRunning)
        {
            m_subscriber.connect("tcp://localhost:5556");

            //  Subscribe to hourly data, 0110
            String subscription = String.format("%03d", m_documentId);
            m_subscriber.subscribe(subscription.getBytes());
            
            super.start();
            m_isRunning = true;
        }
    }
    
    public void end()
    {
        if (m_isRunning)
        {
            m_isRunning = false;
            m_subscriber.close();
        }
    }
    
    @Override
    public void run()
    {
        while (m_isRunning)
        {
            try {
                String topic = m_subscriber.recvStr();
                if (topic == null)
                    break;
                String data = m_subscriber.recvStr();
                System.out.println("Received some data!");
                
                JsonLoader loader = new JsonLoader();
                Drawing drawing = loader.loadDrawing(data);
                
                m_app.view().freezeView();
                m_app.setDrawing(drawing);
                m_app.view().checkDamage();
                m_app.view().unfreezeView();
            } 
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        
        m_subscriber.close();
    }

}
