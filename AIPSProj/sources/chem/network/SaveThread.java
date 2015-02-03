/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.network;

import CH.ifa.draw.application.DrawApplication;
import CH.ifa.draw.framework.Drawing;
import chem.db.DrawingLoader;
import chem.db.JsonLoader;
import chem.figures.persist.PersistableFigure;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zeromq.ZMQ;

/**
 *
 * @author FallenShard
 */
public class SaveThread extends Thread
{
    private ZMQ.Socket m_socket = null;
    private DrawApplication m_app = null;
    
    private volatile boolean m_isRunning = false;
    
    public SaveThread(ZMQ.Context context, DrawApplication app)
    {
        m_app = app;
        
        m_socket = context.socket(ZMQ.REQ);
    }
    
    @Override
    public void start()
    {
        if (!m_isRunning)
        {
            m_socket.connect("tcp://localhost:8888");
            
            super.start();
            m_isRunning = true;
        }
    }
    
    public void end()
    {
        m_isRunning = false;
    }
    
    @Override
    public void run()
    {
        ObjectMapper mapper = new ObjectMapper();
        
        while (m_isRunning)
        {
            try
            {
                //            long start = System.currentTimeMillis();
                
                PersistableFigure doc = (PersistableFigure)(m_app.drawing());
                
                StringBuilder packedJson = new StringBuilder();
                
                doc.appendJson(packedJson, mapper);
                
                String dataToSend = packedJson.toString();
//            System.out.println(dataToSend.length() + " Time: " + (System.currentTimeMillis() - start));
                m_socket.sendMore("SAVE_DOC");
                m_socket.send(dataToSend);
                
                String response = m_socket.recvStr();
                
                if (!response.equalsIgnoreCase("Failed"))
                {
                    m_app.showStatus("Document saved successfully");
                    DrawingLoader loader = new JsonLoader(response);
                    Drawing drawing = loader.createDrawing();
                    m_app.view().freezeView();
                    m_app.setDrawing(drawing);
                    m_app.view().checkDamage();
                    m_app.view().unfreezeView();
                    
                    System.out.println("Saved stuff!");
                }
                else
                    m_app.showStatus("Failed to save document");
                
                sleep(33);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
        
        m_socket.close();
    }
}
