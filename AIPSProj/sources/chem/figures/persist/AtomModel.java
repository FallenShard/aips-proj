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
public class AtomModel implements Serializable
{
    public AtomModel()
    {
        x = 0;
        y = 0;
        type = "";
        documentId = -1;
    }
    
    public AtomModel(String type, int x, int y)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        documentId = -1;
    }
    
    public AtomModel(String type, int x, int y, int documentId)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.documentId = documentId;
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

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }
    
    
    private int Id;
    
    private int x;
    private int y;
    private String type;
    private int documentId;

}
