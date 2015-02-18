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
public class BondModel implements Serializable, Persistable
{
    private int id = -1;
    
    private int startAtomX;
    private int startAtomY;
    private int startElectronIndex;
    
    private int endAtomX;
    private int endAtomY;
    private int endElectronIndex;
    
    private int documentId;
    
    public BondModel() 
    {
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartAtomX() {
        return startAtomX;
    }

    public void setStartAtomX(int startAtomX) {
        this.startAtomX = startAtomX;
    }

    public int getStartAtomY() {
        return startAtomY;
    }

    public void setStartAtomY(int startAtomY) {
        this.startAtomY = startAtomY;
    }

    public int getStartElectronIndex() {
        return startElectronIndex;
    }

    public void setStartElectronIndex(int startElectronIndex) {
        this.startElectronIndex = startElectronIndex;
    }

    public int getEndAtomX() {
        return endAtomX;
    }

    public void setEndAtomX(int endAtomX) {
        this.endAtomX = endAtomX;
    }

    public int getEndAtomY() {
        return endAtomY;
    }

    public void setEndAtomY(int endAtomY) {
        this.endAtomY = endAtomY;
    }

    public int getEndElectronIndex() {
        return endElectronIndex;
    }

    public void setEndElectronIndex(int endElectronIndex) {
        this.endElectronIndex = endElectronIndex;
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
        if (id == -1)
        {
            saveAs(session, documentId);
        }
        else
        {
            // Otherwise, pull from database and refresh
            Query query = session.createQuery("from BondModel b where b.id = " + id);
            Object obj = query.list().get(0);
            
            BondModel persBond = (BondModel)obj;
            persBond.startAtomX = this.startAtomX;
            persBond.startAtomY = this.startAtomY;
            persBond.startElectronIndex = this.startElectronIndex;
            persBond.endAtomX = this.endAtomX;
            persBond.endAtomY = this.endAtomY;
            persBond.endElectronIndex = this.endElectronIndex;
            
            session.beginTransaction();
            session.saveOrUpdate(persBond);
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
