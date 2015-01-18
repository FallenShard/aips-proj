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
            ZMQ.Socket responder = context.socket(ZMQ.REP);
            responder.bind("tcp://*:8888");
            
            System.out.println("WAITING TO GET_DOCS");
            byte[] req = responder.recv(0);
            String reqStr = new String(req);
            
            if (reqStr.equalsIgnoreCase("GET_DOCS"))
            {
                for (DocumentModel dm : docObj)
                {
                    String json = om.writeValueAsString(dm);
                    responder.send(json);
                    
                    String sendNext = responder.recvStr(0);
                    System.out.println(sendNext);
                }
            }
            
            responder.send("END");
            System.out.println("Send END string");
            

            responder.close();
            
            System.out.println("Shutting down...");
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
//        AtomModel am = new AtomModel();
//        am.setDocumentId(1);
//        am.setType("Sexy");
//        am.setX(20);
//        am.setY(50);
//        am.setId(123456);
//        
//        ObjectMapper om = new ObjectMapper();
//        String str = om.writeValueAsString(am);
//        byte[] bson = om.writeValueAsBytes(am);
//        
//
//        //  Socket to talk to clients
//        ZMQ.Socket responder = context.socket(ZMQ.REP);
//        responder.bind("tcp://*:5555");
//
//        int z = 1000;
//        while (z > 0) {
//            // Wait for next request from the client
//            byte[] request = responder.recv(0);
//            System.out.println("Received Hello");
//
//            // Do some 'work'
//            //Thread.sleep(1000);
//
//            // Send reply back to client
//            String reply = "Hi " + x++;
//            responder.send(String.format("%03d", 333), ZMQ.SNDMORE);
//            //responder.send(str.getBytes());
//            responder.send(bson);
//            
//            System.out.println("Sent " + reply);
//            
//            z--;
//        }
        
        context.term();
        
        System.out.println("Shutting down... for real");
    }
    
}
