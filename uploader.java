import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class uploader implements Runnable {
	
	String fileName;
	String path;
	Socket socket;
	static PrintWriter out;
	
	FileInputStream fis;
	BufferedInputStream bis;
	OutputStream os;
	
	public uploader(String f, Socket client_socket, String client_path) throws IOException {
		this.fileName = f;
		this.socket = client_socket;
		this.path = client_path;
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true); 
		
		
	}
	@Override
	public void run() {
		out.println("uploadFile:" + fileName);
		String fileDir = path +"/"+ fileName;
		File archivo = new File(fileDir);
		
		int port = 50001;
		out.println("upload:"+ port);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		
	    try {
		    SocketAddress sad = new InetSocketAddress("127.0.0.1", port);
		    SocketChannel sc = SocketChannel.open();
		    sc.connect(sad);
		    sc.configureBlocking(true);

		    long fsize = archivo.length();
		    
		    fis = new FileInputStream(archivo);
			FileChannel fc = fis.getChannel();
		    long curnset = 0;
		    curnset =  fc.transferTo(0, fsize, sc);
		    System.out.println("Subida completa: " + fileName);
		    
		    fc.close();
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	       
	}
	

}
