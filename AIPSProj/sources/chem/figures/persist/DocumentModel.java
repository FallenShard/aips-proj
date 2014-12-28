/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures.persist;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author FallenShard
 */
public class DocumentModel  implements Serializable
{
    private int id;
    private String name;
    private Date timestamp;
    
    public DocumentModel()
    {
        id = -1;
        name = "Untitled";
        timestamp = new Date();
    }
    
    public DocumentModel(String name, Date timestamp)
    {
        this.name = name;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    
}
