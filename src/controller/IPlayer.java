package controller;

public interface IPlayer {
	/**
	 * Returns the name of the player.
	 * @return
	 */
	public String name();
	
	/**
	 * Called when a class, conforming to the player type
	 * should make a move.
	 */
	public void move(IGameController controller);
}
