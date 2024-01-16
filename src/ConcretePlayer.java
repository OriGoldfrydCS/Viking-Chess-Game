

public class ConcretePlayer implements Player, Cloneable{		

	// Data Members
	private int victories;					// A variable that counts winnings for Player 1 & 2
	private boolean isPlayerOne;			// A flag for Player 1 (the attacker)

	// Constructor
	public ConcretePlayer(boolean isPlayerOne) {
        this.isPlayerOne = isPlayerOne;
        this.victories = 0;
    }

	// Methods
	@Override
	public boolean isPlayerOne() {		
		return isPlayerOne;
	}

	@Override
	public int getWins() {				
		return victories;
	}
	
	public void incrementVictories() {
		victories++;
	}

}
