/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.anim;

import chem.figures.AtomFigure;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.standard.StandardDrawing;
import chem.figures.persist.DocumentModel;
import chem.figures.persist.Persistable;
import chem.figures.persist.PersistableFigure;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class AnimatedDrawing extends StandardDrawing implements Animatable, PersistableFigure
{
    /*
     * Serialization support.
     */
    private static final long serialVersionUID = -8566272817418441758L;
    private int bouncingDrawingSerializedDataVersion = 1;
    
    List<Animatable> elements = new LinkedList<>();
    
    DocumentModel m_model = new DocumentModel();
   
    public AnimatedDrawing(int documentId)
    {
        m_model = new DocumentModel();
        m_model.setId(documentId);
    }
    
    public AnimatedDrawing(DocumentModel model)
    {
        m_model = model;
    }
    
    public void setDocumentName(String name)
    {
        m_model.setName(name);
    }

    @Override
    public synchronized Figure add(Figure figure) {
        if (figure instanceof Animatable)
        {
            elements.add((Animatable)figure);
        }
        return super.add(figure);
    }

    @Override
    public synchronized Figure remove(Figure figure) {
        Figure f = super.remove(figure);
        if (f instanceof AnimationDecorator)
        {
            //elements.remove((AnimationDecorator)f);
            //return ((AnimationDecorator) f).peelDecoration();
        }
            
        return f;
    }

    @Override
    public synchronized void replace(Figure figure, Figure replacement) {
        if (replacement instanceof AtomFigure)
        {
            //replacement = new AnimationDecorator(replacement);
            //elements.remove((AnimationDecorator)figure);
            //elements.add((AnimationDecorator)replacement);
        }
            
        super.replace(figure, replacement);
    }
    
    @Override
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2));
        FigureEnumeration k = figures();
        while (k.hasMoreElements())
            k.nextFigure().draw(g);
    }

    @Override
    public void animationStep(float timeDelta)
    {
        elements.stream().forEach((element) ->
        {
            element.animationStep(timeDelta);
        });
    }
    
    @Override
    public DocumentModel getModel()
    {
        return m_model;
    }

    @Override
    public void setModel(Persistable model)
    {
        m_model = (DocumentModel)model;
    }

    @Override
    public void saveToDatabase(Session session, int documentId)
    {
        m_model.save(session, documentId);
        
        FigureEnumeration figures = figures();
        
        while (figures.hasMoreElements())
        {
            Figure fig = figures.nextFigure();
            PersistableFigure pf = (PersistableFigure)fig;
            pf.saveToDatabase(session, m_model.getId());
        }
    }
    
    @Override
    public void saveToDatabaseAs(Session session, int documentId)
    {
        m_model.saveAs(session, documentId);
        
        FigureEnumeration figures = figures();
        
        while (figures.hasMoreElements())
        {
            Figure fig = figures.nextFigure();
            PersistableFigure pf = (PersistableFigure)fig;
            pf.saveToDatabaseAs(session, m_model.getId());
        }
    }
    
    @Override
    public void deleteFromDatabase(Session session)
    {
        
    }
}
