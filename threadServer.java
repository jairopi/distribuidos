

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class threadServer implements Runnable {
	
	static Socket sc;
	static BufferedReader in;
	static ObjectOutputStream out;
	static String serverFolder;
	static File dirServer;
	
	public threadServer(Socket so, String path){
		sc = so;
		serverFolder = path;
	}
	
	@Override
	public void run() {
		try {
			clientConnected();
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public static void clientConnected() throws IOException, NoSuchAlgorithmException{
		
		serverFolder = "/";
		dirServer = new File(serverFolder);
		
		in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
		out = new ObjectOutputStream(sc.getOutputStream());
		
		while(true){
			String msg=null;
			while(msg==null){
				msg= in.readLine();
			}
			listenIn(msg);
		}
		
		
	}
	
	public static void listenIn(String msg) throws NoSuchAlgorithmException, IOException{
		
		System.out.print("Mensaje recibido: ");
		String str = msg.substring(0, msg.indexOf(':'));
		switch(str){
		case "serverList": 
			System.out.println("serverList");
			out.writeObject(genPacket());
			break;
		case "changePath":
			System.out.println("changePath");
			changePath((msg.substring(msg.indexOf(':')+1)));
			break;
		case "serverTime":
			System.out.println("severTime");
			long serverTime = System.currentTimeMillis();
			out.writeObject(serverTime);
			break;
		case "fileTime":
			System.out.println("fileTime");
			out.writeObject(tFile(msg.substring(msg.indexOf(':')+1)));
			break;
		case "uploadFile": //C->S
			System.out.println("uploadFile");
			String filePath = (msg.substring(msg.indexOf(':')+1));
			System.out.println("Subiendo archivo" + filePath + " al servidor: ");
			upload(filePath);
			break;
		case "downloadFile": //C->S
			System.out.println("downloadFile");
			String fileName = (msg.substring(msg.indexOf(':')+1));
			System.out.println("Descargando archivo" + fileName + " al cliente: ");
			downlaod(fileName);
			break;
			
		}
		
		
	}
	
	private static void changePath(String newPath){
		serverFolder = newPath;
		dirServer = new File (serverFolder);
		if (!dirServer.exists()){
			dirServer.mkdirs();
		}
		System.out.println("Ruta cambiada: " + newPath);
	}
	
	private static void downlaod(String fileName) {
		String fileDir = serverFolder + "/" + fileName;
		File archivo = new File(fileDir);
		
		int port = 50001;
		System.out.println("start upload");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
	    	System.out.println("Sending file "+ fileName);
		    SocketAddress sad = new InetSocketAddress("127.0.0.1", port);
		    SocketChannel sc = SocketChannel.open();
		    sc.connect(sad);
		    sc.configureBlocking(true);

		    long fsize = archivo.length();
		    
		    FileInputStream fis = new FileInputStream(archivo);
			FileChannel fc = fis.getChannel();
		    long curnset = 0;
		    curnset =  fc.transferTo(0, fsize, sc);
		
		    fc.close();
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public static void upload(String filePath) throws IOException{
		
		
		if (!dirServer.exists()){		//Si no existe la carpeta la crea
			dirServer.mkdirs();
		}
		
		String pathDir = dirServer.getAbsolutePath();
		String fileDir = pathDir +"/"+ filePath;
			
		System.out.println("FileReceive readData");
		ByteBuffer dst = ByteBuffer.allocate(4096);
		
		ServerSocketChannel listener = ServerSocketChannel.open();
		ServerSocket ss = listener.socket();
		ss.setReuseAddress(true);
		ss.bind(new InetSocketAddress(50001));
		
		try {
				System.out.println("readData: "+ filePath);
				SocketChannel conn = listener.accept();
				System.out.println("Accepted : "+conn);
				conn.configureBlocking(true);
				RandomAccessFile fis = new RandomAccessFile(fileDir, "rw");
				FileChannel fc = fis.getChannel();
				int nread = 0;
				while (nread != -1)  {
					try {
						nread = conn.read(dst);
						dst.flip();
						fc.write(dst);
						dst.clear();

					} catch (IOException e) {
						e.printStackTrace();
						nread = -1;
					}
					dst.rewind();
					ss.close(); //Si no, no aceptaria las siguientes
				}
				System.out.println("END readData: "+ filePath);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static long tFile(String file){
		String pathDir = dirServer.getAbsolutePath();
		String fileDir = pathDir +"/"+ file;
		File archivo = new File(fileDir);
		
		if (archivo.exists()){
		return archivo.lastModified();
		}
		else{
			return -1;	
		}
		
}
	
	public static HashMap genPacket() throws NoSuchAlgorithmException, IOException{ 	//Creamos el paquete que enviamos al servidor, este paquete consiste en un Map (FileName:FileHash)
		HashMap<String,byte[]> packet = new HashMap<String,byte[]>();
		
		if (dirServer.exists()){
			File[] server_files = dirServer.listFiles();
			
			for (int i=0; i<server_files.length; i++){
				String fName = server_files[i].getName();
				byte[] fHash = calcularHASH(server_files[i]);
				packet.put(fName, fHash);
			}
			
		} else {
			System.out.println("La carpeta remota no existe!");
		}
		return packet;
		
	}
	
	public static byte[] calcularHASH(File f) throws NoSuchAlgorithmException, IOException{
			
			String datafile = f.getAbsolutePath();
			
		    MessageDigest md = MessageDigest.getInstance("SHA1");
		    FileInputStream fis = new FileInputStream(datafile);
		    byte[] dataBytes = new byte[1024];
		    
		    int nread = 0; 
		    
		    while ((nread = fis.read(dataBytes)) != -1) {
		      md.update(dataBytes, 0, nread);
		    };
	
		    byte[] mdbytes = md.digest();
			
			return mdbytes;		
		}

}
