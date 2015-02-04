/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package protocol;

/**
 *
 * @author FallenShard
 */
public enum MessageType
{
    GET_DOCS("GET_DOCS"),
    CHECK_EDITOR("CHECK_EDITOR"),
    LOAD_DOC_EDITOR("LOAD_DOC_EDITOR"),
    LOAD_DOC_VIEWER("LOAD_DOC_VIEWER"),
    SAVE_DOC("SAVE_DOC"),
    SAVE_ATOMS("SAVE_ATOMS"),
    SAVE_BONDS("SAVE_BONDS"),
    DISC_EDITOR("DISC_EDITOR"),
    DISC_VIEWER("DISC_VIEWER");
    
    private final String type;
    
    private MessageType(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return type;
    }
}
