/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.core;

import chem.anim.AnimatedDrawing;
import chem.anim.AnimatorThread;
import chem.figures.ChemicalBond;
import chem.tools.CovalentBondTool;
import CH.ifa.draw.application.DrawApplication;
import static CH.ifa.draw.application.DrawApplication.IMAGES;
import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.ToolButton;
import chem.anim.Animatable;
import chem.db.HibernateUtil;
import chem.figures.AtomFactory;
import chem.figures.AtomFigure;
import chem.figures.CarbonFigure;
import chem.figures.ElectronFigure;
import chem.figures.persist.AtomModel;
import chem.figures.persist.ChemicalBondModel;
import chem.figures.persist.DocumentModel;
import chem.figures.persist.ElectronModel;
import chem.tools.AtomSelectionTool;
import java.awt.Panel;
import java.awt.Point;
import java.util.Calendar;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ChemApp extends DrawApplication
{
    private AnimatorThread animator;
    
    private static final String CUSTOM_IMAGES = "/chem/res/";
    
    ChemApp(String title)
    {
        super(title);
    }
    
    @Override
    public void open()
    {
        super.open();
        
        startAnimation();
    }
    
    @Override
    public void destroy()
    {
        super.destroy();
        
        endAnimation();
    }
    
    @Override
    protected void createTools(Panel palette)
    {
        super.createTools(palette);
        
        AtomFactory atomFact = new AtomFactory();
        
        Tool tool = new CreationTool(view(), atomFact.createAtom(AtomFactory.Type.CARBON));
        palette.add(new ToolButton(this, CUSTOM_IMAGES + "CARBON", "Carbon Creation Tool", tool));
        
        tool = new CreationTool(view(), atomFact.createAtom(AtomFactory.Type.OXYGEN));
        palette.add(new ToolButton(this, CUSTOM_IMAGES + "OXYGEN", "Oxygen Creation Tool", tool));
        
        tool = new CreationTool(view(), atomFact.createAtom(AtomFactory.Type.HYDROGEN));
        palette.add(new ToolButton(this, CUSTOM_IMAGES + "HYDROGEN", "Hydrogen Creation Tool", tool));
        
        tool = new CovalentBondTool(view(), new ChemicalBond());
        palette.add(new ToolButton(this, IMAGES + "LINE", "Covalent Bond Tool", tool));
    }
    
    @Override
    protected Tool createSelectionTool()
    {
        return new AtomSelectionTool(view());
    }
    
    @Override
    protected Drawing createDrawing()
    {
        return new AnimatedDrawing(-1);
    }
    
    public void startAnimation() {
        if (drawing() instanceof Animatable && animator == null)
        {
            animator = new AnimatorThread((Animatable)drawing(), view());
            animator.start();
        }
    }
    
    public void endAnimation()
    {
        if (animator != null) {
            animator.end();
            animator = null;
        }
    }
    
    @Override
    public void promptSaveAs()
    {
        toolDone();
        
        try
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            
            DocumentModel docm = ((AnimatedDrawing)drawing()).getModel();
            Query query = session.createQuery("from DocumentModel d where d.id = " + docm.getId());
            Object doc = query.list().get(0);

            DocumentModel pers = (DocumentModel)doc;
            
            session.beginTransaction();
            session.saveOrUpdate(pers);
            session.getTransaction().commit();
            
            int id = pers.getId();
            
            FigureEnumeration k = drawing().figures();
            
            
            while (k.hasMoreElements())
            {
                Figure f = k.nextFigure();

                if (f instanceof AtomFigure)
                {
                    AtomFigure a = (AtomFigure)f;
                    //List<ElectronFigure> efs = a.getElectrons();

                    AtomModel am = a.getModel();
                    am.setDocumentId(id);
                    
                    query = session.createQuery("from AtomModel am where am.documentId = " + id + " and " + "am.id = " + am.getId());
                    List list = query.list();
                    
                    if (list.isEmpty())
                    {
                        session.beginTransaction();
                        session.saveOrUpdate(am);
                        session.getTransaction().commit();
                    }
                    else
                    {
                        doc = query.list().get(0);

                        AtomModel persAm = (AtomModel)doc;
                        persAm.setX(am.getX());
                        persAm.setY(am.getY());

                        session.beginTransaction();
                        session.saveOrUpdate(persAm);
                        session.getTransaction().commit();
                    }

//                    for (ElectronFigure ef : efs)
//                    {
//                        ElectronModel em = ef.getModel();
//
//                        session.beginTransaction();
//                        session.saveOrUpdate(em);
//                        session.getTransaction().commit();
//                    }
                }
            }

            session.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
    
    @Override
    public void promptOpen()
    {
        toolDone();
        
        try
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from DocumentModel");
            Object doc = query.list().get(0);

            DocumentModel docm = (DocumentModel)doc;
            
            int id = docm.getId();
            
            query = session.createQuery("from AtomModel a where a.documentId = " + id);
            List res = query.list();
            
            Drawing drawing = new AnimatedDrawing(docm);
            
            for (Object o : res)
            {
                AtomModel am = (AtomModel)o;
                if (am.getType().equalsIgnoreCase("C"))
                {
                    AtomFigure fig = new CarbonFigure();
                    fig.setModel(am);
                    fig.moveBy(am.getX(), am.getY());
                    drawing.add(fig);
                }
            }
            
            setDrawing(drawing);
            
            view().checkDamage();
            
            session.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
        
        
    }
}
