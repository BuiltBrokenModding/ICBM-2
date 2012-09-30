package icbm.daodan;

import icbm.ICBM;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.ItemStack;
import universalelectricity.implement.ITier;
import universalelectricity.prefab.Vector3;

public class DaoDan implements ITier
{
	public static final DaoDan AntiBallistic = new DFanDan("Anti-Ballistic", 101, 2);
	public static final DaoDan Cluster = new DFenZhiDan("Cluster", 102, 2);
	public static final DaoDan NuclearCluster = new DYuanZiFenZhiDan("Nuclear Cluster", 103, 2);

	public static DaoDan[] list;

	private String name;
	private int ID;
	private int tier;

	public DaoDan(String name, int ID, int tier)
	{
    	if(list == null)
    	{
    		list = new DaoDan[256];
    	}
    	
    	if(list[ID] != null)
        {
            throw new IllegalArgumentException("Missile " + ID + " is already occupied when adding "+this+"!");
        }
    	
    	list[ID] = this;
    	this.name = name;
        this.tier = tier;
        this.ID = ID;
    }
	
	public int getID() { return this.ID; }
	
	public String getMing() { return this.name; }
	
	@Override
	public int getTier() { return this.tier; }

	@Override
	public void setTier(int tier) { }
	
	/**
	 * Called every tick while flying
	 */
	public void onTickFlight(EDaoDan missileObj)
	{
		
	}
	
	/**
	 * Called when the missile blows up
	 * @param missileObj
	 */
	public void onExplode(EDaoDan missileObj)
	{
		ZhaPin.createBaoZha(missileObj.worldObj, Vector3.get(missileObj), missileObj, missileObj.missileID);
	}
	
	public ItemStack getItemStack()
	{
		return new ItemStack(ICBM.itemDaoDan, 1, this.getID());
	}
	
	public boolean isCruise() { return true; }
}
