package chess;

public class Pawn extends Piece{
	
	/**
	 * Keep track of whether or not this pawn has moved
	 */
	boolean hasMoved;

	/**
	 * Pawn constructor
	 * 
	 * @param name Name of piece 
	 * @param posx X coordinate of piece's current location
	 * @param posy Y coordinate of piece's current location
	 * @param color Color of the piece
	 * @param sqrColor Color of the square the piece is standing on
	 */
	public Pawn(String name, int posx, int posy, String color, String sqrColor) {
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
		
		int stepUpDown = Math.abs(currX-destX);
		
		if(isLegalMove(destX,destY) && !Piece.preCheck(this, kingSpace, destX, destY))
		{
			this.hasMoved=true;

			this.posx = destX;
			this.posy = destY;
			
			int[] dest = {destX,destY};
			
			/*Flags an En Passant as a possible move on the board 
			 * for one turn only. 
			 * 0 = En Passant can't happen
			 * 1 = En Passant can happen
			 * 2 = En Passant can happen (rare case where both players 
			 * have the option in a row)
			 * 2 or 3 causes flag to decrement by 2 after 
			 * opponent's move
			 */
			if(stepUpDown == 2)
			{
				Piece ep1=null,ep2=null;
				
				if(destY-1>-1)
				{
					ep1 = Chess.chessBoard.pieces[destX][destY-1];
				}
				if(destY+1<8)
				{
					ep2 = Chess.chessBoard.pieces[destX][destY+1];
				}
				
				if(ep1!=null && ep1 instanceof Pawn && !this.color.equals(ep1.color))
				{
					Chess.chessBoard.canPerformEnPassant++;
				}	
				else if(ep2!=null && ep2 instanceof Pawn && !this.color.equals(ep2.color))
				{
					Chess.chessBoard.canPerformEnPassant++;
				}
			}
			
			//If En Passant is occurring
			if(Chess.chessBoard.canPerformEnPassant>=1 && Piece.isSpotEmpty(dest))
			{
				Piece ep1=null,ep2=null;
				
				if(currY-1>-1)
				{
					ep1 = Chess.chessBoard.pieces[currX][currY-1];
				}
				if(currY+1<8)
				{
					ep2 = Chess.chessBoard.pieces[destX][destY+1];
				}
				
				if(this.color.equals("white") && currX==3)
				{
					if(ep1!=null && ep1 instanceof Pawn && !ep1.color.equals("white"))
					{
						enPassantCapture(currX,currY-1);
					}
					else if(ep2!=null && ep2 instanceof Pawn && !ep2.color.equals("white"))
					{
						enPassantCapture(currX,currY+1);
					}
				}
				else if(this.color.equals("black") && currX==4)
				{
					if(ep1!=null && ep1 instanceof Pawn && !ep1.color.equals("black"))
					{
						enPassantCapture(currX,currY-1);
					}
					else if(ep2!=null && ep2 instanceof Pawn && !ep2.color.equals("black"))
					{
						enPassantCapture(currX,currY+1);
					}
				}
			}
			else
			{
				//Capture Piece if a piece is being attacked
				if(Chess.chessBoard.pieces[destX][destY]!=null)
				{
					Chess.chessBoard.board[destX][destY]=Chess.chessBoard.pieces[destX][destY].sqrColor;
					Chess.chessBoard.pieces[destX][destY]=null;
				}
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
	 * Handles the unique form of capturing that a Pawn is capable of
	 * during the execution of an En Passant maneuver
	 * @param targetX X coordinate of enemy being captured
	 * @param targetY Y coordinate of enemy being captured
	 */
	public void enPassantCapture(int targetX, int targetY)
	{
			Chess.chessBoard.board[targetX][targetY]=Chess.chessBoard.pieces[targetX][targetY].sqrColor;
			Chess.chessBoard.pieces[targetX][targetY]=null;
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
		
		//double move
		if(!this.hasMoved && Math.abs(stepUpDown)==2 && stepLeftRight==0 && isPathClear(destX,destY))
		{	
			return true;
		}
		//single move straight 
		else if(((stepUpDown==1 && this.color.equals("white"))|| (stepUpDown==-1&& this.color.equals("black")))
		 && stepLeftRight==0 && isPathClear(destX,destY))
		{
			return true;
		}
		//En Passant
		else if(Chess.chessBoard.canPerformEnPassant>=1 && Piece.isSpotEmpty(dest))
		{
			
			Piece ep1=null,ep2=null;
			
			if(curr[1]-1>-1)
			{
				ep1 = Chess.chessBoard.pieces[curr[0]][curr[1]-1];
			}
			if(curr[1]+1<8)
			{
				ep2 = Chess.chessBoard.pieces[curr[0]][curr[1]+1];
			}
			
			if(this.color.equals("white") && curr[0]==3)
			{
				if(ep1!=null && ep1 instanceof Pawn && !ep1.color.equals("white"))
				{
					return true;
				}
				else if(ep2!=null && ep2 instanceof Pawn && !ep2.color.equals("white"))
				{
					return true;
				}
			}
			else if(this.color.equals("black") && curr[0]==4)
			{
				if(ep1!=null && ep1 instanceof Pawn && !ep1.color.equals("black"))
				{
					return true;
				}
				else if(ep2!=null && ep2 instanceof Pawn && !ep2.color.equals("black"))
				{
					return true;
				}
			}
		}
		//single move diagonal
		else if((stepUpDown== 1 || stepUpDown==-1) && (stepLeftRight==1 || stepLeftRight==-1)
		 && isPathClear(destX,destY))
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
		/*For pawn possible move directions include: Up or Down, 
		 *Diagonally up/left, Diagonally up/right, Diagonally down/left,
		 *and Diagonally down/right
		 * (Depending on whose side is moving the pawn)
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
			for(int i=stepUpDown;i>0;i--)
			{
				tmp[0]--;
				if(!isSpotEmpty(tmp))
				{
					return false;
				}
			}
			
			return true;
		}
		//If stepUpDown is < 0 we know the player is moving down the board
		else if(stepUpDown < 0  && stepLeftRight == 0)
		{
			for(int i=stepUpDown;i<0;i++)
			{
				tmp[0]++;
				if(!isSpotEmpty(tmp))
				{
					return false;
				}
			}
			
			return true;
		}
		/*If stepUpDown = stepLeftRight and both are greater than 0 
		*we know the player is moving diagonally up/left so they end
		*up one square left and one square up
		*/
		else if(stepUpDown == stepLeftRight && stepUpDown > 0)
		{
				tmp[0]--;
				tmp[1]--;
				
				if(isEnemy(curr,tmp))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp) && !isEnemy(curr,tmp))
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
				if(isEnemy(curr,tmp))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp) && !isEnemy(curr,tmp))
				{
					return false;
				}
		}
		/*If stepUpDown > stepLeftRight and stepUpDown is > 0 
		*we know the player is moving diagonally up/right so they end
		*up one square right and one square up
		*/
		else if(stepUpDown > stepLeftRight && stepUpDown > 0)
		{
				tmp[0]--;
				tmp[1]++;
				if(isEnemy(curr,tmp))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp) && !isEnemy(curr,tmp))
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
				if(isEnemy(curr,tmp))
				{
					return true;
				}
				else if(!isSpotEmpty(tmp) && !isEnemy(curr,tmp))
				{
					return false;
				}
		}
		
		
	}	
		return false;
	}
	
	/**
	 * Promotes a Pawn to either a Night,Bishop,Rook, or Queen (Queen 
	 * is the default)
	 * @param newPiece char representing the piece this pawn is being 
	 * promoted to, (N,B,R,Q) default Q
	 */
	public void promote(char newPiece)
	{
		System.out.println("New Piece Type = "+newPiece);
		
		switch (newPiece)
		{
		case 'N':
			
			Piece knight;
			
			if(this.color.equals("white"))
			{
				knight = new Knight("wN ",this.posx,this.posy,this.color,this.sqrColor);
			}
			else 
			{
				knight = new Knight("bN ",this.posx,this.posy,this.color,this.sqrColor);
			}
			//Update pieces and board arrays
			Chess.chessBoard.pieces[knight.posx][knight.posy]=knight;
			Chess.chessBoard.board[knight.posx][knight.posy]=knight.name;
			
			updateAttackCapabilities();	
			
			break;
			
		case 'B':
			
			Piece bishop;
			
			if(this.color.equals("white"))
			{
				bishop = new Bishop("wB ",this.posx,this.posy,this.color,this.sqrColor);
			}
			else 
			{
				bishop = new Bishop("bB ",this.posx,this.posy,this.color,this.sqrColor);
			}
			//Update pieces and board arrays
			Chess.chessBoard.pieces[bishop.posx][bishop.posy]=bishop;
			Chess.chessBoard.board[bishop.posx][bishop.posy]=bishop.name;
			
			updateAttackCapabilities();	
			
			break;
			
		case 'R':
			Piece rook;
			
			if(this.color.equals("white"))
			{
				rook = new Rook("wR ",this.posx,this.posy,this.color,this.sqrColor);
			}
			else 
			{
				rook = new Rook("bR ",this.posx,this.posy,this.color,this.sqrColor);
			}
			//Update pieces and board arrays
			Chess.chessBoard.pieces[rook.posx][rook.posy]=rook;
			Chess.chessBoard.board[rook.posx][rook.posy]=rook.name;
			
			updateAttackCapabilities();	
			
			break;
			
		default:
			Piece queen;
			
			if(this.color.equals("white"))
			{
				queen = new Queen("wQ ",this.posx,this.posy,this.color,this.sqrColor);
			}
			else 
			{
				queen = new Queen("bQ ",this.posx,this.posy,this.color,this.sqrColor);
			}
			//Update pieces and board arrays
			Chess.chessBoard.pieces[queen.posx][queen.posy]=queen;
			Chess.chessBoard.board[queen.posx][queen.posy]=queen.name;
			
			updateAttackCapabilities();	
			
			break;
		
		}
			
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
		//If stepUpDown is > 0 we know the player is moving up the board
		if(stepUpDown > 0 && stepLeftRight == 0)
		{
			for(int i=stepUpDown+1;i>0;i--)
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
			}
		}
		//If stepUpDown is < 0 we know the player is moving down the board
		else if(stepUpDown < 0  && stepLeftRight == 0)
		{
			for(int i=stepUpDown-1;i<0;i++)
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
			}
		}
		/*If stepUpDown = stepLeftRight and both are greater than 0 
		*we know the player is moving diagonally up/left so they end
		*up n squares left and n squares up
		*/
		else if(stepUpDown == stepLeftRight && stepUpDown > 0)
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
