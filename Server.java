import java.io.*;
import java.net.*;

public class Server {

  public static int PORT = 69;
  public static String[] board = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };

  public static void main(String[] args) {
    System.out.println("Server is running...");
    try {
      ServerSocket serverSocket = new ServerSocket(PORT);
      System.out.println("Server IP: " + InetAddress.getLocalHost().getHostAddress());
      System.out.println("Waiting for 2 players on port " + PORT + "...");

      // Accept both players
      Socket socket1 = serverSocket.accept();
      System.out.println("Player 1 (X) connected: " + socket1.getInetAddress().getHostAddress());

      Socket socket2 = serverSocket.accept();
      System.out.println("Player 2 (O) connected: " + socket2.getInetAddress().getHostAddress());

      // Input and output streams for each player
      BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
      BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
      PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
      PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);

      // Tell each player their role
      out1.println("ROLE X");
      out2.println("ROLE O");

      String[] symbols = { "X", "O" };
      BufferedReader[] inputs = { in1, in2 };
      PrintWriter[] outputs = { out1, out2 };

      int turn = 0; // 0 = X's turn, 1 = O's turn

      while (true) {
        // Send the current board to both players
        sendBoard(out1, out2);

        // Tell the current player to move, tell the other to wait
        outputs[turn].println("YOUR_TURN");
        outputs[1 - turn].println("WAIT");

        // Read the move from the current player
        String move = inputs[turn].readLine();

        // If null, the player disconnected
        if (move == null) {
          out1.println("DISCONNECT");
          out2.println("DISCONNECT");
          System.out.println("A player disconnected. Game over.");
          break;
        }

        // Make sure it is a number
        int pos;
        try {
          pos = Integer.parseInt(move.trim());
        } catch (NumberFormatException e) {
          outputs[turn].println("ERROR Invalid input. Enter 1-9.");
          continue;
        }

        // Make sure the position is valid and not already taken
        if (pos < 1 || pos > 9 || !board[pos - 1].equals(String.valueOf(pos))) {
          outputs[turn].println("ERROR Invalid move. Try again.");
          continue;
        }

        // Place the symbol on the board
        board[pos - 1] = symbols[turn];
        System.out.println("Player " + (turn + 1) + " (" + symbols[turn] + ") played position " + pos);

        // Check if the current player won
        if (checkWin(symbols[turn])) {
          sendBoard(out1, out2);
          outputs[turn].println("WIN");
          outputs[1 - turn].println("LOSE");
          System.out.println("Player " + (turn + 1) + " wins!");
          break;
        }

        // Check for a draw
        if (checkDraw()) {
          sendBoard(out1, out2);
          out1.println("DRAW");
          out2.println("DRAW");
          System.out.println("Game ended in a draw.");
          break;
        }

        // Switch turns
        turn = 1 - turn;
      }

      socket1.close();
      socket2.close();
      serverSocket.close();

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  // Sends the board state to both players as "BOARD 1 2 3 4 5 6 7 8 9"
  public static void sendBoard(PrintWriter out1, PrintWriter out2) {
    String b = "BOARD " + String.join(" ", board);
    out1.println(b);
    out2.println(b);
  }

  // Returns true if the given symbol occupies any winning line
  public static boolean checkWin(String s) {
    return (board[0].equals(s) && board[1].equals(s) && board[2].equals(s)) ||
        (board[3].equals(s) && board[4].equals(s) && board[5].equals(s)) ||
        (board[6].equals(s) && board[7].equals(s) && board[8].equals(s)) ||
        (board[0].equals(s) && board[3].equals(s) && board[6].equals(s)) ||
        (board[1].equals(s) && board[4].equals(s) && board[7].equals(s)) ||
        (board[2].equals(s) && board[5].equals(s) && board[8].equals(s)) ||
        (board[0].equals(s) && board[4].equals(s) && board[8].equals(s)) ||
        (board[2].equals(s) && board[4].equals(s) && board[6].equals(s));
  }

  // Returns true if all 9 cells are taken (no number strings remain)
  public static boolean checkDraw() {
    for (String cell : board) {
      if (!cell.equals("X") && !cell.equals("O"))
        return false;
    }
    return true;
  }
}
