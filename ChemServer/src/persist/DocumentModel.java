/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persist;

import java.io.Serializable;
import java.util.Date;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class DocumentModel implements Serializable, Persistable
{
    private int id = -1;
    private String name;
    private Date timestamp;
    
    public DocumentModel()
    {
        name = "Untitled";
        timestamp = new Date();
    }
    
    public DocumentModel(String name, Date timestamp)
    {
        this.name = name;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "Name : " + name + " id: " + id;
    }

    @Override
    public void save(Session session, int documentId)
    {        
        // If id is -1, we're saving for the first time
        if (id == -1)
        {
            saveAs(session, documentId);
        }
        else
        {
            // Otherwise, pull from database and refresh
            Query query = session.createQuery("from DocumentModel d where d.id = " + id);
            Object doc = query.list().get(0);
            
            DocumentModel persDoc = (DocumentModel)doc;
            persDoc.setName(name);
            persDoc.setTimestamp(new Date());
            
            session.beginTransaction();
            session.saveOrUpdate(persDoc);
            session.getTransaction().commit();
        }
    }

    @Override
    public void saveAs(Session session, int documentId)
    {
        this.timestamp = new Date();
        this.id = documentId;
        session.beginTransaction();
        session.save(this);
        session.getTransaction().commit();
    }

    @Override
    public void delete(Session session)
    {
    }
}
