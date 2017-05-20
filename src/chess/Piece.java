package chess;

public abstract class Piece {
	
	String name;
	int posx,posy;
	String color;
	boolean canAttackKing;
	String sqrColor;

	/**
	 * Piece Constructor
	 * 
	 * @param name Name of piece 
	 * @param posx X coordinate of piece's current location
	 * @param posy Y coordinate of piece's current location
	 * @param color Color of the piece
	 * @param sqrColor Color of the square the piece is standing on
	 */
	public Piece(String name,int posx, int posy, String color, String sqrColor)
	{
		this.name = name;
		this.posx = posx;
		this.posy = posy;
		this.color = color;
		this.canAttackKing = false;
		this.sqrColor = sqrColor;
	}
	
	/**
	 * Returns true if the move the user played succeeded, false 
	 * otherwise. If the move is legal, this method will handle
	 * moving the piece at the location specified by currX and currY 
	 * to the destination specified by destX and destY. If the move 
	 * involves capturing a piece, this will also be handled.
	 * 
	 * @param currX X coordinate of piece's current location
	 * @param currY Y coordinate of piece's current location
	 * @param destX X coordinate of piece's destination
	 * @param destY Y coordinate of piece's destination
	 * @return true if move occurs, false otherwise
	 */
	public abstract boolean move(int currX, int currY, int destX, int destY );
	/**
	 * Checks if the piece that the user wants to move can actually 
	 * move to the destination location specified by destX and destY.
	 * 
	 * @param destX X coordinate of piece's destination
	 * @param destY Y coordinate of piece's destination
	 * @return true if move is legal, false otherwise
	 */
	public abstract boolean isLegalMove(int destX, int destY);
	/**
	 * Checks if the current piece's path to destination 
	 * destX,destY is not blocked.
	 * 
	 * @param destX X coordinate of piece's destination
	 * @param destY Y coordinate of piece's destination
	 * @return true if the current piece's path to destination 
	 * destX,destY is not blocked, false otherwise
	 */
	public abstract boolean isPathClear(int destX, int destY);
	
	/**
	 * Determines if an opponent's piece that can attack your King can be either 
	 * captured or have its path blocked by one of your pieces
	 * @param destX X coordinate of piece's destination
	 * @param destY Y coordinate of piece's destination
	 * @return true if attacking piece can be intercepted, false otherwise
	 */
	public abstract boolean canBeIntercepted(int destX, int destY);
	
	/**
	 * Takes the user's move and converts it into an int array 
	 * (length=6) of the following format: 
	 * </p> resultArray[0] = row index of the piece to be moved
	 * <br> resultArray[1] = column index of the piece to be moved
	 * <br> resultArray[2] = row index of the piece's destination
	 * <br> resultArray[3] = column index of the piece's destination
	 * <br> resultArray[4] = action code
	 * <br> resultArray[5] = ASCII value of character used only for 
	 * pawn promotion case, O if no promotion occurring
	 * </p>
	 * Action Codes are the following:
	 * </p>
	 * 0 if nothing happened or draw is declined <br>
	 * 1 if there is a draw offer <br>
	 * 2 if the entire input move is the word draw meaning the other player 
	 * accepted the draw. <br>
	 * 3 if the player resigns
	 * </p>
	 * 
	 * @param move String representing the player's move
	 * @return int array of length 6
	 */
	public static int[] convertMove(String move)
	{
		int[] newMove = new int[6];
		
		//Player is doing a normal move
		if(move.length()==5)
		{
		//First char is column number, second char is row number
		String curr = move.substring(0,2);
		String dest = move.substring(3,5);
				
		newMove[0] = Math.abs(curr.charAt(1)-'0'-8);
		newMove[1] = curr.charAt(0)-97;
		newMove[2] = Math.abs(dest.charAt(1)-'0'-8);
		newMove[3] = dest.charAt(0)-97;
		}
		//Player makes move and offers a draw to the opponent 
		else if(move.length()==11)
		{
			//First char is column number, second char is row number
			String curr = move.substring(0,2);
			String dest = move.substring(3,5);
					
			newMove[0] = Math.abs(curr.charAt(1)-'0'-8);
			newMove[1] = curr.charAt(0)-97;
			newMove[2] = Math.abs(dest.charAt(1)-'0'-8);
			newMove[3] = dest.charAt(0)-97;
			newMove[4] = 1;
		
			//Determines if the current player offered a draw and updates their flag accordingly
			if(Chess.chessBoard.turn.equals("White's Move: "))
			{
				Chess.white.offeredDraw = true;
			}
			else if(Chess.chessBoard.turn.equals("Black's Move: "))
			{
				Chess.black.offeredDraw = true;
			}
		}
		else if(move.length()==4)
		{
			//Other player accepted the draw offer
			newMove[4] = 2;
		}
		else if(move.length()==6)
		{
			//Current player resigns
			newMove[4] = 3;
		}
		else if(move.length()==7)
		{
		//First char is column number, second char is row number
		String curr = move.substring(0,2);
		String dest = move.substring(3,5);
				
		newMove[0] = Math.abs(curr.charAt(1)-'0'-8);
		newMove[1] = curr.charAt(0)-97;
		newMove[2] = Math.abs(dest.charAt(1)-'0'-8);
		newMove[3] = dest.charAt(0)-97;
		newMove[5] = move.charAt(6)-'0';
		}
		
		return newMove;
	}
	
