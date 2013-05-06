package icbm.gangshao;

import icbm.api.ICBM;
import icbm.api.ICBMFlags;
import icbm.core.ICBMTab;
import icbm.core.ZhuYaoBase;
import icbm.gangshao.platform.BlockTurretPlatform;
import icbm.gangshao.terminal.command.CommandAccess;
import icbm.gangshao.terminal.command.CommandDestroy;
import icbm.gangshao.terminal.command.CommandGet;
import icbm.gangshao.terminal.command.CommandTarget;
import icbm.gangshao.turret.BlockTurret;
import icbm.gangshao.turret.EntityFakeMountable;
import icbm.gangshao.turret.ItemAmmo;
import icbm.gangshao.turret.ItemBlockTurret;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.flag.CommandFlag;
import universalelectricity.prefab.flag.FlagRegistry;
import universalelectricity.prefab.flag.ModFlag;
import universalelectricity.prefab.flag.NBTFileLoader;
import universalelectricity.prefab.multiblock.BlockMulti;
import universalelectricity.prefab.network.PacketManager;
import calclavia.lib.UniversalRecipes;
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
import dark.library.terminal.commands.CommandHelp;
import dark.library.terminal.commands.CommandRegistry;
import dark.library.terminal.commands.CommandUser;

@Mod(modid = ZhuYaoGangShao.NAME, name = ZhuYaoGangShao.NAME, version = ICBM.VERSION, dependencies = "after:BasicComponents", useMetadata = true)
@NetworkMod(channels = { ZhuYaoGangShao.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class ZhuYaoGangShao extends ZhuYaoBase
{
	public static final String NAME = ICBM.NAME + "|Sentry";
	public static final String CHANNEL = ICBM.NAME;

	@SidedProxy(clientSide = "icbm.gangshao.ClientProxy", serverSide = "icbm.gangshao.CommonProxy")
	public static CommonProxy proxy;

	@Instance(NAME)
	public static ZhuYaoGangShao instance;

	@Metadata(NAME)
	public static ModMetadata metadata;

	public static final int BLOCK_ID_PREFIX = 3517;
	public static final int ITEM_ID_PREFIX = 20948;

	public static final int ENTITY_ID_PREFIX = 50;

	/**
	 * 
	 * Multiblock.
	 */
	public static BlockMulti blockFake;
	public static Block blockTurret, blockPlatform;

	public static Item itemAmmo;

	/**
	 * ItemStack helpers. Do not modify theses.
	 */
	public static ItemStack conventionalBullet, railgunBullet, antimatterBullet, bulletShell;

	public static final String FLAG_RAILGUN = FlagRegistry.registerFlag("ban_railgun");

	@Override
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);

		NetworkRegistry.instance().registerGuiHandler(this, ZhuYaoGangShao.proxy);
		MinecraftForge.EVENT_BUS.register(this);

		ZhuYaoBase.CONFIGURATION.load();

		blockTurret = new BlockTurret(BLOCK_ID_PREFIX);
		blockPlatform = new BlockTurretPlatform(BLOCK_ID_PREFIX + 1);
		blockFake = new BlockMulti(ZhuYaoBase.CONFIGURATION.getBlock("Sentry Multiblock", BLOCK_ID_PREFIX + 2).getInt());

		itemAmmo = new ItemAmmo(ITEM_ID_PREFIX + 1);
		ZhuYaoBase.CONFIGURATION.save();

		bulletShell = new ItemStack(itemAmmo, 1, 0);
		conventionalBullet = new ItemStack(itemAmmo, 1, 1);
		railgunBullet = new ItemStack(itemAmmo, 1, 2);
		antimatterBullet = new ItemStack(itemAmmo, 1, 3);

		GameRegistry.registerBlock(blockTurret, ItemBlockTurret.class, "ICBMTurret");
		GameRegistry.registerBlock(blockPlatform, "ICBMPlatform");
		GameRegistry.registerBlock(blockFake, "ICBMFake");

		EntityRegistry.registerGlobalEntityID(EntityFakeMountable.class, "ICBMFake", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityFakeMountable.class, "ICBMFake", ENTITY_ID_PREFIX + 7, this, 50, 5, true);

		ICBMTab.itemStack = new ItemStack(blockTurret);

		proxy.preInit();
	}

	@Init
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		ZhuYaoBase.setModMetadata(NAME, metadata);
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

		if (OreDictionary.getOres("battery").size() > 0)
		{
			// Turret Platform
			GameRegistry.addRecipe(new ShapedOreRecipe(blockPlatform, new Object[] { "SPS", "CBC", "SAS", 'P', Block.pistonBase, 'A', UniversalRecipes.BATTERY, 'S', UniversalRecipes.PRIMARY_PLATE, 'C', Block.chest, 'B', UniversalRecipes.CIRCUIT_T1 }));
		}

		// Gun Turret
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 0), new Object[] { "SSS", "CS ", 'C', UniversalRecipes.CIRCUIT_T1, 'S', UniversalRecipes.PRIMARY_METAL }));
		// Railgun
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTurret, 1, 1), new Object[] { "DDD", "CS ", "GS ", 'D', Item.diamond, 'S', UniversalRecipes.PRIMARY_PLATE, 'C', UniversalRecipes.CIRCUIT_T3, 'G', new ItemStack(blockTurret, 1, 0) }));

		CommandRegistry.register(new CommandAccess());
		CommandRegistry.register(new CommandDestroy());
		CommandRegistry.register(new CommandUser());
		CommandRegistry.register(new CommandHelp());
		CommandRegistry.register(new CommandGet());
		CommandRegistry.register(new CommandTarget());
		proxy.init();
	}

	/**
	 * Is a specific position being protected from a specific type of danger?
	 */
	public static boolean isProtected(World world, Vector3 diDian, String banFlag)
	{
		if (FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_GLOBAL, "true", diDian))
		{
			return true;
		}

		return FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, banFlag, "true", diDian);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		FlagRegistry.registerModFlag(FlagRegistry.DEFAULT_NAME, new ModFlag(NBTFileLoader.loadData(FlagRegistry.DEFAULT_NAME)));

		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
		serverCommandManager.registerCommand(new CommandFlag(FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME)));
	}

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