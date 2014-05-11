package icbm.sentry;

import icbm.Reference;
import icbm.Settings;
import icbm.TabICBM;
import icbm.core.ICBMCore;
import icbm.sentry.interfaces.IKillCount;
import icbm.sentry.platform.BlockTurretPlatform;
import icbm.sentry.platform.cmd.CMDAccessSettings;
import icbm.sentry.platform.cmd.CMDSentryTargetting;
import icbm.sentry.platform.cmd.CommandSentry;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.TurretRegistry;
import icbm.sentry.turret.TurretType;
import icbm.sentry.turret.auto.TurretAntiAir;
import icbm.sentry.turret.auto.TurretAutoBow;
import icbm.sentry.turret.auto.TurretGun;
import icbm.sentry.turret.auto.TurretLaser;
import icbm.sentry.turret.block.BlockTurret;
import icbm.sentry.turret.block.ItemBlockTurret;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.items.ItemAmmo;
import icbm.sentry.turret.items.ItemSentryUpgrade;
import icbm.sentry.turret.items.ItemSentryUpgrade.Upgrades;
import icbm.sentry.turret.mounted.MountedRailgun;
import icbm.sentry.weapon.hand.blocks.modifier.BlockConvModifier;
import icbm.sentry.weapon.hand.blocks.modifier.TileConventionalModifier;
import icbm.sentry.weapon.hand.blocks.printer.BlockMunitionPrinter;
import icbm.sentry.weapon.hand.blocks.printer.TileMunitionPrinter;
import icbm.sentry.weapon.hand.items.conventional.ItemConventionalClip;
import icbm.sentry.weapon.hand.items.conventional.variants.ItemAssaultRifle;
import icbm.sentry.weapon.hand.items.conventional.variants.ItemShotgun;
import icbm.sentry.weapon.hand.items.conventional.variants.ItemSniperRifle;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.damage.ObjectDamageSource;
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

    //public static final int BLOCK_ID_PREFIX = 3517;
    //public static final int ITEM_ID_PREFIX = 20948;

    public static final int ENTITY_ID_PREFIX = 50;

    public static Block blockMunitionPrinter, blockConventionalModifier, blockTurret, blockPlatform, blockLaserGate;

    public static Item itemAmmo;
    public static Item itemMagazine;
    public static Item itemUpgrade;
    public static Item itemAssaultRifle, itemSniperRifle, itemShotgun;

    /** ItemStack helpers. Do not modify theses. */
    public static ItemStack conventionalBullet, railgunBullet, antimatterBullet, bulletShell;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.instance().registerGuiHandler(INSTANCE, proxy);
        MinecraftForge.EVENT_BUS.register(this);
        TurretType.load();

        blockConventionalModifier = ICBMCore.contentRegistry.createBlock(BlockConvModifier.class, null, TileConventionalModifier.class);
        blockMunitionPrinter = ICBMCore.contentRegistry.createBlock(BlockMunitionPrinter.class, null, TileMunitionPrinter.class);
        blockTurret = ICBMCore.contentRegistry.createBlock(BlockTurret.class, ItemBlockTurret.class, TileTurret.class);
        blockPlatform = ICBMCore.contentRegistry.createBlock(BlockTurretPlatform.class);
        
        itemAmmo = ICBMCore.contentRegistry.createItem("ItemAmmo", ItemAmmo.class, false);
        itemUpgrade = ICBMCore.contentRegistry.createItem("ItemSentryUpgrade", ItemSentryUpgrade.class, false);
        itemMagazine = ICBMCore.contentRegistry.createItem("gunMagazine", ItemConventionalClip.class, false);
        
        //Conventional
        itemAssaultRifle = ICBMCore.contentRegistry.createItem("itemAssaultRifle", ItemAssaultRifle.class, false);
        itemSniperRifle = ICBMCore.contentRegistry.createItem("itemSniperRifle", ItemSniperRifle.class, false);
        itemShotgun = ICBMCore.contentRegistry.createItem("itemShotgun", ItemShotgun.class, false);
        
        bulletShell = new ItemStack(itemAmmo, 1, 0);
        conventionalBullet = new ItemStack(itemAmmo, 1, 1);
        railgunBullet = new ItemStack(itemAmmo, 1, 2);
        antimatterBullet = new ItemStack(itemAmmo, 1, 3);

        EntityRegistry.registerGlobalEntityID(EntityMountableDummy.class, "ICBMSentryFake", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityMountableDummy.class, "ICBMFake", ENTITY_ID_PREFIX + 7, INSTANCE, 50, 5, true);

        TabICBM.itemStack = TurretRegistry.getItemStack(TurretAntiAir.class);

        //TurretEntitySelector.configTurretTargeting(); //Removed as @Config

        proxy.preInit();
        CommandRegistry.register(new CMDAccessSettings(), "admin");
        CommandRegistry.register(new CMDSentryTargetting(), "admin");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Settings.setModMetadata(ID, NAME, metadata, Reference.NAME);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
        // Shell
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 16, 0), new Object[] { "T", "T", 'T', "ingotTin" }));
        // Bullets
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 16, 1), new Object[] { "SBS", "SGS", "SSS", 'B', "ingotLead", 'G', Item.gunpowder, 'S', bulletShell.copy() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 16, 1), new Object[] { "SBS", "SGS", "SSS", 'B', Item.ingotIron, 'G', Item.gunpowder, 'S', bulletShell.copy() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 2, 2), new Object[] { "D", "B", "B", 'D', Item.diamond, 'B', conventionalBullet }));
        GameRegistry.addRecipe(new ShapedOreRecipe(antimatterBullet, new Object[] { "A", "B", 'A', "antimatterGram", 'B', railgunBullet }));

        // Turret Platform
        GameRegistry.addRecipe(new ShapedOreRecipe(blockPlatform, new Object[] { "SPS", "CBC", "SAS", 'P', Block.pistonBase, 'A', UniversalRecipe.BATTERY.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', Block.chest, 'B', UniversalRecipe.CIRCUIT_T1.get() }));

        // Gun Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(TurretGun.class), new Object[] { "SSS", "CS ", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'S', UniversalRecipe.PRIMARY_METAL.get() }));
        // Railgun
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(MountedRailgun.class), new Object[] { "DDD", "CS ", " S ", 'D', Block.blockDiamond, 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get(), /*'G', new ItemStack(blockTurret, 1, 0) */}));
        // AA Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(TurretAntiAir.class), new Object[] { "DDS", "CS ", " S ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get(), /*'G', new ItemStack(blockTurret, 1, 0) */}));
        // Laser Turret
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(TurretLaser.class), new Object[] { "DDG", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get(), 'D', Item.diamond, 'G', Block.glass }));
        // Crossbox sentry
        GameRegistry.addRecipe(new ShapedOreRecipe(TurretRegistry.getItemStack(TurretAutoBow.class), new Object[] { "BCL", "DW ", "WW ", 'D', Block.dispenser, 'B', Item.bow, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'W', Block.planks}));

        // Upgrades
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 1, Upgrades.RANGE.ordinal()), new Object[] { "B", "I", 'B', Item.bow, 'I', Item.diamond }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 1, Upgrades.COLLECTOR.ordinal()), new Object[] { "BBB", " I ", "BBB", 'B', Block.cloth, 'I', Item.bowlEmpty }));

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

    @ForgeSubscribe
    public void livingDeathEvent(LivingDeathEvent event)
    {
        if (event.source != null && event.entity != null && !event.entity.worldObj.isRemote)
        {
            if (event.source.getEntity() instanceof IKillCount)
                ((IKillCount) event.source.getEntity()).onKillOfEntity(event.entity);

            if (event.source instanceof ObjectDamageSource)
            {
                if (((ObjectDamageSource) event.source).attacker() instanceof IKillCount)
                {
                    ((IKillCount) ((ObjectDamageSource) event.source).attacker()).onKillOfEntity(event.entity);
                }
            }
        }
    }
}