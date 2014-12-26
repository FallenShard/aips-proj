/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.standard.StandardDrawing;
import CH.ifa.draw.util.Animatable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author FallenShard
 */
public class AnimatedDrawing extends StandardDrawing implements Animatable
{
    /*
     * Serialization support.
     */
    private static final long serialVersionUID = -8566272817418441758L;
    private int bouncingDrawingSerializedDataVersion = 1;
    
    List<AnimationDecorator> elements = new LinkedList<>();

    @Override
    public synchronized Figure add(Figure figure) {
        if (figure instanceof AtomFigure)
        {
            //figure = new AnimationDecorator(figure);
            //elements.add((AnimationDecorator)figure);
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
    public void animationStep() {
        elements.stream().forEach((element) -> {
            element.animationStep();
        });
            
    }
}
