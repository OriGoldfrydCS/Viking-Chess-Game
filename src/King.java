public class King extends ConcretePiece {

	// Constructor
	public King(ConcretePlayer owner, Position position,int id) {
		super(owner, position, id);
	}

	// Methods

	// This method returns the player's tool
	@Override
	public String getType() {
		return "♔";
	}

	// This method return 'K' for the king
	public char getPrefix() {
		return KING_CODE;
	}
}
