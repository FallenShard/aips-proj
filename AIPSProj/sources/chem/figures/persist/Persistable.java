/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.figures.persist;

/**
 *
 * @author Mefi
 */
public interface Persistable
{
    public int getId();
    public void setModel();
    public void save();
    public void delete();
}
