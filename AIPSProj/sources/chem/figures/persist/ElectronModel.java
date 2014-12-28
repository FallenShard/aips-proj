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
public class ElectronModel implements Serializable
{
    private int id;
    private double angle;
    private int atomId;
    private int bondId;
    private int otherElectronId;
    
    public ElectronModel()
    {
        angle = 0.0;
    }
    
    public ElectronModel(double angle)
    {
        this.angle = angle;
    }
    
    public ElectronModel(double angle, int atomId, int bondId, int otherElectronId)
    {
        this.angle = angle;
        this.atomId = atomId;
        this.bondId = bondId;
        this.otherElectronId = otherElectronId;
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

    public int getBondId() {
        return bondId;
    }

    public void setBondId(int bondId) {
        this.bondId = bondId;
    }

    public int getOtherElectronId() {
        return otherElectronId;
    }

    public void setOtherElectronId(int otherElectronId) {
        this.otherElectronId = otherElectronId;
    }
    
    
}
