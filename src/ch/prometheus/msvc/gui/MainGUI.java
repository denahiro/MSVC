/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.PrintListener;
import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerStateListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;


/**
 *
 * @author Denahiro
 */
public class MainGUI extends javax.swing.JFrame{

    private static final int MIN_WIDTH=600;
    private static final int MIN_HEIGHT=400;
    
    private final ServerHandler myServerHandler;
    
    private ServerTask myServerTask;
    private final Object myServerTaskMutex=new Object();
    
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
    private final JButton serverUpdateButton=new JButton();
    private final JTextField serverInputLine=new JTextField();
    private final JTextArea serverOutputTextArea=new JTextArea();
    private final JScrollPane serverOutputScrollPane=new JScrollPane(serverOutputTextArea);
    
    private void initComponents() {
        this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        
        this.serverOutputTextArea.setLineWrap(true);
        this.serverOutputTextArea.setWrapStyleWord(true);
        this.serverOutputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

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
        
        this.serverUpdateButton.setText("update server");
        this.serverUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainGUI.this.serverUpdateButtonAction(e);
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
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.serverOutputScrollPane)
                .addComponent(this.serverInputLine))
            .addGroup(true,layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.serverStateButton,GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                .addComponent(this.serverUpdateButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(this.serverOutputScrollPane)
                .addComponent(this.serverInputLine,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(this.serverStateButton)
                .addComponent(this.serverUpdateButton))
        );
        
        layout.linkSize(SwingConstants.HORIZONTAL, this.serverStateButton,this.serverUpdateButton);
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        Rectangle sb=this.getGraphicsConfiguration().getBounds();
        this.setSize(sb.width/2,sb.height/2);
        this.setLocation(sb.width/2-this.getWidth()/2,sb.height/2-this.getHeight()/2);
    }
    
    private void serverStateButtonAction(ActionEvent event) {
        synchronized(this.serverStateButton) {
            if(this.myServerHandler.getServerState()==ServerHandler.ServerState.STOPPED) {
                this.addServerTask(new ServerTask() {
                    @Override
                    public void runImpl() {
                        try {
                            MainGUI.this.myServerHandler.launchServer();
                        } catch (IOException e) {
                            MainGUI.this.serverOutputPrintLine(e.toString());
                            System.out.println(e);
                        }
                    }
                });
            } else if(this.myServerHandler.getServerState()== ServerHandler.ServerState.RUNNING) {
                this.addServerTask(new ServerTask() {
                    @Override
                    public void runImpl() {
                        MainGUI.this.myServerHandler.shutdownServer();
                    }
                });
            }
        }
    }
    
    private void serverUpdateButtonAction(ActionEvent event) {
        synchronized(this.serverStateButton) {
            if(this.myServerHandler.getServerState()== ServerHandler.ServerState.STOPPED) {
                this.addServerTask(new ServerTask() {
                    @Override
                    public void runImpl() {
                        try {
                            MainGUI.this.myServerHandler.updateServer();
                        } catch (IOException e) {
                            MainGUI.this.serverOutputPrintLine(e.toString());
                            System.out.println(e);
                        }
                    }
                });
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
                    this.serverStateButton.setText("stopping server...");
                    break;
                case UPDATING:
                    this.serverStateButton.setText("updating server...");
                    break;
            }
        }
    }
    
    private void serverInputLineAction(ActionEvent event) {
        this.myServerHandler.println(this.serverInputLine.getText());
        this.serverInputLine.setText(null);
    }
    
    private void serverOutputPrintLine(String newLine) {
        this.serverOutputTextArea.append(newLine+System.lineSeparator());
        this.serverOutputTextArea.setCaretPosition(this.serverOutputTextArea.getDocument().getLength());
    }
    
    private void addServerTask(ServerTask toAdd) {
        synchronized(this.myServerTaskMutex) {
            if(this.myServerTask==null) {
                this.myServerTask=toAdd;
                (new Thread(this.myServerTask)).start();
            } else {
                this.serverOutputPrintLine("The server is currently busy.");
            }
        }
    }
    
    private abstract class ServerTask implements Runnable {

        @Override
        public final void run() {
            this.runImpl();
            MainGUI.this.myServerTask=null;
        }
        
        public abstract void runImpl();
    }
}
