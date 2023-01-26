package server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
private Socket socket; private int clientNumber; public ClientHandler(Socket socket, int clientNumber) {
this.socket = socket;
this.clientNumber = clientNumber; System.out.println("New connection with client#" + clientNumber + " at" + socket);}
public void run() { // Création de thread qui envoi un message à un client
try {
DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi 
out.writeUTF("command");
out.writeUTF("Hello from server - you re client#" + clientNumber); // envoi de message



out.writeUTF("command");
	//BufferedReader read;
     //read = new BufferedReader(new InputStreamReader(System.in));
     //String command= read.readLine();
     /*switch (command) {
     default:
    	 
    	 
    	
    	 
     case "exit":
    	 socket.close();
    	 
     }*/

} catch (IOException e) {
System.out.println("Error handling client# " + clientNumber + ": " + e);
} finally {
try {
socket.close();
} catch (IOException e) {
System.out.println("Couldn't close a socket, what's going on?");}
System.out.println("Connection with client# " + clientNumber+ " closed");}
}}