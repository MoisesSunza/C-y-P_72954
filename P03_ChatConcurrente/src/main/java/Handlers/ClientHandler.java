package Handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private final Set<ClientHandler> clients; 
    private BufferedReader in;
    private PrintWriter out;
    
    public String username = ""; 

    public ClientHandler(Socket clientSocket, Set<ClientHandler> clients) {
         this.socket = clientSocket;
         this.clients = clients;
    }
    
    @Override
    public void run (){
        
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            Thread.sleep(500);
            
            out.println("se ha establecido la conexión");
            out.flush();
            
            out.println("introduce tu nombre de usuario:");
            out.flush();
            
            while (username.isEmpty()) {
                if (in.ready()) {
                    username = in.readLine().trim();
                    System.err.println(username + " se ha unido al chat.");
                    globalMessage("**" + username + "** se ha unido al chat.", this); 
                    break;
                }
            }
            
            String inputMessage = ""; 
            
            do {                
                if(in.ready()){
                    inputMessage = in.readLine().trim();
                    
                    if (inputMessage.startsWith("/exit")) {
                        break; 
                    } else if (inputMessage.equalsIgnoreCase("/list-users")) {
                        listConnectedUsers();
                    } else if (inputMessage.startsWith("/change-userName ")) {
                        String newUsername = inputMessage.substring(17).trim();
                        ChangeUserName(newUsername);
                    } else if (inputMessage.startsWith("/send-msg ")) {
                        String[] parts = inputMessage.split(" ", 3);
                        if (parts.length == 3) {
                            privateMessage(parts[1], parts[2]);
                        } else {
                            out.println("Uso: /send-msg <usuario> <mensaje>");
                            out.flush();
                        }
                    } else if (inputMessage.startsWith("/global-msg ")) {
                        String message = inputMessage.substring(12).trim();
                        globalMessage(message, this);
                    } else if (!inputMessage.isEmpty()) {
                        globalMessage(inputMessage, this);
                    }
                }
            } while (!inputMessage.equalsIgnoreCase("/exit"));
            
            System.err.println(username + " se ha desconectado.");
            globalMessage("**" + username + "** ha abandonado el chat.", this);
            clients.remove(this); 
            socket.close();
            
        } catch (IOException ex) {
            System.getLogger(ClientHandler.class.getName()).log(System.Logger.Level.WARNING, "Cliente desconectado inesperadamente: " + username, ex);
        } catch (InterruptedException ex) {
            System.getLogger(ClientHandler.class.getName()).log(System.Logger.Level.ERROR, "Interrupción del hilo del cliente", ex);
        }
    }
    
    public void ChangeUserName(String newUsername){
        if (newUsername.isEmpty()) {
            out.println("Error: El nombre de usuario no puede estar vacío.");
            out.flush();
            return;
        }
        String oldUsername = this.username;
        this.username = newUsername;
        
        String notification = String.format("[Servidor]: El usuario **%s** ha cambiado su nombre a **%s**.", oldUsername, newUsername);
        globalMessage(notification, null); 
        out.println("Tu nombre de usuario ha sido cambiado a: **" + newUsername + "**");
        out.flush();
        System.err.println(oldUsername + " cambió su nombre a " + newUsername);
    }
    
    public void privateMessage(String targetUsername, String message){
        boolean found = false;
        String formattedMessage = String.format("(Privado de %s): %s", this.username, message);
        
        for (ClientHandler client : clients) {
            if (client.username.equalsIgnoreCase(targetUsername)) {
                client.out.println(formattedMessage);
                client.out.flush();
                out.println("Mensaje privado enviado a " + targetUsername);
                out.flush();
                found = true;
                break;
            }
        }
        
        if (!found) {
            out.println("Error: El usuario '" + targetUsername + "' no está en línea o no existe.");
            out.flush();
        }
    }
    
    public void globalMessage(String message, ClientHandler sender){
        int recipients = 0;
        
        String formattedMessage = sender == null ? 
            message : 
            String.format("%s: %s", sender.username, message);

        for (ClientHandler client : clients) {
            if (sender == null || !client.equals(sender)) {
                client.out.println(formattedMessage);
                client.out.flush();
                recipients++;
            }
        }
        
        if (sender != null) {
            sender.out.println(String.format("Mensaje global enviado. Recibido por %d usuarios.", recipients));
            sender.out.flush();
            System.out.println("Mensaje global de " + sender.username + " a " + recipients + " clientes.");
        } else {
             System.out.println("Notificación del Servidor enviada a " + recipients + " clientes.");
        }
    }

    //Comando agregado /list-users, que muestra un listado de todos los usuarios conectados
    public void listConnectedUsers() {
        StringBuilder userList = new StringBuilder();
        userList.append("--- Usuarios Conectados (").append(clients.size()).append(") ---\n");
        
        for (ClientHandler client : clients) {
            userList.append("- ").append(client.username);
            
            if (client.equals(this)) {
                userList.append(" (Tú)");
            }
            userList.append("\n");
        }
        userList.append("------------------------------------------");
        
        out.println(userList.toString());
        out.flush();
    }
}