/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test.core;

import Test.figures.CarbonFigure;
import Test.anim.AnimatedDrawing;
import Test.anim.Animator;
import Test.figures.ChemicalConnection;
import Test.tools.ElConnTool;
import Test.figures.OxygenFigure;
import CH.ifa.draw.application.DrawApplication;
import static CH.ifa.draw.application.DrawApplication.IMAGES;
import CH.ifa.draw.figures.ConnectedTextTool;
import CH.ifa.draw.figures.LineConnection;
import CH.ifa.draw.figures.TextFigure;
import CH.ifa.draw.figures.TextTool;
import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.ConnectionTool;
import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.ToolButton;
import CH.ifa.draw.util.Animatable;
import Test.figures.HydrogenFigure;
import java.awt.Panel;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author FallenShard
 */
public class ChemApp extends DrawApplication
{
    private Animator animator;
    
    private List<Animatable> electrons;
    
    ChemApp(String title)
    {
        super(title);
        
        electrons = new LinkedList<>();
        //animator = new CircularAnimator((Animatable)drawing(), view());
    }
    
    @Override
    public void open()
    {
        super.open();
        
        //startAnimation();
    }
    
    @Override
    public void destroy() {
        super.destroy();
        
        //endAnimation();
    }
    
    @Override
    protected void createTools(Panel palette)
    {
        super.createTools(palette);
        
        Tool tool = new CreationTool(view(), new CarbonFigure());
        palette.add(new ToolButton(this, IMAGES + "ELLIPSE", "Carbon Creation Tool", tool));
        
        tool = new CreationTool(view(), new OxygenFigure());
        palette.add(new ToolButton(this, IMAGES + "ELLIPSE", "Oxygen Creation Tool", tool));
        
        tool = new CreationTool(view(), new HydrogenFigure());
        palette.add(new ToolButton(this, IMAGES + "ELLIPSE", "Hydrogen Creation Tool", tool));
        
        tool = new ElConnTool(view(), new ChemicalConnection());
        palette.add(new ToolButton(this, IMAGES + "LINE", "Atom Connection Tool", tool));
    }
    
    @Override
    protected Drawing createDrawing()
    {
        return new AnimatedDrawing();
    }
    
    public void startAnimation() {
        if (drawing() instanceof Animatable && animator == null)
        {
            animator = new Animator((Animatable)drawing(), view());
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
