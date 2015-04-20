package sensorServer;

import javax.swing.JFrame;

import javax.swing.JScrollPane;

import javax.swing.JList;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ServerGUI extends JFrame implements ActionListener, Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JList<Object> serverLog;
	private ArrayList<String> logData = new ArrayList<String>();
	private ServerGUIController controller;
	
	public ServerGUI(ServerGUIController controller) {
		this.controller = controller;
		getContentPane().setLayout(null);
		setTitle("Server log");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 480);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBounds(12, 13, 358, 319);
		getContentPane().add(scrollPane);
		
		this.serverLog = new JList<Object>();
		scrollPane.setViewportView(serverLog);
		
		JButton btnRestart = new JButton("Restart");
		btnRestart.setActionCommand("restart");
		btnRestart.addActionListener(this);
		btnRestart.setBounds(12, 345, 150, 25);
		getContentPane().add(btnRestart);
		
		JButton btnShutdown = new JButton("Shut down");
		btnShutdown.setBounds(244, 345, 126, 25);
		btnShutdown.setActionCommand("shutdown");
		btnShutdown.addActionListener(this);
		getContentPane().add(btnShutdown);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "shutdown":
			controller.shutdownServer();
			break;
		case "restart":
			controller.restartServer();
			break;
		default:
			break;
		}
	}
	public void addMsgToLog(String text){ 
		this.logData.add(text);
		this.serverLog.setListData(logData.toArray());
		this.serverLog.ensureIndexIsVisible(logData.size()-1);
	}

	@Override
	public void run() {
		this.setVisible(true);
		
	}
}