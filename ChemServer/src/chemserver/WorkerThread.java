/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import org.zeromq.ZMQ;

/**
 *
 * @author FallenShard
 */
public class WorkerThread extends Thread
{
    private ZMQ.Context m_context = null;
    private ZMQ.Socket m_socket = null;
    
    public WorkerThread(ZMQ.Context context)
    {
        m_context = context;
    }
    
    @Override
    public void start()
    {
        super.start();
        
        m_socket = m_context.socket(ZMQ.REQ);
        m_socket.connect("ipc://backend.ipc");
        m_socket.send("READY");
    }
    
    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            byte[] address = m_socket.recv();
            String delimiter = m_socket.recvStr();
            
            String header = m_socket.recvStr();
            String body = m_socket.recvStr();
            
            
        }
        
    }
}
