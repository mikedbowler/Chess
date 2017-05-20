package chess;

public class Bishop extends Piece{

	/**
	 * Bishop constructor
	 * 
	 * @param name Name of piece 
	 * @param posx X coordinate of piece's current location
	 * @param posy Y coordinate of piece's current location
	 * @param color Color of the piece
	 * @param sqrColor Color of the square the piece is standing on
	 */
	public Bishop(String name, int posx, int posy, String color, String sqrColor) {
		super(name, posx, posy, color, sqrColor);
		
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
			this.posx = destX;
			this.posy = destY;
			
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
		
		//Move diagonal
		if((Math.abs(stepUpDown) == Math.abs(stepLeftRight)) && isPathClear(destX,destY))
		{
			return true;
		}
			
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isPathClear(int destX, int destY)
	{
		/*For bishop possible move directions include: 
		 *Diagonally up/left, Diagonally up/right, Diagonally down/left,
		 *and Diagonally down/right
		 * (As many as 7 squares and as little as one square in any 
		 * of the listed directions)
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
		/*If stepUpDown = stepLeftRight and both are greater than 0 
		*we know the player is moving diagonally up/left so they end
		*up n squares left and n squares up
		*/
		if(stepUpDown == stepLeftRight && stepUpDown > 0)
		{
			for(int i=0;i<stepUpDown;i++)
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
		}
		/*If stepUpDown = stepLeftRight and both are less than 0 
		*we know the player is moving diagonally down/right so they end
		*up n squares right and n squares down
		*/
		else if(stepUpDown == stepLeftRight && stepUpDown < 0)
		{
			for(int i=0;i>stepUpDown;i--)
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
		}
		/*If stepUpDown > stepLeftRight and stepUpDown is > 0 
		*we know the player is moving diagonally up/right so they end
		*up n squares right and up n squares
		*/
		else if(stepUpDown > stepLeftRight && stepUpDown > 0)
		{
			for(int i=0;i<stepUpDown;i++)
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
		}
		/*If stepUpDown < stepLeftRight and stepUpDown is < 0 
		*we know the player is moving diagonally down/left so they end
		*up n squares left and n squares down
		*/
		else if(stepUpDown < stepLeftRight && stepUpDown < 0)
		{
			for(int i=0;i>stepUpDown;i--)
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
	 }
	 return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean canBeIntercepted(int destX, int destY)
	{
		
		int[] curr = {this.posx,this.posy};
		int[] dest = {destX,destY};
		
		int[] kingSpace = new int[2];
		
		if(this.color.equals("white"))
		{
			kingSpace = Chess.chessBoard.bKingLocation;
		}
		else if(this.color.equals("black"))
		{
			kingSpace = Chess.chessBoard.wKingLocation;
		}
		
		Piece currentPiece = null;
		
		int[] tmp = new int[2];
		tmp[0]=curr[0];
		tmp[1]=curr[1];
		
		int stepUpDown = curr[0]-dest[0];
		int stepLeftRight = curr[1]-dest[1];
		
		while(tmp[0]!=dest[0] || tmp[1]!=dest[1])
		{
			/*If stepUpDown = stepLeftRight and both are greater than 0 
			*we know the player is moving diagonally up/left so they end
			*up n squares left and n squares up
			*/
			if(stepUpDown == stepLeftRight && stepUpDown > 0)
			{
				for(int i=0;i<stepUpDown+1;i++)
				{
					if(tmp[0]==dest[0] && tmp[1]==dest[1])
					{
						break;
					}
					else 
					{
						for(int x=0;x<8;x++)
						{
							for(int y=0;y<8;y++)
							{
								currentPiece = Chess.chessBoard.pieces[x][y];
								
								if(currentPiece!=null && !currentPiece.color.equals(this.color) && currentPiece.isLegalMove(tmp[0],tmp[1]) && !Piece.preCheck(this, kingSpace, tmp[0],tmp[1]))
								{
									return true;
								}
							}
						}
					}
					tmp[0]--;
					tmp[1]--;
				}
			}
			/*If stepUpDown = stepLeftRight and both are less than 0 
			*we know the player is moving diagonally down/right so they end
			*up n squares right and n squares down
			*/
			else if(stepUpDown == stepLeftRight && stepUpDown < 0)
			{
				for(int i=0;i>stepUpDown-1;i--)
				{
					if(tmp[0]==dest[0] && tmp[1]==dest[1])
					{
						break;
					}
					else 
					{
						for(int x=0;x<8;x++)
						{
							for(int y=0;y<8;y++)
							{
								currentPiece = Chess.chessBoard.pieces[x][y];
								
								if(currentPiece!=null && !currentPiece.color.equals(this.color) && currentPiece.isLegalMove(tmp[0],tmp[1]) && !Piece.preCheck(this, kingSpace, tmp[0],tmp[1]))
								{
									return true;
								}
							}
						}
					}
					tmp[0]++;
					tmp[1]++;
				}
			}
			/*If stepUpDown > stepLeftRight and stepUpDown is > 0 
			*we know the player is moving diagonally up/right so they end
			*up n squares right and up n squares
			*/
			else if(stepUpDown > stepLeftRight && stepUpDown > 0)
			{
				for(int i=0;i<stepUpDown+1;i++)
				{
					if(tmp[0]==dest[0] && tmp[1]==dest[1])
					{
						break;
					}
					else 
					{
						for(int x=0;x<8;x++)
						{
							for(int y=0;y<8;y++)
							{
								currentPiece = Chess.chessBoard.pieces[x][y];
								
								if(currentPiece!=null && !currentPiece.color.equals(this.color) && currentPiece.isLegalMove(tmp[0],tmp[1]) && !Piece.preCheck(this, kingSpace, tmp[0],tmp[1]))
								{
									return true;
								}
							}
						}
					}
					tmp[0]--;
					tmp[1]++;
				}
			}
			/*If stepUpDown < stepLeftRight and stepUpDown is < 0 
			*we know the player is moving diagonally down/left so they end
			*up n squares left and n squares down
			*/
			else if(stepUpDown < stepLeftRight && stepUpDown < 0)
			{
				for(int i=0;i>stepUpDown-1;i--)
				{
					if(tmp[0]==dest[0] && tmp[1]==dest[1])
					{
						break;
					}
					else 
					{
						for(int x=0;x<8;x++)
						{
							for(int y=0;y<8;y++)
							{
								currentPiece = Chess.chessBoard.pieces[x][y];
								
								if(currentPiece!=null && !currentPiece.color.equals(this.color) && currentPiece.isLegalMove(tmp[0],tmp[1]) && !Piece.preCheck(this, kingSpace, tmp[0],tmp[1]))
								{
									return true;
								}
							}
						}
					}
					tmp[0]++;
					tmp[1]--;
				}
			}
		}
		return false;
	}
}
