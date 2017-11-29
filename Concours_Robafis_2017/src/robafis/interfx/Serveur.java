package robafis.interfx;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
	private int port = 2018;
	private ServerSocket server = null;
	private boolean isRunning = true;
	private BufferedInputStream reader = null;

	public Serveur() throws IOException {
		server = new ServerSocket(port);
	}

	public void open() throws IOException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Socket client = server.accept();
					System.out.println("Connexion cliente reçue");
					Thread t = new Thread (new Runnable() {
						public void run() {
							while(isRunning) {
								try {
									reader = new BufferedInputStream(client.getInputStream());
									String reponse = read();
									int reponse_int = Integer.parseInt(reponse);
									if (reponse_int == 97) {
										isRunning=false;
										reader = null;
										client.close();
										System.out.println("Adieu");
									}
									else {
										System.out.println(reponse_int);
									}
								} catch (IOException e) {
									System.err.println("La connexion a été interrompue");
									isRunning = false;
									reader = null;
								}
							}
						}
					});
					t.start();
				} catch(IOException e) {e.printStackTrace();}
				try {
					server.close();
					System.out.println("Serveur fermé");
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
		response = new String(b, 0, stream);
		return response;
	}
}