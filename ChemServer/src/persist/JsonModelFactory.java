/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 *
 * @author FallenShard
 */
public class JsonModelFactory implements ModelFactory
{
    private ObjectMapper m_mapper;
    
    public JsonModelFactory()
    {
        m_mapper = new ObjectMapper();
    }

    @Override
    public Persistable unpackModel(String json, String classCode) throws IOException
    {
        switch (classCode)
        {
            case "A":
                return m_mapper.readValue(json, AtomModel.class);
            case "E":
                return m_mapper.readValue(json, ElectronModel.class);
            case "B":
                return m_mapper.readValue(json, BondModel.class);
            case "D":
                return m_mapper.readValue(json, DocumentModel.class);
        }
        
        return null;
    }
}
