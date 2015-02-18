/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures.persist;

import java.io.Serializable;
import java.util.Date;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class DocumentModel implements Serializable, Persistable
{
    private int id = -1;
    private String name;
    private Date timestamp;
    
    public DocumentModel()
    {
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
