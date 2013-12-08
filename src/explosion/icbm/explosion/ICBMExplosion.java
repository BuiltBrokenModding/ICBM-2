package icbm.explosion;

import icbm.api.ICBM;
import icbm.api.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import icbm.api.explosion.ExplosiveType;
import icbm.api.explosion.IExplosive;
import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import icbm.core.ICBMFlags;
import icbm.core.implement.IChunkLoadHandler;
import icbm.explosion.cart.EntityBombCart;
import icbm.explosion.cart.ItemBombCart;
import icbm.explosion.explosive.EntityExplosion;
import icbm.explosion.items.ItemBombDefuser;
import icbm.explosion.items.ItemLaserDesignator;
import icbm.explosion.items.ItemRadarGun;
import icbm.explosion.items.ItemRemoteDetonator;
import icbm.explosion.items.ItemRocketLauncher;
import icbm.explosion.machines.BlockICBMMachine;
import icbm.explosion.machines.BlockICBMMachine.MachineData;
import icbm.explosion.machines.ItemBlockMachine;
import icbm.explosion.missile.BlockExplosive;
import icbm.explosion.missile.EntityExplosive;
import icbm.explosion.missile.EntityGrenade;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.ExplosiveRegistry;
import icbm.explosion.missile.ItemBlockExplosive;
import icbm.explosion.missile.ItemGrenade;
import icbm.explosion.missile.missile.EntityMissile;
import icbm.explosion.missile.missile.ItemMissile;
import icbm.explosion.missile.modular.BlockMissileTable;
import icbm.explosion.missile.modular.ItemBlockMissileTable;
import icbm.explosion.potion.PChuanRanDu;
import icbm.explosion.potion.PDaDu;
import icbm.explosion.potion.PDongShang;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.core.item.ItemElectric;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import calclavia.lib.UniversalRecipe;
import calclavia.lib.flag.FlagRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ICBMExplosion.NAME, name = ICBMExplosion.NAME, version = ICBM.VERSION, dependencies = "after:ICBM|Sentry;after:AtomicScience", useMetadata = true)
@NetworkMod(channels = ICBMExplosion.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = ICBMPacketHandler.class)
public class ICBMExplosion extends ICBMCore
{
    public static final String NAME = ICBM.NAME + "|Explosion";
    public static final String CHANNEL = ICBM.NAME + "|E";

    @Instance(NAME)
    public static ICBMExplosion instance;

