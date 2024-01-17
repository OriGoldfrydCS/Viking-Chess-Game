import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GameLogic implements PlayableLogic {

	// Data Members
	private final ConcretePiece[][] board;                    // A 2D array that represents the board game which stores the ConcretePieces
	private ArrayList<Position> movesHistory = new ArrayList<Position>();            // an ArrayList to keep track of moves of all players
	private ConcretePiece[] defensePieces = new ConcretePiece[NUMBER_OF_DEFENDERS];       // 1D Array for storing the defenders
	private ConcretePiece[] attackPieces = new ConcretePiece[NUMBER_OF_ATTACKERS];        // 1D Array for storing the attackers
	private final ConcretePlayer defender;                    // A variable represents Player 1
	private final ConcretePlayer attacker;                    // A variable represents Player 2
	private King theKing;                                // An object for the King who is unique on the board
	private final Position CORNER_UP_LEFT = new Position(0, 0);                      // 1st corner of the board game
	private final Position CORNER_UP_RIGHT = new Position(0, BOARD_SIZE - 1);        // 2nd corner of the board game
	private final Position CORNER_DOWN_LEFT = new Position(BOARD_SIZE - 1, 0);       // 3rd corner of the board game
	private final Position CORNER_DOWN_RIGHT = new Position(BOARD_SIZE - 1, BOARD_SIZE - 1);    // 4th corner of the board game
	public static final int BOARD_SIZE = 11;                // Given a board size of 11x11
	public static final int NUMBER_OF_DEFENDERS = 13;       // Number of defenders (including the King)
	public static final int NUMBER_OF_ATTACKERS = 24;       // Number of attackers
	private boolean isDefenderTurn;                        // A flag to switch turns between players


	// Constructor
	public GameLogic() {
		this.defender = new ConcretePlayer(true);
		this.attacker = new ConcretePlayer(false);
		board = new ConcretePiece[BOARD_SIZE][BOARD_SIZE];
		init();                                                    // see function in: "private section" below
		isDefenderTurn = false;
	}

	// Methods
	@Override
	public boolean move(Position a, Position b) {

		// Check move validity over few cases (see function in: "private section" below)
		if (!isValidMove(a, b)) {
			return false;
		}

		// In case that a move is valid -> move piece
		ConcretePiece piece = board[a.getRow()][a.getCol()];
		board[b.getRow()][b.getCol()] = piece;
		board[a.getRow()][a.getCol()] = null;

		// Check if the new position causes to enemy pieces' kill (see function in: "private section" below)
		if (piece instanceof Pawn) {
			checkIfEnemyKilled(b, (Pawn) piece, getCurrentPlayer());
		}

		// save move in history - for back bottom
		movesHistory.add(a);
		movesHistory.add(b);

		// update position of piece and save the new position to the ConcretePiece array of moves
		piece.moveTo(b);

		// check if the game finished by one of the relevant rules (see function in: "private section" below)
		if (isGameFinished()) {
			getCurrentPlayer().incrementVictories();
			showStatistic();
			reset();

		} else {
			// Other player's turn
			isDefenderTurn = !isDefenderTurn;
		}
		return true;
	}

	// This method prints a data regarding a game after any player's victory
	private void showStatistic() {

		ConcretePlayer winner = getCurrentPlayer();        // The winner plays the last turn
		ConcretePiece[] firstArray = attackPieces;        // A reference to the attackers' array
		ConcretePiece[] SecondArray = defensePieces;    // A reference to the defenders' array

		if (winner == defender) {                        // if the winner is the defender -> change order of the references
			firstArray = defensePieces;
			SecondArray = attackPieces;
		}

		// Print pieces sorted by total moves
		savePieceSortedByTotalMoves(firstArray);                        // (see functions in: "private section" below)
		savePieceSortedByTotalMoves(SecondArray);
		printSeparator();

		// Print pieces sorted by total killings
		ConcretePiece[] combinedArray = combineDefenseAndAttack();        // (see functions in: "private section" below)
		savePieceSortedByTotalKills(combinedArray);
		printSeparator();

		// Print pieces sorted by total distance made
		savePieceSortedByTotalDistance(combinedArray);                    // (see functions in: "private section" below)
		printSeparator();


		// Print squares sorted by total pieces
		saveSquareSortedByPieces(combinedArray);                        // (see functions in: "private section" below)
		printSeparator();
	}

	@Override
	public Piece getPieceAtPosition(Position p) {
		int row = p.getRow();
		int col = p.getCol();
		return board[row][col];
	}

	@Override
	public Player getFirstPlayer() {
		return defender;
	}

	@Override
	public Player getSecondPlayer() {
		return attacker;
	}

	public ConcretePlayer getCurrentPlayer() {
		if (isDefenderTurn)
			return defender;
		else
			return attacker;
	}

	@Override
	public boolean isGameFinished() {

		// Case 1: The king succeeded to reach one of the board's corner (see function in: "private section" below)
		if (getCurrentPlayer() == defender) {
			if (isKingInOneCorner())
				return true;
		} else {
			// Case 2: The king surrounded by 4 attackers (see function in: "private section" below)
			if (isKingSurroundedByEnemy())
				return true;
		}
		if (NumberOfAttackersAlive() < 3) {        // see function in: "private section" below
			return true;
		}
		return false;
	}

	private int NumberOfAttackersAlive() {
		int counterAlive = 0;
		for (int i = 0; i < attackPieces.length; i++) {
			if (attackPieces[i].isAlive() == true) {
				counterAlive++;
			}
		}
		return counterAlive;
	}


	@Override
	public void reset() {
		init();
		isDefenderTurn = false;
		movesHistory.clear();
	}

	@Override
	public void undoLastMove() throws IndexOutOfBoundsException {
		int size = movesHistory.size();
		if(movesHistory.size() < 2){	// Check validity of the undo request
			System.err.println("You cannot use that button. This is the beginning of the game");
			return;
		}

		clearBoard();
		movesHistory.remove(size - 1);
		movesHistory.remove(size - 2);
		for (int i = 0; i < movesHistory.size(); i += 2) {
			Position from = movesHistory.get(i);
			Position to = movesHistory.get(i + 1);
			restoreMove(from, to);
		}
	}

	private void restoreMove(Position a, Position b) {

		// In case that a move is valid -> move piece
		ConcretePiece piece = board[a.getRow()][a.getCol()];
		board[b.getRow()][b.getCol()] = piece;
		board[a.getRow()][a.getCol()] = null;

		// update position of piece and save the new position to the ConcretePiece array of moves
		piece.moveTo(b);

		// Check if the new position causes to enemy pieces' kill (see function in: "private section" below)
		if (piece instanceof Pawn) {
			checkIfEnemyKilled(b, (Pawn) piece, getCurrentPlayer());
		}

		isDefenderTurn = !isDefenderTurn;

	}

	private void clearBoard() {
		init();
		isDefenderTurn = false;
	}

	@Override
	public int getBoardSize() {
		return BOARD_SIZE;
	}


	@Override
	public boolean isSecondPlayerTurn() {
		return !isDefenderTurn;
	}


	/////////////////////    PRIVATE SECTION     /////////////////////


	// This function initiates pieces and places them on the board (mentioned in: public GameLogic())
	private void init() {
		Position[] defendersOriginPosition = {new Position(5, 3), new Position(4, 4), new Position(5, 4), new Position(6, 4), new Position(3, 5), new Position(4, 5), new Position(5, 5),
				new Position(6, 5), new Position(7, 5), new Position(4, 6), new Position(5, 6), new Position(6, 6), new Position(5, 7)};    // positions of defenders
		Position[] attackersOriginPosition = {new Position(3, 0), new Position(4, 0), new Position(5, 0), new Position(6, 0), new Position(7, 0), new Position(5, 1),
				new Position(0, 3), new Position(10, 3), new Position(0, 4), new Position(10, 4), new Position(0, 5), new Position(1, 5),
				new Position(9, 5), new Position(10, 5), new Position(0, 6), new Position(10, 6), new Position(0, 7), new Position(10, 7),
				new Position(5, 9), new Position(3, 10), new Position(4, 10), new Position(5, 10), new Position(6, 10), new Position(7, 10)}; // position of attackers

		// For avoidance of doubts, initiate all places on the board with nulls
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				board[i][j] = null;
			}
		}

		// Create 12 objects of Pawn for the defenders, plus one object of King and place them is origin squares
		for (int i = 0; i < defensePieces.length; i++)
			if (i == 6) {
				theKing = new King(defender, defendersOriginPosition[i], i + 1);
				defensePieces[i] = theKing;
			} else
				defensePieces[i] = new Pawn(defender, defendersOriginPosition[i], i + 1);

		// Create 24 objects of Pawn for the attackers
		for (int i = 0; i < attackPieces.length; i++)
			attackPieces[i] = new Pawn(attacker, attackersOriginPosition[i], i + 1);

		putPiecesOnBoard(defensePieces, defendersOriginPosition);
		putPiecesOnBoard(attackPieces, attackersOriginPosition);
	}

	// This assistance-function mention in: private void init()
	private void putPiecesOnBoard(ConcretePiece[] pieces, Position[] originPositions) {
		for (int i = 0; i < pieces.length; i++) {
			ConcretePiece piece = pieces[i];
			Position pos = piece.getPosition();
			board[pos.getRow()][pos.getCol()] = piece;
		}
	}

	// This function checks if a move on the board is valid (mentioned in: public boolean move(Position a, Position b))
	private boolean isValidMove(Position a, Position b) {

		// Invalid move no. 1: Not out of board's boarders
		if (!isInsideBoard(b)) {
			return false;
		}
		if (!isInsideBoard(a)) {
			return false;
		}

		// Invalid move no. 2: The two position are identical
		if (a.equals(b))
			return false;

		// Invalid move no. 3: No piece picked
		if (board[a.getRow()][a.getCol()] == null) {
			return false;
		}

		// Invalid move no. 4: The destination square should be empty (as well as the source square)
		if (getPieceAtPosition(b) != null) {
			return false;
		}

		Piece p = getPieceAtPosition(a);
		if (p == null)
			return false;

		// Invalid move no. 5 (The piece lifted is owned by the current player
		if (isDefenderTurn && p.getOwner() != defender)
			return false;

		if (!isDefenderTurn && p.getOwner() != attacker)
			return false;

		// Invalid move no. 6 (only for Pawns): Board's corners are out of the game
		if (p instanceof Pawn) {
			if (isCorner(b)) {
				return false;
			}
		}

		// Invalid move no. 7: Diagonally move
		if ((b.getRow() != a.getRow()) && (b.getCol() != a.getCol())) {
			return false;
		}

		// Invalid move no. 8: No piece located in the path between a and b
		if (b.getRow() == a.getRow()) {                     // Case 1: 	Piece moves horizontally
			int start = Math.min(b.getCol(), a.getCol());
			int end = Math.max(b.getCol(), a.getCol());
			for (int i = start + 1; i < end; i++) {
				if (board[a.getRow()][i] != null) {
					return false;
				}
			}
		} else {                                            // Case 2: Piece moves vertically
			int start = Math.min(b.getRow(), a.getRow());
			int end = Math.max(b.getRow(), a.getRow());
			for (int i = start + 1; i < end; i++) {
				if (board[i][a.getCol()] != null) {
					return false;
				}
			}
		}
		return true;
	}

	// This function checks if a new position causes to enemy pieces' kill (mentioned in: private boolean isValidMove(Position a, Position b))
	private boolean isInsideBoard(Position p) {
		if (p.getRow() < BOARD_SIZE && p.getCol() < BOARD_SIZE && p.getRow() >= 0 && p.getCol() >= 0) {
			return true;
		}
		return false;
	}

	// This function checks if a new position causes to enemy pieces' kill (mentioned in: public boolean move(Position a, Position b))

	private void checkIfEnemyKilled(Position pos, Pawn currentPiece, ConcretePlayer currentPlayer) {

		ConcretePiece p;
		ConcretePlayer enemy = getEnemyPlayer();
		Position left = pos.getOneLeft();
		Position right = pos.getOneRight();
		Position up = pos.getOneUp();
		Position down = pos.getOneDown();

		Position[] positionsArray = {left, right, up, down};
		Position[] nextPositionsArray = checkOtherMove(positionsArray);

		for (int i = 0; i < positionsArray.length; i++) {
			if (positionsArray[i] != null) {
				p = (ConcretePiece) getPieceAtPosition(positionsArray[i]);              // p is the piece next to currentPiece
				if ((p != null) && (p.getPosition() != theKing.getPosition())) {        // Check if we King isn't is p (a king can't be captured regularly
					Position nextNext = nextPositionsArray[i];

					// Case 1: Check if currentPiece captured enemy p with a border
					if (nextNext == null && p.isNearBoarders(positionsArray[i]) && p.getOwner().toString().equals(enemy.toString())) {
						killTheEnemy(currentPiece, p, positionsArray[i]);
						currentPiece.addKill();
					}

					// Case 2: Check if currentPiece captured enemy p with one of the board's corners
					if (nextNext != null && isCorner(nextNext)) {
						killTheEnemy(currentPiece, p, positionsArray[i]);
						currentPiece.addKill();
					}

					// Case 3: Check if currentPiece captured enemy p with its others from its group (and not the King)
					if (nextNext != null && p.getOwner().toString().equals(enemy.toString()) &&
							(nextNext.getCol() != theKing.getPosition().getCol() || nextNext.getRow() != theKing.getPosition().getRow())) {
						if ((isPieceOwnerOnPos(nextNext, currentPlayer))) {
							killTheEnemy(currentPiece, p, positionsArray[i]);
							currentPiece.addKill();
						}
					}

				}
			}
		}
	}


	// This assistance-function mention in: private void checkIfEnemyKilled(Position pos, Pawn currentPiece, ConcretePlayer currentPlayer)
	private void killTheEnemy(Pawn currentPiece, ConcretePiece p, Position position) {
		currentPiece.addDefeat();
		p.setAlive(false);
		board[position.getRow()][position.getCol()] = null;
	}


	// This assistance-function mention in: private void checkIfEnemyKilled(Position pos, Pawn currentPiece, ConcretePlayer currentPlayer)
	private boolean isPieceOwnerOnPos(Position pos, ConcretePlayer player) {
		ConcretePiece p = (ConcretePiece)getPieceAtPosition(pos);
		if(p == null)
			return false;
		return (p.getOwner() == player);
	}

	// This assistance-function mention in: private void checkIfEnemyKilled(Position pos, Pawn currentPiece, ConcretePlayer currentPlayer)
	//								and in: private boolean isValidMove(Position a, Position b) {
	private boolean isCorner(Position p) {
		if (p.equals(CORNER_UP_LEFT) || p.equals(CORNER_UP_RIGHT) || p.equals(CORNER_DOWN_LEFT) || p.equals(CORNER_DOWN_RIGHT))
			return true;
		return false;
	}

	// This assistance-function mention in: private void checkIfEnemyKilled(Position pos, Pawn currentPiece, ConcretePlayer currentPlayer)
	private Position[] checkOtherMove(Position[] positionsArray) {
		Position[] nextPositionsArray = new Position[4];
		if(positionsArray[0] != null){
			nextPositionsArray[0] = positionsArray[0].getOneLeft();
		}
		else{
			nextPositionsArray[0] = null;
		}
		if(positionsArray[1] != null){
			nextPositionsArray[1] = positionsArray[1].getOneRight();
		}
		else{
			nextPositionsArray[1] = null;
		}
		if(positionsArray[2] != null){
			nextPositionsArray[2] = positionsArray[2].getOneUp();
		}
		else{
			nextPositionsArray[2] = null;
		}
		if(positionsArray[3] != null){
			nextPositionsArray[3] = positionsArray[3].getOneDown();
		}
		else{
			nextPositionsArray[3] = null;
		}
		return nextPositionsArray;
	}

	// This assistance-function mention in: checkIfEnemyKilled(Position pos, Pawn currentPiece, ConcretePlayer currentPlayer)
	private ConcretePlayer getEnemyPlayer() {
		if (isDefenderTurn)
			return attacker;
		else
			return defender;
	}

	// This assistance-function mention in: private void showStatistic()

	private ConcretePiece[] combineDefenseAndAttack() {
		ConcretePiece[] res = new ConcretePiece[defensePieces.length + attackPieces.length];
		int i = 0;
		for(ConcretePiece element : defensePieces){
			res[i++] = element;
		}
		for(ConcretePiece element : attackPieces){
			res[i++] = element;
		}
		return res;
	}

	// This assistance-function that prints all Pieces Sorted by their total moves and id (mention in: private void showStatistic())
	private void savePieceSortedByTotalMoves(ConcretePiece[] piecesArray) {
		Arrays.sort(piecesArray, new compareByTotalMoves());
		for(int i = 0; i < piecesArray.length; i++){
			if(piecesArray[i].getTotalMoves() > 1){
				System.out.println(piecesArray[i].toString());
			}
		}
		Arrays.sort(piecesArray, new compareById());			// Sort the original array by id
	}

	// A comparator the sorts all Pieces by their total moves and id (mention in: private void showStatistic()
	class compareByTotalMoves implements Comparator<ConcretePiece> {
		public int compare(ConcretePiece o1, ConcretePiece o2) {
			int result = (o1.getTotalMoves() - o2.getTotalMoves());
			if (result == 0) {
				return o1.getId() - o2.getId();
			}else{
				return result;
			}
		}
	}

	// A comparator the sorts all Pieces by their total id (mention in: 	private void savePieceSortedByTotalMoves(ConcretePiece[] piecesArray))
	static class compareById implements Comparator<ConcretePiece> {
		public int compare(ConcretePiece o1, ConcretePiece o2) {
			return o1.getId() - o2.getId();
		}
	}

	// This assistance-function that prints all Pieces sorted by their total kills and id (mention in: private void showStatistic())
	private void savePieceSortedByTotalKills(ConcretePiece[] piecesArray) {
		Arrays.sort(piecesArray, new compareByTotalKills());
		for(int i = 0; i < piecesArray.length; i++){
			if(piecesArray[i].getTotalKills()>0){
				System.out.print(piecesArray[i].getOutputPrefix());
				System.out.println(" "+piecesArray[i].getTotalKills()+" kills");
			}
		}
	}

	// A comparator the sorts all Pieces by their total kills, id and owner (mention in: private void showStatistic()
	class compareByTotalKills implements Comparator<ConcretePiece>{
		public int compare(ConcretePiece o1, ConcretePiece o2) {
			int result = (o2.getTotalKills() - o1.getTotalKills());
			if (result == 0) {
				result = o1.getId() - o2.getId();
				if (result == 0) {
					if(o2.getOwner() == getCurrentPlayer()) {
						return 1;
					}else{
						return -1;
					}
				}
				return result;
			}
			return result;
		}
	}

	// This assistance-function that prints all Pieces sorted by the total distances made, id and owner (mention in: private void showStatistic())
	private void savePieceSortedByTotalDistance(ConcretePiece[] piecesArray) {
		Arrays.sort(piecesArray, new compareByTotalDistance());
		for(int i = 0; i < piecesArray.length; i++){
			if(piecesArray[i].getTotalMoves() > 1){
				System.out.print(piecesArray[i].getOutputPrefix());
				System.out.println(" " + piecesArray[i].getTotalDistance() + " squares");
			}
		}
	}

	// A comparator the sorts all Pieces by total distances made, id and owner (mention in: private void showStatistic()
	class compareByTotalDistance implements Comparator<ConcretePiece> {
		public int compare(ConcretePiece o1, ConcretePiece o2) {
			int result = (o2.getTotalDistance() - o1.getTotalDistance());
			if (result == 0) {
				result = o1.getId() - o2.getId();
				if (result == 0) {
					if(o2.getOwner() == getCurrentPlayer()) {
						return 1;
					}
					else {
						return -1;
					}
				}
				return result;
			}
			return result;
		}
	}

	// This assistance-function that prints all squares sorted by the number it being stepped during the game (mention in: private void showStatistic())
	private void saveSquareSortedByPieces(ConcretePiece[] combinedArray) {
		int[][] cellInfo = new int[BOARD_SIZE * BOARD_SIZE][3];		// A 2D array the stores the information about the board game

		// Fill the 2D array with row, column, and value information
		int index = 0;
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				cellInfo[index][0] = i; 		// Row
				cellInfo[index][1] = j; 		// Col
				cellInfo[index][2] = 0; 		// Value (number of steps)
				index++;
			}
		}

		fillArrayWithMoves(cellInfo, combinedArray);		// (see assistance function below)
		Arrays.sort(cellInfo, new SquareComparator());

		for (int i = 0; i < cellInfo.length; i++) {
			if (cellInfo[i][2] >= 2) {
				Position p = new Position(cellInfo[i][1], cellInfo[i][0]);
				System.out.println(p.toString() + cellInfo[i][2] + " pieces");
			}
		}
	}

	// An assistance-function the fills the 2D array with the total steps
	private void fillArrayWithMoves(int[][] cellInfo,ConcretePiece[] combinedArray) {
		Position position;
		ConcretePiece p;
		for(int i = 0; i < combinedArray.length; i++){
			p = combinedArray[i];
			for (int row = 0; row < BOARD_SIZE; row++) {
				for (int col = 0; col < BOARD_SIZE; col++) {
					position = new Position(col, row);
					if(p.getMovesList().contains(position)) {
						cellInfo[row * BOARD_SIZE + col][2]++;		// increase the steps made in the square
					}
				}
			}
		}
	}

	// A comparator the sorts all Pieces by total steps made on a square, x's and y's (mention in: private void showStatistic()
	private class SquareComparator implements Comparator<int[]> {
		@Override
		public int compare(int[] square1, int[] square2) {
			int pieces1 = square1[2];
			int pieces2 = square2[2];

			if (pieces1 == pieces2) {
				int col1 = square1[1];
				int col2 = square2[1];

				if (col1 == col2) {
					int row1 = square1[0];
					int row2 = square2[0];
					return Integer.compare(row1, row2); // Sort by Y if columns are equal
				}
				return Integer.compare(col1, col2); // Sort by X if pieces and rows are equal
			}
			return Integer.compare(pieces2, pieces1); // Sort by number of pieces
		}
	}

	// This assistance-function that prints separator(*) between prints (mention in: private void showStatistic())
	private void printSeparator() {
		for(int i = 0; i < 75; i++){
			System.out.print("*");
		}
		System.out.println();
	}

	// This function check if the King is in a corner, meaning the defenders wins (mentioned in: public boolean isGameFinished())
	private boolean isKingInOneCorner() {
		Position[] corners = {CORNER_UP_LEFT, CORNER_UP_RIGHT, CORNER_DOWN_LEFT, CORNER_DOWN_RIGHT};
		Position kingPos = theKing.getPosition();
		for (int i = 0; i < 4; i++) {
			if (kingPos.equals(corners[i]))
				return true;
		}
		return false;
	}

	// This function check if the King surrounded, meaning the attackers wins (mentioned in: public boolean isGameFinished())
	private boolean isKingSurroundedByEnemy() {
		Position kingPosition = theKing.getPosition();

		if (kingPosition.isPositionOnRightEdge() == false)
			if (!checkIfEnemyThere(kingPosition.getOneRight(), attacker))
				return false;

		if (kingPosition.isPositionOnLeftEdge() == false)
			if (!checkIfEnemyThere(kingPosition.getOneLeft(), attacker))
				return false;

		if (kingPosition.isPositionOnTopEdge() == false)
			if (!checkIfEnemyThere(kingPosition.getOneUp(), attacker))
				return false;

		if (kingPosition.isPositionOnBottomEdge() == false)
			if (!checkIfEnemyThere(kingPosition.getOneDown(), attacker))
				return false;

		return true;
	}

	// This assistance-function mention in: private boolean isKingSurroundedByEnemy()
	private boolean checkIfEnemyThere(Position tocheck, ConcretePlayer enemy) {

		Piece p = getPieceAtPosition(tocheck);
		if (p == null)
			return false;
		if (p.getOwner() == enemy)
			return true;

		return false;
	}
}
