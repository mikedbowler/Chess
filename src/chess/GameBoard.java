package chess;

public class GameBoard {

	/**
	 * Holds all game Pieces that are on the Chessboard
	 */
	Piece[][] pieces = new Piece[8][8];
	/**
	 * Holds graphical representation of the Chessboard
	 */
	String[][] board;
	/**
	 * Maintains whose turn it is
	 */
	String turn = "White's Move: ";
	/**
	 * Allows for easy identification of whether a player can do an
	 * En Passant
	 */
	int canPerformEnPassant;
	
	//Locations of both kings kept here for easy use in inCheck method
	/**
	 * location of the Black King 
	 */
	int[] bKingLocation = {0,4};
	/**
	 * location of the White King
	 */
	int[] wKingLocation = {7,4};
	
	/**
	 * GameBoard constructor
	 */
	public GameBoard()
	{
		this.board = new String[9][9];
		this.turn = "White's Move: ";
		this.canPerformEnPassant = 0;
		
		initializeBoard(board);
	}
	
	/**
	 * Moves the game piece from current location to destination and 
	 * redraws the board. And switches turns when necessary.
	 * @param currX X coordinate of this piece's current location
	 * @param currY Y coordinate of this piece's current location
	 * @param destX X coordinate of this piece's destination
	 * @param destY Y coordinate of this piece's destination
	 * @param np char for conducting pawn promotion 
	 */
	public void moveAndDraw(int currX, int currY, int destX, int destY, char np)
	{
        boolean moveSuccessful = false;
        
        int[] tmp = new int[4];
		tmp[0]=currX;tmp[1]=currY;tmp[2]=destX;tmp[3]=destY;
		
		if(!Piece.inBounds(tmp))
		{
			moveSuccessful = false;
		}
		else if(this.pieces[currX][currY]==null)
		{
			moveSuccessful = false;
		}
		else if(this.turn.equals("White's Move: ") && this.pieces[currX][currY].color.equals("black"))
		{
			moveSuccessful = false;
		}
		else if(this.turn.equals("Black's Move: ") && this.pieces[currX][currY].color.equals("white"))
		{
			moveSuccessful = false;
		}
		else
		{
        
		if(this.canPerformEnPassant==1)
		{
			this.canPerformEnPassant++;
		}
			
		moveSuccessful=this.pieces[currX][currY].move(currX, currY, destX, destY);
		
		if(this.pieces[destX][destY] instanceof Pawn && moveSuccessful && (destX==0 || destX==7))
		{
			Pawn pawn = (Pawn) this.pieces[destX][destY];
			pawn.promote(np);
			
			//After move has occurred determines if the opponent is in check
			if(Chess.chessBoard.turn.equals("White's Move: ") && Piece.inCheck(this.bKingLocation))
			{
				Chess.black.inCheck = true;
			}
			else if(Chess.chessBoard.turn.equals("Black's Move: ") && Piece.inCheck(this.wKingLocation))
			{
				Chess.white.inCheck = true;
			}
		}
		
		this.drawBoard();
		
		if(this.canPerformEnPassant>1)
		{
			this.canPerformEnPassant = this.canPerformEnPassant-2;
		}
	
		}
		
		
		
		if(moveSuccessful && this.turn.equals("White's Move: "))
		{
			if(Piece.isCheckmate(this.bKingLocation))
			{
				Chess.black.checkMate = true;
			}
		}
		else if(moveSuccessful && this.turn.equals("Black's Move: "))
		{
			if(Piece.isCheckmate(this.wKingLocation))
			{
				Chess.white.checkMate = true;
			}
		}
		
		if(moveSuccessful && Chess.black.checkMate)
		{
			System.out.println("White Wins");
			System.exit(0);
		}
		else if(moveSuccessful && Chess.white.checkMate)
		{
			System.out.println("Black Wins");
			System.exit(0);
		}
		
		/*If move was not legal, the player who made the illegal
		 * move will continue their turn until they input a valid move.
		 */
		if(!moveSuccessful)
		{
			System.out.println("Illegal Move, Try Again!");
		}
		else
		{
			if(this.turn.equals("White's Move: "))
			{
				this.turn = "Black's Move: ";
			}
			else
			{
				this.turn = "White's Move: ";
			}
		}
	}
	
