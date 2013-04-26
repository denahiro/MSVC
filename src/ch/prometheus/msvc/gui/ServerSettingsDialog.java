/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author Denahiro
 */
public class ServerSettingsDialog extends JDialog{

    private static final String DEFAULT_SETTINGS="/ch/prometheus/msvc/server/server.properties";
    private static final File PROPERTY_FILE=new File(ServerHandler.SERVER_DIRECTORY, "server.properties");
    
    private final JButton dirtyButton=new JButton("Dirty Button");
    
    private Properties loadedProperties=new Properties();
    
    private boolean isDirty=false;
    
    public ServerSettingsDialog(Window owner) {
        super(owner, "Server Settings", ModalityType.DOCUMENT_MODAL);
        
        initWindowListeners();
        
        Properties defaultPorperties = initDefaultProperties();
        
        initProperties(defaultPorperties);
        
        initComponents();
    }
    
    private void initComponents(){
        final GroupLayout myLayout=new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(myLayout);
        
        this.dirtyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ServerSettingsDialog.this.isDirty=true;
            }
        });
        
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.dirtyButton));
        
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addComponent(this.dirtyButton));
        
        pack();
        
        this.setLocationRelativeTo(this.getOwner());
    }

    private Properties initDefaultProperties() {
        Properties defaultPorperties=new Properties();
        try(InputStream defaultStream=this.getClass().getResourceAsStream(ServerSettingsDialog.DEFAULT_SETTINGS)) {
            defaultPorperties.load(defaultStream);
        } catch (IOException e) {
            Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "<init>", e);
        }
        return defaultPorperties;
    }

    private void initProperties(Properties defaultPorperties) {
        this.loadedProperties=new Properties(defaultPorperties);
        if(ServerSettingsDialog.PROPERTY_FILE.canRead()) {
            try(FileReader propertyReader=new FileReader(ServerSettingsDialog.PROPERTY_FILE)) {
                this.loadedProperties.load(propertyReader);
            } catch (IOException e) {
                Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "<init>", e);
            }
        }
    }
    
    private void onWindowClosing() {
        if(isDirty) {
            int closingResponse=JOptionPane.showConfirmDialog(this,"Would you like to save the modified settings?");

            switch(closingResponse) {
                case 0:
                    this.saveProperties();
                    break;
                case 1:
                    break;
                case 2:
                    return;
            }
        }
        
        this.dispose();
    }
    
    private void saveProperties() {
        System.out.println("saveProperties() not implemented yet.");
    }

    private void initWindowListeners() {
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerSettingsDialog.this.onWindowClosing();
            }
        });
    }
}
