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
import java.util.concurrent.BlockingQueue;
import org.zeromq.ZMQ;
import protocol.MessageType;
import protocol.Ports;

/**
 *
 * @author FallenShard
 */
public class SaveThread extends Thread
{
    private ZMQ.Socket m_socket = null;
    private DrawApplication m_app = null;
    private BlockingQueue<Boolean> m_updateQueue = null;
    
    private volatile boolean m_isRunning = false;
    private volatile boolean m_isSaving = false;
    
    public SaveThread(ZMQ.Context context, DrawApplication app, BlockingQueue<Boolean> updateQueue)
    {
        m_app = app;
        
        m_socket = context.socket(ZMQ.REQ);
        
        m_updateQueue = updateQueue;
    }
    
    @Override
    public void start()
    {
        if (!m_isRunning)
        {
            m_socket.connect("tcp://localhost:" + Ports.MAIN_PORT);
            
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
                Boolean shouldSave = m_updateQueue.poll();
                
                if (shouldSave != null)
                {
                    m_isSaving = shouldSave;
                    if (m_isSaving)
                        System.out.println("Starting real-time saving!");
                    else
                        System.out.println("Disabling real-time saving!");
                }
                    

                if (m_isSaving)
                {
                    PersistableFigure doc = (PersistableFigure)(m_app.drawing());

                    StringBuilder packedJson = new StringBuilder();
                    doc.appendJson(packedJson, mapper);

                    String dataToSend = packedJson.toString();
                    m_socket.sendMore(MessageType.SAVE_DOC.getType());
                    m_socket.send(dataToSend);

                    String response = m_socket.recvStr();

                    if (response.equalsIgnoreCase("UpdateOnly"))
                    {
                        //System.out.println("Updated document!");
                    }
                    else if (response.equalsIgnoreCase("Failed"))
                    {
                        //m_app.showStatus("Failed to save document");
                    }
                    else
                    {
                        //m_app.showStatus("Document saved successfully");
                        DrawingLoader loader = new JsonLoader(response);
                        Drawing drawing = loader.createDrawing();
                        m_app.view().freezeView();
                        m_app.setDrawing(drawing);
                        m_app.view().checkDamage();
                        m_app.view().unfreezeView();
                        
                        //System.out.println("Saved document and loaded!");
                    }
                    
                    //System.out.println("Saved document! " + System.currentTimeMillis() % 1000);
                }
                
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
