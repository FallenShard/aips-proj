/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.zeromq.ZMQ;
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
    Map<Integer, List<byte[]>> m_viewers = null;
    Map<Integer, PublisherThread> m_publishers = null;
    
    public Broker()
    {
        m_editors = new ConcurrentHashMap<>();
        m_viewers = new ConcurrentHashMap<>();
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
            String result = "";
            
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
    
    public Task createTask(byte[] address, String messageHeader, String messageBody)
    {
        switch (messageHeader)
        {
            case "GET_DOCS":
            {
                return new GetDocsTask();
            }
            
            case "CHECK_EDITOR":
            {
                return new CheckEditorTask(messageBody, m_editors);
            }
            
            case "LOAD_DOC_EDITOR":
            {
                int docId = Integer.parseInt(messageBody);
                m_editors.put(docId, new String(address, ZMQ.CHARSET));

                PublisherThread pb = new PublisherThread(m_context, docId);
                m_publishers.put(docId, pb);
                pb.start();
                
                return new LoadDocumentTask(docId);
            }
            
            case "REFRESH_EDITOR":
            {
                
            }
            
            case "LOAD_DOC_VIEWER":
            {
                int docId = Integer.parseInt(messageBody);

                if (m_viewers.containsKey(docId))
                {
                    List<byte[]> addresses = m_viewers.get(docId);
                    addresses.add(address);
                }
                else
                {
                    List<byte[]> list = new ArrayList<>();
                    list.add(address);
                    m_viewers.put(docId, list);
                }
                
                return new LoadDocumentTask(docId);
            }

            case "SAVE_DOC":
            {
                return new SaveDocumentTask(messageBody);
            }
            
            default:
            {
                return null;
            }
        }
    }
}
