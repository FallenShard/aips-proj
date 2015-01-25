/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chemserver;

import persist.DocumentModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.zeromq.ZMQ;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.Query;
import org.hibernate.Session;
import task.*;

/**
 *
 * @author FallenShard
 */
public class ChemServer {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        
        ZMQ.Context context = ZMQ.context(1);
        
        Map<Integer, String> m_editors = new ConcurrentHashMap<>();
        Map<Integer, List<byte[]>> m_viewers = new ConcurrentHashMap<>();
        
        Map<Integer, PublisherThread> m_publishers = new ConcurrentHashMap<>();
        
        try
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            Query query = session.createQuery("from DocumentModel d");
            List<DocumentModel> docObj = query.list();
            
            session.close();
            
            ObjectMapper om = new ObjectMapper();
            
            //  Socket to talk to clients
            ZMQ.Socket broker = context.socket(ZMQ.ROUTER);
            
            broker.bind("tcp://*:8888");
            System.out.println("Binding a ROUTER socket on port localhost 8888");
            
            while (true)
            {
                System.out.println("Ready to receive commands...");
                byte[] address = broker.recv();
                String empty = broker.recvStr();
                String header = broker.recvStr();
                String content = broker.recvStr();
                String result = "";
                
                switch (header)
                {
                    case "GET_DOCS":
                    {
                        Task task = new GetDocsTask();
                        task.run();
                        result = task.getResult();
                        break;                        
                    } 
                    
                    case "CHECK_EDITOR":
                    {
                        Task task = new CheckEditorTask(content, m_editors);
                        task.run();
                        result = task.getResult();
                        break;
                    }
                    
                    case "LOAD_DOC_EDITOR":
                    {
                        int docId = Integer.parseInt(content);
                        m_editors.put(docId, new String(address));
                        Task task = new LoadDocumentTask(docId);
                        task.run();
                        result = task.getResult();
                        
                        PublisherThread pb = new PublisherThread(context, docId);
                        m_publishers.put(docId, pb);
                        pb.start();
                        
                        break;
                    }
                    
                    case "LOAD_DOC_VIEWER":
                    {
                        int docId = Integer.parseInt(content);
                        Task task = new LoadDocumentTask(docId);
                        task.run();
                        result = task.getResult();
                        
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
                        break;
                    }
                    
                    case "SAVE_DOC":
                    {
                        Task task = new SaveDocumentTask(content);
                        task.run();
                        result = task.getResult();
                        break;
                    }
                }

                broker.sendMore(address);
                broker.sendMore(empty);
                broker.send(result);
                
                System.out.println("Sent: " + result);
                System.out.println("Length: " + result.length() + " bytes");
            }
            
            
           // responder.close();
            //System.out.println("Shutting down...");
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
        context.term();
        
        System.out.println("Shutting down... for real");
    }
    
}
