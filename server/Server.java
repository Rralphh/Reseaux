package server;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
public class Server {
private static ServerSocket Listener; // Application Serveur

public static void main(String[] args) throws Exception {
	int clientNumber = 0;
// Adresse et port du serveur
String serverAddress = AskIpAdress();  
int serverPort = AskPort();

// Création de la connexien pour communiquer ave les, clients
	Listener = new ServerSocket();
	Listener.setReuseAddress(true);
	
	// Association de l'adresse et du port à la connexion
	InetAddress serverIP = InetAddress.getByName(serverAddress);
	Listener.bind(new InetSocketAddress(serverIP, serverPort));
	System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
	try {
// À chaque fois qu'un nouveau client se, connecte, on exécute la fonstion
// run() de l'objet ClientHandler
		while (true) {
// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
// Une nouvetle connection : on incémente le compteur clientNumber 
		new ClientHandler(Listener.accept(), clientNumber++).start();
		}
		} finally {
// Fermeture de la connexion
Listener.close();
} } 
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