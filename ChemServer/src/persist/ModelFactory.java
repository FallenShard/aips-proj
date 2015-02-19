/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persist;

import java.io.IOException;

/**
 *
 * @author FallenShard
 */
public interface ModelFactory
{
    
    public Persistable unpackModel(String modelData, String additionalData) throws IOException;
}
