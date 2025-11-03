package concurrentChat;

import Handlers.WriteHandler;
import Handlers.ReadHandler;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {

    private static int PORT;
    private static String ADDRESS;
    
    public static Socket conection = null;

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner( System.in);
        
        System.out.println("Introduce el comando 'start-conection' para iniciar:");
        String command = scanner.nextLine();
        
        if (command.equalsIgnoreCase("start-conection")) {
            // Solicitar IP y Puerto
            System.out.println("IP (e.g., localhost o 127.0.0.1):");
            ADDRESS = scanner.nextLine();
            
            System.out.println("Puerto (e.g., 8080):");
            try {
                PORT = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Puerto inválido. Usando el puerto por defecto 8080.");
                PORT = 8080;
            }
            
            StartConection(PORT, ADDRESS);
        } else {
            System.err.println("Comando no reconocido. Terminando aplicación.");
            return;
        }
        
        if (conection != null) {
            WriteHandler writer = new WriteHandler(conection);
            ReadHandler reader = new ReadHandler(conection);

            Thread writeThread = new Thread(writer);
            Thread readThread = new Thread(reader);

            writeThread.start();
            readThread.start();
        } else {
            System.err.println("No se pudo establecer la conexión. Terminando.");
        }
    }

    public static void StartConection(int port, String address) {
        try {
            System.out.println("Intentando conectar a " + address + ":" + port + "...");
            conection = new Socket(address, port);
            System.out.println("Conexión establecida con éxito.");
        } catch (IOException ex) {
            System.err.println("Error de conexión: " + ex.getMessage());
            conection = null; 
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}