	/**
	 * Draws the chessboard
	 */
	public void drawBoard()
	{
		System.out.println();
		
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
		
		System.out.println();
	}

	/**
	 * Initializes the chessboard on startup
	 * @param chessBoard The board to be initialized
	 */
	public void initializeBoard(String[][] chessBoard) {
		
		//Initialize all Black Pieces
		pieces[0][0] = new Rook("bR ",0,0,"black","   ");
		pieces[0][1] = new Knight("bN ",0,1,"black","## ");
		pieces[0][2] = new Bishop("bB ",0,2,"black","   ");
		pieces[0][3] = new Queen("bQ ",0,3,"black","## ");
		pieces[0][4] = new King("bK ",0,4,"black","   ");
		pieces[0][5] = new Bishop("bB ",0,5,"black","## ");
		pieces[0][6] = new Knight("bN ",0,6,"black","   ");
		pieces[0][7] = new Rook("bR ",0,7,"black","## ");
		pieces[1][0] = new Pawn("bp ",1,0,"black","## ");
		pieces[1][1] = new Pawn("bp ",1,1,"black","   ");
		pieces[1][2] = new Pawn("bp ",1,2,"black","## ");
		pieces[1][3] = new Pawn("bp ",1,3,"black","   ");
		pieces[1][4] = new Pawn("bp ",1,4,"black","## ");
		pieces[1][5] = new Pawn("bp ",1,5,"black","   ");
		pieces[1][6] = new Pawn("bp ",1,6,"black","## ");
		pieces[1][7] = new Pawn("bp ",1,7,"black","   ");
				
		//Initialize all White Pieces
		pieces[7][0] = new Rook("wR ",7,0,"white","## ");
		pieces[7][1] = new Knight("wN ",7,1,"white","   ");
		pieces[7][2] = new Bishop("wB ",7,2,"white","## ");
		pieces[7][3] = new Queen("wQ ",7,3,"white","   ");
		pieces[7][4] = new King("wK ",7,4,"white","## ");
		pieces[7][5] = new Bishop("wB ",7,5,"white","   ");
		pieces[7][6] = new Knight("wN ",7,6,"white","## ");
		pieces[7][7] = new Rook("wR ",7,7,"white","   ");
		pieces[6][0] = new Pawn("wp ",6,0,"white","   ");
		pieces[6][1] = new Pawn("wp ",6,1,"white","## ");
		pieces[6][2] = new Pawn("wp ",6,2,"white","   ");
		pieces[6][3] = new Pawn("wp ",6,3,"white","## ");
		pieces[6][4] = new Pawn("wp ",6,4,"white","   ");
		pieces[6][5] = new Pawn("wp ",6,5,"white","## ");
		pieces[6][6] = new Pawn("wp ",6,6,"white","   ");
		pieces[6][7] = new Pawn("wp ",6,7,"white","## ");
		
		//Display files 
		board[8][0] = " a ";
		board[8][1] = " b ";
		board[8][2] = " c ";
		board[8][3] = " d ";
		board[8][4] = " e ";
		board[8][5] = " f ";
		board[8][6] = " g ";
		board[8][7] = " h ";
		
		//Display ranks
		board[0][8] = "8";
		board[1][8] = "7";
		board[2][8] = "6";
		board[3][8] = "5";
		board[4][8] = "4";
		board[5][8] = "3";
		board[6][8] = "2";
		board[7][8] = "1";
		
		//Make empty space
		board[8][8] = "";
		
		
		boolean color = false;
		
		//Initialize the game board's tiles
		for(int i=0;i<8;i++)
		{
			
			for(int j=0;j<8;j++)
			{
				if(color)
				{
				board[i][j] = "## ";
				if(pieces[i][j]!=null)
				{
					board[i][j] = pieces[i][j].name;
				}
				color = false;
				}
				else
				{
				board[i][j] = "   ";
				if(pieces[i][j]!=null)
				{
					board[i][j] = pieces[i][j].name;
				}
				color = true; 
				}
			}
			
			if(color)
			{
				color = false;
			}
			else 
			{
				color = true;
			}
		}
		
		
		
		
	}
	
}
