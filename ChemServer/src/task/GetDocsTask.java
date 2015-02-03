/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import persist.DocumentModel;
import chemserver.HibernateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class GetDocsTask implements Task
{
    private String m_result = "";
    private Set<Integer> m_openedDocs = null;
    
    public GetDocsTask(Set<Integer> openedDocs)
    {
        m_openedDocs = openedDocs;
    }

    @Override
    public void run()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
            
        Query query = session.createQuery("from DocumentModel d");
        List<DocumentModel> docObj = query.list();
            
        session.close();
            
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder jsonPack = new StringBuilder();
        for (DocumentModel dm : docObj)
        {
            try
            {
                String json = mapper.writeValueAsString(dm);
                jsonPack.append(json);
                
                if (m_openedDocs.contains(dm.getId()))
                {
                    jsonPack.append("$E$");
                }
                else
                {
                    jsonPack.append("$F$");
                }
            }
            catch (JsonProcessingException ex)
            {
                ex.printStackTrace();
            }
        }
        
        m_result = jsonPack.toString();
    }
    
    @Override
    public String getResult()
    {
        return m_result;
    }
}
