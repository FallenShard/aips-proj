/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures.persist;

import java.io.Serializable;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ChemicalBondModel implements Serializable, Persistable
{
    private int id = -1;
    private int startElectronId;
    private int endElectronId;
    private int documentId;
    
    public ChemicalBondModel()
    {
        startElectronId = -1;
        endElectronId = -1;
    }
    
    public ChemicalBondModel(int startElectronId, int endElectronId)
    {
        this.startElectronId = startElectronId;
        this.endElectronId = endElectronId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartElectronId() {
        return startElectronId;
    }

    public void setStartElectronId(int startElectronId) {
        this.startElectronId = startElectronId;
    }

    public int getEndElectronId() {
        return endElectronId;
    }

    public void setEndElectronId(int endElectronId) {
        this.endElectronId = endElectronId;
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
//            Query query = session.createQuery("from ChemicalBondModel cb where cb.id = " + id);
//            Object obj = query.list().get(0);
//            
//            ChemicalBondModel persBond = (ChemicalBondModel)obj;
//
//            session.beginTransaction();
//            session.saveOrUpdate(persBond);
//            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Session session)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
