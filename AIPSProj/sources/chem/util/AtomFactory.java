/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.util;

import chem.figures.AtomFigure;
import chem.figures.CarbonFigure;
import chem.figures.HydrogenFigure;
import chem.figures.OxygenFigure;

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
    
    public AtomFigure createAtom(String name)
    {
        switch (name)
        {
            case "C":
                return new CarbonFigure();
                
            case "O":
                return new OxygenFigure();
                
            case "H":
                return new HydrogenFigure();
        }
        
        return null;
    }
}
