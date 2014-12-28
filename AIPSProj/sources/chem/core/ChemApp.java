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
    public void destroy() {
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
        return new AnimatedDrawing();
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
        
//        String path = getSavePath("Save File...");
//        if (path != null) {
//            if (!path.endsWith(".draw"))
//                path += ".draw";
//            saveAsStorableOutput(path);
//        }
        
        FigureEnumeration k = drawing().figures();
        
        while (k.hasMoreElements())
        {
            Figure f = k.nextFigure();
            
            if (f instanceof AtomFigure)
            {
                AtomFigure a = (AtomFigure)f;
                List<ElectronFigure> efs = a.getElectrons();
                
                Session session = HibernateUtil.getSessionFactory().openSession();
                
                for (ElectronFigure ef : efs)
                {
                    ElectronModel em = ef.getModel();

                    session.beginTransaction();
                    session.saveOrUpdate(em);
                    session.getTransaction().commit();
                }
                
                session.close();
            }
        }
        
        
        
        
        FigureEnumeration k1 = drawing().figures();
        
        while (k1.hasMoreElements())
        {
            Figure f = k1.nextFigure();
            
            if (f instanceof ChemicalBond)
            {
                ChemicalBond at = (ChemicalBond)f;
                
                ChemicalBondModel m = new ChemicalBondModel();
                
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                session.saveOrUpdate(m);
                session.getTransaction().commit();
                session.close();
            }
        }
        
        /*DocumentModel dm = new DocumentModel();
        dm.setName("ASDFG");
        Calendar calendar = Calendar.getInstance();
 
    // 2) get a java.util.Date from the calendar instance.
    //    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

        // 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        dm.setTimestamp(currentTimestamp);
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(dm);
        session.getTransaction().commit();
        
        session.close();*/
    }
    
    @Override
    public void promptOpen()
    {
        toolDone();
        
//        String path = getSavePath("Save File...");
//        if (path != null) {
//            if (!path.endsWith(".draw"))
//                path += ".draw";
//            saveAsStorableOutput(path);
//        }        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        try
        {
            Query query = session.createQuery("from DocumentModel");
            List documents = query.list();

            for (Object obj : documents)
            {
                DocumentModel doc = (DocumentModel)obj;
                //AtomFigure af = new CarbonFigure();

                //int ax = atm.getX();
                //int ay = atm.getY();

                //af.basicDisplayBox(new Point(ax, ay), new Point(ax + 120, ay + 120));

                //view().add(af);
                
                System.out.println("Document name: " + doc.getName());
                System.out.println("Date Modified: " + doc.getTimestamp().toString());
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
        
        session.close();
    }
}
