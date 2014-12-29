/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.figures.persist;

import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public interface Persistable
{
    public int getId();
    public void setId(int id);
    public void save(Session session, int documentId);
    public void saveAs(Session session, int documentId);
    public void delete(Session session);
}
