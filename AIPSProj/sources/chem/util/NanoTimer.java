/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.util;

/**
 *
 * @author FallenShard
 */
public class NanoTimer
{
    private long accumulatedTime;

    public NanoTimer()
    {
        accumulatedTime = System.nanoTime();
    }

    public long getElapsedTime()
    {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - accumulatedTime;

        return elapsedTime;
    }

    public long restart()
    {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - accumulatedTime;
        accumulatedTime = currentTime;

        return elapsedTime;
    }
}
