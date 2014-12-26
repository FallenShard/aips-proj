/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

/**
 *
 * @author FallenShard
 */
public class AtomFactory
{
    public enum Type
    {
        CARBON,
        OXYGEN,
        HYDROGEN
    };
    
    public AtomFigure createAtom(Type type)
    {
        switch (type)
        {
            case CARBON:
                return new CarbonFigure();
                
            case OXYGEN:
                return new OxygenFigure();
                
            case HYDROGEN:
                return new HydrogenFigure();
        }
        
        return null;
    }
}
