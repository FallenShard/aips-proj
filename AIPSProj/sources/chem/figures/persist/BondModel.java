/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures.persist;

import java.io.Serializable;

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
}
