import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ClientGUI {

  private final JFrame frame;
  private final JButton[] cells;
  private final JLabel statusLabel;
  private char currentPlayer;
  private boolean gameOver;

  public ClientGUI() {
    frame = new JFrame("Tic Tac Toe");
    cells = new JButton[9];
    statusLabel = new JLabel("Player X's turn", SwingConstants.CENTER);
    currentPlayer = 'X';
    gameOver = false;

    JPanel boardPanel = new JPanel(new GridLayout(3, 3));
    Font cellFont = new Font("Arial", Font.BOLD, 48);

    for (int i = 0; i < cells.length; i++) {
      final int index = i;
      cells[i] = new JButton("");
      cells[i].setFont(cellFont);
      cells[i].addActionListener(event -> playMove(index));
      boardPanel.add(cells[i]);
    }

    JButton resetButton = new JButton("Reset");
    resetButton.addActionListener(event -> resetGame());

    frame.setLayout(new BorderLayout());
    frame.add(statusLabel, BorderLayout.NORTH);
    frame.add(boardPanel, BorderLayout.CENTER);
    frame.add(resetButton, BorderLayout.SOUTH);
    frame.setSize(400, 450);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private void playMove(int index) {
    if (gameOver || !cells[index].getText().isEmpty()) {
      return;
    }

    cells[index].setText(String.valueOf(currentPlayer));

    if (hasWinner()) {
      statusLabel.setText("Player " + currentPlayer + " wins!");
      gameOver = true;
    } else if (isBoardFull()) {
      statusLabel.setText("It's a tie!");
      gameOver = true;
    } else {
      currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
      statusLabel.setText("Player " + currentPlayer + "'s turn");
    }
  }

  private boolean hasWinner() {
    int[][] winningLines = {
      {0, 1, 2},
      {3, 4, 5},
      {6, 7, 8},
      {0, 3, 6},
      {1, 4, 7},
      {2, 5, 8},
      {0, 4, 8},
      {2, 4, 6}
    };

    for (int[] line : winningLines) {
      String first = cells[line[0]].getText();
      String second = cells[line[1]].getText();
      String third = cells[line[2]].getText();

      if (!first.isEmpty() && first.equals(second) && second.equals(third)) {
        return true;
      }
    }

    return false;
  }

  private boolean isBoardFull() {
    for (JButton cell : cells) {
      if (cell.getText().isEmpty()) {
        return false;
      }
    }

    return true;
  }

  private void resetGame() {
    for (JButton cell : cells) {
      cell.setText("");
    }

    currentPlayer = 'X';
    gameOver = false;
    statusLabel.setText("Player X's turn");
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(ClientGUI::new);
  }
}
