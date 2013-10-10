package icbm.explosion.zhapin;

import icbm.api.explosion.IExplosive;
import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.zhapin.daodan.DFanDan;
import icbm.explosion.zhapin.daodan.DFenZhiDan;
import icbm.explosion.zhapin.daodan.DModule;
import icbm.explosion.zhapin.daodan.DYuanZiFenZhiDan;
import icbm.explosion.zhapin.daodan.DZhuiZhong;
import icbm.explosion.zhapin.daodan.DaoDan;
import icbm.explosion.zhapin.ex.ExBingDan;
import icbm.explosion.zhapin.ex.ExDiLei;
import icbm.explosion.zhapin.ex.ExDianCi;
import icbm.explosion.zhapin.ex.ExDuQi;
import icbm.explosion.zhapin.ex.ExFanWuSu;
import icbm.explosion.zhapin.ex.ExHongSu;
import icbm.explosion.zhapin.ex.ExHuanYuan;
import icbm.explosion.zhapin.ex.ExHuo;
import icbm.explosion.zhapin.ex.ExPiaoFu;
import icbm.explosion.zhapin.ex.ExQunDan;
import icbm.explosion.zhapin.ex.ExShengBuo;
import icbm.explosion.zhapin.ex.ExTaiYang;
import icbm.explosion.zhapin.ex.ExTuPuo;
import icbm.explosion.zhapin.ex.ExTuiLa;
import icbm.explosion.zhapin.ex.ExWan;
import icbm.explosion.zhapin.ex.ExWuQi;
import icbm.explosion.zhapin.ex.ExYaSuo;
import icbm.explosion.zhapin.ex.ExYuanZi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import calclavia.lib.flag.FlagRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The explosive registry class. Used to register explosions.
 */
public abstract class Explosive implements IExplosive
{
	/** Explosives */
	public static final Explosive yaSuo;
	public static final Explosive xiaoQunDan;
	public static final Explosive huo;
	public static final Explosive wuQi;
	public static final Explosive duQi;
	public static final Explosive zhen;
	public static final Explosive tui;
	public static final Explosive la;

	public static final Explosive qunDan;
	public static final Explosive chuanRan;
	public static final Explosive shengBuo;
	public static final Explosive tuPuo;
	public static final Explosive huanYuan;
	public static final Explosive wenYa;
	public static final Explosive sMine;

	public static final Explosive yuanZi;
	public static final Explosive dianCi;
	public static final Explosive taiYang;
	public static final Explosive bingDan;
	public static final Explosive piaoFu;
	public static final Explosive wanDan;
	public static final Explosive chaoShengBuo;

	public static final Explosive fanWuSu;
	public static final Explosive hongSu;

	/** Missiles */
	public static final DaoDan missileModule;
	public static final DaoDan zhuiZhong;
	public static final DaoDan fanDan;
	public static final DaoDan fenZhiDan;
	public static final DaoDan yuanZiFenZhiDan;

	public static boolean registered = false;

	static
	{
		ICBMConfiguration.CONFIGURATION.load();

		yaSuo = ExplosiveRegistry.register(new ExYaSuo("condensed", 1));
		xiaoQunDan = ExplosiveRegistry.register(new ExQunDan("shrapnel", 1));
		huo = ExplosiveRegistry.register(new ExHuo("incendiary", 1));
		wuQi = ExplosiveRegistry.register(new ExWuQi("debilitation", 1));
		duQi = ExplosiveRegistry.register(new ExDuQi("chemical", 1));
		zhen = ExplosiveRegistry.register(new ExQunDan("anvil", 1));
		tui = ExplosiveRegistry.register(new ExTuiLa("repulsive", 1));
		la = ExplosiveRegistry.register(new ExTuiLa("attractive", 1));

		qunDan = ExplosiveRegistry.register(new ExQunDan("fragmentation", 2));
		chuanRan = ExplosiveRegistry.register(new ExDuQi("contagious", 2));
		shengBuo = ExplosiveRegistry.register(new ExShengBuo("sonic", 2));
		tuPuo = ExplosiveRegistry.register(new ExTuPuo("breaching", 2));
		huanYuan = ExplosiveRegistry.register(new ExHuanYuan("rejuvenation", 2));
		wenYa = ExplosiveRegistry.register(new ExYuanZi("thermobaric", 2));
		sMine = ExplosiveRegistry.register(new ExDiLei("sMine", 2));

		yuanZi = ExplosiveRegistry.register(new ExYuanZi("nuclear", 3));
		dianCi = ExplosiveRegistry.register(new ExDianCi("emp", 3));
		taiYang = ExplosiveRegistry.register(new ExTaiYang("exothermic", 3));
		bingDan = ExplosiveRegistry.register(new ExBingDan("endothermic", 3));
		piaoFu = ExplosiveRegistry.register(new ExPiaoFu("antiGravitational", 3));
		wanDan = ExplosiveRegistry.register(new ExWan("ender", 3));
		chaoShengBuo = ExplosiveRegistry.register(new ExShengBuo("hypersonic", 3));

		fanWuSu = ExplosiveRegistry.register(new ExFanWuSu("antimatter", 4));
		hongSu = ExplosiveRegistry.register(new ExHongSu("redMatter", 4));

		/** Missiles */
		missileModule = (DaoDan) ExplosiveRegistry.register(new DModule("missileModule", 1));
		zhuiZhong = (DaoDan) ExplosiveRegistry.register(new DZhuiZhong("homing", 1));
		fanDan = (DaoDan) ExplosiveRegistry.register(new DFanDan("antiBallistic", 2));
		fenZhiDan = (DaoDan) ExplosiveRegistry.register(new DFenZhiDan("cluster", 2));
		yuanZiFenZhiDan = (DaoDan) ExplosiveRegistry.register(new DYuanZiFenZhiDan("nuclearCluster", 3));

		ICBMConfiguration.CONFIGURATION.save();
		registered = true;
	}

