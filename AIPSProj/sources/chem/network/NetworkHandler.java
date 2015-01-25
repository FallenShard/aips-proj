/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.network;

import org.zeromq.ZMQ;

/**
 *
 * @author FallenShard
 */
public class NetworkHandler
{
    ZMQ.Context m_context = null;
    
    ZMQ.Socket m_connectionSocket = null;
    
    public NetworkHandler()
    {
        m_context = ZMQ.context(1);
    }
    
    public void dispose()
    {
        m_context.term();
    }
    
    public ZMQ.Context getContext()
    {
        return m_context;
    }
    
    public ZMQ.Socket createSocket(int type)
    {
        return m_context.socket(type);
    }
}
