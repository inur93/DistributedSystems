package sensor.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;

import common.ILog;

import sensor.Publisher;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class SensorGUI extends JFrame implements ILog, ActionListener, Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Publisher controller;
	private ArrayList<String> logData = new ArrayList<String>();
	private JList<Object> sensorLog;
	public SensorGUI(Publisher controller) {
		this.controller = controller;
		getContentPane().setLayout(null);
		setTitle("sensor log");
		setBounds(600, 100, 500, 680);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 13, 458, 571);
		getContentPane().add(scrollPane);
		
		this.sensorLog = new JList<Object>();
		scrollPane.setViewportView(sensorLog);
		
		JButton btnRestart = new JButton("Restart");
		btnRestart.setActionCommand("restart");
		btnRestart.addActionListener(this);
		btnRestart.setBounds(12, 597, 157, 25);
		getContentPane().add(btnRestart);
		
		JButton btnShutdown = new JButton("Shut down");
		btnShutdown.setActionCommand("shutdown");
		btnShutdown.addActionListener(this);
		btnShutdown.setBounds(305, 597, 165, 25);
		getContentPane().add(btnShutdown);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "restart":
			this.controller.restartSensor();
			break;
		case "shutdown":
			this.controller.shutdownSensor();
			break;
		default:
			break;
		}
		
	}
	
	public synchronized void addMsg(String msg){ 
		this.logData.add(msg);
		this.sensorLog.setListData(logData.toArray());
		this.sensorLog.ensureIndexIsVisible(logData.size()-1);
	}

	@Override
	public void run() {
		setVisible(true);
	}

}