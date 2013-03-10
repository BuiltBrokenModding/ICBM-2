package mffs.api;

public interface IForceEnergyStorageBlock {
	
	public int getPercentageStorageCapacity();
	
	public int getPowerStorageID();
	
	public int getStorageMaxPower();
	
	public int getStorageAvailablePower();
	
	public boolean consumePowerfromStorage(int powerAmount,boolean simulation);
	
	public boolean insertPowertoStorage(int powerAmount,boolean simulation);

	public int getfreeStorageAmount();
	
}
