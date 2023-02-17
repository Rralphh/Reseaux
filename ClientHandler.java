package server;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	if(commandSplit.length==1) {
		
		System.out.println(commandSplit.length);
		out.writeUTF((System.getProperty("user.dir")));
	}
	else if(commandSplit[0].equals("cd") && commandSplit[1].equals("..")) {
		
		String localDisk=(System.getProperty("user.dir")).substring(0, 3);
		if (System.getProperty("user.dir").equals(localDisk)) {
			out.writeUTF("Can't go any further you are in the local disk");
			break;
		}
		String currentDir = System.getProperty("user.dir");
		File parentDir = new File(currentDir).getParentFile();
		System.setProperty("user.dir", parentDir.getAbsolutePath());
		out.writeUTF((System.getProperty("user.dir")));
		
	}
	else if(commandSplit[0].equals("cd")) {
		
		String path=(System.getProperty("user.dir")+"\\"+commandSplit[1]);
		Path filePath = Paths.get(path);
		if (Files.exists(filePath)) {
			System.setProperty("user.dir", path );
			out.writeUTF((System.getProperty("user.dir")));
			
			
		} else {
		    // file does not exist
			out.writeUTF("file does not exist in this directory");
		   }
		
	}
	
  break;
  
case "ls":
	
	String directoryFile = System.getProperty("user.dir");
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
	String workingDirectory = System.getProperty("user.dir");
	
	File fileDirectory = new File(workingDirectory+"/"+commandSplit[1]);
	if (!fileDirectory.exists()){
	    fileDirectory.mkdirs();
	    out.writeUTF("commande mkdir recu et fichier creer");
	}
	else {
		out.writeUTF("commande mkdir recu mais impossible de creer le fichier (ce fichier existe deja)");
	}
	
	
  break;
  
case "upload":
    InputStream inputStream = socket.getInputStream();
    DataInputStream dataInputStream = new DataInputStream(inputStream);

  
    FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir")+"\\"+commandSplit[1]);

    
    byte[] bufferUser = new byte[4096];
    int bytesRead;
    while ((bytesRead = dataInputStream.read(bufferUser)) != -1) {
        fileOutputStream.write(bufferUser, 0, bytesRead);
    }

    fileOutputStream.close();
	break;
	
	

  
case "download":
	
	if(commandSplit.length==1) {
		out.writeUTF("fichier n'existe pas");
		break;
		}
	
	File fileChecking = new File(System.getProperty("user.dir")+'\\'+ commandSplit[1]);
	if(!fileChecking.exists()) {
		out.writeUTF("fichier n'existe pas");
		break;
	}
	else if (fileChecking.exists()) {
		out.writeUTF("");
		File file = new File(commandSplit[1]);
		int filesize = (int)file.length();
		out.writeUTF(Integer.toString(filesize));
		String path = System.getProperty("user.dir");
		
		DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
		FileInputStream fis = new FileInputStream(path +"\\"+ commandSplit[1]);
		
		
		byte[] buffer = new byte[4096];
		int read = 0;
		int remaining = filesize;
		while((read = fis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
							remaining -= read;
							dout.write(buffer, 0, read);
		}
		fis.close();

		System.out.println("finis dowload");
		break;
	
		
	}
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
System.out.println("Error handling client# " + clientNumber + ": " + e.getStackTrace());
} finally {
try {
socket.close();
} catch (IOException e) {
System.out.println("Couldn't close a socket, what's going on?");}
System.out.println("Connection with client# " + clientNumber+ " closed");}}

        
}


