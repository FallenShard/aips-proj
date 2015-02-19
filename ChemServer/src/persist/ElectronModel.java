/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persist;

import java.io.Serializable;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ElectronModel implements Serializable, Persistable
{
    private int id = -1;
    
    private int atomX;
    private int atomY;
    private int index;
    
    private int angle;
    
    private int documentId;
    
    public ElectronModel()
    {
        angle = 0;
        index = -1;
    }
    
    public ElectronModel(int angle)
    {
        this.angle = angle;
    }
    
    public ElectronModel(int angle, int atomX, int atomY)
    {
        this.angle = angle;
        this.atomX = atomX;
        this.atomY = atomY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAtomX() {
        return atomX;
    }

    public void setAtomX(int atomX) {
        this.atomX = atomX;
    }

    public int getAtomY() {
        return atomY;
    }

    public void setAtomY(int atomY) {
        this.atomY = atomY;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
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
        if (id == -1)
        {
            saveAs(session, documentId);
        }
        else
        {
            // Otherwise, pull from database and refresh
            Query query = session.createQuery("from ElectronModel e where e.id = " + id);
            Object obj = query.list().get(0);
            
            ElectronModel persEl = (ElectronModel)obj;
            persEl.angle = this.angle;
            persEl.atomX = this.atomX;
            persEl.atomY = this.atomY;
            
            session.beginTransaction();
            session.saveOrUpdate(persEl);
            session.getTransaction().commit();
        }
    }

    @Override
    public void saveAs(Session session, int documentId)
    {
        this.id = -1;
        this.documentId = documentId;
        session.beginTransaction();
        session.save(this);
        session.getTransaction().commit();
    }

    @Override
    public void delete(Session session)
    {
    }
}
