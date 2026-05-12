# Tic Tac Toe

## How to Start the Game

**Both computers must be on the same Wi-Fi network.**

---

### Computer 1 (Host + Player 1)

1. Open a terminal and go to the project folder
2. Compile everything: `javac Server.java ClientGUI.java`
3. Open **two** terminals in the project folder
4. Terminal 1 — start the server: `java Server`
5. The server will print its IP address — share it with the other player
6. Terminal 2 — open the game window: `java ClientGUI 127.0.0.1`

---

### Computer 2 (Player 2)

1. Open a terminal and go to the project folder
2. Compile: `javac ClientGUI.java`
3. Open the game window: `java ClientGUI 192.168.x.x` (replace with the IP printed by Computer 1)

---

The game starts automatically once both players are connected.
