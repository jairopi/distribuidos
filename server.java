import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {

	static int port;
	static Socket serverSocket;
	
	static BufferedReader in;
	static ObjectOutputStream out;
	static String serverFolder;

	
	public server (int puerto){
		this.port = puerto;
		this.serverFolder = "/Users/antonio_bermejo/TDistribuidos/Remota_Default";
		server.main(null);
	}
	
	public static void main(String[] args) {

			
		
			ServerSocket sc;
			ExecutorService es = Executors.newCachedThreadPool();
			try {
				
				sc = new ServerSocket(port);
				System.out.println("Servidor corriento en el puerto: " + port);
				
				while (true){ 							//Aceptar peticiones de multiples clientes.
					serverSocket = sc.accept();
					System.out.println("Cliente conectado...");
					threadServer hiloServer = new threadServer(serverSocket, serverFolder);
					es.execute(hiloServer);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		
		
	}
	
	
	
}
