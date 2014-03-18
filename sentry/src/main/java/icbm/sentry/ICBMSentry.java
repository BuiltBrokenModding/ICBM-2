package icbm.sentry;

import icbm.Reference;
import icbm.core.ICBMCore;
import icbm.core.Settings;
import icbm.core.TabICBM;
import icbm.sentry.platform.BlockTurretPlatform;
import icbm.sentry.platform.gui.user.TerminalCMDUser;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.TurretRegistry;
import icbm.sentry.turret.TurretType;
import icbm.sentry.turret.ai.TurretEntitySelector;
import icbm.sentry.turret.auto.TurretAntiAir;
import icbm.sentry.turret.auto.TurretGun;
import icbm.sentry.turret.auto.TurretLaser;
import icbm.sentry.turret.block.BlockTurret;
import icbm.sentry.turret.block.ItemBlockTurret;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.items.ItemAmmo;
import icbm.sentry.turret.items.ItemSentryUpgrade;
import icbm.sentry.turret.items.ItemSentryUpgrade.TurretUpgradeType;
import icbm.sentry.turret.mounted.MountedRailgun;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.terminal.CommandRegistry;
import calclavia.lib.recipe.UniversalRecipe;
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

@Mod(modid = ICBMSentry.ID, name = ICBMSentry.NAME, version = Reference.VERSION, dependencies = "required-after:ICBM")
@NetworkMod(channels = { Reference.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class ICBMSentry
{
    public static final String NAME = Reference.NAME + " Sentry";
    public static final String ID = Reference.NAME + "|Sentry";

    @Instance(ID)
    public static ICBMSentry INSTANCE;

    @SidedProxy(clientSide = "icbm.sentry.ClientProxy", serverSide = "icbm.sentry.CommonProxy")
    public static CommonProxy proxy;

    @Metadata(ID)
    public static ModMetadata metadata;

    public static final int BLOCK_ID_PREFIX = 3517;
    public static final int ITEM_ID_PREFIX = 20948;

    public static final int ENTITY_ID_PREFIX = 50;

    public static Block blockTurret, blockPlatform;

    public static Item itemAmmo;
    public static Item itemUpgrade;

    /** ItemStack helpers. Do not modify theses. */
    public static ItemStack conventionalBullet, railgunBullet, antimatterBullet, bulletShell;

    @EventHandler
    // @Optional.Method(modid = ID)
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.instance().registerGuiHandler(INSTANCE, proxy);
        MinecraftForge.EVENT_BUS.register(this);
        TurretType.load();

        blockTurret = ICBMCore.contentRegistry.createBlock(BlockTurret.class, ItemBlockTurret.class, TileTurret.class);
        blockPlatform = ICBMCore.contentRegistry.createBlock(BlockTurretPlatform.class);

        itemAmmo = ICBMCore.contentRegistry.createItem("ItemAmmo", ItemAmmo.class, false);
        itemUpgrade = ICBMCore.contentRegistry.createItem("ItemSentryUpgrade", ItemSentryUpgrade.class, false);

        bulletShell = new ItemStack(itemAmmo, 1, 0);
        conventionalBullet = new ItemStack(itemAmmo, 1, 1);
        railgunBullet = new ItemStack(itemAmmo, 1, 2);
        antimatterBullet = new ItemStack(itemAmmo, 1, 3);

        EntityRegistry.registerGlobalEntityID(EntityMountableDummy.class, "ICBMSentryFake", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityMountableDummy.class, "ICBMFake", ENTITY_ID_PREFIX + 7, INSTANCE, 50, 5, true);

        TabICBM.itemStack = new ItemStack(blockTurret);

        TurretEntitySelector.configTurretTargeting();

        proxy.preInit();
        CommandRegistry.register(new TerminalCMDUser(), null);
    }

    @EventHandler
    // @Optional.Method(modid = ID)
    public void init(FMLInitializationEvent event)
    {
        Settings.setModMetadata(ID, NAME, metadata, Reference.NAME);
    }

    @EventHandler
    // @Optional.Method(modid = ID)
    public void postInit(FMLPostInitializationEvent event)
    {
        // Shell
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 16, 0), new Object[] { "T", "T", 'T', "ingotTin" }));
        // Bullets
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 16, 1), new Object[] { "SBS", "SGS", "SSS", 'B', Item.ingotIron, 'G', Item.gunpowder, 'S', bulletShell.copy() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 2, 2), new Object[] { "D", "B", "B", 'D', Item.diamond, 'B', conventionalBullet }));
        GameRegistry.addRecipe(new ShapedOreRecipe(antimatterBullet, new Object[] { "A", "B", 'A', "antimatterGram", 'B', railgunBullet }));

        // Turret Platform
        GameRegistry.addRecipe(new ShapedOreRecipe(blockPlatform, new Object[] { "SPS", "CBC", "SAS", 'P', Block.pistonBase, 'A', UniversalRecipe.BATTERY.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', Block.chest, 'B', UniversalRecipe.CIRCUIT_T1.get() }));

        // Gun Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(TurretGun.class), new Object[] { "SSS", "CS ", 'C', UniversalRecipe.CIRCUIT_T1.get(), 'S', UniversalRecipe.PRIMARY_METAL.get() }));
        // Railgun
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(MountedRailgun.class), new Object[] { "DDD", "CS ", "GS ", 'D', Item.diamond, 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get(), 'G', new ItemStack(blockTurret, 1, 0) }));
        // AA Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(TurretAntiAir.class), new Object[] { "DDS", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T2.get(), 'G', new ItemStack(blockTurret, 1, 0) }));
        // Laser Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(TurretLaser.class), new Object[] { "DDG", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T1.get(), 'D', Block.glass, 'G', Block.glass }));

        // Upgrades
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 1, TurretUpgradeType.RANGE.ordinal()), new Object[] { "B", "I", 'B', Item.bow, 'I', Item.diamond }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 1, TurretUpgradeType.COLLECTOR.ordinal()), new Object[] { "BBB", " I ", "BBB", 'B', Block.cloth, 'I', Item.bowlEmpty }));

        proxy.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // Setup command
        CommandSentry commandSentry = new CommandSentry();
        MinecraftForge.EVENT_BUS.register(commandSentry);

        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(commandSentry);

    }
}