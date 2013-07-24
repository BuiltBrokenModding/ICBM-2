package icbm.zhapin.zhapin;

import icbm.api.explosion.IExplosive;
import icbm.core.SheDing;
import icbm.core.base.MICBM;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.daodan.DFanDan;
import icbm.zhapin.zhapin.daodan.DFenZhiDan;
import icbm.zhapin.zhapin.daodan.DModule;
import icbm.zhapin.zhapin.daodan.DYuanZiFenZhiDan;
import icbm.zhapin.zhapin.daodan.DZhuiZhong;
import icbm.zhapin.zhapin.daodan.DaoDan;
import icbm.zhapin.zhapin.ex.ExBingDan;
import icbm.zhapin.zhapin.ex.ExDiLei;
import icbm.zhapin.zhapin.ex.ExDianCi;
import icbm.zhapin.zhapin.ex.ExDuQi;
import icbm.zhapin.zhapin.ex.ExFanWuSu;
import icbm.zhapin.zhapin.ex.ExHongSu;
import icbm.zhapin.zhapin.ex.ExHuanYuan;
import icbm.zhapin.zhapin.ex.ExHuo;
import icbm.zhapin.zhapin.ex.ExPiaoFu;
import icbm.zhapin.zhapin.ex.ExQunDan;
import icbm.zhapin.zhapin.ex.ExShengBuo;
import icbm.zhapin.zhapin.ex.ExTaiYang;
import icbm.zhapin.zhapin.ex.ExTuPuo;
import icbm.zhapin.zhapin.ex.ExTuiLa;
import icbm.zhapin.zhapin.ex.ExWan;
import icbm.zhapin.zhapin.ex.ExWuQi;
import icbm.zhapin.zhapin.ex.ExYaSuo;
import icbm.zhapin.zhapin.ex.ExYuanZi;
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
public abstract class ZhaPin implements IExplosive
{
	/** Explosives */
	public static final ZhaPin yaSuo;
	public static final ZhaPin xiaoQunDan;
	public static final ZhaPin huo;
	public static final ZhaPin wuQi;
	public static final ZhaPin duQi;
	public static final ZhaPin zhen;
	public static final ZhaPin tui;
	public static final ZhaPin la;

	public static final ZhaPin qunDan;
	public static final ZhaPin chuanRan;
	public static final ZhaPin shengBuo;
	public static final ZhaPin tuPuo;
	public static final ZhaPin huanYuan;
	public static final ZhaPin wenYa;
	public static final ZhaPin diLei;

	public static final ZhaPin yuanZi;
	public static final ZhaPin dianCi;
	public static final ZhaPin taiYang;
	public static final ZhaPin bingDan;
	public static final ZhaPin piaoFu;
	public static final ZhaPin wanDan;
	public static final ZhaPin chaoShengBuo;

	public static final ZhaPin fanWuSu;
	public static final ZhaPin hongSu;

	/** Missiles */
	public static final DaoDan missileModule;
	public static final DaoDan zhuiZhong;
	public static final DaoDan fanDan;
	public static final DaoDan fenZhiDan;
	public static final DaoDan yuanZiFenZhiDan;

	static
	{
		SheDing.CONFIGURATION.load();
		yaSuo = ZhaPinRegistry.register(new ExYaSuo("condensed", 1));
		xiaoQunDan = ZhaPinRegistry.register(new ExQunDan("shrapnel", 1));
		huo = ZhaPinRegistry.register(new ExHuo("incendiary", 1));
		wuQi = ZhaPinRegistry.register(new ExWuQi("debilitation", 1));
		duQi = ZhaPinRegistry.register(new ExDuQi("chemical", 1));
		zhen = ZhaPinRegistry.register(new ExQunDan("anvil", 1));
		tui = ZhaPinRegistry.register(new ExTuiLa("repulsive", 1));
		la = ZhaPinRegistry.register(new ExTuiLa("attractive", 1));

		qunDan = ZhaPinRegistry.register(new ExQunDan("fragmentation", 2));
		chuanRan = ZhaPinRegistry.register(new ExDuQi("contagious", 2));
		shengBuo = ZhaPinRegistry.register(new ExShengBuo("sonic", 2));
		tuPuo = ZhaPinRegistry.register(new ExTuPuo("breaching", 2));
		huanYuan = ZhaPinRegistry.register(new ExHuanYuan("rejuvenation", 2));
		wenYa = ZhaPinRegistry.register(new ExYuanZi("thermobaric", 2));
		diLei = ZhaPinRegistry.register(new ExDiLei("sMine", 2));

		yuanZi = ZhaPinRegistry.register(new ExYuanZi("nuclear", 3));
		dianCi = ZhaPinRegistry.register(new ExDianCi("emp", 3));
		taiYang = ZhaPinRegistry.register(new ExTaiYang("exothermic", 3));
		bingDan = ZhaPinRegistry.register(new ExBingDan("endothermic", 3));
		piaoFu = ZhaPinRegistry.register(new ExPiaoFu("antiGravitational", 3));
		wanDan = ZhaPinRegistry.register(new ExWan("ender", 3));
		chaoShengBuo = ZhaPinRegistry.register(new ExShengBuo("hypersonic", 3));

		fanWuSu = ZhaPinRegistry.register(new ExFanWuSu("antimatter", 4));
		hongSu = ZhaPinRegistry.register(new ExHongSu("redMatter", 4));

		/** Missiles */
		missileModule = (DaoDan) ZhaPinRegistry.register(new DModule("missileModule", 1));
		zhuiZhong = (DaoDan) ZhaPinRegistry.register(new DZhuiZhong("homing", 1));
		fanDan = (DaoDan) ZhaPinRegistry.register(new DFanDan("antiBallistic", 2));
		fenZhiDan = (DaoDan) ZhaPinRegistry.register(new DFenZhiDan("cluster", 2));
		yuanZiFenZhiDan = (DaoDan) ZhaPinRegistry.register(new DYuanZiFenZhiDan("nuclearCluster", 3));

		SheDing.CONFIGURATION.save();
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

	protected ZhaPin(String mingZi, int tier)
	{
		this.mingZi = mingZi;
		this.tier = tier;
		this.yinXin = 100;

		this.hasBlock = true;
		this.hasMissile = true;
		this.hasGrenade = this.tier <= 1;
		this.hasMinecart = this.tier <= 2;

		this.qiZi = FlagRegistry.registerFlag("ban_" + this.mingZi);
		this.isDisabled = SheDing.CONFIGURATION.get("Disable_Explosives", "Disable " + this.mingZi, false).getBoolean(false);

	}

	@Override
	public final int getID()
	{
		return ZhaPinRegistry.getID(this.getUnlocalizedName());
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

	public ZhaPin setYinXin(int fuse)
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
	public MICBM getBlockModel()
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
	public MICBM getMissileModel()
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
		return new ItemStack(ZhuYaoZhaPin.bZhaDan, 1, this.getID());
	}

	public ItemStack getItemStack(int amount)
	{
		return new ItemStack(ZhuYaoZhaPin.bZhaDan, amount, this.getID());
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
