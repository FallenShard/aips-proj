/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.network;

import chem.figures.persist.DocumentModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMsg;

/**
 *
 * @author FallenShard
 */
public class ConnectThread extends Thread
{
    private Random rand = new Random(System.nanoTime());
    private ZMQ.Socket m_connectSocket = null;
    private DocumentReceiver m_receiver = null;
    
    private volatile boolean m_isRunning = false;
    
    public ConnectThread(ZMQ.Context context, DocumentReceiver receiver)
    {
        m_connectSocket = context.socket(ZMQ.REQ);
        m_receiver = receiver;
    }
    
    @Override
    public void start()
    {
        if (!m_isRunning)
        {
            m_connectSocket.connect("tcp://localhost:" + 8888);
            //String id = String.format("%04X-%04X", rand.nextInt(), rand.nextInt());
            //m_connectSocket.setIdentity(id.getBytes());
            
            m_isRunning = true;
            super.start();
        }
    }
    
    public void end()
    {
        if (m_isRunning)
        {
            m_isRunning = false;
            m_connectSocket.close();
        }
    }

    @Override
    public void run()
    {
        try
        {
            ObjectMapper om = new ObjectMapper();

            while (m_isRunning)
            {
                m_connectSocket.sendMore("GET_DOCS");
                m_connectSocket.send("");
                String content = m_connectSocket.recvStr();

                String[] jsons = content.split("\\$");
                List<DocumentModel> freeDocsList = new ArrayList<>();
                List<DocumentModel> openedDocsList = new ArrayList<>();
                
                for (int i = 0; i < jsons.length; i += 2)
                {
                    DocumentModel dm = om.readValue(jsons[i], DocumentModel.class);
                    if (jsons[i + 1].equalsIgnoreCase("E"))                
                        openedDocsList.add(dm);
                    else if (jsons[i + 1].equalsIgnoreCase("F"))
                        freeDocsList.add(dm);
                    
                    System.out.println("Received " + dm.getName() + " ID: " + dm.getId() + " Status: " + jsons[i + 1]);
                }

                m_receiver.setDocumentModelList(freeDocsList, openedDocsList);
                m_isRunning = false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        m_connectSocket.close();
    }
}
