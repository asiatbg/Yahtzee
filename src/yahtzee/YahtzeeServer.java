/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yahtzee;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
public class YahtzeeServer extends JFrame implements ActionListener, Protocol{
    private JButton start;
    private JPanel panel;
    private JTextField port, textField;
    private JLabel JLPort = new JLabel("Port: ");
    private JLabel JLSend = new JLabel("Wyślij: ");
    private JTextArea statements; 
    private static final int PORT = 2345;
    private boolean started = false;  
    private  Server server;
    private int counter = 0;
   
    
    public YahtzeeServer(){
        super("Yahtzee Serwer");
        
        setSize(400,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        panel = new JPanel(new FlowLayout());
        statements = new JTextArea();
        statements.setLineWrap(true);
        statements.setEditable(false);
        
        port = new JTextField((new Integer(PORT)).toString(), 8);
        
        start = new JButton("Uruchom");    
        start.addActionListener(this);
        
        panel.add(JLPort);
        panel.add(port);
        panel.add(start);
        
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(statements), BorderLayout.CENTER);
        
        textField = new JTextField();
     
        add(JLSend, BorderLayout.SOUTH);
        add(textField, BorderLayout.SOUTH);
               
        setVisible(true);
    } 

    @Override
    public void actionPerformed(ActionEvent e) {
       Object source = e.getSource();
       
       if (source == start)
       {
            server = new Server();
            server.start();
            started = true; 
            start.setEnabled(false);
            port.setEnabled(false);
            repaint(); 
               
       }   
    }
    private class Server extends Thread {
        private ServerSocket server;
        private Game game;
        public void run(){
            try {
                server = new ServerSocket(new Integer(port.getText()));
                displayMessage("Serwer uruchomiony na porcie: " +
                port.getText() + "\n");
                while (started) {
                    if (counter == 0 ||counter >= 4){
                        game = new Game();
                        counter = 0;
                    }                  
                    counter++;
                    Socket socket = server.accept();
                    displayMessage("Nowe połączenie.\n");
                    game.new Player(socket).start();      
                }
            } catch (SocketException e){
            } catch (Exception e){
                 displayMessage(e.toString() + "error!");
            } finally {
                try {
                if(server != null) 
                    server.close();
            }catch (IOException e){
                 displayMessage(e.toString());
                }
            } 
            displayMessage("Serwer zatrzymany.\n");
        }
}
 private void displayMessage(String tekst){
        statements.append(tekst + "\n");
        statements.setCaretPosition(statements.getDocument().getLength());
    }
    
 class Game implements Protocol {
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<String> nicks = new ArrayList<>();
    private ArrayList<Integer> scores = new ArrayList<>();
    private int activePlayerIndex = 0;
    private int startOfTheGame = 0;
    class Player extends Thread{
        private String[] curses = {"kurwa", "spierdalaj", "zajebiście", "chuj"};
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String line = "", nick = "";
        private boolean correct = true;
        public Player(Socket s){
               socket = s;
               try {
               in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
               out = new PrintWriter(socket.getOutputStream(), true);
               
               synchronized(players){
                   players.add(this);
                   scores.add(0);
               }
              
           } catch (IOException e) {
               System.out.println("Player died: " + e);
           }       
        }
        private boolean CheckNick(){
            if (nick.equals("null") || nick.trim().equals(""))
            {
                out.println(LOGIN_COMMAND + "Podaj poprawny nick!"); 
                return false;
            }
            correct = true;
            for (Player player : players) {
                if (player.compare(this) && player != this) {
                    correct = false;
                }
            }
             if (!correct)
             {
                 out.println(LOGIN_COMMAND + "Taki nick już jest zajęty! Podaj inny.");  
                 return false;
             }
             return true;        
        }
        private boolean compare(Player p) {
            return (p.nick.equals(nick));
        }
        private void info() {
            for(Player chat : players) {  
                chat.out.println(POST_COMMAND + "Użytkownik " + nick + " dołączył do czatu");
            }      
        }
        private void send(String protocol, String nick) {
            for(Player player : players) {
                player.out.println(protocol + nick);   
            }   
         }
        private void updateTable(){
            String previousNicks = "";
            for (String previousNick: nicks)
            {
                previousNicks += previousNick + " ";
            }
            if(!previousNicks.equals(""))
                out.println(UPDATE_COL_COMMAND + previousNicks);
        }
        private void checkGameplayOrder(int activePlayer, Player currentPlayer){
            
            if(activePlayer != players.indexOf(currentPlayer))
            {
                out.println(WAIT_COMMAND); 
                if(startOfTheGame != 0)
                    notifayPlayer(activePlayer);
            } 
            else
            {
                 currentPlayer.out.println(PLAY_COMMAND);
            }
        }
        
