/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui.settings;

import ch.prometheus.msvc.server.ServerHandler;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JComponent;
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
    
    private final Properties loadedProperties;
    
    private final JLabel titleLabel=new JLabel("Current World:");
    private final JLabel worldNameLabel=new JLabel();
    
    private final JButton saveButton=new JButton("save");
    private final JButton cancelButton=new JButton("cancel");
    
    private boolean isDirty=false;
    
    private final List<PropertyControl> propertyList=new ArrayList<>();
    
    public ServerSettingsDialog(Window owner) {
        super(owner, "Server Settings", ModalityType.DOCUMENT_MODAL);
        
        initWindowListeners();
        
        Properties defaultPorperties = initDefaultProperties();
        
        this.loadedProperties=initProperties(defaultPorperties);
        
        initPropertyList();
        
        initButtons();
        
        initComponents();
    }
    
    public String getProperty(String key) {
        return this.loadedProperties.getProperty(key);
    }
    
    public void setProperty(String key, String value) {
        this.isDirty=true;
        this.loadedProperties.setProperty(key, value);
    }
    
    private void initComponents(){
        final GroupLayout myLayout=new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(myLayout);
        
        GroupLayout.Group horizontalLabelGroup=myLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.Group horizontalControlGroup=myLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.Group horizontalSaveCancelGroup=myLayout.createSequentialGroup();
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                    .addGroup(horizontalLabelGroup)
                    .addGroup(horizontalControlGroup))
                .addGroup(horizontalSaveCancelGroup));
        
        GroupLayout.Group verticalGroup=myLayout.createSequentialGroup();
        myLayout.setVerticalGroup(verticalGroup);
        
        this.titleLabel.setFont(new Font(
                this.titleLabel.getFont().getFontName(),
                Font.BOLD,
                Math.round(this.titleLabel.getFont().getSize()*1.5f)));
        this.worldNameLabel.setFont(new Font(
                this.worldNameLabel.getFont().getFontName(),
                Font.BOLD,
                Math.round(this.worldNameLabel.getFont().getSize()*1.5f)));
        this.worldNameLabel.setText(this.loadedProperties.getProperty("level-name"));
        addLabelControlPair(this.titleLabel, this.worldNameLabel, horizontalLabelGroup, horizontalControlGroup, verticalGroup, myLayout);
        
        for(PropertyControl pc:this.propertyList) {
            addLabelControlPair(pc.getLabel(), pc.getControl(), horizontalLabelGroup, horizontalControlGroup, verticalGroup, myLayout);
        }
        
        horizontalSaveCancelGroup
                .addComponent(this.saveButton)
                .addComponent(this.cancelButton);
        verticalGroup.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.saveButton)
                .addComponent(this.cancelButton));
        
        myLayout.linkSize(this.saveButton,this.cancelButton);
        
        myLayout.setAutoCreateContainerGaps(true);
        myLayout.setAutoCreateGaps(true);
        
        pack();
        
        this.setLocationRelativeTo(this.getOwner());
    }
    
    private void initButtons() {
        this.saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ServerSettingsDialog.this.saveProperties();
                ServerSettingsDialog.this.onWindowClosing();
            }
        });
        
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ServerSettingsDialog.this.isDirty=false;
                ServerSettingsDialog.this.onWindowClosing();
            }
        });
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

    private Properties initProperties(Properties defaultPorperties) {
        Properties output=new Properties(defaultPorperties);
        if(ServerSettingsDialog.PROPERTY_FILE.canRead()) {
            try(FileReader propertyReader=new FileReader(ServerSettingsDialog.PROPERTY_FILE)) {
                output.load(propertyReader);
            } catch (IOException e) {
                Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "<init>", e);
            }
        }
        return output;
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
        try(Writer outputWriter=new FileWriter(PROPERTY_FILE)) {
            this.loadedProperties.store(outputWriter, "Minecraft server properties");
            this.isDirty=false;
        } catch (IOException ie) {
            System.out.println(ie);
        }
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

    private void initPropertyList() {
        this.propertyList.add(new TextPropertyControl(this, "generator-settings"));
        this.propertyList.add(new BooleanPropertyControl(this, "allow-nether"));
        this.propertyList.add(new BooleanPropertyControl(this, "allow-flight"));
        this.propertyList.add(new IntegerPropertyControl(this, "server-port",1024,65535));
        this.propertyList.add(new TextPropertyControl(this, "level-type"));
        this.propertyList.add(new TextPropertyControl(this, "level-seed"));
        this.propertyList.add(new TextPropertyControl(this, "server-ip"));
        this.propertyList.add(new IntegerPropertyControl(this, "max-build-height",0,256));
        this.propertyList.add(new BooleanPropertyControl(this, "spawn-npcs"));
        this.propertyList.add(new BooleanPropertyControl(this, "white-list"));
        this.propertyList.add(new BooleanPropertyControl(this, "spawn-animals"));
        this.propertyList.add(new BooleanPropertyControl(this, "hardcore"));
        this.propertyList.add(new TextPropertyControl(this, "texture-pack"));
        this.propertyList.add(new BooleanPropertyControl(this, "online-mode"));
        this.propertyList.add(new BooleanPropertyControl(this, "pvp"));
        this.propertyList.add(new ChoicePropertyControl(this, "difficulty",PropertyChoice.difficultyChoices));
        this.propertyList.add(new ChoicePropertyControl(this, "gamemode",PropertyChoice.gamemodeChoices));
        this.propertyList.add(new IntegerPropertyControl(this, "max-players",1,Integer.MAX_VALUE));
        this.propertyList.add(new BooleanPropertyControl(this, "spawn-monsters"));
        this.propertyList.add(new IntegerPropertyControl(this, "view-distance",1,Integer.MAX_VALUE));
        this.propertyList.add(new BooleanPropertyControl(this, "generate-structures"));
        this.propertyList.add(new TextPropertyControl(this, "motd"));
    }

    private void addLabelControlPair(JLabel label, JComponent control, Group horizontalLabelGroup, Group horizontalControlGroup, Group verticalGroup, final GroupLayout myLayout) {
        horizontalLabelGroup.addComponent(label);
        horizontalControlGroup.addComponent(control,GroupLayout.PREFERRED_SIZE,300,Short.MAX_VALUE);
        verticalGroup.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(label)
                .addComponent(control));
    }
}
