/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import CH.ifa.draw.application.DrawApplication;
import static CH.ifa.draw.application.DrawApplication.IMAGES;
import CH.ifa.draw.figures.ConnectedTextTool;
import CH.ifa.draw.figures.TextFigure;
import CH.ifa.draw.figures.TextTool;
import CH.ifa.draw.framework.Drawing;
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.ToolButton;
import CH.ifa.draw.util.Animatable;
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
        
        CreationTool ellipseTool = new CreationTool(view(), new AtomFigure()); //for example
        ToolButton ellipseButton = new ToolButton(this, IMAGES + "ELLIPSE", "Ellipse Tool", ellipseTool);
        palette.add(ellipseButton);
        
        Tool tool = new TextTool(view(), new TextFigure());
        ToolButton textButton = new ToolButton(this, IMAGES + "TEXT", "Text Tool", tool);
        palette.add(textButton);
        
        tool = new ConnectedTextTool(view(), new TextFigure());
        ToolButton connTextButton =createToolButton(IMAGES + "ATEXT", "Connected Text Tool", tool);
        palette.add(connTextButton);
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
