/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.db;

import CH.ifa.draw.framework.Drawing;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public interface DocumentLoader
{
    public Drawing loadDrawing(Session session, int documentId);
}
