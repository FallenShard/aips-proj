/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.network;

import CH.ifa.draw.application.DrawApplication;
import CH.ifa.draw.framework.Drawing;
import chem.db.DrawingLoader;
import chem.db.JsonLoader;
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
    
    public ViewerThread(ZMQ.7Context context, DrawApplication app, int docId)
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
        }
    }
    
    @Override
    public void run()
    {
        while (m_isRunning)
        {
            String topic = m_subscriber.recvStr();
            if (topic == null)
                break;
            String data = m_subscriber.recvStr();
            System.out.println("Received some data!");
            
            DrawingLoader loader = new JsonLoader(data);
            Drawing drawing = loader.createDrawing();
            
            m_app.view().freezeView();
            m_app.setDrawing(drawing);
            m_app.view().checkDamage();
            m_app.view().unfreezeView();
        }
        
        m_subscriber.close();
    }

}
