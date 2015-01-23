/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.network;

import chem.figures.persist.DocumentModel;
import java.util.List;

/**
 *
 * @author FallenShard
 */
public interface DocumentReceiver
{
    public void setDocumentModelList(List<DocumentModel> list);
}
