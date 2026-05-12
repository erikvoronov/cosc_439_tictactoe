import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

  private static final int DEFAULT_PORT = 69;

  public static void main(String[] args) {
    String host = args.length > 0 ? args[0] : "127.0.0.1";
    int port = DEFAULT_PORT;
    if (args.length > 1) {
      try {
        port = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        System.out.println("Invalid port: " + args[1]);
        return;
      }
    }

    try (
      Socket socket = new Socket(host, port);
      BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))
    ) {
      System.out.println("Connected to server " + host + ":" + port);
      System.out.println("Type a message and press Enter. Type 'exit' to quit.");

      while (true) {
        System.out.print("You: ");
        String message = userInput.readLine();

        if (message == null || "exit".equalsIgnoreCase(message.trim())) {
          System.out.println("Closing connection.");
          break;
        }

        serverOutput.println(message);

        String response = serverInput.readLine();
        if (response == null) {
          System.out.println("Server disconnected.");
          break;
        }

        System.out.println(response);
      }
    } catch (IOException e) {
      System.out.println("Client error: " + e.getMessage());
    }
  }
}
