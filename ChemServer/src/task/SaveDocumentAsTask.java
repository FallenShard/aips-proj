/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import chemserver.HibernateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.hibernate.Session;
import org.zeromq.ZMQ;
import persist.AtomModel;
import persist.BondModel;
import persist.DocumentModel;
import persist.ElectronModel;
import persist.Persistable;

/**
 *
 * @author FallenShard
 */
public class SaveDocumentAsTask implements Task
{
    private final String m_jsonDoc;
    private final byte[] m_address;
    Map<Integer, String> m_editors;
    private String m_result;
    
    public SaveDocumentAsTask(String jsonDoc, byte[] address, Map<Integer, String> editors)
    {
        m_jsonDoc = jsonDoc;
        m_address = address;
        m_editors = editors;
    }
    
    private Persistable unpackModel(ObjectMapper mapper, String json, String classCode) throws IOException
    {
        switch (classCode)
        {
            case "A":
                return mapper.readValue(json, AtomModel.class);
            case "E":
                return mapper.readValue(json, ElectronModel.class);
            case "B":
                return mapper.readValue(json, BondModel.class);
        }
        
        return null;
    }
    

    @Override
    public void run()
    {
        try 
        {
            // Mapper can generate objects from strings and vice-versa
            ObjectMapper mapper = new ObjectMapper();
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // The incoming string will be half delete data and half json, throw away delete data
            String[] deleteSaveData = m_jsonDoc.split("%%%");
            
            // First of all, split everything in the form of json on even index + class initial on odd index
            String[] splitData = deleteSaveData[1].split("@");
            
            // Deserialize document and save it to database
            DocumentModel docModel = mapper.readValue(splitData[0], DocumentModel.class);
            docModel.saveAs(session, docModel.getId());
            int docId = docModel.getId();
            
            for (int i = 2; i < splitData.length; i += 2)
            {
                Persistable model = unpackModel(mapper, splitData[i], splitData[i + 1]);
                model.saveAs(session, docId);
            }
            
            session.close();
            
            m_editors.put(docId, new String(m_address, ZMQ.CHARSET));
            
            Task task = new LoadDocumentTask(docId);
            task.run();

            m_result = task.getResult();
            return;
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        }
        
        m_result = "Failed";
    }

    @Override
    public String getResult()
    {
        return m_result;
    }
}
