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
}
