/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.zeromq.ZMQ;
import protocol.MessageType;
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
    public static final int MAIN_PORT = 8888;
    
    ZMQ.Context m_context = null;
    ZMQ.Socket m_routerSocket = null;
        
    Map<Integer, String> m_editors = null;
    Map<Integer, PublisherThread> m_publishers = null;
    
    public Broker()
    {
        m_editors = new ConcurrentHashMap<>();
        m_publishers = new ConcurrentHashMap<>();
    }
    
    public void initialize()
    {
        m_context = ZMQ.context(1);
        m_routerSocket = m_context.socket(ZMQ.ROUTER);
        
        m_routerSocket.bind("tcp://*:" + MAIN_PORT);
        System.out.println("Binding a ROUTER socket on localhost. Port: " + MAIN_PORT);
    }
    
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            System.out.println("Ready to receive commands...");
            
            byte[] address = m_routerSocket.recv();
            String empty = m_routerSocket.recvStr();
            String header = m_routerSocket.recvStr();
            String content = m_routerSocket.recvStr();
            String result = "Default message";
            
            Task task = createTask(address, header, content);
            if (task != null)
            {
                task.run();
                result = task.getResult();
            }

            m_routerSocket.sendMore(address);
            m_routerSocket.sendMore(empty);
            m_routerSocket.send(result);
            
            System.out.println("Sent: " + result);
            System.out.println("Length: " + result.length() + " bytes");
        }
        
        m_routerSocket.close();
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

        PublisherThread pb = new PublisherThread(m_context, docId);
        m_publishers.put(docId, pb);
        pb.start();
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
        
        PublisherThread pub = m_publishers.get(docId);
        pub.end();
        m_publishers.remove(docId);
    }
    
    public synchronized void viewerDisconnected(int docId)
    {
    }
    
    public synchronized Set<Integer> getEditedDocs()
    {
        return m_editors.keySet();
    }
}
