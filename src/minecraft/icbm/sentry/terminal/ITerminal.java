package icbm.sentry.terminal;

import java.util.List;

public interface ITerminal extends ISpecialAccess
{
	/**
	 * Gets an output of the string stored in the console.
	 */
	public List<String> getTerminalOuput();

	/**
	 * Adds a string to the console. Server side only.
	 */
	public boolean addToConsole(String msg);

	/**
	 * Scrolls the terminal up and down. Client side only.
	 * 
	 * @param amount - Lines.
	 */
	public void scrollUp(int amount);

	public void scrollDown(int amount);

	public int getScroll();

}
