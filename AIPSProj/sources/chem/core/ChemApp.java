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
import CH.ifa.draw.figures.GroupCommand;
import CH.ifa.draw.figures.UngroupCommand;
import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.AlignCommand;
import CH.ifa.draw.standard.BringToFrontCommand;
import CH.ifa.draw.standard.CopyCommand;
import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.CutCommand;
import CH.ifa.draw.standard.DeleteCommand;
import CH.ifa.draw.standard.DuplicateCommand;
import CH.ifa.draw.standard.PasteCommand;
import CH.ifa.draw.standard.SendToBackCommand;
import CH.ifa.draw.standard.ToggleGridCommand;
import CH.ifa.draw.standard.ToolButton;
import CH.ifa.draw.util.CommandMenu;
import chem.UI.LoadDialog;
import chem.UI.SaveDialog;
import chem.anim.Animatable;
import chem.db.DrawingLoader;
import chem.db.JsonLoader;
import chem.util.AtomFactory;
import chem.figures.persist.PersistableFigure;
import chem.network.NetworkHandler;
import chem.network.SaveThread;
import chem.network.ViewerThread;
import chem.tools.AtomSelectionTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import org.zeromq.ZMQ;

/**
 *
 * @author FallenShard
 */
public class ChemApp extends DrawApplication
{
    private AnimatorThread animator;
    
    private static final String CUSTOM_IMAGES = "/chem/res/";
    
    private Panel m_palette;
    
    private NetworkHandler m_networkHandler = null;
    
    private ViewerThread m_viewerThread = null;
    private SaveThread m_saveThread = null;
    
    private static final int CH4_NONE = 0;
    private static final int CH4_EDITOR = 1;
    private static final int CH4_VIEWER = 2;
    
    private static int m_userStatus = CH4_NONE;
    
