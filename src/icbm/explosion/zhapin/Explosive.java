package icbm.explosion.zhapin;

import icbm.api.explosion.IExplosive;
import icbm.core.ICBMConfiguration;
import icbm.core.base.ModelICBM;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.zhapin.ex.ExEndothermic;
import icbm.explosion.zhapin.ex.ExSMine;
import icbm.explosion.zhapin.ex.ExEMP;
import icbm.explosion.zhapin.ex.ExChemical;
import icbm.explosion.zhapin.ex.ExAntimatter;
import icbm.explosion.zhapin.ex.ExRedMatter;
import icbm.explosion.zhapin.ex.ExRejuvenation;
import icbm.explosion.zhapin.ex.ExIncendiary;
import icbm.explosion.zhapin.ex.ExAntiGravitational;
import icbm.explosion.zhapin.ex.ExShrapnel;
import icbm.explosion.zhapin.ex.ExSonic;
import icbm.explosion.zhapin.ex.ExExothermic;
import icbm.explosion.zhapin.ex.ExBreaching;
import icbm.explosion.zhapin.ex.ExRepulsive;
import icbm.explosion.zhapin.ex.ExEnder;
import icbm.explosion.zhapin.ex.ExDebilitation;
import icbm.explosion.zhapin.ex.ExCondensed;
import icbm.explosion.zhapin.ex.ExNuclear;
import icbm.explosion.zhapin.missile.DYuanZiFenZhiDan;
import icbm.explosion.zhapin.missile.MissileHoming;
import icbm.explosion.zhapin.missile.Missile;
import icbm.explosion.zhapin.missile.MissileAnti;
import icbm.explosion.zhapin.missile.MissileCluster;
import icbm.explosion.zhapin.missile.MissileModule;
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
	public static final Missile missileModule;
	public static final Missile zhuiZhong;
	public static final Missile fanDan;
	public static final Missile fenZhiDan;
	public static final Missile yuanZiFenZhiDan;

	public static boolean registered = false;

	static
	{
		ICBMConfiguration.CONFIGURATION.load();

		yaSuo = ExplosiveRegistry.register(new ExCondensed("condensed", 1));
		xiaoQunDan = ExplosiveRegistry.register(new ExShrapnel("shrapnel", 1));
		huo = ExplosiveRegistry.register(new ExIncendiary("incendiary", 1));
		wuQi = ExplosiveRegistry.register(new ExDebilitation("debilitation", 1));
		duQi = ExplosiveRegistry.register(new ExChemical("chemical", 1));
		zhen = ExplosiveRegistry.register(new ExShrapnel("anvil", 1));
		tui = ExplosiveRegistry.register(new ExRepulsive("repulsive", 1));
		la = ExplosiveRegistry.register(new ExRepulsive("attractive", 1));

		qunDan = ExplosiveRegistry.register(new ExShrapnel("fragmentation", 2));
		chuanRan = ExplosiveRegistry.register(new ExChemical("contagious", 2));
		shengBuo = ExplosiveRegistry.register(new ExSonic("sonic", 2));
		tuPuo = ExplosiveRegistry.register(new ExBreaching("breaching", 2));
		huanYuan = ExplosiveRegistry.register(new ExRejuvenation("rejuvenation", 2));
		wenYa = ExplosiveRegistry.register(new ExNuclear("thermobaric", 2));
		sMine = ExplosiveRegistry.register(new ExSMine("sMine", 2));

		yuanZi = ExplosiveRegistry.register(new ExNuclear("nuclear", 3));
		dianCi = ExplosiveRegistry.register(new ExEMP("emp", 3));
		taiYang = ExplosiveRegistry.register(new ExExothermic("exothermic", 3));
		bingDan = ExplosiveRegistry.register(new ExEndothermic("endothermic", 3));
		piaoFu = ExplosiveRegistry.register(new ExAntiGravitational("antiGravitational", 3));
		wanDan = ExplosiveRegistry.register(new ExEnder("ender", 3));
		chaoShengBuo = ExplosiveRegistry.register(new ExSonic("hypersonic", 3));

		fanWuSu = ExplosiveRegistry.register(new ExAntimatter("antimatter", 4));
		hongSu = ExplosiveRegistry.register(new ExRedMatter("redMatter", 4));

		/** Missiles */
		missileModule = (Missile) ExplosiveRegistry.register(new MissileModule("missileModule", 1));
		zhuiZhong = (Missile) ExplosiveRegistry.register(new MissileHoming("homing", 1));
		fanDan = (Missile) ExplosiveRegistry.register(new MissileAnti("antiBallistic", 2));
		fenZhiDan = (Missile) ExplosiveRegistry.register(new MissileCluster("cluster", 2));
		yuanZiFenZhiDan = (Missile) ExplosiveRegistry.register(new DYuanZiFenZhiDan("nuclearCluster", 3));

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
		return new ItemStack(ICBMExplosion.blockExplosive, amount, this.getID());
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
