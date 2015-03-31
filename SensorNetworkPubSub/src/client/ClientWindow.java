package client;

import javax.swing.JFrame;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.sql.Timestamp;

public class ClientWindow implements IClientWindow, ActionListener{

	private JFrame frame;
	public enum ConnectionStatus{CONNECTING, CONNECTED, FAILED};
	private IMeanClient controller;
	private JLabel lblConnectionStatusInfo;
	private JLabel lblCurrentMeanVal;
	private JButton btnReConnect;
	private JButton btnGetMean;
	private JLabel lblTimestamp;
	
	/**
	 * Create the application.
	 * @param meanClient 
	 */
	public ClientWindow(IMeanClient controller) {
		this.controller = controller;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 460, 109);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		JLabel lblCurrentMean = new JLabel("Current mean:");
		panel.add(lblCurrentMean);
		
		
		
		this.lblCurrentMeanVal = new JLabel("NaN");
		panel.add(this.lblCurrentMeanVal);
		this.lblCurrentMeanVal.setForeground(Color.BLACK);
		
		
//		JPanel panel_1 = new JPanel();
//		frame.getContentPane().add(panel_1);
//		JLabel lblTimestampTxt = new JLabel("last measurement from:");
//		this.lblCurrentMeanVal.setForeground(Color.BLACK);
//		panel_1.add(lblTimestampTxt);

//		this.lblTimestamp = new JLabel("No values");
//		this.lblTimestamp.setForeground(Color.BLACK);
//		panel_1.add(this.lblTimestamp);
		
		this.btnGetMean = new JButton("Get Mean");
		btnGetMean.setActionCommand("getmean");
		btnGetMean.addActionListener(this);
		frame.getContentPane().add(btnGetMean);
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2);
		
		JLabel lblConnectionStatus = new JLabel("connection status:");
		panel_2.add(lblConnectionStatus);
		
		this.lblConnectionStatusInfo = new JLabel("Connecting...");
		lblConnectionStatusInfo.setForeground(Color.ORANGE);
		panel_2.add(lblConnectionStatusInfo);
		
		this.btnReConnect = new JButton("Reconnect");
		btnReConnect.setVisible(false);
		btnReConnect.setActionCommand("reconnect");
		btnReConnect.addActionListener(this);
		panel_2.add(btnReConnect);
		
		
	}
	
	public void setConnectionStatus(ConnectionStatus status){
		switch (status){
		case CONNECTING:
			this.lblConnectionStatusInfo.setText("Connecting...");
			this.lblConnectionStatusInfo.setForeground(Color.ORANGE);
			this.btnGetMean.setEnabled(false);
			break;
		case CONNECTED:
			this.lblConnectionStatusInfo.setText("Connected.");
			this.lblConnectionStatusInfo.setForeground(Color.GREEN);
			this.btnGetMean.setEnabled(true);
			this.btnReConnect.setVisible(false);
			break;
		case FAILED:
			this.lblConnectionStatusInfo.setText("Connection to server failed.");
			this.lblConnectionStatusInfo.setForeground(Color.RED);
			this.btnGetMean.setEnabled(false);
			this.btnReConnect.setVisible(true);
			break;
		}
	}
	
	public void setMeanVal(double value){
		this.lblCurrentMeanVal.setText(String.valueOf(value));
	}

	public void setVisible(boolean visible) {
		this.frame.setVisible(visible);	
	}

	@Override
	public void run() {
		this.frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		switch (arg.getActionCommand()) {
		case "getmean":
			this.controller.calculateMean();
			break;
		case "reconnect":
			this.controller.reConnect();
			break;
		default:
			break;
		}
	}

	@Override
	public void setMeanTimestamp(Timestamp time) {
		System.out.println(time);
		this.lblTimestamp.setText(time == null ? "no values" : time.toString());
		
	}

}
