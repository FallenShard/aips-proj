/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import java.util.HashMap;
import java.util.Map;
import org.zeromq.ZMQ;

/**
 *
 * @author FallenShard
 */
public class Dispatcher
{
    public static final int WELCOME_PORT = 8888;
    
    ZMQ.Context m_context = null;
    
    WelcomeThread m_welcomeThread = null;
    
    Map<Integer, Boolean> m_editedDocs = new HashMap<>();
    
    public Dispatcher()
    {
    }
    
    public void initialize()
    {
        m_context = ZMQ.context(1);
        
        m_welcomeThread = new WelcomeThread(m_context, this);
    }
    
    public boolean isAvailableToEdit(int docId)
    {
        return m_editedDocs.get(docId);
    }
    
    public void addEditor(int docId)
    {
        m_editedDocs.put(docId, false);
    }

}
