package icbm.gangshao;

public interface IScroll
{
	/**
	 * Scrolls the text field up or down. Client side only. Positive value will scroll the text down
	 * while a negative value will scroll it up.
	 */
	public void scroll(int amount);

	public void setScroll(int length);

	public int getScroll();
}
