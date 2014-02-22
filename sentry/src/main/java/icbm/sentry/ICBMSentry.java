package icbm.sentry;

import cpw.mods.fml.common.FMLCommonHandler;
import icbm.Reference;
import icbm.core.TabICBM;
import icbm.core.ICBMCore;
import icbm.core.Settings;
import icbm.sentry.platform.BlockTurretPlatform;
import icbm.sentry.turret.EntitySentryFake;
import icbm.sentry.turret.SentryRegistry;
import icbm.sentry.turret.SentryTypes;
import icbm.sentry.turret.block.BlockTurret;
import icbm.sentry.turret.block.ItemBlockTurret;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.items.ItemAmmo;
import icbm.sentry.turret.items.ItemSentryUpgrade;
import icbm.sentry.turret.items.ItemSentryUpgrade.TurretUpgradeType;
import icbm.sentry.turret.modules.TurretAntiAir;
import icbm.sentry.turret.modules.TurretGun;
import icbm.sentry.turret.modules.TurretLaser;
import icbm.sentry.turret.modules.mount.MountedRailGun;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.recipe.UniversalRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.logging.Logger;

@Mod(modid = ICBMSentry.ID, name = ICBMSentry.NAME, version = Reference.VERSION, dependencies = "required-after:ICBM")
@NetworkMod(channels = { Reference.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class ICBMSentry
{
	public static final String NAME = Reference.NAME + " Sentry";
	public static final String ID = Reference.NAME + "|Sentry";
	@SidedProxy(clientSide = "icbm.sentry.ClientProxy", serverSide = "icbm.sentry.CommonProxy")
	public static CommonProxy proxy;

	@Instance(ID)
	public static ICBMSentry instance;

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

    public static Logger LOGGER = Logger.getLogger("ICBMSentry");

    public ICBMSentry()
    {
        LOGGER.setParent(FMLCommonHandler.instance().getFMLLogger());
    }

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(this, ICBMSentry.proxy);
		MinecraftForge.EVENT_BUS.register(this);
		SentryTypes.load();

		blockTurret = ICBMCore.contentRegistry.createBlock(BlockTurret.class, ItemBlockTurret.class, TileTurret.class);
		blockPlatform = ICBMCore.contentRegistry.createBlock(BlockTurretPlatform.class);

		itemAmmo = ICBMCore.contentRegistry.createItem("ItemAmmo", ItemAmmo.class, false);
		itemUpgrade = ICBMCore.contentRegistry.createItem("ItemSentryUpgrade", ItemSentryUpgrade.class, false);

		bulletShell = new ItemStack(itemAmmo, 1, 0);
		conventionalBullet = new ItemStack(itemAmmo, 1, 1);
		railgunBullet = new ItemStack(itemAmmo, 1, 2);
		antimatterBullet = new ItemStack(itemAmmo, 1, 3);

		EntityRegistry.registerGlobalEntityID(EntitySentryFake.class, "ICBMSentryFake", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntitySentryFake.class, "ICBMFake", ENTITY_ID_PREFIX + 7, this, 50, 5, true);

		TabICBM.itemStack = new ItemStack(blockTurret);

		proxy.preInit();

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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 16, 1), new Object[] { "SBS", "SGS", "SSS", 'B', Item.ingotIron, 'G', Item.gunpowder, 'S', bulletShell.copy() }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAmmo, 2, 2), new Object[] { "D", "B", "B", 'D', Item.diamond, 'B', conventionalBullet }));
		GameRegistry.addRecipe(new ShapedOreRecipe(antimatterBullet, new Object[] { "A", "B", 'A', "antimatterGram", 'B', railgunBullet }));

		// Turret Platform
		GameRegistry.addRecipe(new ShapedOreRecipe(blockPlatform, new Object[] { "SPS", "CBC", "SAS", 'P', Block.pistonBase, 'A', UniversalRecipe.BATTERY.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', Block.chest, 'B', UniversalRecipe.CIRCUIT_T1.get() }));


		// Gun Turret
		GameRegistry.addRecipe(new ShapedOreRecipe(SentryRegistry.getItemStack(TurretGun.class), new Object[] { "SSS", "CS ", 'C', UniversalRecipe.CIRCUIT_T1.get(), 'S', UniversalRecipe.PRIMARY_METAL.get() }));
		// Railgun
		GameRegistry.addRecipe(new ShapedOreRecipe(SentryRegistry.getItemStack(MountedRailGun.class), new Object[] { "DDD", "CS ", "GS ", 'D', Item.diamond, 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get(), 'G', new ItemStack(blockTurret, 1, 0) }));
		// AA Turret
		GameRegistry.addRecipe(new ShapedOreRecipe(SentryRegistry.getItemStack(TurretAntiAir.class), new Object[] { "DDS", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T2.get(), 'G', new ItemStack(blockTurret, 1, 0) }));
		// Laser Turret
		GameRegistry.addRecipe(new ShapedOreRecipe(SentryRegistry.getItemStack(TurretLaser.class), new Object[] { "DDG", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T1.get(), 'D', Block.glass, 'G', Block.glass }));

		// Upgrades
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 1, TurretUpgradeType.RANGE.ordinal()), new Object[] { "B", "I", 'B', Item.bow, 'I', Item.diamond }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 1, TurretUpgradeType.COLLECTOR.ordinal()), new Object[] { "BBB", " I ", "BBB", 'B', Block.cloth, 'I', Item.bowlEmpty }));

		proxy.init();
	}
}