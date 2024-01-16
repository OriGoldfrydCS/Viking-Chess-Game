public class King extends ConcretePiece {

	// Constructor
	public King(ConcretePlayer owner, Position position,int id) {
		super(owner, position, id);
	}

	// Methods
	@Override
	public String getType() {
		return "♔";
	}
	
	public char getPrefix() {
		return 'K';
	}
}
