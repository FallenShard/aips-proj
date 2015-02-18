/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.db;

import CH.ifa.draw.framework.Connector;
import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;
import chem.anim.AnimatedDrawing;
import chem.figures.AtomFigure;
import chem.figures.ChemicalBond;
import chem.figures.ElectronFigure;
import chem.figures.persist.AtomModel;
import chem.figures.persist.BondModel;
import chem.figures.persist.DocumentModel;
import chem.figures.persist.ElectronModel;
import chem.util.AtomFactory;
import chem.util.Dim;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Point;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author FallenShard
 */
public class JsonLoader implements DrawingLoader
{
    private String m_packedJsons = null;
    
    public JsonLoader(String packedJsons)
    {
        m_packedJsons = packedJsons;
    }

    @Override
    public Drawing createDrawing()
    {
        try
        {
            // Mapper can generate objects from strings and vice-versa
            ObjectMapper mapper = new ObjectMapper();
            
            // First of all, split on per-class basis
            String[] firstSplit = m_packedJsons.split("\\*");
            
            // Create the drawing with the model
            DocumentModel docModel = mapper.readValue(firstSplit[0], DocumentModel.class);
            Drawing drawing = new AnimatedDrawing(docModel);
            
            // This split will contain atom and electron data on per-atom basis
            String[] atomData = firstSplit[1].split("\\$");
            
            // Use atom factory to generate atoms
            AtomFactory af = new AtomFactory();
            
            for (String atomJson : atomData)
            {
                // Split the atomData[i] to receive atom and its electrons
                String[] individualData = atomJson.split("@");
                // Read json atom model, and create a figure from it
                AtomModel atomModel = mapper.readValue(individualData[0], AtomModel.class);
                AtomFigure atom = af.createAtom(atomModel.getType());
                atom.setModel(atomModel);
                // Time to get all those electron figures
                Vector<Figure> electronFigures = new Vector<>();
                for (int j = 1; j < individualData.length; j++)
                {
                    ElectronModel electronModel = mapper.readValue(individualData[j], ElectronModel.class);
                    double c = Math.cos(electronModel.getAngle());
                    double s = Math.sin(electronModel.getAngle());
                    int dX = (int)((Dim.ATOM_RADIUS - Dim.ELECTRON_RADIUS) * c + Dim.ATOM_RADIUS);
                    int dY = (int)((Dim.ATOM_RADIUS - Dim.ELECTRON_RADIUS) * s + Dim.ATOM_RADIUS);
                    
                    // Create a new electron figure
                    ElectronFigure electron = new ElectronFigure(new Point(dX, dY), Dim.ELECTRON_RADIUS, atom, electronModel.getIndex());
                    electron.setModel(electronModel);
                    electronFigures.add(electron);
                }
                atom.setElectrons(electronFigures);
                atom.moveBy(atomModel.getX(), atomModel.getY());
                drawing.add(atom);
            }
            
            // Now let's add the bonds as well
            String[] bondData = firstSplit[2].split("\\$");
            
            // Add later as a vector, because it will contain all valid bonds
            Vector<Figure> bondFigures = new Vector<>();
            
            for (int i = 0; i < bondData.length; i++)
            {
                BondModel bondModel = mapper.readValue(bondData[i], BondModel.class);
                
                // Create a new bond with null connectors initially
                ChemicalBond bond = new ChemicalBond();
                Connector startCon = null;
                Connector endCon = null;
                
                // Query all the atoms for their electron bonds
                FigureEnumeration figures = drawing.figures();
                
                while (figures.hasMoreElements())
                {
                    Figure figure = figures.nextFigure();
                    
                    // Check for safety reasons
                    if (figure instanceof AtomFigure)
                    {
                        // This figure is definitely an atom
                        AtomFigure atom = (AtomFigure)figure;
                        Vector<ElectronFigure> electrons = atom.getElectrons();
                        
                        for (ElectronFigure electron : electrons)
                        {
                            int electronId = electron.getModel().getId();
                            
                            // If currently inspected electron has id equal to startId, mark it as startConnector
//                            if (electronId == bondModel.getStartElectronId())
//                            {
//                                startCon = electron.connectorAt(electron.center().x, electron.center().y);
//                                bond.startPoint(electron.center().x, electron.center().y);
//                                bond.endPoint(electron.center().x + 20, electron.center().y + 20);
//                            }
//                            
//                            // If currently inspected electron has id equal to endId, mark it as endConnector
//                            if (electronId == bondModel.getEndElectronId())
//                            {
//                                endCon = electron.connectorAt(electron.center().x, electron.center().y);
//                            }
                        }
                    }
                    
                    if (startCon != null && endCon != null)
                        break;
                }
                
                // In the end, if both connectors are found (and they should be!)
                if (startCon != null && endCon != null)
                {
                    // Update the connection
                    bond.connectStart(startCon);
                    bond.connectEnd(endCon);
                    bond.updateConnection();
                    bond.setModel(bondModel);
                    bondFigures.add(bond);
                }
            }
            
            // Add all the bond figures to the drawing
            drawing.addAll(bondFigures);
            
            return drawing;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        return new AnimatedDrawing(-1);
    }
}
