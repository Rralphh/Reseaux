package server;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
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

String commandeClient = null;
String demandeCommande;
boolean actif = true;
ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));
do {
	if (commandeClient.startsWith("cd")){
		out.writeUTF("commande cd recue");
	}
	else if (commandeClient.equals("ls")){
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
	}
	else if(commandeClient.startsWith("mkdir")){
		out.writeUTF("commande mkdir recue");
	}
	else if(commandeClient.startsWith("upload")){
		File fichierUpload = demanderNomFichierUpload(reader);
		envoyerFichierUpload(fichierUpload, objectOutputStream);
		out.writeUTF("commande upload recue");
	}
	else if(commandeClient.startsWith("download")){
		String fichierDownload = commandeClient;
		recevoirFichierDownload(fichierDownload, objectInputStream);
		
		out.writeUTF("commande download recue");
	}
	else if(commandeClient.equals("exit")){
		actif = false;
		out.writeUTF("Au revoir");
	}
} while(true);


}

} catch (IOException e) {
System.out.println("Error handling client# " + clientNumber + ": " + e);
} finally {
try {
socket.close();
} catch (IOException e) {
System.out.println("Couldn't close a socket, what's going on?");}
System.out.println("Connection with client# " + clientNumber+ " closed");}}

private static File demanderNomFichierUpload(BufferedReader reader) throws IOException{
	String nomFichier = null;
	File fichierUpload = null;
	nomFichier=reader.readLine();
	fichierUpload= new File("/." + nomFichier);
	return fichierUpload;
}



private static void envoyerFichierUpload(File fichierUpload, ObjectOutputStream objetsocketOut) throws IOException{
	byte [] byteEnvoye = new byte [(int)fichierUpload.length()];

	BufferedInputStream inputStrem = null;
	inputStrem = new BufferedInputStream(new FileInputStream(fichierUpload));
	inputStrem.read(byteEnvoye, 0, byteEnvoye.length);
	inputStrem.close();

}
private static void recevoirFichierDownload(String nomFichierDownload, ObjectInputStream objetSocketIn) throws IOException
{
        byte [] byteRecu = (new byte [4*1024]);
        BufferedOutputStream fichierOutputStream = null;
        fichierOutputStream = new BufferedOutputStream( new FileOutputStream("./" + nomFichierDownload));
        fichierOutputStream.write(byteRecu, 0, byteRecu.length);
        fichierOutputStream.close();

}
}


