package icbm.common.daodan;

import icbm.common.ZhuYao;
import icbm.common.zhapin.ZhaPin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.ITier;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class DaoDan implements ITier
{
	public static final DaoDan AntiBallistic = new DFanDan("antiBallistic", 101, 2);
	public static final DaoDan Cluster = new DFenZhiDan("cluster", 102, 2);
	public static final DaoDan NuclearCluster = new DYuanZiFenZhiDan("nuclearCluster", 103, 2);
	public static final DaoDan Homing = new DZhuiZhong("homing", 104, 1);

	public static DaoDan[] list;

	private String mingZi;
	private int ID;
	private int tier;

	public DaoDan(String name, int ID, int tier)
	{
		if (list == null)
		{
			list = new DaoDan[256];
		}

		if (list[ID] != null) { throw new IllegalArgumentException("Missile " + ID + " is already occupied when adding " + this + "!"); }

		list[ID] = this;
		this.mingZi = name;
		this.tier = tier;
		this.ID = ID;
	}

	public int getID()
	{
		return this.ID;
	}

	public String getMing()
	{
		return LanguageRegistry.instance().getStringLocalization("icbm.missile." + this.mingZi);
	}

	@Override
	public int getTier()
	{
		return this.tier;
	}

	@Override
	public void setTier(int tier)
	{
	}

	/**
	 * Called every tick while flying
	 */
	public void onTickFlight(EDaoDan missileObj)
	{
	}

	public boolean onInteract(EDaoDan missileObj, EntityPlayer par1EntityPlayer)
	{
		return false;
	}

	/**
	 * Called when the missile blows up
	 * 
	 * @param missileObj
	 */
	public void onExplode(EDaoDan missileObj)
	{
		ZhaPin.createBaoZha(missileObj.worldObj, new Vector3(missileObj), missileObj, missileObj.haoMa);
	}

	public ItemStack getItemStack()
	{
		return new ItemStack(ZhuYao.itDaoDan, 1, this.getID());
	}

	public boolean isCruise()
	{
		return true;
	}
}
