

public class Pawn extends ConcretePiece {

	// Data Members
	private int enemyDefeats;			// A unique variable for Pawn that counts how many enemy's pieces it kills

	// Constructor
	public Pawn(ConcretePlayer owner, Position position,int id) {
		super(owner, position,id);
		this.enemyDefeats = 0;
	}

	// Methods
	@Override
	public String getType() {
		return "♙";
	}

	public char getPrefix() {
		if(owner.isPlayerOne())
			return DEFENSE_CODE;
		else
			return ATTACK_CODE;
	}
	
	public int getEnemyDefeats(){
		return this.enemyDefeats;
	}

	public void addDefeat(){
		enemyDefeats++;
	}



//	@Override
//	public Player getOwner() {
//		return getOwner();
//	}
}
