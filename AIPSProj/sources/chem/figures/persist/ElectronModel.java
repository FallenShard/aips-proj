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
public class ElectronModel implements Serializable, Persistable
{
    private int id = -1;
    private double angle;
    private int atomId;
    private int index;
    
    public ElectronModel()
    {
        angle = 0.0;
        atomId = -1;
        index = -1;
    }
    
    public ElectronModel(double angle)
    {
        this.angle = angle;
    }
    
    public ElectronModel(double angle, int atomId)
    {
        this.angle = angle;
        this.atomId = atomId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getAtomId() {
        return atomId;
    }

    public void setAtomId(int atomId) {
        this.atomId = atomId;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public void setIndex(int index)
    {
        this.index = index;
    }

    @Override
    public void save(Session session, int documentId)
    {
        // If id is -1, we're saving for the first time, set docId
        if (id == -1 || documentId == -1)
        {
            id = -1;
            session.beginTransaction();
            session.save(this);
            session.getTransaction().commit();
        }
        else
        {
            // Otherwise, pull from database and refresh
            Query query = session.createQuery("from ElectronModel e where e.id = " + id);
            Object obj = query.list().get(0);
            
            ElectronModel persEl = (ElectronModel)obj;
            persEl.angle = angle;
            
            session.beginTransaction();
            session.saveOrUpdate(persEl);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Session session) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveAs(Session session, int documentId)
    {
        id = -1;
        session.beginTransaction();
        session.save(this);
        session.getTransaction().commit();
    }
}
