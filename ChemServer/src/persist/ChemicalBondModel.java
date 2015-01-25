/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persist;

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
    public String toString()
    {
        return "Id: " + id + " start: " + startElectronId + " end: " + endElectronId;
    }

    @Override
    public void save(Session session, int documentId)
    {
        if (id == -1)
        {
            saveAs(session, documentId);
        }
        // else, don't touch the database, no need
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
