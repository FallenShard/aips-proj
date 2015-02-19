/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.core;

/**
 *
 * @author FallenShard
 */
public class UserStatus
{
    public static final int CH4_NONE = 0;
    public static final int CH4_EDITOR = 1;
    public static final int CH4_VIEWER = 2;
    
    private int m_userStatus = CH4_NONE;

    public int getUserStatus()
    {
        return m_userStatus;
    }

    public void setUserStatus(int m_userStatus)
    {
        this.m_userStatus = m_userStatus;
    }
}
