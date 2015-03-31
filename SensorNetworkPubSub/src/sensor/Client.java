package sensor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

class Client implements Runnable{
	SensorController ctrl;
	private String host;
	private int port;

	Client(SensorController ctrl, String host, int port){
		this.ctrl = ctrl;
		this.host = host;
		this.port = port;
	}

	@Override
	public void run() {
		while(true){
			while(!ctrl.queue.isEmpty()){
				try{
					pushData(host, port, ctrl.queue.peek());
					System.out.println("pushed " + ctrl.queue.peek());
					ctrl.queue.pop();
				} catch (ConnectException ce){
					System.out.println("No connection");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void pushData(String hostname, int port, String data) throws ConnectException {
		Socket socket = null;
		DataOutputStream out = null;

		/* Initialize socket and stream */
		try {
			socket = new Socket(hostname, port);
			out = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
			throw new ConnectException();
		} finally {

			/* Write data out */
			if (socket != null && out != null) {
				try {
					out.writeBytes(data);
				} 
				catch (IOException e) {
					System.err.println("Can't write to outputstream" + e);
				}

				/* Close resources */
				try {
					out.close();
					socket.close();
				} catch (IOException e) {
					System.err.println("Could not close socket" + e);
				}
			}

		}
	}


}