    @Metadata(NAME)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = "icbm.explosion.ClientProxy", serverSide = "icbm.explosion.CommonProxy")
    public static CommonProxy proxy;
    public static Item Du;
    public static final int ENTITY_ID_PREFIX = 50;

    /** Settings and Configurations */
    // Blocks
    public static Block blockExplosive;
    public static Block blockMachine;
    public static Block blockMissileTable;
    // Items
    public static Item itemMissile;

    public static ItemElectric itemBombDefuser;
    public static ItemElectric itemRadarGun;
    public static ItemElectric itemRemoteDetonator;
    public static ItemElectric itemLaserDesignator;
    public static ItemElectric itemRocketLauncher;

    public static Item itemGrenade;
    public static Item itemBombCart;

    public static final ContagiousPoison DU_DU = new ContagiousPoison("Chemical", 1, false);
    public static final ContagiousPoison DU_CHUAN_RAN = new ContagiousPoison("Contagious", 1, true);

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        NetworkRegistry.instance().registerGuiHandler(this, proxy);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(proxy);

        ICBMConfiguration.CONFIGURATION.load();
        blockExplosive = new BlockExplosive(ICBM.BLOCK_ID_PREFIX + 3);
        blockMachine = new BlockICBMMachine(ICBM.BLOCK_ID_PREFIX + 4);
        blockMissileTable = new BlockMissileTable(ICBM.BLOCK_ID_PREFIX + 12);

        // ITEMS
        itemMissile = new ItemMissile(ICBM.ITEM_ID_PREFIX + 3, "missile");

        itemBombDefuser = new ItemBombDefuser(ICBM.ITEM_ID_PREFIX + 5);
        itemRadarGun = new ItemRadarGun(ICBM.ITEM_ID_PREFIX + 6);
        itemRemoteDetonator = new ItemRemoteDetonator(ICBM.ITEM_ID_PREFIX + 7);
        itemLaserDesignator = new ItemLaserDesignator(ICBM.ITEM_ID_PREFIX + 8);
        itemRocketLauncher = new ItemRocketLauncher(ICBM.ITEM_ID_PREFIX + 11);

        itemGrenade = new ItemGrenade(ICBM.ITEM_ID_PREFIX + 12);
        itemBombCart = new ItemBombCart(ICBM.ITEM_ID_PREFIX + 11);

        /** Potion Effects */
        PDaDu.INSTANCE = new PDaDu(22, true, 5149489, "toxin");
        PChuanRanDu.INSTANCE = new PChuanRanDu(23, false, 5149489, "virus");
        PDongShang.INSTANCE = new PDongShang(24, false, 5149489, "frostBite");

        ICBMConfiguration.CONFIGURATION.save();

        CreativeTabICBM.itemStack = new ItemStack(ICBMExplosion.blockExplosive);

        /** Dispenser Handler */
        BlockDispenser.dispenseBehaviorRegistry.putObject(ICBMExplosion.itemGrenade, new IBehaviorDispenseItem()
        {
            @Override
            public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
            {
                World world = blockSource.getWorld();

                if (!world.isRemote)
                {
                    int x = blockSource.getXInt();
                    int y = blockSource.getYInt();
                    int z = blockSource.getZInt();
                    EnumFacing enumFacing = EnumFacing.getFront(blockSource.getBlockMetadata());

                    EntityGrenade entity = new EntityGrenade(world, new Vector3(x, y, z), itemStack.getItemDamage());
                    entity.setThrowableHeading(enumFacing.getFrontOffsetX(), 0.10000000149011612D, enumFacing.getFrontOffsetZ(), 0.5F, 1.0F);
                    world.spawnEntityInWorld(entity);
                }

                itemStack.stackSize--;
                return itemStack;
            }
        });

        BlockDispenser.dispenseBehaviorRegistry.putObject(ICBMExplosion.itemBombCart, new IBehaviorDispenseItem()
        {
            private final BehaviorDefaultDispenseItem defaultItemDispenseBehavior = new BehaviorDefaultDispenseItem();

            @Override
            public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
            {
                World world = blockSource.getWorld();

                if (!world.isRemote)
                {
                    int x = blockSource.getXInt();
                    int y = blockSource.getYInt();
                    int z = blockSource.getZInt();

                    EnumFacing var3 = EnumFacing.getFront(blockSource.getBlockMetadata());
                    World var4 = blockSource.getWorld();
                    double var5 = blockSource.getX() + var3.getFrontOffsetX() * 1.125F;
                    double var7 = blockSource.getY();
                    double var9 = blockSource.getZ() + var3.getFrontOffsetZ() * 1.125F;
                    int var11 = blockSource.getXInt() + var3.getFrontOffsetX();
                    int var12 = blockSource.getYInt();
                    int var13 = blockSource.getZInt() + var3.getFrontOffsetZ();
                    int var14 = var4.getBlockId(var11, var12, var13);
                    double var15;

                    if (BlockRailBase.isRailBlock(var14))
                    {
                        var15 = 0.0D;
                    }
                    else
                    {
                        if (var14 != 0 || !BlockRailBase.isRailBlock(var4.getBlockId(var11, var12 - 1, var13)))
                        {
                            return this.defaultItemDispenseBehavior.dispense(blockSource, itemStack);
                        }

                        var15 = -1.0D;
                    }

                    EntityBombCart var22 = new EntityBombCart(world, var5, var7 + var15, var9, itemStack.getItemDamage());
                    world.spawnEntityInWorld(var22);
                    world.playAuxSFX(1000, x, y, z, 0);
                }

                itemStack.stackSize--;
                return itemStack;
            }
        });

        /** Chunk loading handler. */
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new LoadingCallback()
        {
            @Override
            public void ticketsLoaded(List<Ticket> tickets, World world)
            {
                for (Ticket ticket : tickets)
                {
                    if (ticket.getEntity() instanceof IChunkLoadHandler)
                    {
                        ((IChunkLoadHandler) ticket.getEntity()).chunkLoaderInit(ticket);
                    }
                    else
                    {
                        if (ticket.getModData() != null)
                        {
                            Vector3 position = Vector3.readFromNBT(ticket.getModData());

                            TileEntity tileEntity = position.getTileEntity(ticket.world);

                            if (tileEntity instanceof IChunkLoadHandler)
                            {
                                ((IChunkLoadHandler) tileEntity).chunkLoaderInit(ticket);
                            }
                        }
                    }
                }
            }
        });

        // -- Registering Blocks
        GameRegistry.registerBlock(blockExplosive, ItemBlockExplosive.class, "bZhaDan");
        GameRegistry.registerBlock(blockMachine, ItemBlockMachine.class, "bJiQi");
        GameRegistry.registerBlock(blockMissileTable, ItemBlockMissileTable.class, "blockMissileTable");

        ICBM.explosionManager = ExplosiveRegistry.class;

        ICBMExplosion.proxy.preInit();
    }

    @EventHandler
    public void load(FMLInitializationEvent evt)
    {
        super.init(evt);
        ICBMCore.setModMetadata(NAME, metadata);
    }

    @Override
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);

        /** Add all Recipes */
        // Rocket Launcher
        GameRegistry.addRecipe(new ShapedOreRecipe(itemRocketLauncher, new Object[] { "SCR", "SB ", 'R', itemRadarGun, 'C', new ItemStack(blockMachine, 1, MachineData.CruiseLauncher.ordinal() + 6), 'B', Block.stoneButton, 'S', UniversalRecipe.PRIMARY_METAL.get() }));

        // Radar Gun
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.itemRadarGun), new Object[] { "@#!", " $!", "  !", '@', Block.glass, '!', UniversalRecipe.PRIMARY_METAL.get(), '#', UniversalRecipe.CIRCUIT_T1.get(), '$', Block.stoneButton }));
        // Remote
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.itemRemoteDetonator), new Object[] { "?@@", "@#$", "@@@", '@', UniversalRecipe.PRIMARY_METAL.get(), '?', Item.redstone, '#', UniversalRecipe.CIRCUIT_T2.get(), '$', Block.stoneButton }));
        // Laser Designator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.itemLaserDesignator), new Object[] { "!  ", " ? ", "  @", '@', ElectricItemHelper.getUncharged(ICBMExplosion.itemRemoteDetonator), '?', UniversalRecipe.CIRCUIT_T3.get(), '!', ElectricItemHelper.getUncharged(ICBMExplosion.itemRadarGun) }));
        // Defuser
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.itemBombDefuser), new Object[] { "I  ", " W ", "  C", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'W', UniversalRecipe.WRENCH.get(), 'I', UniversalRecipe.WIRE.get() }));
        // Missile Launcher Platform
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 0), new Object[] { "! !", "!C!", "!!!", '!', UniversalRecipe.SECONDARY_METAL.get(), 'C', UniversalRecipe.CIRCUIT_T1.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 1), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(ICBMExplosion.blockMachine, 1, 0), '!', UniversalRecipe.PRIMARY_METAL.get(), 'C', UniversalRecipe.CIRCUIT_T2.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 2), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(ICBMExplosion.blockMachine, 1, 1), '!', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get() }));
        // Missile Launcher Panel
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 3), new Object[] { "!!!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T1.get(), '!', Block.glass, '?', UniversalRecipe.WIRE.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 4), new Object[] { "!$!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T2.get(), '!', UniversalRecipe.PRIMARY_METAL.get(), '?', UniversalRecipe.WIRE.get(), '$', new ItemStack(ICBMExplosion.blockMachine, 1, 3) }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 5), new Object[] { "!$!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T3.get(), '!', Item.ingotGold, '?', UniversalRecipe.WIRE.get(), '$', new ItemStack(ICBMExplosion.blockMachine, 1, 4) }));
        // Missile Launcher Support Frame
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 6), new Object[] { "! !", "!!!", "! !", '!', UniversalRecipe.SECONDARY_METAL.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 7), new Object[] { "! !", "!@!", "! !", '!', UniversalRecipe.PRIMARY_METAL.get(), '@', new ItemStack(ICBMExplosion.blockMachine, 1, 6) }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 8), new Object[] { "! !", "!@!", "! !", '!', UniversalRecipe.PRIMARY_PLATE.get(), '@', new ItemStack(ICBMExplosion.blockMachine, 1, 7) }));
        // Radar Station
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 9), new Object[] { "?@?", " ! ", "!#!", '@', ElectricItemHelper.getUncharged(ICBMExplosion.itemRadarGun), '!', UniversalRecipe.PRIMARY_PLATE.get(), '#', UniversalRecipe.CIRCUIT_T1.get(), '?', Item.ingotGold }));
        // EMP Tower
        RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 10), new Object[] { "?W?", "@!@", "?#?", '?', UniversalRecipe.PRIMARY_PLATE.get(), '!', UniversalRecipe.CIRCUIT_T3.get(), '@', UniversalRecipe.BATTERY_BOX.get(), '#', UniversalRecipe.MOTOR.get(), 'W', UniversalRecipe.WIRE.get() }), "EMP Tower", ICBMConfiguration.CONFIGURATION, true);
        // Cruise Launcher
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, 11), new Object[] { "?! ", "@@@", '@', UniversalRecipe.PRIMARY_PLATE.get(), '!', new ItemStack(ICBMExplosion.blockMachine, 1, 2), '?', new ItemStack(ICBMExplosion.blockMachine, 1, 8) }));
        // Missile Coordinator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.blockMachine, 1, MachineData.MissileCoordinator.ordinal()), new Object[] { "R R", "SCS", "SSS", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'R', itemRemoteDetonator }));
        //Missile module
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), new Object[] { " @ ", "@#@", "@?@", '@', UniversalRecipe.PRIMARY_METAL.get(), '?', Item.flintAndSteel, '#', UniversalRecipe.CIRCUIT_T1.get() }));
        // Homing
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.homing.getID()), new Object[] { " B ", " C ", "BMB", 'M', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), 'C', UniversalRecipe.CIRCUIT_T1.get(), 'B', UniversalRecipe.SECONDARY_METAL.get() }));
        // Anti-ballistic
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.antiBallistic.getID()), new Object[] { "!", "?", "@", '@', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), '?', new ItemStack(ICBMExplosion.blockExplosive, 1, 0), '!', UniversalRecipe.CIRCUIT_T1.get() }));
        // Cluster
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.cluster.getID()), new Object[] { " ! ", " ? ", "!@!", '@', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), '?', Explosive.fragmentation.getItemStack(), '!', new ItemStack(ICBMExplosion.itemMissile, 1, 0) }));
        // Nuclear Cluster
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.nuclearCluster.getID()), new Object[] { " N ", "NCN", 'C', new ItemStack(itemMissile, 1, Explosive.cluster.getID()), 'N', Explosive.nuclear.getItemStack() }));

        /** Add all explosive recipes. */
        for (Explosive zhaPin : ExplosiveRegistry.getAllZhaPin())
        {
            zhaPin.init();

            // Missile
            RecipeHelper.addRecipe(new ShapelessOreRecipe(new ItemStack(ICBMExplosion.itemMissile, 1, zhaPin.getID()), new Object[] { new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), new ItemStack(ICBMExplosion.blockExplosive, 1, zhaPin.getID()) }), zhaPin.getUnlocalizedName() + " Missile", ICBMConfiguration.CONFIGURATION, true);

            if (zhaPin.getTier() < 2)
            {
                // Grenade
                RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.itemGrenade, 1, zhaPin.getID()), new Object[] { "?", "@", '@', new ItemStack(ICBMExplosion.blockExplosive, 1, zhaPin.getID()), '?', Item.silk }), zhaPin.getUnlocalizedName() + " Grenade", ICBMConfiguration.CONFIGURATION, true);
            }

            if (zhaPin.getTier() < 3)
            {
                // Minecart
                RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ICBMExplosion.itemBombCart, 1, zhaPin.getID()), new Object[] { "?", "@", '?', new ItemStack(ICBMExplosion.blockExplosive, 1, zhaPin.getID()), '@', Item.minecartEmpty }), zhaPin.getUnlocalizedName() + " Minecart", ICBMConfiguration.CONFIGURATION, true);
            }
        }

        EntityRegistry.registerGlobalEntityID(EntityExplosive.class, "ICBMExplosive", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityMissile.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityExplosion.class, "ICBMProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityFlyingBlock.class, "ICBMGravityBlock", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityLightBeam.class, "ICBMLightBeam", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityFragments.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityGrenade.class, "ICBMGrenade", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityBombCart.class, "ICBMChe", EntityRegistry.findGlobalUniqueEntityId());

        EntityRegistry.registerModEntity(EntityExplosive.class, "ICBMExplosive", ENTITY_ID_PREFIX, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityMissile.class, "ICBMMissile", ENTITY_ID_PREFIX + 1, this, 500, 1, true);
        EntityRegistry.registerModEntity(EntityExplosion.class, "ICBMProceduralExplosion", ENTITY_ID_PREFIX + 2, this, 100, 5, true);
        EntityRegistry.registerModEntity(EntityFlyingBlock.class, "ICBMGravityBlock", ENTITY_ID_PREFIX + 3, this, 50, 15, true);
        EntityRegistry.registerModEntity(EntityLightBeam.class, "ICBMLightBeam", ENTITY_ID_PREFIX + 4, this, 80, 5, true);
        EntityRegistry.registerModEntity(EntityFragments.class, "ICBMFragment", ENTITY_ID_PREFIX + 5, this, 40, 8, true);
        EntityRegistry.registerModEntity(EntityGrenade.class, "ICBMGrenade", ENTITY_ID_PREFIX + 6, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityBombCart.class, "ICBMChe", ENTITY_ID_PREFIX + 8, this, 50, 4, true);

        ICBMExplosion.proxy.init();
    }

    @ForgeSubscribe
    public void enteringChunk(EnteringChunk evt)
    {
        if (evt.entity instanceof EntityMissile)
        {
            ((EntityMissile) evt.entity).updateLoadChunk(evt.newChunkX, evt.newChunkZ);
        }
    }

    @ForgeSubscribe
    public void explosionEvent(ExplosivePreDetonationEvent evt)
    {
        if (shiBaoHu(evt.world, new Vector3(evt.x, evt.y, evt.z), evt.type, evt.explosion))
        {
            evt.setCanceled(true);
        }
    }

    @ForgeSubscribe
    public void creeperDeathEvent(LivingDropsEvent evt)
    {
        if (evt.entityLiving instanceof EntityCreeper)
        {
            evt.entityLiving.dropItem(this.itemSulfurDust.itemID, 3 + evt.entityLiving.worldObj.rand.nextInt(6));
        }
    }

    /** Is a specific position being protected from a specific type of danger? */
    public static boolean shiBaoHu(World world, Vector3 diDian, ExplosiveType type, IExplosive zhaPin)
    {
        if (zhaPin != null)
        {
            if (FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME) != null)
            {
                if (FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_GLOBAL, "true", diDian))
                {
                    return true;
                }

                boolean baoHu = false;

                switch (type)
                {
                    case ALL:
                        baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_MINECART, "true", diDian) || FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_MISSILE, "true", diDian) || FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_GRENADE, "true", diDian) || FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_EXPLOSIVE, "true", diDian);
                        break;
                    case VEHICLE:
                        baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_MINECART, "true", diDian);
                        break;
                    case AIR:
                        baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_MISSILE, "true", diDian);
                        break;
                    case ITEM:
                        baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_GRENADE, "true", diDian);
                        break;
                    case BLOCK:
                        baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_EXPLOSIVE, "true", diDian);
                        break;
                }

                String flag = zhaPin instanceof Explosive ? ((Explosive) zhaPin).flagName : "ban_" + zhaPin.getUnlocalizedName();

                return FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, flag, "true", diDian) || baoHu;
            }
        }

        return false;
    }

    public static boolean shiBaoHu(World world, Vector3 diDian, ExplosiveType type, int haoMa)
    {
        return shiBaoHu(world, diDian, type, ExplosiveRegistry.get(haoMa));
    }

    @Override
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        super.serverStarting(event);
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new ICBMCommand());
    }

    @Override
    protected String getChannel()
    {
        return CHANNEL;
    }
}
