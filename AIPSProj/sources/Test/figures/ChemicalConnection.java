/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.figures;

import CH.ifa.draw.figures.LineConnection;
import CH.ifa.draw.framework.Figure;

/**
 *
 * @author FallenShard
 */
public class ChemicalConnection extends LineConnection
{
    public ChemicalConnection()
    {
        super();
        setStartDecoration(null);
        setEndDecoration(null);
    }
    
    @Override
    public boolean canConnect(Figure start, Figure end)
    {
        return true;
//        if (start instanceof ElectronFigure && end instanceof HoleFigure)
//            return true;
//        
//        return start instanceof HoleFigure && end instanceof ElectronFigure;
    }
}
