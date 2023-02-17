package client;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
// Application client
public class Client {
private static Socket socket;
public static void main(String[] args) throws Exception {
// Adresse et port du serveur
String serverAddress = "127.0.0.1";
int port = 5001;

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
		din.close();
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


}
