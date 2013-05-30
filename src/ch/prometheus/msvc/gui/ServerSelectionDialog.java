/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 *
 * @author Denahiro
 */
public class ServerSelectionDialog extends JDialog{

    private final JButton newServerButton=new JButton("New");

    public ServerSelectionDialog(Window owner) {
        super(owner, "Select Server", ModalityType.DOCUMENT_MODAL);

        initComponents();
    }

    private void initComponents(){
        final GroupLayout myLayout=new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(myLayout);

        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(newServerButton));

        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addComponent(newServerButton));

        newServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JDialog newServerDialog=new NewServerDialog(ServerSelectionDialog.this);
                newServerDialog.setVisible(true);
            }
        });

        pack();

        this.setLocationRelativeTo(this.getOwner());
    }
}
