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
import protocol.Ports;

/**
 *
 * @author FallenShard
 */
public class ViewerThread extends Thread
{
    private DrawApplication m_app = null;
    private ZMQ.Socket m_subscriber = null;
    private volatile boolean m_isRunning = false;
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
            m_subscriber.connect("tcp://localhost:" + Ports.PUBSUB_PORT);

            //  Subscribe to document data, on id
            String subscription = String.format("%03d", m_documentId);
            m_subscriber.subscribe(subscription.getBytes());
            
            System.out.println("Starting viewer thread on port " + Ports.PUBSUB_PORT + ". Subscribing on: " + subscription);
            
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
            //sleep(500);
//            String topic = m_subscriber.recvStr();
//            if (topic == null)
//                break;
//            String data = m_subscriber.recvStr();
//            System.out.println("Received some data in viewer thread!");
            
            ZMQ.Poller polledSockets = new ZMQ.Poller(1);
            
            polledSockets.register(m_subscriber, ZMQ.Poller.POLLIN);
            
            polledSockets.poll(16);
            
            if (polledSockets.pollin(0))
            {
                String topic = m_subscriber.recvStr(ZMQ.DONTWAIT);
                System.out.println(topic);
                if (topic == null)
                    break;
                String data = m_subscriber.recvStr(ZMQ.DONTWAIT);
                System.out.println("Received some data in viewer thread!");
                
                DrawingLoader loader = new JsonLoader(data);
                Drawing drawing = loader.createDrawing();

                m_app.view().freezeView();
                m_app.setDrawing(drawing);
                m_app.view().checkDamage();
                m_app.view().unfreezeView();

                // ugly hack to refresh the white space
                m_app.setSize(m_app.getSize());
                m_app.setVisible(true);
            }
            
//            DrawingLoader loader = new JsonLoader(data);
//            Drawing drawing = loader.createDrawing();
//            
//            m_app.view().freezeView();
//            m_app.setDrawing(drawing);
//            m_app.view().checkDamage();
//            m_app.view().unfreezeView();
//
//            // ugly hack to refresh the white space
//            m_app.setSize(m_app.getSize());
//            m_app.setVisible(true);
        }
        
        m_subscriber.close();
    }

}
