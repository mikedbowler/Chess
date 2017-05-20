package chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Chess {
	
	/**
	 * Global variable used to hold the game board
	 */
	public static GameBoard chessBoard = new GameBoard();
	/**
	 * Global variable for White player
	 */
	public static Player white = new Player("White");
	/**
	 * Global variable for Black player
	 */
	public static Player black = new Player("Black");

	/**
	 * Main method used for running the Chess Program
	 * @param args Code statements
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		//Draws Board Initially for Display Purposes
		chessBoard.drawBoard();
		
		while(true)
		{
		
		if((white.inCheck && chessBoard.turn.equals("White's Move: ")) || (black.inCheck && chessBoard.turn.equals("Black's Move: ")))
		{
			System.out.println("Check");
		}
			
			
		System.out.print(chessBoard.turn);
		String move = br.readLine();
		int[] newMove = Piece.convertMove(move);
		
		int currX = newMove[0];
		int currY = newMove[1];
		int destX = newMove[2];
		int destY = newMove[3];
		/*This action variable will be 0 if nothing happened or draw
		 * is declined. It will be 1 if there is a draw offer, 2 if 
		 * the entire move is the word draw meaning the other player 
		 * accepted the draw. Or 3 if the player resigns
		 */
		int action = newMove[4]; 
		char np = (char) (newMove[5]+'0');

		//Determines which course of action to take
		if(action==0 || action==1)
		{
		 chessBoard.moveAndDraw(currX, currY, destX, destY, np);
		}
		else if(action==2 && (white.offeredDraw || black.offeredDraw))
		{
			System.out.println("Draw");
			return;
		}
		else if(action==3)
		{
			if(chessBoard.turn.equals("White's Move: "))
			{
				System.out.println("Black wins");
				return;
			}
			else if(chessBoard.turn.equals("Black's Move: "))
			{
				System.out.println("White wins");
				return;
			}
		}
		
		}

	}

}
