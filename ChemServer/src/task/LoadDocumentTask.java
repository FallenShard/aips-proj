/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import persist.AtomModel;
import persist.ChemicalBondModel;
import persist.DocumentModel;
import persist.ElectronModel;
import chemserver.HibernateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class LoadDocumentTask implements Task
{
    private int m_docId = -1;
    private String m_result = "";
    
    public LoadDocumentTask(int docId)
    {
        m_docId = docId;
    }

    @Override
    public void run()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            StringBuilder docContent = new StringBuilder();
            
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from DocumentModel d where d.id = " + m_docId);
            DocumentModel docModel = (DocumentModel)query.list().get(0);
            
            docContent.append(mapper.writeValueAsString(docModel));
            docContent.append("*");
            
            query = session.createQuery("from AtomModel a where a.documentId = " + m_docId);
            List<AtomModel> atomList = query.list();
            for (AtomModel am : atomList)
            {
                String json = mapper.writeValueAsString(am);
                docContent.append(json);
                
                query = session.createQuery("from ElectronModel e where e.atomId = " + am.getId() + " ORDER BY e.index ASC");
                List<ElectronModel> electronList = query.list();
                
                for (ElectronModel em : electronList)
                {
                    docContent.append("@");
                    docContent.append(mapper.writeValueAsString(em));
                }
                
                docContent.append("$");
            }
            docContent.append("*");
            
            query = session.createQuery("from ChemicalBondModel cb where cb.documentId = " + m_docId);
            List<ChemicalBondModel> bondList = query.list();
            for (ChemicalBondModel bm : bondList)
            {
                docContent.append(mapper.writeValueAsString(bm));
                docContent.append("$");
            }
            
            m_result = docContent.toString();
        } 
        catch (JsonProcessingException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public String getResult()
    {
        return m_result;
    }
}
