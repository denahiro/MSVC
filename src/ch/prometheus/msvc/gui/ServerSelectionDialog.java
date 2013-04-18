/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import java.awt.Window;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 *
 * @author Denahiro
 */
public class ServerSelectionDialog extends JDialog{

    JLabel dummy=new JLabel("to be done");
    
    public ServerSelectionDialog(Window owner) {
        super(owner, "Select Server", ModalityType.DOCUMENT_MODAL);
        
        initComponents();
    }
    
    private void initComponents(){
        final GroupLayout myLayout=new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(myLayout);
        
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.dummy));
        
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addComponent(this.dummy));
        
        pack();
        
        this.setLocationRelativeTo(this.getOwner());
    }
}
