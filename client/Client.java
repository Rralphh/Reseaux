package client;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
}
