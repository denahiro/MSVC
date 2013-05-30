/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ExecutorHolder;
import ch.prometheus.msvc.server.PrintListener;
import ch.prometheus.msvc.server.ServerHandler;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


/**
 *
 * @author Denahiro
 */
public class MainGUI extends JFrame{

    private static final int MIN_WIDTH=600;
    private static final int MIN_HEIGHT=400;

    private final ServerHandler myServerHandler;

    private final GroupLayout myLayout=new GroupLayout(getContentPane());;

    public MainGUI(){
        this.myServerHandler=new ServerHandler(new PrintListener() {
            @Override
            public void println(String line) {
                MainGUI.this.serverOutputPrintLine(line);
            }
        });

        this.controlPanel=new ServerStoppedControlPanel(this);

        initComponents();
    }

    private ControlPanel controlPanel;
    private final Object controlPanelMutex=new Object();
    private final JTextField inputLine=new JTextField();
    private final JTextArea outputTextArea=new JTextArea();
    private final JScrollPane outputScrollPane=new JScrollPane(outputTextArea);

    private void initComponents() {
        this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        initClosingAction();
        initOutputTextArea();
        initInputLine();
        initLayout();

        this.setLocationByPlatform(true);

        pack();
    }

    public void setControlPanel(ControlPanel newControlPanel) {
        synchronized(this.controlPanelMutex) {
            this.myLayout.replace(this.controlPanel, newControlPanel);
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

    private void initClosingAction() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if(MainGUI.this.myServerHandler.getServerState()==ServerHandler.ServerState.LAUNCHING
                        || MainGUI.this.myServerHandler.getServerState()==ServerHandler.ServerState.RUNNING) {
                    MainGUI.this.myServerHandler.shutdownServer();
                }
                ExecutorHolder.EXECUTOR.shutdown();
                MainGUI.this.dispose();
            }
        });
    }

    private void initOutputTextArea() {
        this.outputTextArea.setLineWrap(true);
        this.outputTextArea.setWrapStyleWord(true);
        this.outputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }

    private void initInputLine() {
        this.inputLine.setText("");
        this.inputLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainGUI.this.serverInputLineAction(e);
            }
        });
    }

    private void initLayout() {
        getContentPane().setLayout(myLayout);
        myLayout.setHorizontalGroup(
            myLayout.createSequentialGroup()
            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.outputScrollPane)
                .addComponent(this.inputLine))
            .addComponent(this.controlPanel, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
        );
        myLayout.setVerticalGroup(
            myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(myLayout.createSequentialGroup()
                .addComponent(this.outputScrollPane)
                .addComponent(this.inputLine,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE))
            .addComponent(this.controlPanel)
        );


        myLayout.setAutoCreateGaps(true);
        myLayout.setAutoCreateContainerGaps(true);
    }
}
