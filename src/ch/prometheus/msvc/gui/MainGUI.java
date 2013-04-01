/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.PrintListener;
import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerStateListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 *
 * @author Denahiro
 */
public class MainGUI extends javax.swing.JFrame{

    private final ServerHandler myServerHandler;
    public MainGUI(){
        this.myServerHandler=new ServerHandler(new PrintListener() {
            @Override
            public void println(String line) {
                MainGUI.this.serverOutputPrintLine(line);
            }
        });
        initComponents();
    }
    
    private final JButton serverStateButton=new JButton();
    private final JTextField serverInputLine=new JTextField();
    private final JScrollPane serverOutputScrollPane=new JScrollPane();
    private final JTextArea serverOutputTextArea=new JTextArea();
    
    private void initComponents() {
//        this.serverStateButton = new JButton();
//        this.serverInputLine = new JTextField();
//        this.serverOutputScrollPane = new JScrollPane();
//        this.serverOutputTextArea=new JTextArea();
        this.serverOutputTextArea.setLineWrap(true);
        this.serverOutputScrollPane.add(this.serverOutputTextArea);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        this.serverStateChange(this.myServerHandler.getServerState());
        this.serverStateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainGUI.this.serverStateButtonAction(e);
            }
        });
        this.myServerHandler.addServerStateListener(new ServerStateListener() {
            @Override
            public void stateChangeEvent(ServerHandler.ServerState newState) {
                MainGUI.this.serverStateChange(newState);
            }
        });

        this.serverInputLine.setText("");
        this.serverInputLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainGUI.this.serverInputLineAction(e);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.serverOutputScrollPane, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                    .addComponent(this.serverInputLine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.serverStateButton)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(this.serverOutputScrollPane,GroupLayout.DEFAULT_SIZE,400,Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(this.serverInputLine))
            .addGroup(layout.createSequentialGroup()
                .addComponent(this.serverStateButton))
        );

        pack();
    }
    
    private void serverStateButtonAction(ActionEvent event) {
        synchronized(this.serverStateButton) {
            if(this.myServerHandler.getServerState()== ServerHandler.ServerState.STOPPED) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MainGUI.this.myServerHandler.launchServer();
                        } catch (IOException e) {
                            MainGUI.this.serverOutputPrintLine(e.toString());
                        }
                    }
                })).start();
            } else if(this.myServerHandler.getServerState()== ServerHandler.ServerState.RUNNING) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MainGUI.this.myServerHandler.shutdownServer();
                    }
                })).start();
            }
        }
    }
    
    private void serverStateChange(ServerHandler.ServerState newState) {
        synchronized(this.serverStateButton) {
            switch(newState) {
                case LAUNCHING:
                    this.serverStateButton.setText("launching server...");
                    break;
                case RUNNING:
                    this.serverStateButton.setText("stop server");
                    break;
                case STOPPED:
                    this.serverStateButton.setText("launch server");
                    break;
                case STOPPING:
                    this.serverStateButton.setText("stoping server...");
                    break;
            }
        }
    }
    
    private void serverInputLineAction(ActionEvent event) {
        this.myServerHandler.println(this.serverInputLine.getText());
        this.serverInputLine.setText(null);
    }
    
    private void serverOutputPrintLine(String newLine) {
        this.serverOutputTextArea.append(newLine);
        this.serverOutputTextArea.repaint();
    }
}
