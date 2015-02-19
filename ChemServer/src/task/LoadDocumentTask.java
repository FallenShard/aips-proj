/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import chemserver.HibernateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import persist.AtomModel;
import persist.BondModel;
import persist.DocumentModel;
import persist.ElectronModel;

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
            docContent.append("@D@");
            
            query = session.createQuery("from AtomModel a where a.documentId = " + m_docId);
            List<AtomModel> atomList = query.list();
            for (AtomModel atomModel : atomList)
            {
                String json = mapper.writeValueAsString(atomModel);
                docContent.append(json);
                docContent.append("@A@");
                
                query = session.createQuery("from ElectronModel e where e.atomX = " + atomModel.getX() + " and e.atomY = " + atomModel.getY()
                        + "and e.documentId = " + m_docId  + " ORDER BY e.index ASC");
                List<ElectronModel> electronList = query.list();
                
                for (ElectronModel em : electronList)
                {
                    docContent.append(mapper.writeValueAsString(em));
                    docContent.append("@E@");
                }
            }
            
            query = session.createQuery("from BondModel bond where bond.documentId = " + m_docId);
            List<BondModel> bondList = query.list();
            for (BondModel bm : bondList)
            {
                docContent.append(mapper.writeValueAsString(bm));
                docContent.append("@B@");
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
