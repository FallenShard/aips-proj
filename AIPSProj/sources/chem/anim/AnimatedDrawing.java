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
import chem.db.HibernateUtil;
import chem.figures.CarbonFigure;
import chem.figures.ChemicalBond;
import chem.figures.ElectronFigure;
import chem.figures.HydrogenFigure;
import chem.figures.OxygenFigure;
import chem.figures.persist.ConnectableFigure;
import chem.figures.persist.DocumentFigure;
import chem.figures.persist.DocumentModel;
import chem.figures.persist.Persistable;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.Document;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class AnimatedDrawing extends StandardDrawing implements Animatable, Persistable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    //Used for database
    private DocumentModel m_model = new DocumentModel();
    
    @Override
    public int getId()
    {
        return m_model.getId();
    }
    
    @Override
    public void setModel()
    {
        m_model.setName("Project"); //will be set from dialog
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        m_model.setTimestamp(currentTimestamp);
    }
    
    @Override
    public void save()
    {
        setModel();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(m_model);
        session.getTransaction().commit();
        session.close();
        
        //First pass through figures which saves them
        FigureEnumeration k = figures();
        
        while (k.hasMoreElements())
        {
            Figure f = k.nextFigure();
            
            DocumentFigure df = (DocumentFigure)f;
            df.setDocumentId(m_model.getId());
            
            Persistable per = (Persistable)f;
            per.save();
            
            if (f instanceof AtomFigure)
            {
                AtomFigure af = (AtomFigure)f;
                for (ElectronFigure ef : af.getElectrons())
                {
                    Persistable perEl = (Persistable)ef;
                    perEl.save();
                }
            }
        }
        
        //Second pass through figures which connects them
        FigureEnumeration k1 = figures();
        
        while (k1.hasMoreElements())
        {
            Figure f = k1.nextFigure();
            
            if (f instanceof AtomFigure)
            {
                AtomFigure af = (AtomFigure)f;
                for (ElectronFigure ef : af.getElectrons())
                {
                    ConnectableFigure cf = (ConnectableFigure)ef;
                    cf.setRelations();
                }
            }
            
            if (f instanceof ChemicalBond)
            {
                ConnectableFigure cf = (ConnectableFigure)f;
                cf.setRelations();
            }
        }
    }
    
    @Override
    public void delete()
    {
        //TODO: Add remove code for document.
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /*
     * Serialization support.
     */
    private static final long serialVersionUID = -8566272817418441758L;
    private int bouncingDrawingSerializedDataVersion = 1;
    
    List<Animatable> elements = new LinkedList<>();

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
        //Used for database
        Persistable p = (Persistable)figure;
        p.delete();
        
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
}
