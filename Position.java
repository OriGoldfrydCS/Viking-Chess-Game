public class Position {

	// Data Members
	private int row;
	private int col;

	// Constructor
	public Position(int col, int row) {
		this.row = row;
		this.col = col;
	}

	// Methods
	// 1. Assistance methods (see Getters & Setters below)

	// This method computes the distances between two squares on the board
	public int distanceBetweenSquared(Position p){
		if(this.row == p.getRow()){
			return Math.abs(this.col - p.getCol());
		}
		else if(this.col == p.getCol()){
			return Math.abs(this.row - p.getRow());
		}
		else{											// a case where a piece didn't move
			return 0;
		}
	}

	// This override method checks if two position are identical
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return col == other.col && row == other.row;
	}

	// The four methods below check if a Position is one of the board's edges
	public boolean isPositionOnLeftEdge() {
		return col == 0;
	}

	public boolean isPositionOnRightEdge() {
		return col == GameLogic.BOARD_SIZE - 1;
	}

	public boolean isPositionOnTopEdge() {
		return row == 0;
	}

	public boolean isPositionOnBottomEdge() {
		return row == GameLogic.BOARD_SIZE - 1;
	}

	// The four methods below check return the new position of a piece for each possible direction (left, right, up, down)
	public Position getOneLeft() {
		if(col == 0)
			return null;
		return new Position(col - 1, row);
	}

	public Position getOneRight() {
		if(col == GameLogic.BOARD_SIZE - 1)
			return null;
		return new Position(col + 1, row);
	}

	public Position getOneUp() {
		if(row == 0)
			return null;
		return new Position( col,row - 1);
	}

	public Position getOneDown() {
		if(row == GameLogic.BOARD_SIZE - 1)
			return null;
		return new Position(col,row + 1);
	}

	@Override
	public String toString(){
		return "(" + col + ", " + row + ")";
	}

	// 2. Getters & Setters
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}
