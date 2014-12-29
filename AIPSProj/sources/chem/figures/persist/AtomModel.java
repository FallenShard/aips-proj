/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures.persist;

import java.io.Serializable;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class AtomModel implements Serializable, Persistable
{
    private int id = -1;
    
    private int x;
    private int y;
    private String type;
    
    private int documentId;
    
    public AtomModel()
    {
        x = 0;
        y = 0;
        type = "";
    }
    
    public AtomModel(String type, int x, int y)
    {
        this.type = type;
        this.x = x;
        this.y = y;
    }
    
    public AtomModel(String type, int x, int y, int documentId)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.documentId = documentId;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    @Override
    public void save(Session session, int documentId)
    {
        // If id is -1, we're saving for the first time, set docId
        if (id == -1 || documentId == -1)
        {
            this.id = -1;
            this.documentId = documentId;
            session.beginTransaction();
            session.save(this);
            session.getTransaction().commit();
        }
        else
        {
            // Otherwise, pull from database and refresh
            Query query = session.createQuery("from AtomModel a where a.id = " + id);
            Object obj = query.list().get(0);
            
            AtomModel persAtom = (AtomModel)obj;
            persAtom.x = x;
            persAtom.y = y;
            persAtom.type = type;
            
            session.beginTransaction();
            session.saveOrUpdate(persAtom);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Session session)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
