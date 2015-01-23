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
    private ZMQ.Socket m_connectSocket = null;
    private DocumentReceiver m_receiver = null;
    
    private boolean m_userConnected = false;
    
    public ConnectThread(ZMQ.Context context, DocumentReceiver receiver)
    {
        m_connectSocket = context.socket(ZMQ.DEALER);
        m_receiver = receiver;
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
            PollItem[] polledConnections = new PollItem[] { new PollItem(m_connectSocket, Poller.POLLIN) };

            m_connectSocket.send(String.format("GET_DOCS"), 0);
            
            try
            {
                ObjectMapper om = new ObjectMapper();
                
                while (!m_userConnected)
                {
                    ZMQ.poll(polledConnections, 50);
                    if (polledConnections[0].isReadable())
                    {
                        ZMsg msg = ZMsg.recvMsg(m_connectSocket);
                        ZFrame content = msg.pop();
                        
                        String[] jsons = content.toString().split("\\$");
                        List<DocumentModel> resultList = new ArrayList<>();
                        
                        for (String json : jsons)
                        {
                            DocumentModel dm = om.readValue(json, DocumentModel.class);
                            resultList.add(dm);
                            System.out.println("Received " + dm.getName() + " ID: " + dm.getId());
                        }
                        
                        msg.destroy();
                        
                        m_receiver.setDocumentModelList(resultList);
                        m_userConnected = true;
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        
        m_connectSocket.close();
    }

}
