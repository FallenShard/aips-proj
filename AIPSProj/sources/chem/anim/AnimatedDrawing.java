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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
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
    
    Map<String, PersistableFigure> m_deleteCache = new HashMap<>();
   
    public AnimatedDrawing(int documentId)
    {
        super();
        m_model = new DocumentModel();
        m_model.setId(documentId);
    }
    
    public AnimatedDrawing(DocumentModel model)
    {
        super();
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
        if (figure instanceof PersistableFigure)
        {
            
        }
        
        return super.add(figure);
    }

    @Override
    public synchronized Figure remove(Figure figure) {
        if (figure instanceof PersistableFigure)
        {
            PersistableFigure persFig = (PersistableFigure)figure;
            Persistable model = persFig.getModel();
            
            int i = model.getClass().toString().lastIndexOf('.');
            
            if (i > 0 && model.getId() != -1)
            {
                String className = model.getClass().toString().substring(i + 1);
                m_deleteCache.put(className + "|" + model.getId(), persFig);
            }
        }
        
        Figure f = super.remove(figure);
            
        return f;
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
        //m_model.save(session, documentId);
        
        for (Entry<String, PersistableFigure> entry : m_deleteCache.entrySet())
        {
            if (entry.getValue() instanceof AtomFigure)
            {
                String elHql = "DELETE FROM ElectronModel WHERE atomId = " + entry.getKey();
                Query query = session.createQuery(elHql);
                int rows = query.executeUpdate();
                System.out.println("Rows affected by ElectronModel delete: " + rows);
            }
            
            
            
            String hql = "DELETE FROM " + entry.getValue() + " WHERE id = :delId";
            Query query = session.createQuery(hql);
            query.setParameter("delId", entry.getKey());
            int rows = query.executeUpdate();
            System.out.println("Rows affected by " + entry.getValue() + ": " + rows);
        }
        
        
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
        //m_model.saveAs(session, documentId);
        
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

    @Override
    public void appendJson(StringBuilder packedJson, ObjectMapper mapper)
    {
        try
        {
            
            for (Entry<String, PersistableFigure> entry : m_deleteCache.entrySet())
            {
                entry.getValue().toDeleteString(packedJson);
            }

            packedJson.append("%%%");
            
            packedJson.append(mapper.writeValueAsString(m_model));
            packedJson.append("@D@");
            
            FigureEnumeration k = figures();
            
            while (k.hasMoreElements())
            {
                Figure f = k.nextFigure();
                
                if (f instanceof PersistableFigure)
                {
                    PersistableFigure persFig = (PersistableFigure)f;
                    persFig.appendJson(packedJson, mapper);
                }
            }
        } 
        catch (JsonProcessingException ex)
        {
            Logger.getLogger(AtomFigure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void toDeleteString(StringBuilder deleteBuilder)
    {
        deleteBuilder.append(m_model.getId());
        deleteBuilder.append("|");
        deleteBuilder.append("DocumentModel");
        deleteBuilder.append("~");
    }
}
