/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.PrintListener;
import ch.prometheus.msvc.server.ServerHandler;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


/**
 *
 * @author Denahiro
 */
public class MainGUI extends javax.swing.JFrame{

    private static final int MIN_WIDTH=600;
    private static final int MIN_HEIGHT=400;
    
    private final ServerHandler myServerHandler;
    
    private GroupLayout layout;
    
    public MainGUI(){
        this.myServerHandler=new ServerHandler(new PrintListener() {
            @Override
            public void println(String line) {
                MainGUI.this.serverOutputPrintLine(line);
            }
        });
        initComponents();
    }    
    
    private ControlPanel controlPanel=new ServerStoppedControlPanel(this);
    private final Object controlPanelMutex=new Object();
    private final JTextField inputLine=new JTextField();
    private final JTextArea outputTextArea=new JTextArea();
    private final JScrollPane outputScrollPane=new JScrollPane(outputTextArea);
    
    private void initComponents() {
        this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        
        this.outputTextArea.setLineWrap(true);
        this.outputTextArea.setWrapStyleWord(true);
        this.outputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(MainGUI.this.myServerHandler.getServerState()==ServerHandler.ServerState.LAUNCHING
                        || MainGUI.this.myServerHandler.getServerState()==ServerHandler.ServerState.RUNNING) {
                    MainGUI.this.myServerHandler.shutdownServer();
                }
                MainGUI.this.dispose();
            }
        });

        this.inputLine.setText("");
        this.inputLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainGUI.this.serverInputLineAction(e);
            }
        });

        layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.outputScrollPane)
                .addComponent(this.inputLine))
            .addComponent(this.controlPanel, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(this.outputScrollPane)
                .addComponent(this.inputLine,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE))
            .addComponent(this.controlPanel)
        );
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        Rectangle sb=this.getGraphicsConfiguration().getBounds();
        this.setSize(sb.width/2,sb.height/2);
        this.setLocation(sb.width/2-this.getWidth()/2,sb.height/2-this.getHeight()/2);
    }
    
    public void setControlPanel(ControlPanel newControlPanel) {
        synchronized(this.controlPanelMutex) {
            this.layout.replace(this.controlPanel, newControlPanel);
            this.controlPanel=newControlPanel;
            this.controlPanel.start();
        }
    }
    
    public ServerHandler getServerHandler() {
        return this.myServerHandler;
    }
    
    private void serverInputLineAction(ActionEvent event) {
        this.myServerHandler.println(this.inputLine.getText());
        this.inputLine.setText(null);
    }
    
    private void serverOutputPrintLine(String newLine) {
        this.outputTextArea.append(newLine+System.lineSeparator());
        this.outputTextArea.setCaretPosition(this.outputTextArea.getDocument().getLength());
    }
}
