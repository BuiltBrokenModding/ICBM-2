package icbm.explosion.explosive;

import icbm.ModelICBM;
import icbm.Settings;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.missile.ex.ExAntiGravitational;
import icbm.explosion.missile.ex.ExAntimatter;
import icbm.explosion.missile.ex.ExBreaching;
import icbm.explosion.missile.ex.ExChemical;
import icbm.explosion.missile.ex.ExCondensed;
import icbm.explosion.missile.ex.ExDebilitation;
import icbm.explosion.missile.ex.ExEMP;
import icbm.explosion.missile.ex.ExEnder;
import icbm.explosion.missile.ex.ExEndothermic;
import icbm.explosion.missile.ex.ExExothermic;
import icbm.explosion.missile.ex.ExIncendiary;
import icbm.explosion.missile.ex.ExNuclear;
import icbm.explosion.missile.ex.ExRedMatter;
import icbm.explosion.missile.ex.ExRejuvenation;
import icbm.explosion.missile.ex.ExRepulsive;
import icbm.explosion.missile.ex.ExSMine;
import icbm.explosion.missile.ex.ExShrapnel;
import icbm.explosion.missile.ex.ExSonic;
import icbm.explosion.missile.types.Missile;
import icbm.explosion.missile.types.MissileAnti;
import icbm.explosion.missile.types.MissileCluster;
import icbm.explosion.missile.types.MissileHoming;
import icbm.explosion.missile.types.MissileModule;
import icbm.explosion.missile.types.MissileNuclearCluster;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.api.icbm.explosion.IExplosive;
import calclavia.lib.flag.FlagRegistry;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** The explosive registry class. Used to register explosions. */
public abstract class Explosive implements IExplosive
{
    /** Explosives */
    public static final Explosive condensed;
    public static final Explosive shrapnel;
    public static final Explosive incendiary;
    public static final Explosive debilitation;
    public static final Explosive chemical;
    public static final Explosive anvil;
    public static final Explosive replsive;
    public static final Explosive attractive;

    public static final Explosive fragmentation;
    public static final Explosive contagious;
    public static final Explosive sonic;
    public static final Explosive breaching;
    public static final Explosive rejuvenation;
    public static final Explosive thermobaric;
    public static final Explosive sMine;

    public static final Explosive nuclear;
    public static final Explosive emp;
    public static final Explosive exothermic;
    public static final Explosive endothermic;
    public static final Explosive antiGrav;
    public static final Explosive ender;
    public static final Explosive hypersonic;

    public static final Explosive antimatter;
    public static final Explosive redMatter;

    /** Missiles */
    public static final Missile missileModule;
    public static final Missile homing;
    public static final Missile antiBallistic;
    public static final Missile cluster;
    public static final Missile nuclearCluster;

    public static boolean registered = false;

    static
    {
        Settings.CONFIGURATION.load();

        condensed = ExplosiveRegistry.register(new ExCondensed("condensed", 1));
        shrapnel = ExplosiveRegistry.register(new ExShrapnel("shrapnel", 1));
        incendiary = ExplosiveRegistry.register(new ExIncendiary("incendiary", 1));
        debilitation = ExplosiveRegistry.register(new ExDebilitation("debilitation", 1));
        chemical = ExplosiveRegistry.register(new ExChemical("chemical", 1));
        anvil = ExplosiveRegistry.register(new ExShrapnel("anvil", 1));
        replsive = ExplosiveRegistry.register(new ExRepulsive("repulsive", 1));
        attractive = ExplosiveRegistry.register(new ExRepulsive("attractive", 1));

        fragmentation = ExplosiveRegistry.register(new ExShrapnel("fragmentation", 2));
        contagious = ExplosiveRegistry.register(new ExChemical("contagious", 2));
        sonic = ExplosiveRegistry.register(new ExSonic("sonic", 2));
        breaching = ExplosiveRegistry.register(new ExBreaching("breaching", 2));
        rejuvenation = ExplosiveRegistry.register(new ExRejuvenation("rejuvenation", 2));
        thermobaric = ExplosiveRegistry.register(new ExNuclear("thermobaric", 2));
        sMine = ExplosiveRegistry.register(new ExSMine("sMine", 2));

        nuclear = ExplosiveRegistry.register(new ExNuclear("nuclear", 3));
        emp = ExplosiveRegistry.register(new ExEMP("emp", 3));
        exothermic = ExplosiveRegistry.register(new ExExothermic("exothermic", 3));
        endothermic = ExplosiveRegistry.register(new ExEndothermic("endothermic", 3));
        antiGrav = ExplosiveRegistry.register(new ExAntiGravitational("antiGravitational", 3));
        ender = ExplosiveRegistry.register(new ExEnder("ender", 3));
        hypersonic = ExplosiveRegistry.register(new ExSonic("hypersonic", 3));

        antimatter = ExplosiveRegistry.register(new ExAntimatter("antimatter", 4));
        redMatter = ExplosiveRegistry.register(new ExRedMatter("redMatter", 4));

        /** Missiles */
        missileModule = (Missile) ExplosiveRegistry.register(new MissileModule("missileModule", 1));
        homing = (Missile) ExplosiveRegistry.register(new MissileHoming("homing", 1));
        antiBallistic = (Missile) ExplosiveRegistry.register(new MissileAnti("antiBallistic", 2));
        cluster = (Missile) ExplosiveRegistry.register(new MissileCluster("cluster", 2));
        nuclearCluster = (Missile) ExplosiveRegistry.register(new MissileNuclearCluster("nuclearCluster", 3));

        Settings.CONFIGURATION.save();
        registered = true;
    }

