package icbm.zhapin.daodan;

import icbm.api.explosion.IExplosive;
import icbm.core.HaoMa;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.implement.ITier;

public class DaoDan implements ITier, IExplosive
{
	public static final DaoDan missileModule = new DModule("missileModule", HaoMa.getID(DaoDan.class.getSimpleName(), 100), 1);
	public static final DaoDan zhuiZhong = new DZhuiZhong("homing", HaoMa.getID(DaoDan.class.getSimpleName()), 1);
	public static final DaoDan fanDan = new DFanDan("antiBallistic", HaoMa.getID(DaoDan.class.getSimpleName()), 2);
	public static final DaoDan fenZhiDan = new DFenZhiDan("cluster", HaoMa.getID(DaoDan.class.getSimpleName()), 2);
	public static final DaoDan yuanZiFenZhiDan = new DYuanZiFenZhiDan("nuclearCluster", HaoMa.getID(DaoDan.class.getSimpleName()), 2);

	public static final int MAX_DAO_DAN = 4;

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

		if (list[ID] != null)
		{
			throw new IllegalArgumentException("Missile " + ID + " is already occupied when adding " + this + "!");
		}

		list[ID] = this;
		this.mingZi = name;
		this.tier = tier;
		this.ID = ID;
	}

	@Override
	public int getID()
	{
		return this.ID;
	}

	@Override
	public String getUnlocalizedName()
	{
		return this.mingZi;
	}

	public String getTranslatedMing()
	{
		return TranslationHelper.getLocal("icbm.missile." + this.mingZi);
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
	 * Called when launched.
	 */
	public void launch(EDaoDan missileObj)
	{
	}

	/**
	 * Called every tick while flying.
	 */
	public void update(EDaoDan missileObj)
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
		return new ItemStack(ZhuYaoZhaPin.itDaoDan, 1, this.getID());
	}

	/**
	 * Is this missile compatible with the cruise launcher?
	 * 
	 * @return
	 */
	public boolean isCruise()
	{
		return true;
	}

	@Override
	public String getExplosiveName()
	{
		return this.getTranslatedMing();
	}

	@Override
	public String getGrenadeName()
	{
		return this.getTranslatedMing();
	}

	@Override
	public String getMissileName()
	{
		return this.getTranslatedMing();
	}

	@Override
	public String getMinecartName()
	{
		return this.getTranslatedMing();
	}

	@Override
	public float getRadius()
	{
		return 0;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}
}
