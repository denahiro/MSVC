/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.gui.settings.PropertyControl;
import ch.prometheus.msvc.server.files.PropertyHandler;
import ch.prometheus.msvc.server.files.ServerData;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author Denahiro
 */
public class SettingsDialog extends JDialog {

    protected final PropertyHandler properties;
    private GroupLayout myLayout;
    private Group horizontalLabelGroup;
    private Group horizontalControlGroup;
    private Group horizontalGroup;
    private Group verticalBeforePropertiesGroup;
    private Group verticalPropertiesGroup;
    private Group verticalAfterPropertiesGroup;

    public SettingsDialog(Window owner, String title) {
        super(owner, title, ModalityType.DOCUMENT_MODAL);

        initWindowClosingBehaviour();

        properties = new ServerData().getProperties();
//        properties = new PropertyHandler(new File(ServerHandler.SERVER_DIRECTORY, "server.properties"));

        initComponents();
    }

    protected final void addPropertyControl(PropertyControl control) {
        horizontalLabelGroup.addComponent(control.getLabel());
        horizontalControlGroup.addComponent(control.getControl(), GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE);
        verticalPropertiesGroup.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(control.getLabel())
                .addComponent(control.getControl()));
    }

    protected void addHeading(String heading) {
        final JLabel titleLabel = new JLabel(heading);

        titleLabel.setFont(magnifyFont(titleLabel.getFont()));

        horizontalGroup.addComponent(titleLabel);

        verticalBeforePropertiesGroup.addComponent(titleLabel);
    }

    protected void arrange() {
        pack();
        this.setLocationRelativeTo(this.getOwner());
    }

    private void initWindowClosingBehaviour() {
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SettingsDialog.this.onWindowClosing();
            }
        });
    }

    private void onWindowClosing() {
        if (properties.isDirty()) {
            int closingResponse = JOptionPane.showConfirmDialog(this, "Would you like to save the modified settings?");
            switch (closingResponse) {
                case 0:
                    properties.savePropertyChanges();
                    this.dispose();
                    break;
                case 1:
                    this.dispose();
                    break;
                case 2:
                    break;
            }
        }
    }

    private void initComponents() {
        initLayout();
        initGroups();
        initSaveCancelButtons();
    }

    private void initLayout() {
        myLayout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(myLayout);

        myLayout.setAutoCreateContainerGaps(true);
        myLayout.setAutoCreateGaps(true);
    }

    private void initGroups() {
        initHorizontalGroups();
        initVerticalGroups();
    }

    private void initHorizontalGroups() {
        horizontalLabelGroup = myLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        horizontalControlGroup = myLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        horizontalGroup = myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                .addGroup(horizontalLabelGroup)
                .addGroup(horizontalControlGroup));
        myLayout.setHorizontalGroup(horizontalGroup);
    }

    private void initVerticalGroups() {
        verticalBeforePropertiesGroup = myLayout.createSequentialGroup();
        verticalPropertiesGroup = myLayout.createSequentialGroup();
        verticalAfterPropertiesGroup = myLayout.createSequentialGroup();
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addGroup(verticalBeforePropertiesGroup)
                .addGroup(verticalPropertiesGroup)
                .addGroup(verticalAfterPropertiesGroup));
    }

    private Font magnifyFont(final Font original) {
        return new Font(original.getFontName(), Font.BOLD, Math.round(original.getSize() * 1.5F));
    }

    private void initSaveCancelButtons() {
        JButton saveButton = initSaveButton();
        JButton cancelButton = initCancelButton();

        horizontalGroup.addGroup(myLayout.createSequentialGroup()
                .addComponent(saveButton)
                .addComponent(cancelButton));
        verticalAfterPropertiesGroup.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(saveButton)
                .addComponent(cancelButton));

        myLayout.linkSize(saveButton, cancelButton);
    }

    private JButton initSaveButton() {
        final JButton saveButton = new JButton("save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                SettingsDialog.this.properties.savePropertyChanges();
                SettingsDialog.this.onWindowClosing();
            }
        });
        return saveButton;
    }

    private JButton initCancelButton() {
        final JButton cancelButton = new JButton("cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                SettingsDialog.this.properties.unflagPropertyChanges();
                SettingsDialog.this.onWindowClosing();
            }
        });
        return cancelButton;
    }
}
