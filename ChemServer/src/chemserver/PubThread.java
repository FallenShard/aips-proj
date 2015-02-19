/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import org.zeromq.ZMQ;
import protocol.Ports;
import task.LoadDocumentTask;
import task.Task;

/**
 *
 * @author FallenShard
 */
public class PubThread extends Thread
{
    ZMQ.Socket m_publisher = null;
    BlockingQueue<Integer> m_publishingQueue = null;
    
    private volatile boolean m_isRunning = false;
    
    public PubThread(ZMQ.Context context, BlockingQueue<Integer> publishingQueue)
    {
        m_publisher = context.socket(ZMQ.PUB);
        m_publishingQueue = publishingQueue;
    }
    
    @Override
    public void start()
    {
        if (!m_isRunning)
        {
            m_publisher.bind("tcp://*:" + Ports.PUBSUB_PORT);
            m_isRunning = true;
            
            super.start();
        }
    }
    
    public void end()
    {
        m_isRunning = false;
    }
    
    @Override
    public void run()
    {
        while (m_isRunning)
        {
            try 
            {
                int docId = m_publishingQueue.take();

                Task task = new LoadDocumentTask(docId);
                task.run();
                String result = task.getResult();
                
                m_publisher.sendMore(String.format("%03d", docId));
                m_publisher.send(result);
                System.out.println("Sent snapshot!  of document " + docId);
            } 
            catch (InterruptedException ex) 
            {
                ex.printStackTrace();
            }
        }
        
        m_publisher.close();
    }
}
