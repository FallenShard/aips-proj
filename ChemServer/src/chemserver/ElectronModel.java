/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chemserver;

import java.io.Serializable;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ElectronModel implements Serializable
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

    public String toString()
    {
        return "Index : " + index + " Angle: " + angle;
    }
}
