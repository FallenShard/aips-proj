/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chemserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.zeromq.ZMQ;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.zeromq.ZFrame;
import org.zeromq.ZMsg;

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
        
        try
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            Query query = session.createQuery("from DocumentModel d");
            List<DocumentModel> docObj = query.list();
            
            session.close();
            
            ObjectMapper om = new ObjectMapper();
            //  Socket to talk to clients
            ZMQ.Socket responder = context.socket(ZMQ.ROUTER);
            responder.bind("tcp://*:8888");
            
            System.out.println("WAITING TO GET_DOCS");
            
            while (true)
            {
                ZMsg msg = ZMsg.recvMsg(responder);
                ZFrame address = msg.pop();
                ZFrame content = msg.pop();
                msg.destroy();

                if (content.toString().equalsIgnoreCase("GET_DOCS"))
                {
                    StringBuilder response = new StringBuilder();
                    for (DocumentModel dm : docObj)
                    {
                        String json = om.writeValueAsString(dm);
                        response.append(json).append("$");
                    }
                    System.out.println(response.toString());
                    content = new ZFrame(response.toString());

                    address.send(responder, ZFrame.MORE);
                    content.send(responder, 0);

                    address.destroy();
                    content.destroy();
                }
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
