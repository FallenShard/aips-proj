/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chem.UI;

import chem.core.ChemApp;
import chem.figures.persist.DocumentModel;
import chem.network.ConnectThread;
import chem.network.DocumentReceiver;
import chem.network.NetworkHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import org.zeromq.ZMQ;

/**
 *
 * @author Mefi
 */
public class LoadDialog extends javax.swing.JDialog implements DocumentReceiver
{
    // This dialog sets parent's project name and id
    NetworkHandler m_networkHandler = null;
    ConnectThread m_conThread = null;
    private final ChemApp m_parent;
    
    List<DocumentModel> m_editableDocsModels = new ArrayList<>();
    List<DocumentModel> m_viewableDocsModels = new ArrayList<>();
    
    DocumentModel m_selectedDoc = null;

    /**
     * Creates new form LoadDialog
     */
    public LoadDialog(java.awt.Frame parent, boolean modal, NetworkHandler networkHandler)
    {
        super(parent, modal);
        initComponents();
        
        m_parent = (ChemApp)parent;
        
        setTitle("Load Project");
        setLocationRelativeTo(parent);
        
        tableEditableDocs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableViewableDocs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tableEditableDocs.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> 
        {
            int index = tableEditableDocs.getSelectedRow();
            if (index >= 0 && index <= m_editableDocsModels.size() - 1)
            {
                m_selectedDoc = m_editableDocsModels.get(index);
                lbl_SelectedDoc.setText(m_selectedDoc.getName() + " " + m_selectedDoc.getId() + "Status: Editable");
            
                tableViewableDocs.clearSelection();
            }
        });
        
        tableViewableDocs.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> 
        {
            int index = tableViewableDocs.getSelectedRow();
            if (index >= 0 && index <= m_viewableDocsModels.size() - 1)
            {
                m_selectedDoc = m_viewableDocsModels.get(index);
                lbl_SelectedDoc.setText(m_selectedDoc.getName() + " " + m_selectedDoc.getId() + "Status: Viewable");

                tableEditableDocs.clearSelection();
            }
        });
        
        m_networkHandler = networkHandler;
        
        m_conThread = new ConnectThread(networkHandler.getContext(), this);
    }
    
    public void startNetworking()
    {
        m_conThread.start();
    }
    
    //Helper method for loadProjects()
    public synchronized void displayResult()
    {   
        TableModel editableDocsModel = new DocTableModel(m_editableDocsModels);
        tableEditableDocs.setModel(editableDocsModel);
        
        TableModel viewableDocsModel = new DocTableModel(m_viewableDocsModels);
        tableViewableDocs.setModel(viewableDocsModel);
    }
    
    public boolean checkEditorAvailability()
    {
        ZMQ.Socket docChecker = m_networkHandler.createSocket(ZMQ.REQ);
        docChecker.connect("tcp://localhost:" + 8888);
        docChecker.sendMore("CHECK_EDITOR");
        docChecker.send("" + m_selectedDoc.getId());

        String response = docChecker.recvStr();
        docChecker.close();

        try
        {
            boolean b = Boolean.parseBoolean(response);
            return b;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }
    
    public void refreshData()
    {
        tableEditableDocs.clearSelection();
        tableViewableDocs.clearSelection();
        if (m_conThread != null && !m_conThread.isAlive())
        {
            m_conThread = new ConnectThread(m_networkHandler.getContext(), this);
            m_conThread.start();
            m_selectedDoc = null;
            lbl_SelectedDoc.setText("Nothing selected.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btn_cancel = new javax.swing.JButton();
        btn_Load = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnJoin = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableEditableDocs = new javax.swing.JTable();
        lbl_SelectedDoc = new javax.swing.JLabel();
        btn_Refresh = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableViewableDocs = new javax.swing.JTable();
        btn_Delete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Pick a project to participate in");

        btn_cancel.setText("Cancel");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        btn_Load.setText("Load");
        btn_Load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LoadActionPerformed(evt);
            }
        });

        jLabel2.setText("Editable Documents:");

        jLabel3.setText("Viewable Documents:");

        btnJoin.setText("Join");
        btnJoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJoinActionPerformed(evt);
            }
        });

        tableEditableDocs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableEditableDocs);

        lbl_SelectedDoc.setText("jLabel4");

        btn_Refresh.setText("Refresh");
        btn_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RefreshActionPerformed(evt);
            }
        });

        tableViewableDocs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tableViewableDocs);

        btn_Delete.setText("Delete");
        btn_Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_SelectedDoc)
                            .addComponent(jLabel3)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_Load, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(btn_cancel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(btnJoin, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(btn_Refresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Delete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btn_Refresh))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Load)
                        .addGap(18, 18, 18)
                        .addComponent(btn_Delete))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnJoin)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cancel)
                    .addComponent(lbl_SelectedDoc))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //LOAD - sets parents project name and id
    private void btn_LoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LoadActionPerformed
        if (m_selectedDoc != null)
        {
            if (checkEditorAvailability())
            {
                m_parent.loadDocument(m_selectedDoc.getId());
                setVisible(false);
                dispose();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "That document is already being edited. Please refresh to display up-to-date information.");
            }
        }
        else
            JOptionPane.showMessageDialog(this, "Please select a document first.");
    }//GEN-LAST:event_btn_LoadActionPerformed

    //CANCEL - closes dialog
    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btn_cancelActionPerformed

    private void btn_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RefreshActionPerformed
        refreshData();
    }//GEN-LAST:event_btn_RefreshActionPerformed

    private void btnJoinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJoinActionPerformed
        if (m_selectedDoc != null)
        {
            m_parent.viewDocument(m_selectedDoc.getId());
            setVisible(false);
            dispose();
        }
        else
            JOptionPane.showMessageDialog(this, "Please select a document first.");
    }//GEN-LAST:event_btnJoinActionPerformed

    private void btn_DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DeleteActionPerformed
        // TODO add your handling code here:
        if (m_selectedDoc != null && m_editableDocsModels.contains(m_selectedDoc))
        {
            if (checkEditorAvailability())
            {
                m_networkHandler.deleteDocument(m_selectedDoc.getId());
            }
            else
            {
                JOptionPane.showMessageDialog(this, "That document is already being edited. Please refresh to display up-to-date information.");
            }
        }
    }//GEN-LAST:event_btn_DeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnJoin;
    private javax.swing.JButton btn_Delete;
    private javax.swing.JButton btn_Load;
    private javax.swing.JButton btn_Refresh;
    private javax.swing.JButton btn_cancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_SelectedDoc;
    private javax.swing.JTable tableEditableDocs;
    private javax.swing.JTable tableViewableDocs;
    // End of variables declaration//GEN-END:variables


    @Override
    public void setDocumentModelList(List<DocumentModel> freeDocs, List<DocumentModel> openedDocs) {
        m_editableDocsModels = freeDocs;
        m_viewableDocsModels = openedDocs;
        
        displayResult();
    }
}
