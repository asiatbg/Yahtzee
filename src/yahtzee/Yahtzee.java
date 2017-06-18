/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yahtzee;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import static yahtzee.Protocol.CHANCE;
import static yahtzee.Protocol.END_GAME;
import static yahtzee.Protocol.WINNER;

class YahtzeeClient extends JFrame implements Protocol{
    private JButton bConnect;
    private JButton bDisconnect;
    private JButton bThrow;
    private JLabel textServer;
    private JLabel chance3;
    private JLabel textPort;
    private JLabel chance1;
    private JLabel chance2;
    private JPanel topPanel;
    private JPanel westPanel;
    private JPanel eastPanel;
    private JScrollPane jScrollPane;
    private JScrollPane jScrollPane2;
    private JTable playersTable;
    private JTextArea chat;
    private JTextField jTextFieldServer;
    private JTextField jTextFieldPort;
    private JTextField message;
    private DefaultTableModel tableModel;
    private final int PORT = 2345;
    private final String SERVER = "localhost";
    private int bThrowClick = 0;
    private JLabel [] dices = new JLabel[5];
    private int[] dicesValues = new int [5];
    private boolean connected;
    private Socket socket;
    private ClientThread thread;
     List<Object> movesUsed = new ArrayList<>();
    public YahtzeeClient() throws InterruptedException{
        super("Gra w kości");
        
        topPanel = new JPanel();
        textServer = new JLabel();
        textPort = new JLabel();
        bConnect = new JButton();
        bDisconnect = new JButton();
        jTextFieldServer = new JTextField(SERVER);
        jTextFieldPort = new JTextField(String.valueOf(PORT));
        westPanel = new JPanel();
        dices[0] = new JLabel();
        dices[1] = new JLabel();
        dices[2] = new JLabel();
        dices[3] = new JLabel();
        dices[4] = new JLabel();
        chance1 = new JLabel();
        chance2 = new JLabel();
        chance3 = new JLabel();
        bThrow = new JButton();
        jScrollPane2 = new JScrollPane();
        playersTable = new JTable();
        eastPanel = new JPanel();
        chat = new JTextArea(5,20);
        jScrollPane = new JScrollPane(chat);
        message = new JTextField();
 

        topPanel.setBackground(new java.awt.Color(255, 255, 255));

        textServer.setText("Serwer:");

        textPort.setText("Port:");

        bConnect.setText("Połącz");

        bDisconnect.setText("Rozłącz");

        GroupLayout topLayout = new GroupLayout(topPanel);
        topPanel.setLayout(topLayout);
        topLayout.setHorizontalGroup(topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textServer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldServer, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(textPort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(bConnect)
                .addGap(18, 18, 18)
                .addComponent(bDisconnect)
                .addGap(96, 96, 96))
        );
        topLayout.setVerticalGroup(topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bDisconnect)
                    .addComponent(bConnect)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textPort)
                    .addComponent(jTextFieldServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textServer))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        
        dices[0].setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/emptyDice.png"))); 

        dices[1].setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/emptyDice.png"))); 

        dices[2].setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/emptyDice.png"))); 

        dices[3].setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/emptyDice.png"))); 

        dices[4].setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/emptyDice.png"))); 

        chance1.setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/chance.png"))); 

        chance2.setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/chance.png"))); 

        chance3.setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/chance.png"))); 

        bThrow.setText("Rzuć");
        
        westPanel.setBackground(new java.awt.Color(255, 255, 255));
        GroupLayout westLayout = new GroupLayout(westPanel);
        westPanel.setLayout(westLayout);
        westLayout.setHorizontalGroup(westLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(westLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(westLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dices[4])
                    .addComponent(dices[2])
                    .addGroup(westLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(dices[1])
                        .addComponent(dices[0]))
                    .addComponent(dices[3])
                    .addGroup(westLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(bThrow, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(westLayout.createSequentialGroup()
                            .addComponent(chance1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(chance2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(chance3))))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        westLayout.setVerticalGroup(westLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(westLayout.createSequentialGroup()
                .addComponent(dices[0])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dices[1])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dices[2])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dices[3])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dices[4])
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(westLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chance1)
                    .addComponent(chance2)
                    .addComponent(chance3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bThrow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
       
        tableModel = new DefaultTableModel(new Object[] { "Kategoria"}, 15){
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        playersTable = new JTable(tableModel);       
        playersTable.setValueAt("1", 0, 0);
        playersTable.setValueAt("2", 1, 0);
        playersTable.setValueAt("3", 2, 0);
        playersTable.setValueAt("4", 3, 0);
        playersTable.setValueAt("5", 4, 0); 
        playersTable.setValueAt("6", 5, 0);
        playersTable.setValueAt("1 para", 6, 0);
        playersTable.setValueAt("2 pary", 7, 0);
        playersTable.setValueAt("trójka", 8, 0);
        playersTable.setValueAt("mały strit", 9, 0);
        playersTable.setValueAt("duży strit", 10, 0);
        playersTable.setValueAt("kareta", 11, 0);
        playersTable.setValueAt("poker", 12, 0);
        playersTable.setValueAt("szansa", 13, 0);
        playersTable.setValueAt("suma", 14, 0);
        playersTable.setGridColor(new Color(0, 0, 0));
        playersTable.setSelectionBackground(new Color(102, 102, 255));
        playersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(playersTable);

        eastPanel.setBackground(new Color(255, 255, 255));

        
       

       GroupLayout eastLayout = new GroupLayout(eastPanel);
        eastPanel.setLayout(eastLayout);
        eastLayout.setHorizontalGroup(eastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(message, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
        );
        eastLayout.setVerticalGroup(eastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eastLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(message, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        GroupLayout centerLayout = new GroupLayout(getContentPane());
        getContentPane().setLayout(centerLayout);
        centerLayout.setHorizontalGroup(centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(westPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(eastPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        centerLayout.setVerticalGroup(centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centerLayout.createSequentialGroup()
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(eastPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(westPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        
        ButtonListener buttonListener = new ButtonListener();
        bConnect.addActionListener(buttonListener);
        bDisconnect.addActionListener(buttonListener);
        bThrow.addActionListener(buttonListener);
        bThrow.setEnabled(false);
        DiceClickedAdapter mouseListener = new DiceClickedAdapter();
        dices[0].addMouseListener(mouseListener);
        dices[1].addMouseListener(mouseListener);
        dices[2].addMouseListener(mouseListener);
        dices[3].addMouseListener(mouseListener);
        dices[4].addMouseListener(mouseListener);
        
        TableClickedAdapter tableAdapter = new TableClickedAdapter();
        playersTable.addMouseListener(tableAdapter);
        getContentPane().setBackground(Color.WHITE);
        
        JTextClickedAdapter keyListener = new JTextClickedAdapter();
        message.addKeyListener(keyListener);
        
        
        pack();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);  
    }
    
    
    private class ButtonListener implements ActionListener{
       
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == bConnect)
            {
                try {     
                   socket = new Socket(SERVER, PORT);
                } catch (IOException ex) {
                    Logger.getLogger(YahtzeeClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                bConnect.setEnabled(false);
                bDisconnect.setEnabled(true);
                connected = true; 
                thread = new ClientThread(socket);
                thread.start(); 
                movesUsed.clear();
            }
            if (source == bDisconnect)
            {
               thread.send(LOGOUT_COMMAND, "");
               bThrowClick = 0;
               chance1.setVisible(true);
               chance2.setVisible(true);
               chance3.setVisible(true);
               bThrow.setEnabled(false);
               bDisconnect.setEnabled(false);
               bConnect.setEnabled(true);
               connected = false;
               cleanTable();
               resetAllDices();
            }
            if (source == bThrow && bThrowClick == 0)
            {
                bThrowClick++;
                for (int i = 0; i < 5; i++)
                {
                    dicesValues[i] = throwDice(dices[i]);
                }  
                chance1.setVisible(false);     
            }
            else if (source == bThrow && bThrowClick == 1)
            {
                bThrowClick++;
                chance2.setVisible(false); 
                for (int i = 0; i < 5; i++)
                {
                    if( checkIfEmpty(dices[i]) )
                    dicesValues[i] = throwDice(dices[i]);   
                } 
                chance2.setVisible(false);
            }  
            else if (source == bThrow && bThrowClick == 2)
            {
                bThrowClick++;   
                for (int i = 0; i < 5; i++)
                {
                    if( checkIfEmpty(dices[i]) )
                        dicesValues[i] = throwDice(dices[i]);           
                }
                chance3.setVisible(false);
                bThrow.setEnabled(false);
            }    
        }
    }
     public void cleanTable(){
         tableModel.fireTableDataChanged();
         tableModel.setColumnCount(1);
         chat.setText("");
     }
    
    private class DiceClickedAdapter extends MouseAdapter{

        @Override
        public void mouseClicked(MouseEvent e) {
            Object source = e.getSource();  
            if( source == dices[0])
            {
                resetDice(dices[0]);
            }
            if( source == dices[1])
            {
                resetDice(dices[1]);
            }
            if( source == dices[2])
            {
                resetDice(dices[2]);
            }
             if( source == dices[3])
            {
                resetDice(dices[3]);
            }
            if( source == dices[4])
            {
                resetDice(dices[4]);
            }
        }   
    }    
    
    public int throwDice(JLabel dice){
        int random, diceValue = 0;
        random = (int) ( Math.random() * 6) + 1;
        
        switch(random){
            case 1:
                dice.setIcon( new ImageIcon(getClass().getResource("/yahtzee/res/dice1.png") ) ); 
                diceValue = 1;
                break;
            case 2:
                dice.setIcon( new ImageIcon(getClass().getResource("/yahtzee/res/dice2.png") ) ); 
                diceValue = 2;
                break;
            case 3:
                dice.setIcon( new ImageIcon(getClass().getResource("/yahtzee/res/dice3.png") ) ); 
                diceValue = 3;
                break;
            case 4:
                dice.setIcon( new ImageIcon(getClass().getResource("/yahtzee/res/dice4.png") ) ); 
                diceValue = 4;
                break;
            case 5:
                dice.setIcon( new ImageIcon(getClass().getResource("/yahtzee/res/dice5.png") ) ); 
                diceValue = 5;
                break;
            case 6:
                dice.setIcon( new ImageIcon(getClass().getResource("/yahtzee/res/dice6.png") ) ); 
                diceValue = 6;
                break;      
        }  
        return diceValue;
    }
    
    public void resetAllDices(){
        for (int i = 0; i < 5; i++)
        {
            dices[i].setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/emptyDice.png")));
        } 
    }
    
    public void resetDice(JLabel dice){
        dice.setIcon(new ImageIcon(getClass().getResource("/yahtzee/res/emptyDice.png")));
    }
    
    public boolean checkIfEmpty(JLabel dice){
        String check = String.valueOf( dice.getIcon()); 
        
        if(check.endsWith("emptyDice.png"))
            return true;        
        return false;
    }
    
    private class TableClickedAdapter extends MouseAdapter{
        private int row;
        private Object o;
        private String values = "";
       
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!checkIfDicesWasThrown()) {
                return;
            }
            row = playersTable.rowAtPoint(e.getPoint());
            o = playersTable.getValueAt(row, 0);
            
           
            if (movesUsed.contains(o))
            {
                JOptionPane.showMessageDialog(null,"Kategoria wykorzystana!");
                return;
            } 
            else if (!values.startsWith("0") && o.equals("1"))
            {  
                thread.send(ONE, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("2"))
            {                
                thread.send(TWO, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("3"))
            {
                thread.send(THREE, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("4"))
            {
                thread.send(FOUR, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("5"))
            {
                thread.send(FIVE, values);
            }
            else if (!values.startsWith("0") && o.equals("6"))
            {
                thread.send(SIX, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("1 para"))
            {
                thread.send(PAIR, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("2 pary"))
            {
                thread.send(PAIRS, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("trójka"))
            {
                thread.send(TRIO, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("mały strit"))
            {
                thread.send(SMALL_STRIT, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("duży strit"))
            {
                thread.send(BIG_STRIT, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("kareta"))
            {     
                thread.send(CARRIAGE, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("poker"))
            {        
                thread.send(POKER, values);
                restore();
            }
            else if (!values.startsWith("0") && o.equals("szansa"))
            {               
                thread.send(CHANCE, values);
                restore();
            } 
            movesUsed.add(o);  
            for (int i = 0; i < 5; i++)
            {
                values += dicesValues[i];
                dicesValues[i] = 0;
            }
            
            if (movesUsed.size() >= 14){
//                JOptionPane.showMessageDialog(null,"Koniec gry!");
                thread.send(END_GAME, "");
                
            }
        }
        
        private boolean checkIfDicesWasThrown() {
            for (int i = 0; i < 5; i++) {
                if (dicesValues[i] == 0) {
                    return false;
                }
            }
            return true;
        }
        public void restore(){
            values = "";
            bThrowClick = 0; 
            chance1.setVisible(true);
            chance2.setVisible(true);
            chance3.setVisible(true);
            resetAllDices();
        }
    }
    
    private class JTextClickedAdapter extends KeyAdapter{
            @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == 10) { // kod klawisza Enter
                thread.send(POST_COMMAND, message.getText());
                message.setText("");
            }
        }
    }
    
    private class ClientThread extends Thread {
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private String line;
        int counter = 0;
       
        public ClientThread (Socket s){
            socket = s;   
        }
       
        
        @Override
        public void run(){
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream() ), true);
                send(LOGIN_COMMAND, "");
                while (connected && in.hasNextLine())
                {
                     line = in.nextLine();
                     if(line.startsWith(LOGIN_COMMAND))
                        {                            
                            displayMessage( line.substring( LOGIN_COMMAND.length() ) );
                            String nick = JOptionPane.showInputDialog(null, "Podaj nick: ");
                            send(NICK_COMMAND, nick);
                        }
                     if(line.startsWith(UPDATE_COL_COMMAND))
                        {     
                            String[] nicks = line.substring( UPDATE_COL_COMMAND.length() ).split(" ");   
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                   for(String nick: nicks)
                                    {
                                        tableModel.addColumn(nick);
                                        tableModel.fireTableStructureChanged();
                                    } 
                                }
                            });
                        }
                     if(line.startsWith(ADD_COL_COMMAND))
                        {   
                            String nick = line.substring(ADD_COL_COMMAND.length()); 
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    tableModel.addColumn(nick);
                                    tableModel.fireTableDataChanged();
                                }
                            });
                        }  
                        if (line.startsWith(PLAY_COMMAND)) {  
                            if (movesUsed.size() >= 14) {
                                thread.send(NEXT_PLAYER, "");
                            } else {
                                JOptionPane.showMessageDialog(null,"Twoja kolej, graj!");
                                bThrow.setEnabled(true);
                            }
                        }
                     if(line.startsWith(WAIT_COMMAND))
                        {                            
                           JOptionPane.showMessageDialog(null,"Poczekaj na swoją kolej");
                           bThrow.setEnabled(false);
                        }
                      if(line.startsWith(POST_COMMAND))
                        {                            
                           displayMessage( line.substring( POST_COMMAND.length() ) ); 
                        }
                      if (line.startsWith(ONE) ) {
                            String values = line.substring(ONE.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(ONE);
                            insertPoints(nick, score, sum, index);        
                        }
                        if (line.startsWith(TWO) ) {
                            String values = line.substring(TWO.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(TWO);
                            insertPoints(nick, score, sum, index);       
                        }
                        if (line.startsWith(THREE) ) {
                            String values = line.substring(THREE.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(THREE);
                            insertPoints(nick, score, sum, index);   
                             
                        }
                        if (line.startsWith(FOUR) ) {
                            String values = line.substring(FOUR.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(FOUR);
                            insertPoints(nick, score, sum, index);       
                        }
                        if (line.startsWith(FIVE) ) {
                            String values = line.substring(FIVE.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(FIVE);
                            insertPoints(nick, score, sum, index);    
                        }
                        if (line.startsWith(SIX) ) {
                            String values = line.substring(SIX.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(SIX);
                            insertPoints(nick, score, sum, index);     
                        }
                        if (line.startsWith(PAIR) ) {
                            String values = line.substring(PAIR.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(PAIR);
                            insertPoints(nick, score, sum, index);      
                        }
                        if (line.startsWith(PAIRS) ) {
                            String values = line.substring(PAIRS.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(PAIRS);
                            insertPoints(nick, score, sum, index);        
                        }
                        if (line.startsWith(TRIO) ) {
                            String values = line.substring(TRIO.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(TRIO);
                            insertPoints(nick, score, sum, index);       
                        }
                        if (line.startsWith(SMALL_STRIT) ) {
                            String values = line.substring(SMALL_STRIT.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(SMALL_STRIT);
                            insertPoints(nick, score, sum, index);      
                        }
                        if (line.startsWith(BIG_STRIT) ) {
                            String values = line.substring(BIG_STRIT.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(BIG_STRIT);
                            insertPoints(nick, score, sum, index);         
                        }
                        if (line.startsWith(CARRIAGE) ) {
                            String values = line.substring(CARRIAGE.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(CARRIAGE);
                            insertPoints(nick, score, sum, index);      
                        }
                        if (line.startsWith(POKER) ) {
                            String values = line.substring(POKER.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(POKER);
                            insertPoints(nick, score, sum, index);      
                        }
                        if (line.startsWith(CHANCE) ) {
                            String values = line.substring(CHANCE.length());
                            String score = values.substring(0,3);
                            String nick = values.substring(6);
                            String sum = values.substring(3,6);
                            int index = getIndexValue(CHANCE);
                            insertPoints(nick, score, sum, index);  
                        }
                       if (line.startsWith(WINNER)) {
                           JOptionPane.showMessageDialog(null,"Zwycięstwo!");
                       }
                       if (line.startsWith(LOSER)) {
                           JOptionPane.showMessageDialog(null,"Przegrana!");
                       }
                }
            } catch (IOException ex) {
                Logger.getLogger(YahtzeeClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        public void insertPoints(String nick, String score, String sum, int index){
            if (index == -1) return;
            int columns = playersTable.getColumnCount(); 
            int rows = playersTable.getRowCount();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                for(int i = 1; i < columns  ; i++)
                {
                    if(playersTable.getColumnName(i).equals(nick))
                    {
                        for(int j = 0; j < rows  ; j++)
                        {
                            if(j == index)
                            {
                                playersTable.setValueAt(score, j, i);
                                playersTable.setValueAt(sum, 14, i);
                                tableModel.fireTableDataChanged();
                              
                            }
                        }
                    }    
                }
            }
            }); 
             
        } 
        public int getIndexValue(String category){
            if(category.equals(ONE))
            {
                return  0 ;
            }
            else if (category.equals(TWO))
            {
                return  1 ;
            }
            else if (category.equals(THREE))
            {
                return  2 ;
            }
            else if (category.equals(FOUR))
            {
                return  3 ;
            }
            else if (category.equals(FIVE))
            {
                return 4 ;
            }
            else if (category.equals(SIX))
            {
                return 5 ;
            }
            else if (category.equals(PAIR))
            {
                return 6 ;
            }
            else if (category.equals(PAIRS))
            {
                return 7 ;
            }
            else if (category.equals(TRIO))
            {
                return 8 ;
            }
            else if (category.equals(SMALL_STRIT))
            {
                return  9 ;
            }
            else if (category.equals(BIG_STRIT))
            {
                return  10 ;
            }
            else if (category.equals(CARRIAGE))
            {
                return  11 ;
            }
            else if (category.equals(POKER))
            {
                return  12 ;
            }
            else if (category.equals(CHANCE))
            {
                return   13 ;
            }
            return 17;
        }
        
        
        
        public void send(String protocol, String text){
            out.println(protocol + text);
        }  
        private void displayMessage(String tekst){
            chat.append(tekst + "\n");
            chat.setCaretPosition(chat.getDocument().getLength());
        } 
    }        
}






public class Yahtzee{
    public static void main(String[] args){
        try {               
            new YahtzeeClient();
        } catch (InterruptedException ex) {
            Logger.getLogger(Yahtzee.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
}

 



 