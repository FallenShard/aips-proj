/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import chemserver.HibernateUtil;
import persist.DocumentModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hibernate.Session;
import persist.AtomModel;
import persist.ChemicalBondModel;
import persist.ElectronModel;

/**
 *
 * @author FallenShard
 */
public class SaveDocumentTask implements Task
{
    private final String m_jsonDoc;
    private String m_result;
    
    public SaveDocumentTask(String jsonDoc)
    {
        m_jsonDoc = jsonDoc;
    }

    @Override
    public void run()
    {
        try 
        {
            // Mapper can generate objects from strings and vice-versa
            ObjectMapper mapper = new ObjectMapper();
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // First of all, split on per-class basis
            String[] firstSplit = m_jsonDoc.split("\\*");
            
            // Deserialize document and save it to database
            DocumentModel docModel = mapper.readValue(firstSplit[0], DocumentModel.class);
            docModel.save(session, docModel.getId());
            int docId = docModel.getId();
            
            // This split will contain atom and electron data on per-atom basis
            String[] atomData = firstSplit[1].split("\\$");
            
            
            for (String atomJson : atomData)
            {
                String[] individualAtomData = atomJson.split("@");
                
                AtomModel atomModel = mapper.readValue(individualAtomData[0], AtomModel.class);
                atomModel.save(session, docId);
                
                for (int i = 1; i < individualAtomData.length; i++)
                {
                    ElectronModel elModel = mapper.readValue(individualAtomData[i], ElectronModel.class);
                    elModel.setAtomId(atomModel.getId());
                    elModel.save(session, docId);
                }
            }
            
            if (firstSplit.length > 2)
            {
                String[] bondData = firstSplit[2].split("\\$");
                for (String bond : bondData)
                {
                   ChemicalBondModel bondModel = mapper.readValue(bond, ChemicalBondModel.class);
                   bondModel.save(session, docId);
                }
            }
            
            session.close();
            
            
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
