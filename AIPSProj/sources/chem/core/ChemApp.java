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
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.ToolButton;
import chem.anim.Animatable;
import chem.figures.AtomFactory;
import chem.tools.AtomSelectionTool;
import java.awt.Panel;

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
}
