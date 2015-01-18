/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import java.io.Serializable;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ChemicalBondModel implements Serializable
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
}
