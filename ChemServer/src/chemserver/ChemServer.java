/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chemserver;

/**
 *
 * @author FallenShard
 */
public class ChemServer
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Broker server = new Broker();
        server.initialize();
        server.run();
    }
}
