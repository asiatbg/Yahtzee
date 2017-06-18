
package yahtzee;

public interface Protocol {

    public static final String PLAYERS_COMMAND = "PLAYERS:";
    public static final String WAIT_COMMAND = "WAIT:"; 
    public static final String PLAY_COMMAND = "PLAY:"; 
    public static final String ADD_COL_COMMAND = "ADD_COL:"; 
    public static final String UPDATE_COL_COMMAND = "UPDATE_COL:"; 
    public static final String POST_COMMAND = "POST:"; 
    public static final String LOGIN_COMMAND = "LOGIN:"; 
    public static final String LOGOUT_COMMAND = "LOGOUT:"; 
    public static final String NICK_COMMAND = "NICK:"; 
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
    public static final String SUM = "SUM";
    public static final String END_GAME = "END_GAME";
    public static final String WINNER = "WINNER";
    public static final String LOSER = "LOSER";
    public static final String NEXT_PLAYER = "NEXT_PLAYER";
    
}