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

// Interface for figures in drawing().figures() that need to have pointer to the document
public interface DocumentFigure
{
    public void setDocumentId(int id);
}
