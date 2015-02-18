/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import org.zeromq.ZMQ;
import task.Task;

/**
 *
 * @author FallenShard
 */
public class WorkerThread extends Thread
{
    private Broker m_broker = null;
    private ZMQ.Context m_context = null;
    private ZMQ.Socket m_socket = null;
    
    public WorkerThread(ZMQ.Context context, Broker broker)
    {
        m_context = context;
        m_broker = broker;
    }
    
    @Override
    public void start()
    {
        m_socket = m_context.socket(ZMQ.REQ);
        m_socket.connect("ipc://backend.chem");
        m_socket.send("READY");
        
        super.start();
    }
    
    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            assert(m_socket != null);
            byte[] address = m_socket.recv();
            String empty = m_socket.recvStr();
            String header = m_socket.recvStr();
            String body = m_socket.recvStr();
            
            Task task = m_broker.createTask(address, header, body);
            
            String result = "Default message";
            if (task != null)
            {
                task.run();
                result = task.getResult();
            }
            
            m_socket.sendMore(address);
            m_socket.sendMore(empty);
            m_socket.send(result);
        }
        
        m_socket.close();
    }
}
