/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.figures.persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public interface PersistableFigure
{
    Persistable getModel();
    void setModel(Persistable model);
    
    void saveToDatabase(Session session, int documentId);
    void saveToDatabaseAs(Session session, int documentId);
    void deleteFromDatabase(Session session);
    
    void appendJson(StringBuilder packedJson, ObjectMapper mapper);
    void toDeleteString(StringBuilder deleteBuilder);
}
