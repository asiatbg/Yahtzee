/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yahtzee;

public interface Protocol {
    public static final String PLAYERS_COMMAND = "PLAYERS:"; // ile graczy ma grać
    public static final String WAIT_COMMAND = "WAIT:"; // wait na kolej gracza
    public static final String PLAY_COMMAND = "PLAY:"; // gracz może grać ;)a masło jest maślane.... 
    public static final String ADD_COL_COMMAND = "ADD_COL:"; // dodanie kolumny z nickiem gracza
    public static final String UPDATE_COL_COMMAND = "UPDATE_COL:"; // przesyła nowemu graczowi nicki graczy już zalogowanych
    public static final String POST_COMMAND = "POST:"; // wysyłka wiadomości
    public static final String NICKLIST_COMMAND = "LIST:"; // lista nicków
    public static final String LOGIN_COMMAND = "LOGIN:"; // zalogowanie
    public static final String LOGOUT_COMMAND = "LOGOUT:"; // wylogowanie
    public static final String REMOVE_COL_COMMAND = "REMOVE:"; // usunięcie kolumny 
    public static final String NICK_COMMAND = "NICK:"; // prośba o nick
    public static final String SEND_COMMAND = "SEND:";
    public static final String ONE = "ONE";
    public static final String TWO = "TWO";
    public static final String THREE = "THREE";
    public static final String FOUR = "FOUR";
    public static final String FIVE = "FIVE";
    public static final String SIX = "SIX";
    public static final String PAIR = "1PAIR";
    public static final String PAIRS = "2PAIRS";
    public static final String TRIO = "TRIO";
    public static final String SMALL_STRIT = "SMALL";
    public static final String BIG_STRIT = "BIG";
    public static final String CARRIAGE = "CARRIAGE";
    public static final String POKER = "POKER";
    public static final String CHANCE = "CHANCE"; 
}