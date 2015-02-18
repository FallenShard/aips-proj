/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.zeromq.ZMQ;
import protocol.MessageType;
import protocol.Ports;
import task.CheckEditorTask;
import task.GetDocsTask;
import task.LoadDocumentTask;
import task.SaveDocumentTask;
import task.Task;

/**
 *
 * @author FallenShard
 */
public class Broker
{
    public static final int NBR_WORKERS = 4;
    
    ZMQ.Context m_context = null;
    
    ZMQ.Socket m_frontEnd = null;
    ZMQ.Socket m_backEnd = null;
        
    Map<Integer, String> m_editors = null;
    Map<Integer, PublisherThread> m_publishers = null;
    
    BlockingQueue<Integer> m_publishingQueue = null;
    
    Queue<byte[]> m_availableWorkers = null;
    
    public Broker()
    {
        m_editors = new ConcurrentHashMap<>();
        m_publishers = new ConcurrentHashMap<>();
        
        m_availableWorkers = new LinkedList<>();
        
        m_publishingQueue = new LinkedBlockingQueue<>();
        
        
    }
    
    public void initialize()
    {
        m_context = ZMQ.context(1);

        System.out.println("Binding frontend socket on localhost. Port: " + Ports.MAIN_PORT);
        m_frontEnd = m_context.socket(ZMQ.ROUTER);
        m_backEnd = m_context.socket(ZMQ.ROUTER);
        
        m_frontEnd.bind("tcp://*:" + Ports.MAIN_PORT);
        m_backEnd.bind("ipc://backend.chem");
        
        for (int workerNbr = 0; workerNbr < NBR_WORKERS; workerNbr++)
            new WorkerThread(m_context, this).start();
    }
    
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            System.out.println("Ready to receive commands...");
            
            ZMQ.Poller polledSockets = new ZMQ.Poller(2);
            
            polledSockets.register(m_backEnd, ZMQ.Poller.POLLIN);
            
            // Poll front-end only if we have free workers to do the job
            if (m_availableWorkers.size() > 0)
                polledSockets.register(m_frontEnd, ZMQ.Poller.POLLIN);
            
            if (polledSockets.poll() < 0)
                break;
            
            if (polledSockets.pollin(0))
            {
                m_availableWorkers.add(m_backEnd.recv());
                
                String empty = m_backEnd.recvStr();
                byte[] clientAddress = m_backEnd.recv();
                
                String clientAddrStr = new String(clientAddress, ZMQ.CHARSET);
                
                if (!clientAddrStr.equals("READY"))
                {
                    empty = m_backEnd.recvStr();
                    String reply = m_backEnd.recvStr();
                    
                    m_frontEnd.sendMore(clientAddress);
                    m_frontEnd.sendMore(empty);
                    m_frontEnd.send(reply);
                }
            }
            
            if (polledSockets.pollin(1))
            {
                byte[] clientAddress = m_frontEnd.recv();
                String empty = m_frontEnd.recvStr();
                String header = m_frontEnd.recvStr();
                String content = m_frontEnd.recvStr();
                
                byte[] workerAddress = m_availableWorkers.poll();
                
                m_backEnd.sendMore(workerAddress);
                m_backEnd.sendMore(empty);
                m_backEnd.sendMore(clientAddress);
                m_backEnd.sendMore(empty);
                m_backEnd.sendMore(header);
                m_backEnd.send(content);
            }
        }
        
        m_frontEnd.close();
        m_backEnd.close();
        m_context.term();
        System.out.println("Shutting down...");
    }
    
    public synchronized Task createTask(byte[] address, String messageHeader, String messageBody)
    {
        MessageType type = MessageType.valueOf(messageHeader);
        switch (type)
        {
            case GET_DOCS:
            {
                return new GetDocsTask(getEditedDocs());
            }
            
            case CHECK_EDITOR:
            {
                return new CheckEditorTask(messageBody, m_editors);
            }
            
            case LOAD_DOC_EDITOR:
            {
                int docId = Integer.parseInt(messageBody);
                
                editorConnected(address, docId);
                
                return new LoadDocumentTask(docId);
            }

            case LOAD_DOC_VIEWER:
            {
                int docId = Integer.parseInt(messageBody);

                viewerConnected(address, docId);
                
                return new LoadDocumentTask(docId);
            }

            case SAVE_DOC:
            {
                return new SaveDocumentTask(messageBody);
            }
            
            case SAVE_ATOMS:
            {
                
                
                return null;
            }
            
            case SAVE_BONDS:
            {
                
                return null;
            }
            
            case DISC_EDITOR:
            {
                int docId = Integer.parseInt(messageBody);
                
                editorDisconnected(docId);
                
                return null;
            }
            
            case DISC_VIEWER:
            {
                int docId = Integer.parseInt(messageBody);
                
                viewerDisconnected(docId);
                
                return null;
            }
            
            default:
            {
                return null;
            }
        }
    }
    
    public synchronized void editorConnected(byte[] address, int docId)
    {
        m_editors.put(docId, new String(address, ZMQ.CHARSET));

        //PublisherThread pb = new PublisherThread(m_context, docId);
        //m_publishers.put(docId, pb);
        //pb.start();
    }
    
    public synchronized void viewerConnected(byte[] address, int docId)
    {
//        if (m_viewers.containsKey(docId))
//        {
//            List<byte[]> addresses = m_viewers.get(docId);
//            addresses.add(address);
//        }
//        else
//        {
//            List<byte[]> list = new ArrayList<>();
//            list.add(address);
//            m_viewers.put(docId, list);
//        }
    }
    
    public synchronized void editorDisconnected(int docId)
    {
        m_editors.remove(docId);
        
        //PublisherThread pub = m_publishers.get(docId);
        //pub.end();
        //m_publishers.remove(docId);
    }
    
    public synchronized void viewerDisconnected(int docId)
    {
    }
    
    public synchronized Set<Integer> getEditedDocs()
    {
        return m_editors.keySet();
    }
}
