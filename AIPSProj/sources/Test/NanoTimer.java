/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

/**
 *
 * @author FallenShard
 */
public class NanoTimer
{
    long accumulatedTime;

    NanoTimer()
    {
        accumulatedTime = System.nanoTime();
    }

    long getElapsedTime()
    {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - accumulatedTime;

        return elapsedTime;
    }

    long restart()
    {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - accumulatedTime;
        accumulatedTime = currentTime;

        return elapsedTime;
    }
}
