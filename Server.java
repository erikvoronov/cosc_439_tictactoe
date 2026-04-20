import java.io.*;
import java.net.*;

public class Server {

  static int PORT = 69;

  public static void main(String[] args) throws IOException {
    System.out.println("Server is running...");
    try {
      String serverIP = InetAddress.getLocalHost().getHostAddress();
      System.out.println("Server IP: " + serverIP);
      System.out.println("Waiting for clients to connect on port " + PORT);

      // Sockets
      ServerSocket serverSocket = new ServerSocket(PORT);
      Socket socket = serverSocket.accept();
      System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());

      // Input
      BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));

      // Output
      PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

      while (true) {
        // wait for client message
        String clientMessage = input.readLine();
        if (clientMessage == null) {
          System.out.println("Client disconnected.");
          break;
        }
        System.out.println("Client: " + clientMessage);

        // server response
        String serverMessage = "Server: " + serverInput.readLine();
        output.println(serverMessage);
      }

      socket.close();
      serverSocket.close();

    } catch (UnknownHostException e) {
      System.out.println(e.getMessage());
    }
  }
}