	/**
	 * Checks to see if the user's move is within the gameboard's 
	 * boundaries.
	 * 
	 * @param coordinates Array containing the coordinates you want to check
	 * @return true if move is within the gameboard's boundaries,
	 * false otherwise
	 */
	public static boolean inBounds(int[] coordinates) {
		
		int numItems = coordinates.length;
		
		if(numItems > 4)
		{
			numItems = 4;
		}
		
		for(int i = 0; i<numItems; i ++) {
			if(coordinates[i] <0 || coordinates[i]>7) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 *This method checks to see what pieces can 
	 *attack an opposing King and updates their boolean 
	 *canAttackKing flags to true if they can attack their enemy's 
	 *King, false otherwise.
	 */
	public static void updateAttackCapabilities()
	{
		int[] wKloc = Chess.chessBoard.wKingLocation;
		int[] bKloc = Chess.chessBoard.bKingLocation;
		
		String opposingColor = null;
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				Piece currPiece = Chess.chessBoard.pieces[i][j];
				
				if(currPiece!=null)
				{
				//Determine opposing color
				if(currPiece.color.equals("white"))
				{
					opposingColor = "black";
				}
				else if(currPiece.color.equals("black"))
				{
					opposingColor = "white";
				}
				
				
				/*Determine if the current piece can attack the 
				*opposing color's King 
				*/
				if(opposingColor.equals("white") && currPiece.isLegalMove(wKloc[0],wKloc[1]))
				{
					currPiece.canAttackKing = true;
				}
				else if(opposingColor.equals("black") && currPiece.isLegalMove(bKloc[0],bKloc[1]))
				{
					currPiece.canAttackKing = true;
				}
				else 
				{
					currPiece.canAttackKing = false;
				}
				}
			}
		}
	}
	
