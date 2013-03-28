package mffs.api;

/**
 * Applied to TileEntites that can have different toggle status.
 * 
 * @author Calclavia
 * 
 */
public interface IStatusToggle
{
	public boolean canToggle();

	public void onToggle();

	public boolean getStatusValue();

	public short getStatusMode();
}