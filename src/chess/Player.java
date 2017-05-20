package chess;

public class Player {
	
	/**
	 * Represents the player's color, same as color of chess pieces
	 */
	String color;
	/**
	 * Flag to determine if a draw was offered by this player
	 */
	boolean offeredDraw;
	/**
	 * Flag to determine if a draw was accepted by this player
	 */
	boolean acceptedDraw;
	/**
	 * Flag to signify that this player has been put in check by the
	 * opposing player
	 */
	boolean inCheck;
	/**
	 * Flag to signify that this player has been put in checkmate by the
	 * opposing player
	 */
	boolean checkMate;
	
	/**
	 * Player constructor
	 * @param color Player's color (same as color of their pieces)
	 */
	public Player(String color)
	{
		this.color = color;
		offeredDraw = false;
		inCheck = false;
		checkMate = false;
		acceptedDraw = false;
	}

}
