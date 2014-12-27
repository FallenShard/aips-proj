/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures.persist;

/**
 *
 * @author FallenShard
 */
public class AtomModel
{
    public AtomModel()
    {
        x = 0;
        y = 0;
        type = "";
    }
    
    public AtomModel(String type, int x, int y)
    {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
    private int Id;
    
    private int x;
    private int y;
    private String type;

}