        private void notifayPlayer(int activePlayer){
             for(Player player: players)
                {
                    if(players.indexOf(player) == activePlayer)
                    {
                         player.out.println(PLAY_COMMAND);
                    }
                        
                } 
        }
         private void censorWords()
        {
            for ( String curse: curses)
            {           
                line = line.replace(curse, " ****** ");  
            }       
        }
        private void sendToEveryone(String text) {
            for(Player chat : players) {
                chat.out.println(POST_COMMAND +" <" + nick + "> " + text);   
            }
        }
        private void privateMessage(String text){
            String nck = "";
            text = text.substring( "/private".length() ).trim();
            for(String nickname : nicks)
            {
              if(text.startsWith(nickname))
              {
                  nck = text.substring(0, nickname.length()); 
                  text = text.substring( nck.length() ).trim();              
              }
            }

            for (Player player : players) {           
                if (player.nick.equals(nck)) {
                    player.out.println(POST_COMMAND +"**Private** <" + nick + "> " + text);
                }
            }

        }       
        private void move(){
            activePlayerIndex++;
            startOfTheGame++; 
            if(activePlayerIndex >= players.size())
            {
                activePlayerIndex = 0;
            } 
            checkGameplayOrder(activePlayerIndex, this);     
        }
        private String prepareDataToSend(String data){
            if(data.length() == 1)
            {
                data += "  ";
                return data;
            }
            else if (data.length() == 2)
            {
                data += " ";
                return data;
            }
            return data;
        }
        
        private void play(String protocol,  int userScore){
            int sum = 0;
            move();
            sum = userScore + scores.get(players.indexOf(this));
            scores.set(players.indexOf(this), sum);
            String sUserScore =  prepareDataToSend( String.valueOf(userScore) ) ;
            String sSum =  prepareDataToSend(String.valueOf( scores.get(players.indexOf(this))) );
            send(protocol, sUserScore + sSum + nick);   
        }
        
