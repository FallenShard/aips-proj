/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import chemserver.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class DeleteDocumentTask implements Task
{
    private int m_docId = -1;
    private String m_result = "";
    
    public DeleteDocumentTask(int docId)
    {
        m_docId = docId;
    }

    @Override
    public void run()
    {
        try 
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String hql = "DELETE FROM DocumentModel WHERE id = " + m_docId;
            Query query = session.createQuery(hql);
            query.executeUpdate();
            
            hql = "DELETE FROM AtomModel WHERE documentId = " + m_docId;
            query = session.createQuery(hql);
            query.executeUpdate();
            
            hql = "DELETE FROM ElectronModel WHERE documentId = " + m_docId;
            query = session.createQuery(hql);
            query.executeUpdate();
            
            hql = "DELETE FROM BondModel WHERE documentId = " + m_docId;
            query = session.createQuery(hql);
            query.executeUpdate();
            session.getTransaction().commit();
            session.close();
            
            System.out.println("Deleted document with id:" + m_docId);
            m_result = "Sucess";
            return;
        }
        catch (Exception ex)
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
