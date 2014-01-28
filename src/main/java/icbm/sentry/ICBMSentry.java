package icbm.sentry;

import icbm.Reference;
import icbm.core.CreativeTabICBM;
import icbm.core.ICBMCore;
import icbm.core.Settings;
import icbm.sentry.platform.BlockTurretPlatform;
import icbm.sentry.turret.ItemAmmo;
import icbm.sentry.turret.sentryhandler.EntitySentryFake;
import icbm.sentry.turret.sentryhandler.Sentry;
import icbm.sentry.turret.turret.BlockTurret;
import icbm.sentry.turret.turret.ItemBlockTurret;
import icbm.sentry.turret.upgrades.ItemSentryUpgrade;
import icbm.sentry.turret.upgrades.ItemSentryUpgrade.TurretUpgradeType;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.utility.nbt.SaveManager;
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

@Mod(modid = ICBMSentry.NAME, name = ICBMSentry.NAME, version = Reference.VERSION, dependencies = "required-after:ICBM")
@NetworkMod(channels = { Reference.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class ICBMSentry
{
	public static final String NAME = Reference.NAME + "|Sentry";
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

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(this, ICBMSentry.proxy);
		MinecraftForge.EVENT_BUS.register(this);

		Settings.CONFIGURATION.load();

		blockTurret = new BlockTurret(BLOCK_ID_PREFIX);
		blockPlatform = new BlockTurretPlatform(BLOCK_ID_PREFIX + 1);

		itemAmmo = new ItemAmmo(ITEM_ID_PREFIX + 1);
		itemUpgrade = new ItemSentryUpgrade(ITEM_ID_PREFIX + 2);
		Settings.CONFIGURATION.save();

		bulletShell = new ItemStack(itemAmmo, 1, 0);
		conventionalBullet = new ItemStack(itemAmmo, 1, 1);
		railgunBullet = new ItemStack(itemAmmo, 1, 2);
		antimatterBullet = new ItemStack(itemAmmo, 1, 3);

		GameRegistry.registerBlock(blockTurret, ItemBlockTurret.class, "ICBMTurret");
		GameRegistry.registerBlock(blockPlatform, "ICBMPlatform");

		EntityRegistry.registerGlobalEntityID(EntitySentryFake.class, "ICBMFake", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntitySentryFake.class, "ICBMFake", ENTITY_ID_PREFIX + 7, this, 50, 5, true);
		
		CreativeTabICBM.itemStack = new ItemStack(blockTurret);

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ICBMCore.setModMetadata(NAME, metadata);
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
		// GameRegistry.addRecipe(new ShapedOreRecipe(blockPlatform, new Object[] { "SPS", "CBC",
		// "SAS", 'P', Block.pistonBase, 'A', UniversalRecipe.BATTERY.get(), 'S',
		// UniversalRecipe.PRIMARY_PLATE.get(), 'C', Block.chest, 'B',
		// UniversalRecipe.CIRCUIT_T1.get() }));
		// Gun Turret
		// GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 0), new Object[]
		// { "SSS", "CS ", 'C', UniversalRecipe.CIRCUIT_T1.get(), 'S',
		// UniversalRecipe.PRIMARY_METAL.get() }));
		// Railgun
		// GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 1), new Object[]
		// { "DDD", "CS ", "GS ", 'D', Item.diamond, 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'C',
		// UniversalRecipe.CIRCUIT_T3.get(), 'G', new ItemStack(blockTurret, 1, 0) }));
		// AA Turret
		// GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 2), new Object[]
		// { "DDS", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S',
		// UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T2.get(), 'G', new
		// ItemStack(blockTurret, 1, 0) }));
		// Laser Turret
		// GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 3), new Object[]
		// { "DDG", "CS ", "GS ", 'D', UniversalRecipe.SECONDARY_PLATE.get(), 'S',
		// UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T1.get(), 'D',
		// Block.glass, 'G', Block.glass }));

		// Upgrades
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 3, TurretUpgradeType.RANGE.ordinal()), new Object[] { "B", "I", 'B', Item.bow, 'I', Item.ingotIron }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemUpgrade, 1, TurretUpgradeType.COLLECTOR.ordinal()), new Object[] { "BBB", " I ", "BBB", 'B', Block.cloth, 'I', Item.bowlEmpty }));

		proxy.init();
	}
}