        @Override
        public void run(){
            try {
                while (true)
                {   
                        line = in.readLine();
                        if (line.startsWith(LOGIN_COMMAND)) {  
                            updateTable();
                            out.println(LOGIN_COMMAND + "Witaj na serwerze!" +
                            " Lista dostępnych komend:" +
                            "1. /private nick tekst pozwala na wysłanie wiadomości prywatnej ");   
                        }
                        if (line.startsWith( NICK_COMMAND) ) {    
                            nick = line.substring(NICK_COMMAND.length() );
                            if(CheckNick())
                            {  
                                info();
                                synchronized(nicks){
                                    nicks.add(nick);
                                }
                               send(ADD_COL_COMMAND, nick);                      
                               checkGameplayOrder(activePlayerIndex, this);
                            }      
                        }    
                        if (line.startsWith(ONE) ) {
                            String receivedValues =  line.substring(ONE.length()) ;
                            int userScore = 0;
                            for(int i = 0; i < receivedValues.length(); i++)
                            {
                                if(receivedValues.charAt(i) == '1')
                                    userScore += 1;
                            }
                            play(ONE,userScore);          
                        }
                            if (line.startsWith(TWO) ) {
                            String receivedValues =  line.substring(TWO.length()) ;
                            int userScore = 0;
                            for(int i = 0; i < receivedValues.length(); i++)
                            {
                                if(receivedValues.charAt(i) == '2')
                                    userScore += 2;
                            }
                            play(TWO,userScore); 
                        }
                        if (line.startsWith(THREE) ) {
                            String receivedValues =  line.substring(THREE.length()) ;
                            int userScore = 0;
                            for(int i = 0; i < receivedValues.length(); i++)
                            {
                                if(receivedValues.charAt(i) == '3')
                                    userScore += 3;
                            }
                            play(THREE,userScore); 
                        }
                        if (line.startsWith(FOUR) ) {
                            String receivedValues =  line.substring(FOUR.length()) ;
                            int userScore = 0;
                            for(int i = 0; i < receivedValues.length(); i++)
                            {
                                if(receivedValues.charAt(i) == '4')
                                    userScore += 4;
                            }
                            play(FOUR,userScore); 
                        }
                        if (line.startsWith(FIVE) ) {
                           String receivedValues =  line.substring(FIVE.length()) ;
                            int userScore = 0;
                            for(int i = 0; i < receivedValues.length(); i++)
                            {
                                if(receivedValues.charAt(i) == '5')
                                    userScore += 5;
                            }
                            play(FIVE,userScore); 
                        }
                        if (line.startsWith(SIX) ) {
                            String receivedValues =  line.substring(SIX.length()) ;
                            int userScore = 0;
                            for(int i = 0; i < receivedValues.length(); i++)
                            {
                                if(receivedValues.charAt(i) == '6')
                                    userScore += 6;
                            }
                            play(SIX,userScore); 
                        }
                        if (line.startsWith(PAIR) ) {
                            int userScore = 0;
                            String receivedValues =  line.substring(PAIR.length()) ;
                            int repeatedValue = 0;
                            for(int i = 0; i < receivedValues.length(); i++) {
                                if (repeatedValue != 0) break;
                                for (int j = 0; j < receivedValues.length(); j++) {
                                    if (i == j) continue;
                                    if (receivedValues.charAt(i) == receivedValues.charAt(j)) {
                                        repeatedValue = Character.getNumericValue(receivedValues.charAt(i));
                                        break;
                                    }
                                }
                            }
                            userScore += repeatedValue * 2;
                            play(PAIR,userScore); 
                        }
                        if (line.startsWith(PAIRS) ) {
                            int userScore = 0;
                            String receivedValues =  line.substring(PAIRS.length()) ;
                            int firstRepeatedValue = 0, secondRepeatedValue = 0;
                            for(int i = 0; i < receivedValues.length(); i++) {
                                if (firstRepeatedValue != 0) break;
                                for (int j = 0; j < receivedValues.length(); j++) {
                                    if (i == j) continue;
                                    if (receivedValues.charAt(i) == receivedValues.charAt(j)) {
                                        
                                        firstRepeatedValue = Character.getNumericValue(receivedValues.charAt(i));
                                        receivedValues = receivedValues.replaceFirst(Pattern.quote(String.valueOf(firstRepeatedValue)), "");
                                        receivedValues = receivedValues.replaceFirst(Pattern.quote(String.valueOf(firstRepeatedValue)), "");
                                        break;
                                    }
                                }
                            }
                            
                            for(int i = 0; i < receivedValues.length(); i++) {
                                if (secondRepeatedValue != 0) break;
                                for (int j = 0; j < receivedValues.length(); j++) {
                                    if (i == j) continue;
                                    if (receivedValues.charAt(i) == receivedValues.charAt(j)) {
                                        secondRepeatedValue = Character.getNumericValue(receivedValues.charAt(i));
                                        break;
                                    }
                                }
                            }
                            
                            userScore += firstRepeatedValue * 2 + secondRepeatedValue * 2;
                            play(PAIRS,userScore); 
                        }
                        if (line.startsWith(TRIO) ) {
                            int userScore = 0;
                            String receivedValues =  line.substring(TRIO.length()) ;
                            int repeatedValue = 0, repeatedCount = 0;
                            for(int i = 0; i < receivedValues.length(); i++) {
                                if (repeatedValue != 0) break;
                                repeatedCount++;
                                for (int j = 0; j < receivedValues.length(); j++) {
                                    if (i == j) continue;
                                    if (receivedValues.charAt(i) == receivedValues.charAt(j)) {
                                        repeatedCount++;
                                        if (repeatedCount == 3) {
                                            repeatedValue = Character.getNumericValue(receivedValues.charAt(i));
                                            break;
                                        } else if (j == receivedValues.length() - 1) {
                                            repeatedCount = 0;
                                        }
                                    }
                                }
                            }
                            userScore += repeatedValue * 3;
                            play(TRIO, userScore);
                        }
                        if (line.startsWith(SMALL_STRIT) ) {
                            int userScore = 0;
                            String receivedValues =  line.substring(SMALL_STRIT.length());
                            if (receivedValues.contains("1") && receivedValues.contains("2") && receivedValues.contains("3") && 
                                    receivedValues.contains("4") && receivedValues.contains("5")) {
                                userScore = 15;
                            }
                            play(SMALL_STRIT,userScore); 
                        }
                        if (line.startsWith(BIG_STRIT) ) {
                            int userScore = 0;
                            String receivedValues =  line.substring(BIG_STRIT.length());
                            if (receivedValues.contains("6") && receivedValues.contains("2") && receivedValues.contains("3") && 
                                    receivedValues.contains("4") && receivedValues.contains("5")) {
                                userScore = 20;
                            }
                            play(BIG_STRIT,userScore); 
                        }
                        if (line.startsWith(CARRIAGE) ) {
                            int userScore = 0;
                            String receivedValues =  line.substring(CARRIAGE.length());
                            int repeatedValue = 0, repeatedCount = 0;
                            for(int i = 0; i < receivedValues.length(); i++) {
                                if (repeatedValue != 0) break;
                                repeatedCount++;
                                for (int j = 0; j < receivedValues.length(); j++) {
                                    if (j == receivedValues.length() - 1) {
                                        repeatedCount = 0;
                                    }
                                    if (i == j) continue;
                                    if (receivedValues.charAt(i) == receivedValues.charAt(j)) {
                                        repeatedCount++;
                                        if (repeatedCount == 4) {
                                            repeatedValue = Character.getNumericValue(receivedValues.charAt(i));
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            userScore = (repeatedValue * 4) + (repeatedValue == 0 ? 0 : 20);
                            play(CARRIAGE, userScore);
                        }
                        if (line.startsWith(POKER) ) {
                            int userScore = 0;
                            String receivedValues =  line.substring(POKER.length());
                            int repeatedValue = 0, repeatedCount = 0;
                            for(int i = 0; i < receivedValues.length(); i++) {
                                if (repeatedValue != 0) break;
                                repeatedCount++;
                                for (int j = 0; j < receivedValues.length(); j++) {
                                    if (j == receivedValues.length() - 1) {
                                        repeatedCount = 0;
                                    }
                                    if (i == j) continue;
                                    if (receivedValues.charAt(i) == receivedValues.charAt(j)) {
                                        repeatedCount++;
                                        if (repeatedCount == 5) {
                                            repeatedValue = Character.getNumericValue(receivedValues.charAt(i));
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            userScore = (repeatedValue * 5) + (repeatedValue == 0 ? 0 : 50);
                            play(POKER, userScore);
                        }
                        if (line.startsWith(CHANCE) ) {
                            int userScore = 0;
                            String receivedValues =  line.substring(CHANCE.length());
                            
                            for(int i = 0; i < receivedValues.length(); i++) {
                                userScore += Character.getNumericValue(receivedValues.charAt(i));                            
                            }
                            play(CHANCE, userScore);
                        }
                        if (line.startsWith(POST_COMMAND) ) {
                            line = line.substring(POST_COMMAND.length());
                            censorWords(); 
                            if(line.startsWith("/private"))
                            {
                                privateMessage(line);       
                            }
                            else
                            {
                                 sendToEveryone(line);    
                            }
                        }
                        if (line.startsWith(LOGOUT_COMMAND) ) {
                             sendToEveryone("Opuścił czat");
                             synchronized(players) {
                                nicks.remove(nick);
                                players.remove(this);  
                            } 
                            if(!players.isEmpty())
                            { 
                                move();
                                activePlayerIndex--;
                            }
                        } 
                }
            }catch(IOException e) {
                sendToEveryone("Opuścił czat");
                synchronized(players) {
                   nicks.remove(nick);
                   players.remove(this);  
                }
                if(!players.isEmpty())
                { 
                    move();
                    activePlayerIndex--;
                }
            }catch(NullPointerException op) { //System.out.println(op); 
            }finally {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch(IOException e) {}
            }    
        }    
    }   
}


 
}




class Main{
   public static void main(String[] args ) {
        new YahtzeeServer();
    }
}