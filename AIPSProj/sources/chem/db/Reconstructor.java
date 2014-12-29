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
import chem.util.AtomFactory;
import chem.figures.AtomFigure;
import chem.figures.ChemicalBond;
import chem.figures.ElectronFigure;
import chem.figures.persist.AtomModel;
import chem.figures.persist.ChemicalBondModel;
import chem.figures.persist.DocumentModel;
import chem.figures.persist.ElectronModel;
import chem.util.Dim;
import java.awt.Point;
import java.util.List;
import java.util.Vector;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class Reconstructor implements DocumentLoader
{
    // Reconstructs a drawing with a given open session and document id
    @Override
    public Drawing loadDrawing(Session session, int documentId)
    {
        // First of all, load the document
        Query query = session.createQuery("from DocumentModel d where d.id = " + documentId);
        Object docObj = query.list().get(0);

        // Create the drawing with the model
        DocumentModel docModel = (DocumentModel)docObj;
        Drawing drawing = new AnimatedDrawing(docModel);

        // Now load all the atoms that are associated with this document
        query = session.createQuery("from AtomModel a where a.documentId = " + documentId);
        List atomList = query.list();

        // This atom factory will produce concrete atoms
        AtomFactory af = new AtomFactory();

        for (Object atomObj : atomList)
        {
            AtomModel atomModel = (AtomModel)atomObj;

            // Create the atom based on read type and set the model
            AtomFigure atom = af.createAtom(atomModel.getType());
            atom.setModel(atomModel);

            int atomId = atomModel.getId();

            // Now load all the electrons associated with this atom model, sorted by index
            query = session.createQuery("from ElectronModel e where e.atomId = " + atomId + " ORDER BY e.index ASC");
            List electronList = query.list();

            Vector<Figure> electronFigures = new Vector<>();
            for (Object electronObj : electronList)
            {
                ElectronModel electronModel = (ElectronModel)electronObj;

                double c = Math.cos(electronModel.getAngle());
                double s = Math.sin(electronModel.getAngle());
                int dX = (int)((Dim.ATOM_RADIUS - Dim.ELECTRON_RADIUS) * c + Dim.ATOM_RADIUS);
                int dY = (int)((Dim.ATOM_RADIUS - Dim.ELECTRON_RADIUS) * s + Dim.ATOM_RADIUS);

                // Create a new electron figure
                ElectronFigure electron = new ElectronFigure(new Point(dX, dY), Dim.ELECTRON_RADIUS, atom, electronModel.getIndex());
                electron.setModel(electronModel);
                electronFigures.add(electron);
            }

            // Set a new vector of electrons to the atom
            atom.setElectrons(electronFigures);
            
            // Move the atom to its place
            atom.moveBy(atomModel.getX(), atomModel.getY());

            // Finally, add the atom to the drawing
            drawing.add(atom);
        }
        
        // Now load all the bonds that are part of the document
        query = session.createQuery("from ChemicalBondModel cb where cb.documentId = " + documentId);
        List bondList = query.list();

        // Add to the temporary vector first, then add as vector later
        Vector<Figure> bondFigures = new Vector<>();
        for (Object bondObj : bondList)
        {
            ChemicalBondModel bondModel = (ChemicalBondModel)bondObj;

            // Create a new bond, with null connectors as well
            ChemicalBond bond = new ChemicalBond();
            Connector startCon = null;
            Connector endCon = null;

            // Grab all the current figures (they should all be atoms)
            FigureEnumeration figures = drawing.figures();

            while (figures.hasMoreElements())
            {
                Figure figure = figures.nextFigure();

                // We still have to ask, just in case
                if (figure instanceof AtomFigure)
                {
                    // Alright, we've definitely got an atom
                    AtomFigure atom = (AtomFigure)figure;
                    Vector<ElectronFigure> electrons = atom.getElectrons();
                    for (ElectronFigure electron : electrons)
                    {
                        int electronId = electron.getModel().getId();
                        
                        // If currently inspected electron has id equal to startId, mark it as startConnector
                        if (electronId == bondModel.getStartElectronId())
                        {
                            startCon = electron.connectorAt(electron.center().x, electron.center().y);
                            bond.startPoint(electron.center().x, electron.center().y);
                            bond.endPoint(electron.center().x + 20, electron.center().y + 20);
                        }

                        // If currently inspected electron has id equal to endId, mark it as endConnector
                        if (electronId == bondModel.getEndElectronId())
                        {
                            endCon = electron.connectorAt(electron.center().x, electron.center().y);
                        }
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
        
        drawing.addAll(bondFigures);
        
        return drawing;
    }
}
