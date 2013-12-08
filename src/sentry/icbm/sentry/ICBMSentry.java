package icbm.sentry;

import icbm.api.ICBM;
import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import icbm.core.ICBMFlags;
import icbm.sentry.damage.EntityTileDamagable;
import icbm.sentry.platform.BlockTurretPlatform;
import icbm.sentry.terminal.command.CommandAccess;
import icbm.sentry.terminal.command.CommandDestroy;
import icbm.sentry.terminal.command.CommandGet;
import icbm.sentry.terminal.command.CommandHelp;
import icbm.sentry.terminal.command.CommandRegistry;
import icbm.sentry.terminal.command.CommandTarget;
import icbm.sentry.terminal.command.CommandUser;
import icbm.sentry.turret.BlockTurret;
import icbm.sentry.turret.ItemAmmo;
import icbm.sentry.turret.ItemBlockTurret;
import icbm.sentry.turret.mount.EntityMountPoint;
import icbm.sentry.turret.upgrades.ItemSentryUpgrade;
import icbm.sentry.turret.upgrades.ItemSentryUpgrade.TurretUpgradeType;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;
import calclavia.lib.NBTFileLoader;
import calclavia.lib.UniversalRecipe;
import calclavia.lib.flag.CommandFlag;
import calclavia.lib.flag.FlagRegistry;
import calclavia.lib.flag.ModFlag;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
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

@Mod(modid = ICBMSentry.NAME, name = ICBMSentry.NAME, version = ICBM.VERSION, useMetadata = true)
@NetworkMod(channels = { ICBMSentry.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class ICBMSentry extends ICBMCore
{
    public static final String NAME = ICBM.NAME + "|Sentry";
    public static final String CHANNEL = ICBM.NAME;

    @SidedProxy(clientSide = "icbm.sentry.ClientProxy", serverSide = "icbm.sentry.CommonProxy")
    public static CommonProxy proxy;

    @Instance(NAME)
    public static ICBMSentry instance;

    @Metadata(NAME)
    public static ModMetadata metadata;

    public static final int BLOCK_ID_PREFIX = 3517;
    public static final int ITEM_ID_PREFIX = 20948;

    public static final int ENTITY_ID_PREFIX = 50;

    public static Block blockTurret, blockPlatform;

    public static Item itemAmmo;
    public static Item itemUpgrade;

    /** ItemStack helpers. Do not modify theses. */
    public static ItemStack conventionalBullet, railgunBullet, antimatterBullet, bulletShell;

    public static final String FLAG_RAILGUN = FlagRegistry.registerFlag("ban_railgun");

    @Override
    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

        NetworkRegistry.instance().registerGuiHandler(this, ICBMSentry.proxy);
        MinecraftForge.EVENT_BUS.register(this);

        ICBMConfiguration.CONFIGURATION.load();

        blockTurret = new BlockTurret(BLOCK_ID_PREFIX);
        blockPlatform = new BlockTurretPlatform(BLOCK_ID_PREFIX + 1);

        itemAmmo = new ItemAmmo(ITEM_ID_PREFIX + 1);
        itemUpgrade = new ItemSentryUpgrade(ITEM_ID_PREFIX + 2);
        ICBMConfiguration.CONFIGURATION.save();

        bulletShell = new ItemStack(itemAmmo, 1, 0);
        conventionalBullet = new ItemStack(itemAmmo, 1, 1);
        railgunBullet = new ItemStack(itemAmmo, 1, 2);
        antimatterBullet = new ItemStack(itemAmmo, 1, 3);

        GameRegistry.registerBlock(blockTurret, ItemBlockTurret.class, "ICBMTurret");
        GameRegistry.registerBlock(blockPlatform, "ICBMPlatform");

        EntityRegistry.registerGlobalEntityID(EntityMountPoint.class, "ICBMFake", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityMountPoint.class, "ICBMFake", ENTITY_ID_PREFIX + 7, this, 50, 5, true);
        EntityRegistry.registerGlobalEntityID(EntityTileDamagable.class, "ICBMFakeTile", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityTileDamagable.class, "ICBMFakeTile", ENTITY_ID_PREFIX + 8, this, 50, 5, true);

        CreativeTabICBM.itemStack = new ItemStack(blockTurret);

        proxy.preInit();
    }

    @Override
    @Init
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        ICBMCore.setModMetadata(NAME, metadata);
    }

    @Override
    @PostInit
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);

        // Shell
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 16, 0), new Object[] { "T", "T", 'T', "ingotTin" }));
        // Bullets
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 16, 1), new Object[] { "SBS", "SGS", "SSS", 'B', Item.ingotIron, 'G', Item.gunpowder, 'S', bulletShell.copy() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 2, 2), new Object[] { "D", "B", "B", 'D', Item.diamond, 'B', conventionalBullet }));
        GameRegistry.addRecipe(new ShapedOreRecipe(antimatterBullet, new Object[] { "A", "B", 'A', "antimatterGram", 'B', railgunBullet }));

        // Turret Platform
        GameRegistry.addRecipe(new ShapedOreRecipe(blockPlatform, new Object[] { "SPS", "CBC", "SAS", 'P', Block.pistonBase, 'A', UniversalRecipe.BATTERY.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', Block.chest, 'B', UniversalRecipe.CIRCUIT_T1.get() }));
        // Gun Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 0), new Object[] { "SSS", "CS ", 'C', UniversalRecipe.CIRCUIT_T1.get(), 'S', UniversalRecipe.PRIMARY_METAL.get() }));
        // Railgun
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 1), new Object[] { "DDD", "CS ", "GS ", 'D', Item.diamond, 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get(), 'G', new ItemStack(blockTurret, 1, 0) }));
        // AA Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 2), new Object[] { "DDS", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T2.get(), 'G', new ItemStack(blockTurret, 1, 0) }));
        // Laser Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 3), new Object[] { "DDG", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T1.get(), 'D', Block.glass, 'G', Block.glass }));

        // Upgrades
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 3, TurretUpgradeType.RANGE.ordinal()), new Object[] { "B", "I", 'B', Item.bow, 'I', Item.ingotIron }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 1, TurretUpgradeType.COLLECTOR.ordinal()), new Object[] { "BBB", " I ", "BBB", 'B', Block.cloth, 'I', Item.bowlEmpty }));

        CommandRegistry.register(new CommandAccess());
        CommandRegistry.register(new CommandDestroy());
        CommandRegistry.register(new CommandUser());
        CommandRegistry.register(new CommandHelp());
        CommandRegistry.register(new CommandGet());
        CommandRegistry.register(new CommandTarget());
        proxy.init();
    }

    /** Is a specific position being protected from a specific type of danger? */
    public static boolean isProtected(World world, Vector3 diDian, String banFlag)
    {
        if (FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME) == null)
        {
            return false;
        }

        if (FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_GLOBAL, "true", diDian))
        {
            return true;
        }

        return FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, banFlag, "true", diDian);
    }

    @Override
    @ServerStarting
    public void serverStarting(FMLServerStartingEvent event)
    {
        FlagRegistry.registerModFlag(FlagRegistry.DEFAULT_NAME, new ModFlag(NBTFileLoader.loadData(FlagRegistry.DEFAULT_NAME)));

        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandFlag(FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME)));
    }

    @Override
    @ForgeSubscribe
    public void worldSave(Save evt)
    {
        if (!evt.world.isRemote)
        {
            NBTFileLoader.saveData(FlagRegistry.DEFAULT_NAME, FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).getNBT());
        }
    }

    @Override
    protected String getChannel()
    {
        return CHANNEL;
    }
}