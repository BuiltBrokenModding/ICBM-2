package icbm.gangshao.terminal;

import icbm.gangshao.ISpecialAccess;
import icbm.gangshao.shimian.IScroll;

import java.util.List;

public interface ITerminal extends ISpecialAccess, IScroll
{
	/**
	 * Gets an output of the string stored in the console.
	 */
	public List<String> getTerminalOuput();

	/**
	 * Adds a string to the console. Server side only.
	 */
	public boolean addToConsole(String msg);
}
