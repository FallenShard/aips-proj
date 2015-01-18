/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.network;

import chem.figures.persist.DocumentModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zeromq.ZMQ;

/**
 *
 * @author FallenShard
 */
public class ConnectThread extends Thread
{
    private ZMQ.Socket m_connectSocket = null;
    
    private boolean m_userConnected = false;
    
    public ConnectThread(ZMQ.Context context)
    {
        m_connectSocket = context.socket(ZMQ.REQ);
        
    }
    
    @Override
    public void start()
    {
        if (!m_userConnected)
        {
            m_connectSocket.connect("tcp://localhost:" + 8888);
            
            super.start();
        }
    }

    @Override
    public void run()
    {
        while (!m_userConnected)
        {
            m_connectSocket.send("GET_DOCS");

            try
            {
                ObjectMapper om = new ObjectMapper();
                
                while (true)
                {
                    String doc = m_connectSocket.recvStr(0);
                    
                    if (doc.equalsIgnoreCase("END"))
                        break;
                                        
                    DocumentModel dm = om.readValue(doc, DocumentModel.class);
                    
                    System.out.println("Received " + dm.getName() + " ID: " + dm.getId());
                    
                    m_connectSocket.send("Success: " + dm.getName());
                }
                
                System.out.println("Received all documents.");
                
                m_userConnected = true;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        
        m_connectSocket.close();
    }

}
