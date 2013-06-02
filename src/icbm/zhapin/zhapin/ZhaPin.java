package icbm.zhapin.zhapin;

import icbm.api.explosion.ExplosionEvent.PostExplosionEvent;
import icbm.api.explosion.ExplosionEvent.PreExplosionEvent;
import icbm.api.explosion.IExplosive;
import icbm.core.HaoMa;
import icbm.core.ZhuYaoBase;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.daodan.DaoDan;
import icbm.zhapin.daodan.EDaoDan;
import icbm.zhapin.zhapin.ex.ExBianZhong;
import icbm.zhapin.zhapin.ex.ExBingDan;
import icbm.zhapin.zhapin.ex.ExBingDan2;
import icbm.zhapin.zhapin.ex.ExChaoShengBuo;
import icbm.zhapin.zhapin.ex.ExDiLei;
import icbm.zhapin.zhapin.ex.ExDianCi;
import icbm.zhapin.zhapin.ex.ExDianCiSignal;
import icbm.zhapin.zhapin.ex.ExDianCiWave;
import icbm.zhapin.zhapin.ex.ExDuQi;
import icbm.zhapin.zhapin.ex.ExFanWuSu;
import icbm.zhapin.zhapin.ex.ExFuLan;
import icbm.zhapin.zhapin.ex.ExHongSu;
import icbm.zhapin.zhapin.ex.ExHuanYuan;
import icbm.zhapin.zhapin.ex.ExHuo;
import icbm.zhapin.zhapin.ex.ExPiaoFu;
import icbm.zhapin.zhapin.ex.ExQi;
import icbm.zhapin.zhapin.ex.ExQunDan;
import icbm.zhapin.zhapin.ex.ExShengBuo;
import icbm.zhapin.zhapin.ex.ExTaiYang;
import icbm.zhapin.zhapin.ex.ExTaiYang2;
import icbm.zhapin.zhapin.ex.ExTuPuo;
import icbm.zhapin.zhapin.ex.ExTuiLa;
import icbm.zhapin.zhapin.ex.ExWan;
import icbm.zhapin.zhapin.ex.ExWenYa;
import icbm.zhapin.zhapin.ex.ExYaSuo;
import icbm.zhapin.zhapin.ex.ExYuanZi;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.flag.FlagRegistry;
import universalelectricity.prefab.implement.ITier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ZhaPin implements ITier, IExplosive
{
	public enum ZhaPinType
	{
		QUAN_BU, ZHA_DAN, SHOU_LIU_DAN, DAO_DAN, CHE;

		public static ZhaPinType get(int id)
		{
			if (id >= 0 && id < ZhaPinType.values().length)
			{
				return ZhaPinType.values()[id];
			}

			return null;
		}
	}

	public static final ZhaPin yaSuo = new ExYaSuo("condensed", HaoMa.getID(ZhaPin.class.getSimpleName()), 1);
	public static final ZhaPin xiaoQunDan = new ExQunDan("shrapnel", HaoMa.getID(ZhaPin.class.getSimpleName()), 1);
	public static final ZhaPin huo = new ExHuo("incendiary", HaoMa.getID(ZhaPin.class.getSimpleName()), 1);
	public static final ZhaPin qi = new ExQi("debilitation", HaoMa.getID(ZhaPin.class.getSimpleName()), 1);
	public static final ZhaPin duQi = new ExDuQi("chemical", HaoMa.getID(ZhaPin.class.getSimpleName()), 1);
	public static final ZhaPin zhen = new ExQunDan("anvil", HaoMa.getID(ZhaPin.class.getSimpleName()), 1);
	public static final ZhaPin tui = new ExTuiLa("repulsive", HaoMa.getID(ZhaPin.class.getSimpleName()), 1);
	public static final ZhaPin la = new ExTuiLa("attractive", HaoMa.getID(ZhaPin.class.getSimpleName()), 1);

	public static final int E_YI_ID = la.getID() + 1;

	public static final ZhaPin qunDan = new ExQunDan("fragmentation", HaoMa.getID(ZhaPin.class.getSimpleName()), 2);
	public static final ZhaPin chuanRan = new ExDuQi("contagious", HaoMa.getID(ZhaPin.class.getSimpleName()), 2);
	public static final ZhaPin shengBuo = new ExShengBuo("sonic", HaoMa.getID(ZhaPin.class.getSimpleName()), 2);
	public static final ZhaPin tuPuo = new ExTuPuo("breaching", HaoMa.getID(ZhaPin.class.getSimpleName()), 2);
	public static final ZhaPin huanYuan = new ExHuanYuan("rejuvenation", HaoMa.getID(ZhaPin.class.getSimpleName()), 2);
	public static final ZhaPin wenYa = new ExWenYa("thermobaric", HaoMa.getID(ZhaPin.class.getSimpleName()), 2);

	public static final int E_ER_ID = wenYa.getID() + 1;

	public static final ZhaPin yuanZi = new ExYuanZi("nuclear", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin dianCi = new ExDianCi("emp", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin taiYang = new ExTaiYang("exothermic", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin bingDan = new ExBingDan("endothermic", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin piaoFu = new ExPiaoFu("antiGravitational", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin wanDan = new ExWan("ender", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin chaoShengBuo = new ExChaoShengBuo("hypersonic", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);

	public static final int E_SAN_ID = chaoShengBuo.getID() + 1;

	public static final ZhaPin fanWuSu = new ExFanWuSu("antimatter", HaoMa.getID(ZhaPin.class.getSimpleName()), 4);
	public static final ZhaPin hongSu = new ExHongSu("redMatter", HaoMa.getID(ZhaPin.class.getSimpleName()), 4);

	public static final int E_SI_ID = hongSu.getID() + 1;

	public static final ZhaPin diLei = new ExDiLei("sMine", HaoMa.getID(ZhaPin.class.getSimpleName()), 2);

	// Hidden Explosives
	public static final ZhaPin dianCiWave = new ExDianCiWave("emp", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin dianCiSignal = new ExDianCiSignal("emp", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin taiYang2 = new ExTaiYang2("exothermic", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin fuLan = new ExFuLan("decayLand", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin bianZhong = new ExBianZhong("mutateLiving", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);
	public static final ZhaPin bingDan2 = new ExBingDan2("endothermic", HaoMa.getID(ZhaPin.class.getSimpleName()), 3);

	public static ZhaPin[] list;

	private String mingZi;
	private int ID;
	private int tier;
	private int yinXin;
	public DaoDan daoDan;
	public final String qiZi;
	protected boolean isDisabled;
	protected boolean isMobile = false;

	protected ZhaPin(String mingZi, int ID, int tier)
	{
		if (list == null)
		{
			list = new ZhaPin[32];
		}

		if (list[ID] != null)
		{
			throw new IllegalArgumentException("Explosive " + ID + " is already occupied by " + list[ID].getClass().getSimpleName() + "!");
		}

		list[ID] = this;
		this.mingZi = mingZi;
		this.tier = tier;
		this.yinXin = 100;
		this.ID = ID;
		this.daoDan = new DaoDan(mingZi, ID, tier);
		this.qiZi = FlagRegistry.registerFlag("ban_" + this.mingZi);

		ZhuYaoBase.CONFIGURATION.load();
		this.isDisabled = ZhuYaoBase.CONFIGURATION.get("Disable_Explosives", "Disable " + this.mingZi, false).getBoolean(false);
		ZhuYaoBase.CONFIGURATION.save();
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

	public void setYinXin(int fuse)
	{
		this.yinXin = fuse;
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
	 * Called at the start of a detontation
	 * 
	 * @param worldObj
	 * @param entity
	 */
	public void yinZhaQian(World worldObj, Entity entity)
	{
		worldObj.playSoundAtEntity(entity, "random.fuse", 1.0F, 1.0F);
	}

	/**
	 * Called when the explosive is on fuse and going to explode. Called only when the explosive is
	 * in it's TNT form.
	 * 
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
		worldObj.spawnParticle("smoke", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
	}

	/**
	 * Called when the TNT for of this explosive is destroy by an explosion
	 * 
	 * @return - Fuse left
	 */
	public int onBeiZha()
	{
		return (int) (this.yinXin / 2 + Math.random() * this.yinXin / 4);
	}

	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * 
	 * @param return - Return -1 if this explosive does not need procedural calls
	 */
	protected int proceduralInterval()
	{
		return -1;
	}

	public int proceduralInterval(World worldObj, int callCounts)
	{
		return this.proceduralInterval();
	}

	/**
	 * Called before an explosion happens
	 */
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		MinecraftForge.EVENT_BUS.post(new PreExplosionEvent(worldObj, position.x, position.y, position.z, this));
	}

	@SideOnly(Side.CLIENT)
	public Object[] getRenderData()
	{
		return null;
	}

	/**
	 * Called to do an explosion
	 * 
	 * @param explosionSource - The entity that did the explosion
	 * @param haoMa - The metadata of the explosive
	 * @param callCount - The amount of calls done for calling this explosion. Use only by
	 * procedural explosions
	 * @return - True if this explosive needs to continue to procedurally explode. False if
	 * otherwise
	 */
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource)
	{
	}

	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		doBaoZha(worldObj, position, explosionSource);
		return false;
	}

	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int metadata, int callCount)
	{
		return doBaoZha(worldObj, position, explosionSource, callCount);
	}

	/**
	 * Called every tick when this explosive is doing it's procedural explosion
	 * 
	 * @param ticksExisted - The ticks in which this explosive existed
	 */
	public void gengXin(World worldObj, Vector3 position, int ticksExisted)
	{
	}

	/**
	 * Called after the explosion is completed
	 */
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		MinecraftForge.EVENT_BUS.post(new PostExplosionEvent(worldObj, position.x, position.y, position.z, this));
	}

	public int countIncrement()
	{
		return 1;
	}

	/**
	 * Spawns an explosive (TNT form) in the world
	 * 
	 * @param worldObj
	 * @param position
	 * @param cause - 0: N/A, 1: Destruction, 2: Fire
	 */
	public void spawnZhaDan(World worldObj, Vector3 position, ForgeDirection orientation, byte cause)
	{
		if (!this.isDisabled)
		{
			position.add(0.5D);
			EZhaDan eZhaDan = new EZhaDan(worldObj, position, (byte) orientation.ordinal(), this.getID());

			switch (cause)
			{
				case 1:
					eZhaDan.destroyedByExplosion();
					break;
				case 2:
					eZhaDan.setFire(10);
					break;
			}

			worldObj.spawnEntityInWorld(eZhaDan);
		}
	}

	public void spawnZhaDan(World worldObj, Vector3 position, byte orientation)
	{
		this.spawnZhaDan(worldObj, position, ForgeDirection.getOrientation(orientation), (byte) 0);
	}

	/**
	 * Called to add the recipe for this explosive
	 */
	public void init()
	{
	};

	public ItemStack getItemStack()
	{
		return new ItemStack(ZhuYaoZhaPin.bZhaDan, 1, this.getID());
	}

	public ItemStack getItemStack(int amount)
	{
		return new ItemStack(ZhuYaoZhaPin.bZhaDan, amount, this.getID());
	}

	public static IExplosive getExplosiveByName(String name)
	{
		for (IExplosive explosive : list)
		{
			if (explosive.getUnlocalizedName().equalsIgnoreCase(name))
			{
				return explosive;
			}
		}

		return null;
	}

	/**
	 * Created an ICBM explosion.
	 * 
	 * @param entity - The entity that created this explosion. The explosion source.
	 * @param explosiveID - The ID of the explosive.
	 */
	public static void createExplosion(World worldObj, Double x, Double y, Double z, Entity entity, Integer explosiveID)
	{
		createBaoZha(worldObj, new Vector3(x, y, z), entity, explosiveID);
	}

	public static void createBaoZha(World worldObj, Vector3 position, Entity entity, int explosiveID)
	{
		if (!list[explosiveID].isDisabled)
		{
			if (list[explosiveID].proceduralInterval(worldObj, -1) > 0)
			{
				if (!worldObj.isRemote)
				{
					worldObj.spawnEntityInWorld(new EZhaPin(worldObj, position.clone(), explosiveID, list[explosiveID].isMobile));
				}
			}
			else
			{
				list[explosiveID].baoZhaQian(worldObj, position.clone(), entity);
				list[explosiveID].doBaoZha(worldObj, position.clone(), entity, explosiveID, -1);
				list[explosiveID].baoZhaHou(worldObj, position.clone(), entity);
			}
		}
	}

	public void doDamageEntities(World worldObj, Vector3 position, float radius, float power)
	{
		this.doDamageEntities(worldObj, position, radius, power, true);
	}

	public void doDamageEntities(World worldObj, Vector3 position, float radius, float power, boolean destroyItem)
	{
		// Step 2: Damage all entities
		radius *= 2.0F;
		Vector3 minCoord = position.clone();
		minCoord.add(-radius - 1);
		Vector3 maxCoord = position.clone();
		maxCoord.add(radius + 1);
		List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(minCoord.intX(), minCoord.intY(), minCoord.intZ(), maxCoord.intX(), maxCoord.intY(), maxCoord.intZ()));
		Vec3 var31 = Vec3.createVectorHelper(position.x, position.y, position.z);

		for (int i = 0; i < allEntities.size(); ++i)
		{
			Entity entity = allEntities.get(i);

			if (this.onDamageEntity(entity))
			{
				continue;
			}

			if (entity instanceof EDaoDan)
			{
				((EDaoDan) entity).setExplode();
				continue;
			}

			if (entity instanceof EntityItem && !destroyItem)
				continue;

			double distance = entity.getDistance(position.x, position.y, position.z) / radius;

			if (distance <= 1.0D)
			{
				double xDifference = entity.posX - position.x;
				double yDifference = entity.posY - position.y;
				double zDifference = entity.posZ - position.z;
				double var35 = MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
				xDifference /= var35;
				yDifference /= var35;
				zDifference /= var35;
				double var34 = worldObj.getBlockDensity(var31, entity.boundingBox);
				double var36 = (1.0D - distance) * var34;
				int damage = 0;

				damage = (int) ((var36 * var36 + var36) / 2.0D * 8.0D * power + 1.0D);

				entity.attackEntityFrom(DamageSource.setExplosionSource(null), damage);

				entity.motionX += xDifference * var36;
				entity.motionY += yDifference * var36;
				entity.motionZ += zDifference * var36;
			}
		}
	}

	/**
	 * Called by doDamageEntity on each entity being damaged. This function should be inherited if
	 * something special is to happen to a specific entity.
	 * 
	 * @return True if something special happens to this specific entity.
	 */
	protected boolean onDamageEntity(Entity entity)
	{
		return false;
	}
}
