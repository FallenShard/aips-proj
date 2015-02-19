/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import chemserver.HibernateUtil;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import org.hibernate.Query;
import org.hibernate.Session;
import persist.JsonModelFactory;
import persist.ModelFactory;
import persist.Persistable;

/**
 *
 * @author FallenShard
 */
public class SaveDocumentTask implements Task
{
    private final String m_jsonDoc;
    private BlockingQueue<Integer> m_pubQueue;
    
    private String m_result;
    private boolean m_shouldReload;
    
    public SaveDocumentTask(String jsonDoc, BlockingQueue<Integer> pubQueue)
    {
        m_jsonDoc = jsonDoc;
        m_pubQueue = pubQueue;
        m_shouldReload = false;
    }

    @Override
    public void run()
    {
        try 
        {
            // Mapper can generate objects from strings and vice-versa
            ModelFactory modelFactory = new JsonModelFactory();
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // The incoming string will be half delete data and half json
            String[] deleteSaveData = m_jsonDoc.split("%%%");
            
            // Get the individual delete entries
            if (!deleteSaveData[0].equals(""))
            {
                String[] deleteData = deleteSaveData[0].split("~");
                for (String entry : deleteData)
                {
                    String[] deleteEntry = entry.split("\\|");
                    int id = Integer.parseInt(deleteEntry[0]);
                    String elHql = "DELETE FROM " + deleteEntry[1] + " WHERE id = " + id;
                    Query query = session.createQuery(elHql);
                    int rows = query.executeUpdate();
                    System.out.println("Rows affected by " + deleteEntry[1] + " delete: " + rows);
                }
                
                m_shouldReload = true;
            }
            
            // First of all, split everything in the form of json on even index + class initial on odd index
            String[] splitData = deleteSaveData[1].split("@");
            
            // Deserialize document and save it to database
            Persistable docModel = modelFactory.unpackModel(splitData[0], splitData[1]);
            docModel.save(session, docModel.getId());
            int docId = docModel.getId();
            
            for (int i = 2; i < splitData.length; i += 2)
            {
                Persistable model = modelFactory.unpackModel(splitData[i], splitData[i + 1]);
                if (model.getId() == -1)
                    m_shouldReload = true;
                
                model.save(session, docId);
            }
            
            session.close();
            
            m_pubQueue.put(docId);
            
            if (m_shouldReload)
            {
                Task task = new LoadDocumentTask(docId);
                task.run();

                m_result = task.getResult();
            }
            else
            {
                m_result = "UpdateOnly";
            }
            
            return;
        }
        catch (IOException | InterruptedException ex)
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