	/** The unique identification name for this explosive. */
	private String mingZi;
	/** The tier of this explosive */
	private int tier;
	/** The fuse of this explosive */
	private int yinXin;
	/** The flag name of this explosive */
	public final String qiZi;
	/** Is this explosive disabled? */
	protected boolean isDisabled;
	/** Is this explosive able to be pushed by other explosions? */
	protected boolean isMobile = false;

	protected boolean hasBlock;
	protected boolean hasGrenade;
	protected boolean hasMinecart;
	protected boolean hasMissile;

	protected Explosive(String mingZi, int tier)
	{
		this.mingZi = mingZi;
		this.tier = tier;
		this.yinXin = 100;

		this.hasBlock = true;
		this.hasMissile = true;
		this.hasGrenade = this.tier <= 1;
		this.hasMinecart = this.tier <= 2;

		this.qiZi = FlagRegistry.registerFlag("ban_" + this.mingZi);
		this.isDisabled = ICBMConfiguration.CONFIGURATION.get("Disable_Explosives", "Disable " + this.mingZi, false).getBoolean(false);

	}

	@Override
	public final int getID()
	{
		return ExplosiveRegistry.getID(this.getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName()
	{
		return this.mingZi;
	}

	@Override
	public String getExplosiveName()
	{
		return TranslationHelper.getLocal("icbm.explosive." + this.mingZi + ".name");
	}

	@Override
	public String getGrenadeName()
	{
		return TranslationHelper.getLocal("icbm.grenade." + this.mingZi + ".name");
	}

	@Override
	public String getMissileName()
	{
		return TranslationHelper.getLocal("icbm.missile." + this.mingZi + ".name");
	}

	@Override
	public String getMinecartName()
	{
		return TranslationHelper.getLocal("icbm.minecart." + this.mingZi + ".name");
	}

	@Override
	public int getTier()
	{
		return this.tier;
	}

	@Override
	public void setTier(int tier)
	{
		this.tier = tier;
	}

	public Explosive setYinXin(int fuse)
	{
		this.yinXin = fuse;
		return this;
	}

	/**
	 * The fuse of the explosion
	 * 
	 * @return The Fuse
	 */
	public int getYinXin()
	{
		return yinXin;
	}

	/**
	 * Called at the before the explosive detonated as a block.
	 * 
	 * @param world
	 * @param entity
	 */
	public void yinZhaQian(World world, Entity entity)
	{
		world.playSoundAtEntity(entity, "random.fuse", 1.0F, 1.0F);
	}

	/**
	 * Called while the explosive is being detonated (fuse ticks) in block form.
	 * 
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	public void onYinZha(World world, Vector3 position, int fuseTicks)
	{
		world.spawnParticle("smoke", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
	}

	/**
	 * Called when the block for of this explosive is destroy by an explosion
	 * 
	 * @return - Fuse left
	 */
	public int onBeiZha()
	{
		return (int) (this.yinXin / 2 + Math.random() * this.yinXin / 4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelICBM getBlockModel()
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getBlockResource()
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon()
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelICBM getMissileModel()
	{
		return null;
	}

	public boolean hasGrenadeForm()
	{
		return this.hasGrenade;
	}

	public boolean hasMissileForm()
	{
		return this.hasMissile;
	}

	public boolean hasMinecartForm()
	{
		return this.hasMinecart;
	}

	public boolean hasBlockForm()
	{
		return this.hasBlock;
	}

	/**
	 * Called to add the recipe for this explosive
	 */
	public void init()
	{

	}

	public ItemStack getItemStack()
	{
		return this.getItemStack(1);
	}

	public ItemStack getItemStack(int amount)
	{
		return new ItemStack(ICBMExplosion.bZhaDan, amount, this.getID());
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
	{
		return false;
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (!this.isDisabled)
		{
			this.doCreateExplosion(world, x, y, z, entity);
		}
	}

	public abstract void doCreateExplosion(World world, double x, double y, double z, Entity entity);
}