    ChemApp(String title)
    {
        super(title);
        
        this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                if (JOptionPane.showConfirmDialog(ChemApp.this, 
                    "Are you sure to close this window?", "Really Closing?", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    System.exit(0);
                }
            }
        });
    }
    
    @Override
    public void open()
    {
        super.open();
        
        m_networkHandler = new NetworkHandler();
        startAnimation();
    }
    
    @Override
    public void destroy()
    {
        super.destroy();
        
        if (m_viewerThread != null)
        {
            try
            {
                m_viewerThread.end();
                m_viewerThread.join();
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            m_viewerThread = null;
        }
        
        if (m_saveThread != null)
        {
            try
            {
                m_saveThread.end();
                m_saveThread.join();
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            m_saveThread = null;
        }
        
        m_networkHandler.dispose();
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
        
        m_palette = palette;
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
    public void promptOpen()
    {
        LoadDialog loadDialog = new LoadDialog(this, true, m_networkHandler);
        loadDialog.startNetworking();
        loadDialog.setVisible(true);
    }
    
    @Override
    public void promptSaveAs()
    {
        SaveDialog saveDialog = new SaveDialog(this, true);
        saveDialog.setVisible(true);
    }
    
    public void promptSave()
    {
        if (((PersistableFigure)drawing()).getModel().getId() == -1)
        {
            SaveDialog saveDialog = new SaveDialog(this, true);
            saveDialog.setVisible(true);
        }
        else
            saveDocument();
    }
    
    public void loadDocument(int docId)
    {
        toolDone();
        
        try
        {
            disconnectPrevious();
            m_userStatus = CH4_EDITOR;
            ZMQ.Socket docLoader = m_networkHandler.createSocket(ZMQ.REQ);
            docLoader.connect("tcp://localhost:" + 8888);
            docLoader.sendMore("LOAD_DOC_EDITOR");
            docLoader.send("" + docId);
            
            String response = docLoader.recvStr();
            docLoader.close();
            
            System.out.println(response.length());
            
            DrawingLoader loader = new JsonLoader(response);
            
            Drawing drawing = loader.createDrawing();
            setDrawing(drawing);
            
            this.add("West", m_palette);
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
    }
    
    public void viewDocument(int docId)
    {
       toolDone();
        
        try
        {
            disconnectPrevious();
            m_userStatus = CH4_VIEWER;
            ZMQ.Socket docLoader = m_networkHandler.createSocket(ZMQ.REQ);
            docLoader.connect("tcp://localhost:" + 8888);
            docLoader.sendMore("LOAD_DOC_VIEWER");
            docLoader.send("" + docId);
            
            String response = docLoader.recvStr();
            docLoader.close();
            
            System.out.println(response.length());
            
            DrawingLoader loader = new JsonLoader(response);
            Drawing drawing = loader.createDrawing();
            setDrawing(drawing);
            
            remove(m_palette);
            
            m_viewerThread = new ViewerThread(m_networkHandler.getContext(), this, docId);
            m_viewerThread.start();
            
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        } 
    }
    
    private void disconnectPrevious()
    {
        PersistableFigure doc = (PersistableFigure)drawing();
        if (doc != null && doc.getModel().getId() != -1)
        {
            int docId = doc.getModel().getId();
            
            if (m_userStatus == CH4_EDITOR)
            {
                ZMQ.Socket docLoader = m_networkHandler.createSocket(ZMQ.REQ);
                docLoader.connect("tcp://localhost:" + 8888);
                docLoader.sendMore("DISC_EDITOR");
                docLoader.send("" + docId);

                docLoader.recvStr();
                docLoader.close();
            }
            else if (m_userStatus == CH4_VIEWER)
            {
                ZMQ.Socket docLoader = m_networkHandler.createSocket(ZMQ.REQ);
                docLoader.connect("tcp://localhost:" + 8888);
                docLoader.sendMore("DISC_VIEWER");
                docLoader.send("" + docId);

                docLoader.recvStr();
                docLoader.close();
            }
            
            m_userStatus = CH4_NONE;
        }
    }
    
    public void saveDocument()
    {
        toolDone();
        
        try
        {
//            if (m_saveThread == null && m_userStatus == CH4_EDITOR)
//            {
//                m_saveThread = new SaveThread(m_networkHandler.getContext(), this);
//                m_saveThread.start();
//            }
            long start = System.currentTimeMillis();
            
            PersistableFigure doc = (PersistableFigure)(drawing());
            
            StringBuilder packedJson = new StringBuilder();
            ObjectMapper mapper = new ObjectMapper();
            doc.appendJson(packedJson, mapper);
            
            String dataToSend = packedJson.toString();
            System.out.println(dataToSend.length() + " Time: " + (System.currentTimeMillis() - start));            

            ZMQ.Socket docSaver = m_networkHandler.createSocket(ZMQ.REQ);
            docSaver.connect("tcp://localhost:" + 8888);
            docSaver.sendMore("SAVE_DOC");
            docSaver.send(dataToSend);
            
            String response = docSaver.recvStr();
            docSaver.close();
            
            if (!response.equalsIgnoreCase("Failed"))
            {
                showStatus("Document saved successfully");
                DrawingLoader loader = new JsonLoader(response);
                Drawing drawing = loader.createDrawing();
                setDrawing(drawing);
            }
            else
                showStatus("Failed to save document");
        }
        catch(Exception ex)
        {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
    
    public void saveDocumentAs(String name)
    {
        toolDone();
        
        try
        {
//            Session session = HibernateUtil.getSessionFactory().openSession();
//            
//            PersistableFigure doc = (PersistableFigure)(drawing());
//            AnimatedDrawing dr = (AnimatedDrawing)drawing();
//            dr.setDocumentName(name);
//            
//            doc.saveToDatabaseAs(session, -1);
//
//            session.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
    
    /**
     * Creates the standard menus. Clients override this
     * method to add additional menus.
     */
    @Override
    protected void createMenus(MenuBar mb)
    {
		mb.add(createFileMenu());
		mb.add(createEditMenu());
		mb.add(createAlignmentMenu());
    }

    /**
     * Creates the file menu. Clients override this
     * method to add additional menu items.
     */
    protected Menu createFileMenu() {
		Menu menu = new Menu("File");
		MenuItem mi = new MenuItem("New", new MenuShortcut('n'));
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptNew();
		        }
		    }
		);
		menu.add(mi);

		mi = new MenuItem("Open...", new MenuShortcut('o'));
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptOpen();
		        }
		    }
		);
		menu.add(mi);
        
        mi = new MenuItem("Save...");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptSave();
		        }
		    }
		);
		menu.add(mi);

		mi = new MenuItem("Save As...", new MenuShortcut('s'));
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            promptSaveAs();
		        }
		    }
		);
		menu.add(mi);
		menu.addSeparator();
        
		mi = new MenuItem("Print...", new MenuShortcut('p'));
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            print();
		        }
		    }
		);
		menu.add(mi);
		menu.addSeparator();
        
		mi = new MenuItem("Exit");
		mi.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            exit();
		        }
		    }
		);
		menu.add(mi);
		return menu;
	}

    /**
     * Creates the edit menu. Clients override this
     * method to add additional menu items.
     */
    protected Menu createEditMenu() {
		CommandMenu menu = new CommandMenu("Edit");
		menu.add(new CutCommand("Cut", view()), new MenuShortcut('x'));
		menu.add(new CopyCommand("Copy", view()), new MenuShortcut('c'));
		menu.add(new PasteCommand("Paste", view()), new MenuShortcut('v'));
		menu.addSeparator();
		menu.add(new DuplicateCommand("Duplicate", view()), new MenuShortcut('d'));
		menu.add(new DeleteCommand("Delete", view()));
		menu.addSeparator();
		menu.add(new GroupCommand("Group", view()));
		menu.add(new UngroupCommand("Ungroup", view()));
		menu.addSeparator();
		menu.add(new SendToBackCommand("Send to Back", view()));
		menu.add(new BringToFrontCommand("Bring to Front", view()));
		return menu;
	}

    /**
     * Creates the alignment menu. Clients override this
     * method to add additional menu items.
     */
    protected Menu createAlignmentMenu() {
		CommandMenu menu = new CommandMenu("Align");
		menu.add(new ToggleGridCommand("Toggle Snap to Grid", view(), new Point(4,4)));
		menu.addSeparator();
		menu.add(new AlignCommand("Lefts", view(), AlignCommand.LEFTS));
		menu.add(new AlignCommand("Centers", view(), AlignCommand.CENTERS));
		menu.add(new AlignCommand("Rights", view(), AlignCommand.RIGHTS));
		menu.addSeparator();
		menu.add(new AlignCommand("Tops", view(), AlignCommand.TOPS));
		menu.add(new AlignCommand("Middles", view(), AlignCommand.MIDDLES));
		menu.add(new AlignCommand("Bottoms", view(), AlignCommand.BOTTOMS));
		return menu;
	}

    public static int yesNoCancel(String question)
    {
        return JOptionPane.showConfirmDialog(null, question, "Alert", JOptionPane.YES_NO_CANCEL_OPTION);
    }
}
