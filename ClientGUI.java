import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ClientGUI {

  private static final int DEFAULT_PORT = 69;

  private final JFrame frame;
  private final JButton[] cells;
  private final JLabel statusLabel;
  private PrintWriter serverOutput;
  private String playerSymbol;
  private boolean myTurn;
  private boolean gameOver;

  public ClientGUI(String host, int port) {
    frame = new JFrame("Tic Tac Toe");
    cells = new JButton[9];
    statusLabel = new JLabel("Connecting to server...", SwingConstants.CENTER);
    playerSymbol = "?";
    myTurn = false;
    gameOver = false;

    JPanel boardPanel = new JPanel(new GridLayout(3, 3));
    Font cellFont = new Font("Arial", Font.BOLD, 48);

    for (int i = 0; i < cells.length; i++) {
      final int index = i;
      cells[i] = new JButton("");
      cells[i].setFont(cellFont);
      cells[i].setEnabled(false);
      cells[i].addActionListener(event -> sendMove(index));
      boardPanel.add(cells[i]);
    }

    frame.setLayout(new BorderLayout());
    frame.add(statusLabel, BorderLayout.NORTH);
    frame.add(boardPanel, BorderLayout.CENTER);
    frame.setSize(400, 430);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    connectToServer(host, port);
  }

  private void connectToServer(String host, int port) {
    Thread connectionThread = new Thread(() -> {
      try {
        Socket socket = new Socket(host, port);
        BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        serverOutput = new PrintWriter(socket.getOutputStream(), true);

        updateStatus("Connected. Waiting for another player...");

        String message;
        while ((message = serverInput.readLine()) != null) {
          handleServerMessage(message);
        }

        updateStatus("Server disconnected.");
        setBoardEnabled(false);
      } catch (IOException e) {
        updateStatus("Could not connect to server.");
      }
    });

    connectionThread.start();
  }

  private void handleServerMessage(String message) {
    if (message.startsWith("ROLE ")) {
      playerSymbol = message.substring(5);
      updateStatus("You are Player " + playerSymbol);
    } else if (message.startsWith("BOARD ")) {
      updateBoard(message.substring(6).split(" "));
    } else if (message.equals("YOUR_TURN")) {
      myTurn = true;
      updateStatus("Your turn. You are " + playerSymbol);
      setBoardEnabled(true);
    } else if (message.equals("WAIT")) {
      myTurn = false;
      updateStatus("Waiting for the other player...");
      setBoardEnabled(false);
    } else if (message.equals("WIN")) {
      gameOver = true;
      updateStatus("You win!");
      setBoardEnabled(false);
    } else if (message.equals("LOSE")) {
      gameOver = true;
      updateStatus("You lose.");
      setBoardEnabled(false);
    } else if (message.equals("DRAW")) {
      gameOver = true;
      updateStatus("Draw game.");
      setBoardEnabled(false);
    } else if (message.equals("DISCONNECT")) {
      gameOver = true;
      updateStatus("Other player disconnected.");
      setBoardEnabled(false);
    } else if (message.startsWith("ERROR ")) {
      updateStatus(message.substring(6));
      setBoardEnabled(true);
    }
  }

  private void sendMove(int index) {
    if (!myTurn || gameOver || serverOutput == null || !cells[index].getText().isEmpty()) {
      return;
    }

    serverOutput.println(index + 1);
    myTurn = false;
    setBoardEnabled(false);
    updateStatus("Move sent. Waiting...");
  }

  private void updateBoard(String[] board) {
    SwingUtilities.invokeLater(() -> {
      for (int i = 0; i < cells.length && i < board.length; i++) {
        if (board[i].equals("X") || board[i].equals("O")) {
          cells[i].setText(board[i]);
        } else {
          cells[i].setText("");
        }
      }
    });
  }

  private void updateStatus(String text) {
    SwingUtilities.invokeLater(() -> statusLabel.setText(text));
  }

  private void setBoardEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> {
      for (JButton cell : cells) {
        cell.setEnabled(enabled && !gameOver && cell.getText().isEmpty());
      }
    });
  }

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

    int finalPort = port;
    SwingUtilities.invokeLater(() -> new ClientGUI(host, finalPort));
  }
}
