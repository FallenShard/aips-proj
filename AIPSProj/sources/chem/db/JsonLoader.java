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
            
            // First of all, split everything, document is first, atoms will be next with their electrons, and then bonds
            String[] splitData = m_packedJsons.split("@");
            
            // Create the drawing with the model
            DocumentModel docModel = mapper.readValue(splitData[0], DocumentModel.class);
            Drawing drawing = new AnimatedDrawing(docModel);
            
            // Use atom factory to generate atoms
            AtomFactory af = new AtomFactory();
            
            int i = 2;
            
            while (i < splitData.length && splitData[i + 1].equals("A"))
            {
                String json = splitData[i];
                
                AtomModel atomModel = mapper.readValue(json, AtomModel.class);
                AtomFigure atom = af.createAtom(atomModel.getType());
                atom.setModel(atomModel);
                
                // Advance from AtomModel, electrons are next
                i += 2;
                
                Vector<Figure> electronFigures = new Vector<>();
                while (i < splitData.length && splitData[i + 1].equals("E"))
                {
                    json = splitData[i];
                    
                    ElectronModel electronModel = mapper.readValue(json, ElectronModel.class);
                    double c = Math.cos((double)(electronModel.getAngle() / 180.0 * Math.PI));
                    double s = -Math.sin((double)(electronModel.getAngle() / 180.0 * Math.PI));
                    int dX = (int)((Dim.ATOM_RADIUS - Dim.ELECTRON_RADIUS) * c + Dim.ATOM_RADIUS);
                    int dY = (int)((Dim.ATOM_RADIUS - Dim.ELECTRON_RADIUS) * s + Dim.ATOM_RADIUS);
                    
                    ElectronFigure electron = new ElectronFigure(new Point(dX, dY), Dim.ELECTRON_RADIUS, atom, electronModel.getIndex());
                    electron.setModel(electronModel);
                    electronFigures.add(electron);
                    
                    i += 2;
                }
                
                atom.setElectrons(electronFigures);
                atom.moveBy(atomModel.getX(), atomModel.getY());
                drawing.add(atom);
            }
            
            // Now only the bonds are left
            while (i < splitData.length && splitData[i + 1].equals("B"))
            {
                String json = splitData[i];
                
                BondModel bondModel = mapper.readValue(json, BondModel.class);
                
                ChemicalBond bond = new ChemicalBond();
                Connector startCon = null;
                Connector endCon = null;
                
                // This is bad, because figures can overlap, the case above fails ONLY if figures overlap totally
                //AtomFigure startAtom = (AtomFigure)drawing.findFigure(bondModel.getStartAtomX(), bondModel.getStartAtomY());
                //AtomFigure endAtom = (AtomFigure)drawing.findFigure(bondModel.getEndAtomX(), bondModel.getEndAtomY());
                
                AtomFigure startAtom = null;
                AtomFigure endAtom = null;
                
                FigureEnumeration figures = drawing.figures();

                while (figures.hasMoreElements())
                {
                    Figure fig = figures.nextFigure();
                    
                    if (fig instanceof AtomFigure)
                    {
                        AtomFigure atomFig = (AtomFigure)fig;
                    
                        if (atomFig.displayBox().x == bondModel.getStartAtomX()
                                && atomFig.displayBox().y == bondModel.getStartAtomY())
                            startAtom = atomFig;

                        if (atomFig.displayBox().x == bondModel.getEndAtomX()
                                && atomFig.displayBox().y == bondModel.getEndAtomY())
                            endAtom = atomFig;
                    }
                }
                
                if (startAtom != null && endAtom != null && startAtom != endAtom)
                {
                    Figure startElectron = startAtom.getElectron(bondModel.getStartElectronIndex());
                    startCon = startElectron.connectorAt(startElectron.center().x, startElectron.center().y);
                    bond.startPoint(startElectron.center().x, startElectron.center().y);
                    bond.endPoint(startElectron.center().x + 20, startElectron.center().y + 20);

                    Figure endElectron = endAtom.getElectron(bondModel.getEndElectronIndex());
                    endCon = endElectron.connectorAt(endElectron.center().x, endElectron.center().y);

                    bond.connectStart(startCon);
                    bond.connectEnd(endCon);
                    bond.updateConnection();
                    bond.setModel(bondModel);
                    drawing.add(bond);
                }
                
                i += 2;
            }
            
            return drawing;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        return new AnimatedDrawing(-1);
    }

}
