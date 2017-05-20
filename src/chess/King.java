package chess;

public class King extends Piece{
	
	/**
	 * Keeps track of whether or not this King has moved
	 */
	boolean hasMoved;
	
	/**
	 * King constructor
	 *
     * @param name Name of piece 
	 * @param posx X coordinate of piece's current location
	 * @param posy Y coordinate of piece's current location
	 * @param color Color of the piece
	 * @param sqrColor Color of the square the piece is standing on
	 */
	public King(String name, int posx, int posy, String color, String sqrColor) {
		super(name, posx, posy, color, sqrColor);

		this.hasMoved = false;
		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean move(int currX, int currY, int destX, int destY )
	{
		int[] kingSpace = new int[2];
		
		if(this.color.equals("white"))
		{
			kingSpace = Chess.chessBoard.wKingLocation;
		}
		else if(this.color.equals("black"))
		{
			kingSpace = Chess.chessBoard.bKingLocation;
		}
		
		if(isLegalMove(destX,destY) && !Piece.preCheck(this, kingSpace, destX, destY))
		{
			this.hasMoved=true;
			
			this.posx = destX;
			this.posy = destY;
			
			if(this.color.equals("white"))
			{
				Chess.chessBoard.wKingLocation[0]=this.posx;
				Chess.chessBoard.wKingLocation[1]=this.posy;
			}
			else if(this.color.equals("black"))
			{
				Chess.chessBoard.bKingLocation[0]=this.posx;
				Chess.chessBoard.bKingLocation[1]=this.posy;
			}
			
			//Capture Piece if a piece is being attacked
			if(Chess.chessBoard.pieces[destX][destY]!=null)
			{
			Chess.chessBoard.board[destX][destY]=Chess.chessBoard.pieces[destX][destY].sqrColor;
			Chess.chessBoard.pieces[destX][destY]=null;
			}
			
			//Update pieces and board arrays
			Chess.chessBoard.pieces[destX][destY]=this;
			Chess.chessBoard.pieces[currX][currY]=null;
			
			Chess.chessBoard.board[currX][currY]=this.sqrColor;
			this.sqrColor = Chess.chessBoard.board[destX][destY];
			Chess.chessBoard.board[destX][destY]=this.name;
			
			updateAttackCapabilities();	
		
			//After move has occurred determines if the opponent is in check
			if(Chess.chessBoard.turn.equals("White's Move: ") && inCheck(Chess.chessBoard.bKingLocation))
			{
				Chess.black.inCheck = true;
			}
			else 
			{
				Chess.black.inCheck = false;
			}
			
			if(Chess.chessBoard.turn.equals("Black's Move: ") && inCheck(Chess.chessBoard.wKingLocation))
			{
				Chess.white.inCheck = true;
			}
			else 
			{
				Chess.white.inCheck = false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isLegalMove(int destX, int destY)
	{
		int[] curr = {this.posx,this.posy};

		int[] dest = {destX,destY};

		int stepUpDown = curr[0]-dest[0];

		int stepLeftRight = curr[1]-dest[1];
		
		int[] kingSpace = new int[2];
		
		if(this.color.equals("white"))
		{
			kingSpace = Chess.chessBoard.wKingLocation;
		}
		else if(this.color.equals("black"))
		{
			kingSpace = Chess.chessBoard.bKingLocation;
		}
		
		//Move straight 
		if(((Math.abs(stepUpDown)==1 && stepLeftRight==0) || (Math.abs(stepLeftRight)==1 && stepUpDown==0)) && isPathClear(destX,destY))
		{
			return true;
		}
		//Move diagonal
		else if((Math.abs(stepUpDown) == Math.abs(stepLeftRight)) && (Math.abs(stepUpDown)==1) && isPathClear(destX,destY))
		{
			return true;
		}
		//Castling Kingside/Queenside
		else if(!this.hasMoved && (Math.abs(stepLeftRight)==2 && stepUpDown==0) && isPathClear(destX,destY))
		{	
			//Player is moving left (Castling Queenside)
			if(stepLeftRight > 0 && Chess.chessBoard.pieces[destX][destY-2] instanceof Rook && !((Rook) Chess.chessBoard.pieces[destX][destY-2]).hasMoved  && isPathClear(destX,destY-1))
			{
				if(!Piece.preCheck(this, kingSpace, destX, destY+1) && !Piece.preCheck(this, kingSpace, destX, destY))
				{
					((Rook) Chess.chessBoard.pieces[destX][destY-2]).castlingMove(destX, destY-2, curr[0], curr[1]-1);
					
					return true;
				}
			}
			//Player is moving left (Castling Kingside)
			else if(stepLeftRight < 0 && Chess.chessBoard.pieces[destX][destY+1] instanceof Rook && !((Rook) Chess.chessBoard.pieces[destX][destY+1]).hasMoved)
			{
				if(!Piece.preCheck(this, kingSpace, destX, destY-1) && !Piece.preCheck(this, kingSpace, destX, destY))
				{
					((Rook) Chess.chessBoard.pieces[destX][destY+1]).castlingMove(destX, destY+1, curr[0], curr[1]+1);
					
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isPathClear(int destX, int destY)
	{
		/*For king possible move directions include: Up/Down/Left/Right, 
		 *Diagonally up/left, Diagonally up/right, Diagonally down/left,
		 *and Diagonally down/right
		 * (Only one square in any of the listed directions)
		 */
		
	int[] curr = {this.posx,this.posy};
	int[] dest = {destX,destY};
	
	int[] tmp = new int[2];
	tmp[0]=curr[0];
	tmp[1]=curr[1];
	
	int stepUpDown = curr[0]-dest[0];
	int stepLeftRight = curr[1]-dest[1];
	
	while(tmp[0]!=dest[0] || tmp[1]!=dest[1])
	{
		//If stepUpDown is > 0 we know the player is moving up the board
		if(stepUpDown > 0 && stepLeftRight == 0)
		{
				tmp[0]--;
				if(tmp[0]==dest[0] && tmp[1]==dest[1] && (isSpotEmpty(tmp) || isEnemy(curr,dest)))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp))
				{
					return false;
				}
		}
		//If stepUpDown is < 0 we know the player is moving down the board
		else if(stepUpDown < 0  && stepLeftRight == 0)
		{
				tmp[0]++;
				if(tmp[0]==dest[0] && tmp[1]==dest[1] && (isSpotEmpty(tmp) || isEnemy(curr,dest)))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp))
				{
					return false;
				}
		}
		//If stepLeftRight > 0 we know the player is moving left on the board
		else if(stepLeftRight > 0 && stepUpDown == 0)
		 {
			for(int i=stepLeftRight;i>0;i--)
			{
				tmp[1]--;
				if(tmp[0]==dest[0] && tmp[1]==dest[1] && (isSpotEmpty(tmp) || isEnemy(curr,dest)))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp))
				{
					return false;
				}
			}
		  }
		//If stepUpDown is < 0 we know the player is moving right on the board
		else if(stepLeftRight < 0  && stepUpDown == 0)
		{
			for(int i=stepLeftRight;i<0;i++)
			{
				tmp[1]++;
				if(tmp[0]==dest[0] && tmp[1]==dest[1] && (isSpotEmpty(tmp) || isEnemy(curr,dest)))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp))
				{
					return false;
				}
			}
		}
		/*If stepUpDown = stepLeftRight and both are greater than 0 
		*we know the player is moving diagonally up/left so they end
		*up one square left and one square up
		*/
		else if(stepUpDown == stepLeftRight && stepUpDown > 0)
		{
				tmp[0]--;
				tmp[1]--;

				if(tmp[0]==dest[0] && tmp[1]==dest[1] && (isSpotEmpty(tmp) || isEnemy(curr,dest)))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp))
				{
					return false;
				}
		}
		/*If stepUpDown = stepLeftRight and both are less than 0 
		*we know the player is moving diagonally down/right so they end
		*up one square right and one square down
		*/
		else if(stepUpDown == stepLeftRight && stepUpDown < 0)
		{
				tmp[0]++;
				tmp[1]++;
				if(tmp[0]==dest[0] && tmp[1]==dest[1] && (isSpotEmpty(tmp) || isEnemy(curr,dest)))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp))
				{
					return false;
				}
		}
		/*If stepUpDown > stepLeftRight and stepUpDown is > 0 
		*we know the player is moving diagonally up/right so they end
		*up one square right and up one square
		*/
		else if(stepUpDown > stepLeftRight && stepUpDown > 0)
		{
				tmp[0]--;
				tmp[1]++;
				if(tmp[0]==dest[0] && tmp[1]==dest[1] && (isSpotEmpty(tmp) || isEnemy(curr,dest)))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp))
				{
					return false;
				}
		}
		/*If stepUpDown < stepLeftRight and stepUpDown is < 0 
		*we know the player is moving diagonally down/left so they end
		*up one square left and one square down
		*/
		else if(stepUpDown < stepLeftRight && stepUpDown < 0)
		{
				tmp[0]++;
				tmp[1]--;
				
				if(tmp[0]==dest[0] && tmp[1]==dest[1] && (isSpotEmpty(tmp) || isEnemy(curr,dest)))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp))
				{
					return false;
				}
		}
	}
	return false;
}
	/**
	 * {@inheritDoc}.
	 *In the case of the King this method doesn't apply so it will
	 *do nothing 
	 *
	 */
	public boolean canBeIntercepted(int destX, int destY)
	{
		return false;
	}
}
