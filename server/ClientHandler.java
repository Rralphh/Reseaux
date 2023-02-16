package server;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
private Socket socket; private int clientNumber; public ClientHandler(Socket socket, int clientNumber) {
this.socket = socket;
this.clientNumber = clientNumber; System.out.println("New connection with client#" + clientNumber + " at" + socket);}
public void run() { // Création de thread qui envoi un message à un client
try {
InetAddress address = InetAddress.getLocalHost();
String ipAddress = address.getHostAddress();
int localPort = socket.getLocalPort();

DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi 
out.writeUTF("Hello from server - you re client#" + clientNumber); // envoi de message

DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  //temp pour envoi console
LocalDateTime now = LocalDateTime.now();   

String commandInfo = "["+ipAddress+" : "+localPort+" - "+dtf.format(now)+"]";
System.out.println(commandInfo+"connection etablis");

while (true) {
DataInputStream in = new DataInputStream(socket.getInputStream());
String commandReceived = in.readUTF();
System.out.println(commandInfo+":"+commandReceived);

String[] commandSplit = commandReceived.split("\\s+");


switch(commandSplit[0]) {

case "cd":
	out.writeUTF("commande cd recue");
  break;
  
case "ls":
	
	
	String directoryFile = FileSystems.getDefault().getPath(".").toString();
	File fileObject = new File(directoryFile);
	File[] allFiles = fileObject.listFiles();
	String stringAllFiles="";
	
	for(File file : allFiles) {
		boolean isFile=file.isFile();
		if(isFile) {
			
			stringAllFiles += "File --> "+file.getName()+"\n";
		}
		else {
			
			stringAllFiles += "[Folder] --> "+ file.getName() +"\n";

		}
	}
	
	out.writeUTF(stringAllFiles);
	
  break;
  
case "mkdir":
	out.writeUTF("commande mkdir recue");
  break;
  
case "upload":
	out.writeUTF("commande upload recue");
	
  break;
  
case "download":
	out.writeUTF("commande download recue");

  break;
  
case "exit":
	out.writeUTF("fermeture de la connection");
	socket.close();
	break;
	
default:
	out.writeUTF("commande invalide");
 
}
}

} catch (IOException e) {
System.out.println("Error handling client# " + clientNumber + ": " + e);
} finally {
try {
socket.close();
} catch (IOException e) {
System.out.println("Couldn't close a socket, what's going on?");}
System.out.println("Connection with client# " + clientNumber+ " closed");}}
        
}

