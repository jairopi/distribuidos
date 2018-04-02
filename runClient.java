import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class runClient {
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchAlgorithmException, IOException {
		
		Scanner scanner = new Scanner(System.in);

		String defPath = "/Users/antonio_bermejo/TDistribuidos/Local_Default";
		
		client cl;
		
		String user = chekUser();
		
		
		System.out.println("Introduza la ruta que desea usar como carpeta local o pulse");
		System.out.println(" 'Enter' para tomar la ruta por defecto: ");
		
		
		String path = scanner.nextLine();
		
		if (path.equals("")){
			System.out.println("-Ruta del cliente por defecto-" + defPath);
			String pServ = reqServ(user);
			
			
			cl = new client(defPath,pServ);
			
		}
		
		else{
			System.out.println("Ruta -> " + path);
			String pServ = reqServ(user);
			cl = new client(path,pServ);
		}
		
		cl.main(null);
		
		
	}

	private static String chekUser() {
		String user;
		Scanner scannerU = new Scanner(System.in);
		System.out.println("Introduzca su nombre de usuario: ");
		user = scannerU.nextLine();
		
		while (user.equals("")){
			user = scannerU.nextLine();
		}
		System.out.println("Bienvenido:  " + user);
		return user;
	}

	private static String reqServ(String user) {
		
		String path;
		Scanner scanner2 = new Scanner(System.in);
		
		String sdefPath = "/Users/antonio_bermejo/TDistribuidos/Remote_Default_"+user;
		
		System.out.println("Introduza la ruta que desea usar como carpeta remota en el servidor o pulse");
		System.out.println(" 'Enter' para tomar la ruta por defecto: ");
		
		String sPath = scanner2.nextLine();
		
		if (sPath.equals("")){
			path = sdefPath;
			System.out.println("-Ruta del servidor por defecto-" + path);		
		}
		
		else{
			path = sPath +"_"+user;
			System.out.println("Ruta -> " + path);
		}
		return path;
		
	}
	
}
