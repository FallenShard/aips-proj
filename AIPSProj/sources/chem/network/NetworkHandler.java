/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.network;

import org.zeromq.ZMQ;
import protocol.MessageType;
import protocol.Ports;

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
    
    public String saveDocument(String docData, boolean saveAs)
    {
        ZMQ.Socket saveSocket = createSocket(ZMQ.REQ);
        saveSocket.connect("tcp://localhost:" + Ports.MAIN_PORT);
        
        if (saveAs)
            saveSocket.sendMore(MessageType.SAVE_DOC_AS.getType());
        else
            saveSocket.sendMore(MessageType.SAVE_DOC.getType());
        
        saveSocket.send(docData);

        String response = saveSocket.recvStr();
        saveSocket.close();
            
        return response;
    }
    
    public String loadDocument(int docId, MessageType messageType)
    {
        ZMQ.Socket loadSocket = createSocket(ZMQ.REQ);
        loadSocket.connect("tcp://localhost:" + Ports.MAIN_PORT);
        loadSocket.sendMore(messageType.getType());
        loadSocket.send("" + docId);

        String response = loadSocket.recvStr();
        loadSocket.close();
        
        return response;
    }
    
    public void deleteDocument(int docId)
    {
        ZMQ.Socket delSocket = createSocket(ZMQ.REQ);
        delSocket.connect("tcp://localhost:" + Ports.MAIN_PORT);
        delSocket.sendMore(MessageType.DELETE_DOC.getType());
        delSocket.send("" + docId);

        delSocket.recvStr();
        delSocket.close();
    }
    
    public void disconnect(int docId, MessageType messageType)
    {
        ZMQ.Socket discSocket = createSocket(ZMQ.REQ);
        discSocket.connect("tcp://localhost:" + Ports.MAIN_PORT);
        discSocket.sendMore(messageType.getType());
        discSocket.send("" + docId);

        discSocket.recvStr(ZMQ.DONTWAIT);
        discSocket.close();
    }
}
