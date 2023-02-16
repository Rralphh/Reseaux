package client;
import java.io.BufferedInputStream;
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

while (true){
String command;
BufferedReader read;

read = new BufferedReader(new InputStreamReader(System.in));
command = read.readLine();

DataOutputStream out = new DataOutputStream(socket.getOutputStream());
out.writeUTF(command);

String responseMessage = in.readUTF();
System.out.println(responseMessage);
}

}
private static String askClient() throws IOException {
	String command;
	BufferedReader read;
	
        System.out.println("Entrer la commande");
      
        // envoi de commande
        read = new BufferedReader(new InputStreamReader(System.in));
        command = read.readLine();
        
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(command);
        
        
        //attente de reponse
        DataInputStream in = new DataInputStream(socket.getInputStream());
        String waitResponse = in.readUTF();
        System.out.println(waitResponse);
        
	return "commande envoyer";
}
private static File demanderNomFichier(BufferedReader reader) throws IOException{
        String nomFichier = null;
        File fichierUpload = null;
        nomFichier=reader.readLine();
        fichierUpload= new File("/." + nomFichier);
        return fichierUpload;
}
private static void envoyerFichierUpload(File fichierUpload, ObjectOutputStream objetsocketOut) throws IOException, ClassNotFoundException{
        byte [] byteEnvoye = new byte [(int)fichierUpload.length()];

        BufferedInputStream inputStrem = null;
        inputStrem = new BufferedInputStream(new FileInputStream(fichierUpload));
        inputStrem.read(byteEnvoye, 0, byteEnvoye.length);
        inputStrem.close();

}

private static void recevoirFichierDownload(String nomFichierDownload, ObjectInputStream objetSocketIn) throws IOException, ClassNotFoundException
{
        byte [] byteRecu = (byte []) objetSocketIn.readObject();
        BufferedOutputStream fichierOutputStream = null;
        fichierOutputStream = new BufferedOutputStream( new FileOutputStream("./" + nomFichierDownload));
        fichierOutputStream.write(byteRecu, 0, byteRecu.length);
        fichierOutputStream.close();

}
}
