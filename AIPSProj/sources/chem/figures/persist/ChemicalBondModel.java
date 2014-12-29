/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.figures.persist;

import java.io.Serializable;

/**
 *
 * @author Mefi
 */
public class ChemicalBondModel implements Serializable
{
    private int id;
    private int startElectronId;
    private int endElectronId;
    private int documentId;
    
    public ChemicalBondModel()
    {
        startElectronId = 0;
        endElectronId = 0;
        //documentId = -1;
    }
    
    public ChemicalBondModel(int startElectronId, int endElectronId, int documentId)
    {
        this.startElectronId = startElectronId;
        this.endElectronId = endElectronId;
        this.documentId = documentId;
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
}
