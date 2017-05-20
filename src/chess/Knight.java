package chess;

public class Knight extends Piece{
	
	/**
	 * Knight constructor
	 * 
	 * @param name Name of piece 
	 * @param posx X coordinate of piece's current location
	 * @param posy Y coordinate of piece's current location
	 * @param color Color of the piece
	 * @param sqrColor Color of the square the piece is standing on
	 */
	public Knight(String name, int posx, int posy, String color, String sqrColor) {
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
		
		if(((Math.abs(stepUpDown)==2 && Math.abs(stepLeftRight)==1) || (Math.abs(stepUpDown)==1 && Math.abs(stepLeftRight)==2)) && isPathClear(destX,destY))
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
		int[] curr = {this.posx,this.posy};
		int[] dest = {destX,destY};
		
		if(isSpotEmpty(dest) || isEnemy(curr,dest))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean canBeIntercepted(int destX, int destY)
	{
		int[] curr = {this.posx,this.posy};
		
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
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				currentPiece = Chess.chessBoard.pieces[i][j];
				
				if(currentPiece!=null && currentPiece.isLegalMove(curr[0],curr[1]) && !Piece.preCheck(this, kingSpace, curr[0],curr[1]) && !currentPiece.color.equals(this.color))
				{
					return true;
				}
			}
		}
			
		
		return false;
	}
}