    /** The unique identification name for this explosive. */
    private String nameID;
    /** The tier of this explosive */
    private int tier;
    /** The fuse of this explosive */
    private int fuseTime;
    /** The flag name of this explosive */
    public final String flagName;
    /** Is this explosive disabled? */
    protected boolean isDisabled;
    /** Is this explosive able to be pushed by other explosions? */
    protected boolean isMobile = false;

    protected boolean hasBlock;
    protected boolean hasGrenade;
    protected boolean hasMinecart;
    protected boolean hasMissile;

    protected Explosive(String name, int tier)
    {
        this.nameID = name;
        this.tier = tier;
        this.fuseTime = 100;

        this.hasBlock = true;
        this.hasMissile = true;
        this.hasGrenade = this.tier <= 1;
        this.hasMinecart = this.tier <= 2;

        this.flagName = FlagRegistry.registerFlag("ban_" + this.nameID);
        this.isDisabled = Settings.CONFIGURATION.get("Disable_Explosives", "Disable " + this.nameID, false).getBoolean(false);

    }

    @Override
    public final int getID()
    {
        return ExplosiveRegistry.getID(this.getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName()
    {
        return this.nameID;
    }

    @Override
    public String getExplosiveName()
    {
        return LanguageUtility.getLocal("icbm.explosive." + this.nameID + ".name");
    }

    @Override
    public String getGrenadeName()
    {
        return LanguageUtility.getLocal("icbm.grenade." + this.nameID + ".name");
    }

    @Override
    public String getMissileName()
    {
        return LanguageUtility.getLocal("icbm.missile." + this.nameID + ".name");
    }

    @Override
    public String getMinecartName()
    {
        return LanguageUtility.getLocal("icbm.minecart." + this.nameID + ".name");
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
        this.fuseTime = fuse;
        return this;
    }

    /** The fuse of the explosion
     * 
     * @return The Fuse */
    public int getYinXin()
    {
        return fuseTime;
    }

    /** Called at the before the explosive detonated as a block.
     * 
     * @param world
     * @param entity */
    public void yinZhaQian(World world, Entity entity)
    {
        world.playSoundAtEntity(entity, "random.fuse", 1.0F, 1.0F);
    }

    /** Called while the explosive is being detonated (fuse ticks) in block form.
     * 
     * @param fuseTicks - The amount of ticks this explosive is on fuse */
    public void onYinZha(World world, Vector3 position, int fuseTicks)
    {
        world.spawnParticle("smoke", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
    }

    /** Called when the block for of this explosive is destroy by an explosion
     * 
     * @return - Fuse left */
    public int onBeiZha()
    {
        return (int) (this.fuseTime / 2 + Math.random() * this.fuseTime / 4);
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

    /** Called to add the recipe for this explosive */
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

    /** Checks if the explosive is banned in an area
     * 
     * @param world - world to check in
     * @param x - coord
     * @param y - coord
     * @param z- coord
     * @return true if it is banned */
    public boolean isBannedInRegion(World world, double x, double y, double z)
    {
        boolean flag_all = FlagRegistry.getModFlag().getFlagWorld(world).containsValue("ban_ICBM", "true", new Vector3(x, y, z));
        boolean flag_missile = FlagRegistry.getModFlag().getFlagWorld(world).containsValue(this.flagName, "true", new Vector3(x, y, z));

        return flag_all || flag_missile;
    }
}
