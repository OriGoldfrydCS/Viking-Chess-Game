import java.util.ArrayList;
import java.util.Iterator;


public abstract class ConcretePiece implements Piece {

	// Data Members
	protected final ConcretePlayer owner;      		// Owner represents Player 1 or 2, defender or attacker (respectively)
	private Position position;        				// Any position mentions by (x,y) of a concrete piece
	private ArrayList<Position> movesList = new ArrayList<Position>();		// An ArrayList for storing all moves of a ConcretePiece
	private final int id;             				// A tag 1-13 for defenders, and 1-24 for attackers
	private int totalDistance = 0;					// A variable that stores the total number of squares a piece stepped on during the game
	private int totalKills = 0;						// A variable that stores the total number of enemy's pieces killed by ConcretePiece
	private boolean isAlive;       					// A flag if a piece is alive for dead
	public static final char DEFENSE_CODE = 'D';	// Initial for all defenders' id
	public static final char ATTACK_CODE = 'A';		// Initial for all attackers' id
	public static final char KING_CODE = 'K';		// Initial for king's id

	// Constructor
	public ConcretePiece(ConcretePlayer owner, Position position, int id) {
		this.owner = owner;
		this.position = position;
		this.id = id;
		isAlive = true;
		movesList.add(position);
	}

	// Methods
	// 1. Assistance methods (see Getters & Setters below)

	// This method increases the number of enemy's kills of a ConcretePiece
	public void addKill() {
		totalKills++;
	}

	// This method updates the ConcretePiece's move list and the distance (steps) made by it
	public void moveTo(Position p) {
		position = new Position(p.getCol(), p.getRow());
		movesList.add(position);
		int distanceMadeToNewPosition = p.distanceBetweenSquared(movesList.get(movesList.size() - 2));
		totalDistance += distanceMadeToNewPosition;
	}

	// An abstract method implements in King and Pawn classes that returns 'K', 'D' and 'A' for king, defender and attacker (respectively)
	public abstract char getPrefix();

	// A method return the prefix of a ConcretePiece (for instance K7, means the king with an id number of 7)
	public String getOutputPrefix() {
		char prefix = getPrefix();
		StringBuffer s = new StringBuffer();
		s.append(prefix);
		s.append(id);
		s.append(":");
		return s.toString();
	}

	// This method checks of the concretePiece's position in next to the border of the board
	public boolean isNearBoarders(Position p) {
		if (p.getRow() == 0 || p.getCol() == 0 || p.getRow() == GameLogic.BOARD_SIZE - 1 || p.getCol() == GameLogic.BOARD_SIZE - 1) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		Iterator<Position> iterator = movesList.iterator();
		StringBuffer s = new StringBuffer();

		String tmp = getOutputPrefix();
		s.append(tmp);

		s.append(" [");
		while(iterator.hasNext()){
			s.append(iterator.next());
			if(iterator.hasNext()){
				s.append(", ");
			}
		}
		s.append("]");
		return s.toString();
	}

	// 2. Getters & Setters
	public int getTotalDistance() {
		return totalDistance;
	}

	public int getTotalKills() {
		return totalKills;
	}

	public int getTotalMoves() {
		return movesList.size();
	}

	public boolean isAlive() {
		return isAlive;
	}

	public Player getOwner() {
		return owner;
	}

	public Position getPosition() {
		return position;
	}

	public int getId() {
		return id;
	}

	public ArrayList<Position> getMovesList() {
		return movesList;
	}

	public void setAlive(boolean alive) {
		isAlive = alive;
	}

	public void setPosition(int row, int col){
		this.position = new Position(row, col);
		movesList.add(position);
	}

	public boolean isSameGroup(ConcretePiece other) {        // Checks if two pieces are in the same group
		return this.owner == other.owner;
	}
}
