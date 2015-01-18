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
        
        int x = 1;
        
        AtomModel am = new AtomModel();
        am.setDocumentId(1);
        am.setType("Sexy");
        am.setX(20);
        am.setY(50);
        am.setId(123456);
        
        ObjectMapper om = new ObjectMapper();
        String str = om.writeValueAsString(am);
        
        

        //  Socket to talk to clients
        ZMQ.Socket responder = context.socket(ZMQ.REP);
        responder.bind("tcp://*:5555");

        int z = 1000;
        while (z > 0) {
            // Wait for next request from the client
            byte[] request = responder.recv(0);
            System.out.println("Received Hello");

            // Do some 'work'
            //Thread.sleep(1000);

            // Send reply back to client
            String reply = "Hi " + x++;
            //responder.send(String.format("%03d", 123), ZMQ.SNDMORE);
            responder.send(str.getBytes());
            
            System.out.println("Sent " + reply);
            
            z--;
        }
        responder.close();
        context.term();
    }
    
}
