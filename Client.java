package client;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
// Application client
public class Client {
private static Socket socket;
public static void main(String[] args) throws Exception {
// Adresse et port du serveur
String serverAddress = AskIpAdress();
int port = AskPort();
// Création d'une nouvelle connexion avec le serveur
socket = new Socket(serverAddress, port);
System.out.format("Serveur lancé sur [%s:%d]", serverAddress, port);
// Céation d'un canal entrant pour recevoir les messages envoyés, par le serveur
DataInputStream in = new DataInputStream(socket.getInputStream());
// Attente de la réception d'un message envoyé par le, server sur le canal
String helloMessageFromServer = in.readUTF();
System.out.println(helloMessageFromServer);
DataOutputStream out = new DataOutputStream(socket.getOutputStream());

while (true){

String command;
BufferedReader reader;
System.out.println("Entrer la commande");
reader = new BufferedReader(new InputStreamReader(System.in));
System.out.println("Entrer la commande2");

command = reader.readLine();
String[] commandSplit = command.split("\\s+");

switch(commandSplit[0]) {

case "upload":
	if(commandSplit.length==1) {
		System.out.println("fichier n'existe pas");
		break;
		}
	
	File fileCheckingUser = new File(System.getProperty("user.dir").replace("server", "client")+ "\\" + commandSplit[1]);
	if(!fileCheckingUser.exists()) {
		System.out.println("fichier n'existe pas");
	}
	else {
		out.writeUTF(command);
		OutputStream outputStream = socket.getOutputStream();
	    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
	      File file = new File(System.getProperty("user.dir").replace("server", "client")+ "\\" + commandSplit[1]);
	      FileInputStream fileInputStream = new FileInputStream(file);
	      byte[] buffer = new byte[4096];
          int bytesRead;
          while ((bytesRead = fileInputStream.read(buffer)) != -1) {
              dataOutputStream.write(buffer, 0, bytesRead);
          }
          System.out.println("upload terminer");

          fileInputStream.close();
	}

	
	break;
	
      
case "download":
	out.writeUTF(command);
	String verifyFile = in.readUTF();
	if (!verifyFile.isEmpty() || commandSplit.length==1) {
		System.out.println("File does not exist!");
		break;
	}
	else if (verifyFile.isEmpty()) { 
		String filesizestring = in.readUTF();
		int filesize=Integer.parseInt(filesizestring);
		FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir").replace("server", "client")+ "\\" + commandSplit[1]);
		DataInputStream din = new DataInputStream(socket.getInputStream());
		byte[] buffer = new byte[4096];
		int read = 0;
		int remaining = filesize;
		while((read = din.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			
			remaining -= read;
			fos.write(buffer, 0, read);
			
		}
		fos.close();
		System.out.println("File downloaded successfully!");
		break;
	}	
	break;

	
	
default:
	
	out.writeUTF(command);
	System.out.println(in.readUTF());
	break;
	
	

}





}



}
private static int AskPort() throws IOException {
	int port;
	boolean verifyPort= false;
	BufferedReader read;
	do {
        System.out.println("Entrer le port du serveur");
        read = new BufferedReader(new InputStreamReader(System.in));
        port = Integer.parseInt(read.readLine());
        if (port >= 5000 && port <= 5050)
        {
            System.out.println("Le port est correcte ");
            verifyPort = true;
        } else
            System.out.println("Le port soumis est incorrecte ");
    } while(!verifyPort);
	
	return port;
}
private static String AskIpAdress() throws IOException {
	String ip;
	BufferedReader read;
	do {
        System.out.println("Entrer l'ip du serveur");
        read = new BufferedReader(new InputStreamReader(System.in));
        ip = read.readLine();
        if (verifyIp(ip))
        {
            System.out.println("IP Correcte");
       
        } else
            System.out.println("IP incorrecte");
    } while(!verifyIp(ip));
	
	return ip;
}

public static boolean verifyIp(String ip) {
	Pattern patternIp = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    if (ip.isEmpty()|| ip==null|| ip.startsWith(" ")) {
    	return false;
    }
    ip = ip.trim();
    if ((ip.length() < 6) & (ip.length() > 15)) { 
    	return false;
    }
    try {
        Matcher matcher = patternIp.matcher(ip);
        return matcher.matches();
    } 
    catch (PatternSyntaxException noMatch) {
        return false;
    }
}


}