	/**
	 * Determines if the specified spot on the gameboard is empty
	 * @param spot Array representing the spot's X and Y locations 
	 * @return true if spot is empty, false otherwise
	 */
	public static boolean isSpotEmpty(int[] spot)
	{
		if(Chess.chessBoard.pieces[spot[0]][spot[1]]!=null)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Determines if the specified spot on the gameboard is 
	 * occupied by an enemy piece.
	 * @param currPiece Array representing the current piece's location
	 * @param target Array representing the location of the enemy piece
	 * @return true if spot is occupied by an enemy piece, false
	 * otherwise
	 */
	public static boolean isEnemy(int[] currPiece, int[] target)
	{
		if(Chess.chessBoard.pieces[target[0]][target[1]]==null)
		{
			return false;
		}
		else if(Chess.chessBoard.pieces[currPiece[0]][currPiece[1]].color.equals(Chess.chessBoard.pieces[target[0]][target[1]].color))
		{
			return false;
		}
	
		return true;
	}
	
	/**
	 * Determines if the King at the given coordinates is in check
	 * 
	 * @param kingSpace Array representing the location of the input king
	 * @return true if King is in check, false otherwise
	 */
	public static boolean inCheck(int[] kingSpace)
	{
		int x = kingSpace[0];
		int y = kingSpace[1];
		
		Piece target = Chess.chessBoard.pieces[x][y];
		
		String opposingColor = null;
		
		if(target.color.equals("white"))
		{
			opposingColor = "black";
		}
		else if(target.color.equals("black"))
		{
			opposingColor = "white";
		}
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(Chess.chessBoard.pieces[i][j] != null && Chess.chessBoard.pieces[i][j].canAttackKing && opposingColor.equals(Chess.chessBoard.pieces[i][j].color))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**Checks prior to moving if the move will leave your king 
	*in check (not allowed) by temporarily altering piece array
	*to the state it would've been in after this method returns false
	*no matter the return value everything in the pieces array will 
	*return to the way it was prior to this method. 
	*
	*@param piece Current Piece 
	*@param kingSpace Array representing the location of the input king
	*@param destX X coordinate of piece's destination
	*@param destY Y coordinate of piece's destination
	*@return true if moving the piece to the destination destX,destY 
	*will cause that same player's King to be put in check, false otherwise
	*/
	public static boolean preCheck(Piece piece, int[] kingSpace, int destX, int destY)
	{
		//Saves current location of the argument piece
		int currX = piece.posx;
		int currY = piece.posy;
		//Saves whatever is currently at the destination square
		Piece tmp = Chess.chessBoard.pieces[destX][destY];
		
		//Removes the argument piece from its initial square
		Chess.chessBoard.pieces[currX][currY]=null;
		
		/*Sets the argument piece's current position to its desired 
		 * destination square
		 * */
		piece.posx = destX;
		piece.posy = destY;
		Chess.chessBoard.pieces[destX][destY]=piece;
		
		/*If the piece is a King, must update King's location variable 
		 * which is being held by the chessBoard
		 */
		if(piece instanceof King)
		{
			if(piece.color.equals("white"))
			{
				Chess.chessBoard.wKingLocation[0]=piece.posx;
				Chess.chessBoard.wKingLocation[1]=piece.posy;
			}
			else if(piece.color.equals("black"))
			{
				Chess.chessBoard.bKingLocation[0]=piece.posx;
				Chess.chessBoard.bKingLocation[1]=piece.posy;
			}
		}
		
		updateAttackCapabilities();
		
		int x = kingSpace[0];
		int y = kingSpace[1];
		
		King target = (King) Chess.chessBoard.pieces[x][y];
		
		String opposingColor = null;
		
		if(target.color.equals("white"))
		{
			opposingColor = "black";
		}
		else if(target.color.equals("black"))
		{
			opposingColor = "white";
		}
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(Chess.chessBoard.pieces[i][j]!=null && Chess.chessBoard.pieces[i][j].canAttackKing && opposingColor.equals(Chess.chessBoard.pieces[i][j].color))
				{
					Chess.chessBoard.pieces[destX][destY]=null;
					
					piece.posx = currX;
					piece.posy = currY;
					Chess.chessBoard.pieces[currX][currY]=piece;
					Chess.chessBoard.pieces[destX][destY]=tmp;
					
					if(piece instanceof King)
					{
						if(piece.color.equals("white"))
						{
							Chess.chessBoard.wKingLocation[0]=piece.posx;
							Chess.chessBoard.wKingLocation[1]=piece.posy;
						}
						else if(piece.color.equals("black"))
						{
							Chess.chessBoard.bKingLocation[0]=piece.posx;
							Chess.chessBoard.bKingLocation[1]=piece.posy;
						}
					}
					
					updateAttackCapabilities();
					return true;
				}
			}
		}
		
		Chess.chessBoard.pieces[destX][destY]=null;
		
		piece.posx = currX;
		piece.posy = currY;
		Chess.chessBoard.pieces[currX][currY]=piece;
		Chess.chessBoard.pieces[destX][destY]=tmp;
		
		if(piece instanceof King)
		{
			if(piece.color.equals("white"))
			{
				Chess.chessBoard.wKingLocation[0]=piece.posx;
				Chess.chessBoard.wKingLocation[1]=piece.posy;
			}
			else if(piece.color.equals("black"))
			{
				Chess.chessBoard.bKingLocation[0]=piece.posx;
				Chess.chessBoard.bKingLocation[1]=piece.posy;
			}
		}
		
		updateAttackCapabilities();
		return false;
	}
	
	/**
	 * Determines if the player who has a king at the specified 
	 * location has been put in checkmate by their opponent
	 * @param kingSpace Array representing the location of the input king
	 * @return true if opponent is in checkmate, false otherwise
	 */
	public static boolean isCheckmate(int[] kingSpace)
	{
		/*
		up = {-1,0};
		down = {1,0};
		left = {0,-1};
		right = {0,1};
		upleft = {-1,-1};
		upright = {-1,1};
		downleft = {1,-1};
		downright = {1,1};
		*/
		int currX = kingSpace[0];
		int currY = kingSpace[1];
		
		King king = (King) Chess.chessBoard.pieces[currX][currY];
		
		int[] allKingMoves = {-1,0,1,0,0,-1,0,1,-1,-1,-1,1,1,-1,1,1};
		int[] dest = new int[2];
		int index = 0;
		
		/*If this false is returned in this loop that means 
		 * the King can move to a nearby spot which means 
		 * it is not checkmate*/
		for(int i=0;i<8;i++)
		{
			int destX = currX+allKingMoves[index];
			int destY = currY+allKingMoves[index+1];
			
			dest[0] = destX;
			dest[1] = destY;
			
			if(Piece.inBounds(dest) && king.isLegalMove(destX,destY) && !Piece.preCheck(king, kingSpace, destX, destY))
			{
				return false;
			}
			
			destX=currX;
			destY=currY;
			index+=2;
		}
		
		Piece attacker = null;
		
		Piece currentPiece = null;
		
		int breaker = 0;
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				currentPiece = Chess.chessBoard.pieces[i][j];
				
				if(currentPiece!=null && !(currentPiece instanceof King) && !currentPiece.color.equals(king.color) && currentPiece.canAttackKing)
				{
					attacker = currentPiece;
					breaker=1;
					break;
				}
			}
			
			if(breaker==1)
			{
				break;
			}
		}
		
		if(attacker!=null && attacker.canBeIntercepted(currX, currY))
		{
			return false;
		}
		else if(attacker==null)
		{
			return false;
		}

		return true;
	}
	
}
