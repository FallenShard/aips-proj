/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import org.zeromq.ZMQ;

/**
 *
 * @author FallenShard
 */
public class WelcomeThread extends Thread
{
    private Dispatcher m_dispatcher = null;
    
    private ZMQ.Socket m_welcomeSocket = null;
    
    private boolean m_isRunning = false;

    public WelcomeThread(ZMQ.Context context, Dispatcher disp)
    {
        m_welcomeSocket = context.socket(ZMQ.REP);
        m_dispatcher = disp;
    }
    
    @Override
    public void start()
    {
        System.out.print("Server is up and running on port: " + Dispatcher.WELCOME_PORT);
        if (!m_isRunning)
        {
            m_welcomeSocket.bind("tcp://*:" + Dispatcher.WELCOME_PORT);
            
            m_isRunning = true;
            super.start();
        }
    }

    public void end()
    {
        if (m_isRunning)
        {
            m_isRunning = false;
            
            m_welcomeSocket.close();
        }
    }

    @Override
    public void run()
    {
        while (m_isRunning)
        {
            byte[] connRequest = m_welcomeSocket.recv(0);
            String docIdStr = new String(connRequest);
            
            switch (docIdStr)
            {
                case "ASD":
                    break;
            }

//            try
//            {
//                int docId = Integer.parseInt(docIdStr);
//                
//                if (m_dispatcher.isAvailableToEdit(docId))
//                {
//                    m_dispatcher.addEditor(docId);
//                    m_welcomeSocket.send("SUCCESS");
//                }
//                else
//                    m_welcomeSocket.send("FAIL");
//                
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
        }
    }
}
