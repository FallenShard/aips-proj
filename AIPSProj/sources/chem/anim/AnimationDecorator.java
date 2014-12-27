/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.anim;

import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.standard.DecoratorFigure;
import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

/**
 *
 * @author FallenShard
 */
public class AnimationDecorator extends DecoratorFigure {

    private int fXVelocity;
    private int fYVelocity;
    
    private double time = 0.0;
    double loopDuration = 5.0;
    double scale = Math.PI * 2.0 / 5.0;
    double radius = 5.0;
    
    static int counter = 0;

    /*
     * Serialization support.
     */
    private static final long serialVersionUID = 7894632974364110685L;
    private int animationDecoratorSerializedDataVersion = 1;

    public AnimationDecorator() { }

    public AnimationDecorator(Figure figure) {
        super(figure);
        fXVelocity = 4;
        fYVelocity = 4;
    }

    public void velocity(int xVelocity, int yVelocity) {
        fXVelocity = xVelocity;
        fYVelocity = yVelocity;
    }

    public Point velocity() {
        return new Point(fXVelocity, fYVelocity);
    }

    public void animationStep(float timeDelta) {
	    //int xSpeed = fXVelocity;
	    //int ySpeed = fYVelocity;
	    //Rectangle box = displayBox();

	    //if ((box.x + box.width > 300) && (xSpeed > 0))
    	//	xSpeed = -xSpeed;

	    //if ((box.y + box.height > 300) && (ySpeed > 0))
    	//	ySpeed = -ySpeed;

        //if ((box.x < 0) && (xSpeed < 0))
        //    xSpeed = -xSpeed;

        //if ((box.y < 0) && (ySpeed < 0))
        //    ySpeed = -ySpeed;
        time += 1.0 / AnimatorThread.FPS;
        double timeThroughLoop = time % loopDuration;
        double angle = timeThroughLoop * scale;
        
        double xSpeed = radius * Math.cos(angle);
        double ySpeed = radius * Math.sin(angle);
        
	    //velocity(xSpeed, ySpeed);
	    moveBy((int)(xSpeed), (int)(ySpeed));
	}

	// guard concurrent access to display box

	public synchronized void basicMoveBy(int x, int y) {
	    super.basicMoveBy(x, y);
	}

    public synchronized void basicDisplayBox(Point origin, Point corner) {
        super.basicDisplayBox(origin, corner);
    }

    public synchronized Rectangle displayBox() {
        return super.displayBox();
    }

    //-- store / load ----------------------------------------------

    public void write(StorableOutput dw) {
        super.write(dw);
        dw.writeInt(fXVelocity);
        dw.writeInt(fYVelocity);
    }

    public void read(StorableInput dr) throws IOException {
        super.read(dr);
        fXVelocity = dr.readInt();
        fYVelocity = dr.readInt();
    }

}
