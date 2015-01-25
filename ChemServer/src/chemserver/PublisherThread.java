/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import org.zeromq.ZMQ;
import task.LoadDocumentTask;
import task.Task;

/**
 *
 * @author FallenShard
 */
public class PublisherThread extends Thread
{
    ZMQ.Socket m_publisher = null;
    private boolean m_isRunning = false;
    private int m_docId = -1;
    
    public PublisherThread(ZMQ.Context context, int docId)
    {
        m_publisher = context.socket(ZMQ.PUB);
        m_docId = docId;
    }
    
    @Override
    public void start()
    {
        if (!m_isRunning)
        {
            m_publisher.bind("tcp://*:5556");
            
            
            super.start();
            m_isRunning = true;
            
            
        }
    }
    
    @Override
    public void run()
    {
        while (m_isRunning)
        {
            try
            {
                sleep(1000);
                Task task = new LoadDocumentTask(m_docId);
                task.run();
                String result = task.getResult();
                m_publisher.sendMore(String.format("%03d", m_docId));
                m_publisher.send(result);
                System.out.println("Sent snapshot!  of " + m_docId + " at " + System.currentTimeMillis());
            } 
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
        
        m_publisher.close();
    }
}
