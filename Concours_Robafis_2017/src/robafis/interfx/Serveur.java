package robafis.interfx;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import robafis.interfx.view.InterfxOverviewController;

public class Serveur {
//	private int port = 2017;
	public static ServerSocket server = null;
	public static boolean isRunning = true;
	private BufferedInputStream reader = null;
	public static int reponse_int = 500;

	public Serveur(int port) throws IOException {
		server = new ServerSocket(port);
	}

	public void open() throws IOException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (isRunning) {
					try {
						Socket client = server.accept();
						InterfxOverviewController.message.set("Connexion cliente reçue\n");
						Thread t = new Thread (new Runnable() {
							public void run() {
								while(isRunning) {
									try {
										reader = new BufferedInputStream(client.getInputStream());
										String reponse = read();
										try {
											reponse_int = Integer.parseInt(reponse);
											commMotor.fifoQueue.add(reponse_int);
										} catch (NumberFormatException e) {}
										
										if (reponse_int == 97) {
											isRunning=false;
											reader = null;
											client.close();
											InterfxOverviewController.message.set("Adieu\n");
										}
									} catch (IOException e) {
										InterfxOverviewController.message.set("La connexion a été interrompue\n");
										isRunning = false;
										reader = null;
									}
								}
							}
						});
						t.start();
					} catch(IOException e) {}
				}
				try {
					server.close();
					InterfxOverviewController.message.set("Serveur fermé\n");
				} catch (IOException e) {e.printStackTrace();}
			}
		});
		t.start();
	}

	private String read() throws IOException {
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		if (stream >= 0) response = new String(b, 0, stream);
		return response;
	}
}