/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.anim;

import chem.util.NanoTimer;
import CH.ifa.draw.framework.DrawingView;

/**
 *
 * @author FallenShard
 */
public class AnimatorThread extends Thread
{
    private final DrawingView     m_view;
    private final Animatable      m_animatable;

    private boolean             m_isRunning;
    
    public  static final int    FPS = 60;
    private static final long   NanoTimePerFrame = 1000000000 / FPS;
    private static final float  SecondsPerFrame = 1.f / FPS;

    public AnimatorThread(Animatable animatable, DrawingView view)
    {
        super("Animator");
        m_view = view;
        m_animatable = animatable;
    }

    @Override
    public void start()
    {
        super.start();
        m_isRunning = true;
    }

    public void end()
    {
        m_isRunning = false;
    }

    @Override
    public void run()
    {
        NanoTimer timer = new NanoTimer();
        long timeSinceLastUpdate = 0;
        
        while (m_isRunning)
        {
            long timeDelta = timer.restart();
            timeSinceLastUpdate += timeDelta;
            
            while (timeSinceLastUpdate > NanoTimePerFrame)
            {
                m_view.freezeView();
                m_animatable.animationStep(SecondsPerFrame);
                m_view.checkDamage();
                m_view.unfreezeView();
                
                timeSinceLastUpdate -= NanoTimePerFrame;
            }
        }
    }
}
