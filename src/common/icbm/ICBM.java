package icbm;

import icbm.electronics.ItemDefuser;
import icbm.electronics.ItemLaserDesignator;
import icbm.electronics.ItemRadarGun;
import icbm.electronics.ItemRemote;
import icbm.electronics.ItemSignalDisrupter;
import icbm.electronics.ItemTracker;
import icbm.explosives.BlockExplosive;
import icbm.explosives.Explosive;
import icbm.machines.BlockDetector;
import icbm.machines.BlockICBMMachine;
import icbm.machines.TileEntityDetector;
import icbm.missiles.Missile;

import java.io.File;
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.ICommandManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.OreGenData;
import universalelectricity.UniversalElectricity;
import universalelectricity.Vector3;
import universalelectricity.basiccomponents.BasicComponents;
import universalelectricity.recipe.RecipeManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

@Mod(modid = "ICBM", name = "ICBM", version = ICBM.VERSION, dependencies = "after:BasicComponenets;after:AtomicScience")
@NetworkMod(channels = { "ICBM" }, clientSideRequired = true, serverSideRequired = false, packetHandler = ICBMPacketManager.class)

public class ICBM implements IDispenseHandler, IWorldGenerator
{
	public static ICBM instance;
	
	public static final String VERSION = "0.5.3";
	
	public static final String TEXTURE_FILE_PATH = "/icbm/textures/";
    public static final String BLOCK_TEXTURE_FILE = TEXTURE_FILE_PATH + "blocks.png";
    public static final String ITEM_TEXTURE_FILE = TEXTURE_FILE_PATH + "items.png";
    
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "config/UniversalElectricity/ICBM.cfg"));
    
	@SidedProxy(clientSide = "icbm.ICBMClientProxy", serverSide = "icbm.ICBMCommonProxy")
	public static ICBMCommonProxy proxy;
	
	//BLOCKS
	public static final int ENTITY_ID_PREFIX = 50;
	
	public static final int BLOCK_ID_PREFIX = 3880;
    public static final Block blockSulfurOre = new BlockSulfurOre(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Sulfur Ores", BLOCK_ID_PREFIX-1));
	public static final Block blockGlassPressurePlate = new BlockGlassPressurePlate(UniversalElectricity.getBlockConfigID(CONFIGURATION, "GlassPressurePlate", BLOCK_ID_PREFIX+0), 0);
	public static final Block blockExplosive = new BlockExplosive(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Explosives", BLOCK_ID_PREFIX+1), 16);
	public static final Block blockMachine = new BlockICBMMachine(UniversalElectricity.getBlockConfigID(CONFIGURATION, "BlockMachine", BLOCK_ID_PREFIX+3));
	public static final Block blockInvisible = new BlockInvisible(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Invisible Block", BLOCK_ID_PREFIX+4), 255);
	public static Block blockRadioactive;
	public static final Block blockDetector = new BlockDetector(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Proximity Detector", BLOCK_ID_PREFIX+6), 7);

	//ITEMS
	public static final int itemIDprefix = 3900;
	public static final Item itemSulfur = new ICBMItem("Sulfur", UniversalElectricity.getItemConfigID(CONFIGURATION, "Sulfur", itemIDprefix-2), 3, CreativeTabs.tabMaterials);
	public static final Item itemPoisonPowder = new ICBMItem("Poison Powder", UniversalElectricity.getItemConfigID(CONFIGURATION, "Poison Powder", itemIDprefix), 0, CreativeTabs.tabMaterials);
	public static final Item itemAntidote = new ItemAntidote("Antidote", UniversalElectricity.getItemConfigID(CONFIGURATION, "Antidote", itemIDprefix+1), 5);
	public static final Item itemMissile = new ItemMissile("Missile", UniversalElectricity.getItemConfigID(CONFIGURATION, "Missile", itemIDprefix+2), 32);
	public static final Item itemSpecialMissile = new ItemSpecialMissile("Special Missile", UniversalElectricity.getItemConfigID(CONFIGURATION, "Special Missile", itemIDprefix+3), 32);
	public static final Item itemDefuser = new ItemDefuser("Defuser", UniversalElectricity.getItemConfigID(CONFIGURATION, "Explosive Defuser", itemIDprefix+4), 21);
	public static final Item itemRadarGun = new ItemRadarGun("Radar Gun", UniversalElectricity.getItemConfigID(CONFIGURATION, "RadarGun", itemIDprefix+5), 19);
	public static final Item itemRemote = new ItemRemote("Remote", UniversalElectricity.getItemConfigID(CONFIGURATION, "Remote", itemIDprefix+6), 20);
	public static final Item itemLaserDesignator = new ItemLaserDesignator("Laser Designator", UniversalElectricity.getItemConfigID(CONFIGURATION, "Laser Designator", itemIDprefix+7), 22);
	public static final Item itemGrenade = new ItemGrenade("Grenade", UniversalElectricity.getItemConfigID(CONFIGURATION, "Grenade", itemIDprefix+8), 64);
	public static final Item itemSignalDisruptor = new ItemSignalDisrupter("Signal Disruptor", UniversalElectricity.getItemConfigID(CONFIGURATION, "Signal Disruptor", itemIDprefix+9), 23);
	public static final Item itemBullet = new ItemBullet("Bullet", UniversalElectricity.getItemConfigID(CONFIGURATION, "Bullet", itemIDprefix+10), 80);
	public static final Item itemTracker = new ItemTracker("Tracker", UniversalElectricity.getItemConfigID(CONFIGURATION, "Tracker", itemIDprefix+11), 18);

	public static final PoisonChemical CHEMICALS = new PoisonChemical("Chemical", 1, false);
	public static final PoisonChemical CONTAGIOUS = new PoisonChemical("Contagious", 1, true);
	
	public OreGenData sulfurGenData;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
    {
		instance = this;
		
		UniversalElectricity.registerMod(this, "ICBM", "0.7.0");

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		GameRegistry.registerDispenserHandler(this);
		
		//-- Registering Blocks
		GameRegistry.registerBlock(blockSulfurOre);
		GameRegistry.registerBlock(blockGlassPressurePlate);
		GameRegistry.registerBlock(blockExplosive, ItemBlockExplosive.class);
		GameRegistry.registerBlock(blockMachine, ItemBlockICBMMachine.class);
		GameRegistry.registerBlock(blockDetector);
		
		if(OreDictionary.getOres("blockRadioactive").size() > 0)
		{
			blockRadioactive = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
			System.out.println("Detected radioative block from another mod.");
		}
		
		OreDictionary.registerOre("dustSulfur", itemSulfur);
				
		sulfurGenData = new OreGenData("Sulfur Ore", "oreSulfur", new ItemStack(blockSulfurOre), 55, 30, 10);
		
   		GameRegistry.registerWorldGenerator(this);
   		
		this.proxy.preInit();
    }
	
	@Init
	public void load(FMLInitializationEvent evt)
    {
		if(blockRadioactive == null)
		{
			blockRadioactive = new BlockRadioactive(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Radioactive Block", BLOCK_ID_PREFIX+5), 4);
			GameRegistry.registerBlock(blockRadioactive);
		}

		//-- Add Names
		LanguageRegistry.addName(blockSulfurOre, "Sulfur Ore");
		
		LanguageRegistry.addName(itemSulfur, "Sulfur Dust");
		LanguageRegistry.addName(itemPoisonPowder, "Poison Powder");
		
		LanguageRegistry.addName(ICBM.itemRadarGun, "Radar Gun");
		LanguageRegistry.addName(ICBM.itemRemote, "Remote Detonator");
		LanguageRegistry.addName(ICBM.itemLaserDesignator, "Laser Designator");
		LanguageRegistry.addName(ICBM.itemDefuser, "Explosive Defuser");
		LanguageRegistry.addName(ICBM.itemTracker, "Tracker");
		LanguageRegistry.addName(ICBM.itemSignalDisruptor, "Signal Disruptor");
		LanguageRegistry.addName(new ItemStack(ICBM.itemBullet, 1, 0), "Conventional Bullet");
		LanguageRegistry.addName(new ItemStack(ICBM.itemBullet, 1, 1), "Antimatter Bullet");
				
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 0), "Launcher Platform T1");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 1), "Launcher Platform T2");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 2), "Launcher Platform T3");
		
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 3), "Launcher Control Panel T1");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 4), "Launcher Control Panel T2");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 5), "Launcher Control Panel T3");
		
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 6), "Launcher Support Frame T1");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 7), "Launcher Support Frame T2");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 8), "Launcher Support Frame T3");
		
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 9), "Radar Station");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 10), "EMP Tower");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 11), "Railgun");
		LanguageRegistry.addName(new ItemStack(ICBM.blockMachine, 1, 12), "Cruise Launcher");
		
		LanguageRegistry.addName(ICBM.itemAntidote, "Antidote");
		
		LanguageRegistry.addName(ICBM.blockGlassPressurePlate, "Glass Pressure Plate");
		
		LanguageRegistry.addName(ICBM.blockDetector, "Proximity Detector");
		
		for(int i = 0; i < ((ItemSpecialMissile)ICBM.itemSpecialMissile).names.length; i++)
		{
		    LanguageRegistry.addName(new ItemStack(ICBM.itemSpecialMissile, 1, i), ((ItemSpecialMissile)ICBM.itemSpecialMissile).names[i]);
		}
		
		//Explosives and missile recipe
		for(int i = 0; i < Explosive.MAX_EXPLOSIVE_ID; i++)
		{
			if(i == 0)
			{	
				LanguageRegistry.addName(new ItemStack(ICBM.itemMissile, 1, i), "Conventional Missile");
				LanguageRegistry.addName(new ItemStack(ICBM.itemGrenade, 1, i), "Conventional Grenade");
			}
			else
			{
				LanguageRegistry.addName(new ItemStack(ICBM.itemMissile, 1, i), Explosive.list[i].getName()+" Missile");
			
				if(i < 4)
				{
					LanguageRegistry.addName(new ItemStack(ICBM.itemGrenade, 1, i), Explosive.list[i].getName()+" Grenade");
				}
			}
		
			LanguageRegistry.addName(new ItemStack(ICBM.blockExplosive, 1, i), Explosive.list[i].getName()+" Explosives");
		}
		  
	    //-- Recipes
		RecipeManager.addRecipe(new ItemStack(ICBM.itemBullet, 16, 0), new Object [] {"@", "!", "!", '@', Item.diamond, '!', BasicComponents.itemBronzeIngot});
		RecipeManager.addRecipe(new ItemStack(ICBM.itemBullet, 1, 1), new Object [] {"@", "!", "!", '@', new ItemStack(ICBM.blockExplosive, 1, Explosive.Antimatter.getID()), '!', ICBM.itemBullet});
		
		//Poison Powder
		RecipeManager.addShapelessRecipe(new ItemStack(itemPoisonPowder), new Object [] {Item.fermentedSpiderEye, Item.rottenFlesh});
		
		//Sulfur
		RecipeManager.addSmelting(new ItemStack(blockSulfurOre, 1, 0), new ItemStack(itemSulfur));
		RecipeManager.addRecipe(new ItemStack(Item.gunpowder, 5), new Object [] {"@@@", "@?@", "@@@", '@', itemSulfur, '?', Item.coal});
		RecipeManager.addRecipe(new ItemStack(Item.gunpowder, 5), new Object [] {"@@@", "@?@", "@@@", '@', itemSulfur, '?', new ItemStack(Item.coal, 1, 1)});
		
		//Radar Gun
		RecipeManager.addRecipe(new ItemStack(ICBM.itemRadarGun), new Object [] {"@#!", " $!", "  !", '@', Block.glass, '!', BasicComponents.itemSteelPlate, '#', BasicComponents.itemCircuit, '$', Block.button});
		//Remote
		RecipeManager.addRecipe(new ItemStack(ICBM.itemRemote), new Object [] {"?@@", "@#$", "@@@", '@', BasicComponents.itemSteelIngot, '?', Item.redstone, '#', new ItemStack(BasicComponents.itemCircuit, 1, 1), '$', Block.button});
		//Laser Designator
		RecipeManager.addRecipe(new ItemStack(ICBM.itemLaserDesignator), new Object [] {"!  ", " ? ", "  @", '@', ICBM.itemRemote, '?', new ItemStack(BasicComponents.itemCircuit, 1, 2), '!', ICBM.itemRadarGun});
		//Proximity Detector
		RecipeManager.addRecipe(new ItemStack(ICBM.blockDetector), new Object [] {" ! ", "!?!", " ! ", '!', BasicComponents.itemSteelPlate, '?', new ItemStack(BasicComponents.itemCircuit, 1, 2)});
		//Signal Disrupter
		RecipeManager.addRecipe(new ItemStack(ICBM.itemSignalDisruptor), new Object [] {"!", "?", '!', ICBM.itemRemote, '?', ICBM.blockDetector});
		
		//Antidote
		RecipeManager.addRecipe(new ItemStack(ICBM.itemAntidote, 2), new Object [] {"@@@", "@@@", "@@@", '@', Item.pumpkinSeeds});
		//Defuser
		RecipeManager.addRecipe(new ItemStack(ICBM.itemDefuser), new Object [] {"?  ", " @ ", "  !", '@', new ItemStack(BasicComponents.itemCircuit, 1, 1), '!', BasicComponents.itemSteelPlate, '?', BasicComponents.blockCopperWire});
		//Missile Launcher Platform
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 0), new Object [] {"! !", "!@!", "!!!", '!', "ingotBronze", '@', BasicComponents.itemSteelPlate});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 1), new Object [] {"! !", "! !", "!@!", '@', new ItemStack(ICBM.blockMachine, 1, 0), '!', "ingotSteel"});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 2), new Object [] {"! !", "! !", "!@!", '@', new ItemStack(ICBM.blockMachine, 1, 1), '!', BasicComponents.itemSteelPlate});
		//Missile Launcher Computer
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 3), new Object [] {"!!!", "!#!", "!?!", '#', BasicComponents.itemCircuit, '!', Block.glass, '?', BasicComponents.blockCopperWire});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 4), new Object [] {"!$!", "!#!", "!?!", '#', new ItemStack(BasicComponents.itemCircuit, 1, 1), '!', BasicComponents.itemSteelIngot, '?', BasicComponents.blockCopperWire, '$', new ItemStack(ICBM.blockMachine, 1, 3)});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 5), new Object [] {"!$!", "!#!", "!?!", '#', new ItemStack(BasicComponents.itemCircuit, 1, 2), '!', Item.ingotGold, '?', BasicComponents.blockCopperWire, '$', new ItemStack(ICBM.blockMachine, 1, 4)});
		//Missile Launcher Frame
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 6), new Object [] {"! !", "!!!", "! !", '!', "ingotBronze"});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 7), new Object [] {"! !", "!@!", "! !", '!', BasicComponents.itemSteelIngot, '@', new ItemStack(ICBM.blockMachine, 1, 6)});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 8), new Object [] {"! !", "!@!", "! !", '!', BasicComponents.itemSteelPlate, '@', new ItemStack(ICBM.blockMachine, 1, 7)});
		//Radar Station
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 9), new Object [] {"?@?", " ! ", "!#!", '@', ICBM.itemRadarGun, '!', BasicComponents.itemSteelIngot, '#', new ItemStack(BasicComponents.itemCircuit, 1, 2), '?', Item.ingotGold});
		//EMP Tower
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 10), new Object [] {"???", "@!@", "?#?", '?', BasicComponents.itemSteelPlate, '!', new ItemStack(BasicComponents.itemCircuit, 1, 2), '@', BasicComponents.blockBatteryBox, '#', BasicComponents.itemMotor});
		//Railgun
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 11), new Object [] {"?!#", "@@@", '@', BasicComponents.itemSteelPlate, '!', ICBM.itemRadarGun, '#', Item.diamond, '?', new ItemStack(BasicComponents.itemCircuit, 1, 2)});
		//Cruise Launcher
		RecipeManager.addRecipe(new ItemStack(ICBM.blockMachine, 1, 12), new Object [] {"?! ", "@@@", '@', BasicComponents.itemSteelPlate, '!', new ItemStack(ICBM.blockMachine, 1, 2), '?', new ItemStack(ICBM.blockMachine, 1, 8)});
		//Glass Pressure Plate
		RecipeManager.addRecipe(new ItemStack(ICBM.blockGlassPressurePlate, 1, 0), new Object [] {"##", '#', Block.glass});
		    
		//Missiles
		RecipeManager.addRecipe(new ItemStack(ICBM.itemSpecialMissile, 1, 0), new Object [] {" @ ", "@#@", "@?@", '@', "ingotSteel", '?', Item.coal, '#', BasicComponents.itemCircuit});
		RecipeManager.addRecipe(new ItemStack(ICBM.itemSpecialMissile, 1, 1), new Object [] {"!", "?", "@", '@', new ItemStack(ICBM.itemSpecialMissile, 1, 0), '?', new ItemStack(ICBM.blockExplosive, 1, 0), '!', BasicComponents.itemCircuit});
		RecipeManager.addRecipe(new ItemStack(ICBM.itemSpecialMissile, 1, 2), new Object [] {" ! ", " ? ", "!@!", '@', new ItemStack(ICBM.itemSpecialMissile, 1, 0), '?', Missile.list[Explosive.Fragmentation.getID()].getItemStack(), '!', new ItemStack(ICBM.itemMissile, 1, 0)});
		
		for(int i = 0; i < Explosive.MAX_EXPLOSIVE_ID; i++)
		{
			if(!Explosive.list[i].isDisabled)
			{
				Explosive.list[i].addCraftingRecipe();
				
		    	//Missile
				RecipeManager.addShapelessRecipe(new ItemStack(ICBM.itemMissile, 1, i), new Object [] {new ItemStack(ICBM.itemSpecialMissile, 1, 0), new ItemStack(ICBM.blockExplosive, 1, i)});        
		
				if(i < 4)
				{
					//Grenade
				    RecipeManager.addRecipe(new ItemStack(ICBM.itemGrenade, 1, i), new Object [] {"?", "@", "@", '@', new ItemStack(ICBM.blockExplosive, 1, i), '?', Item.silk});
		        }
			}
		}
		
		GameRegistry.registerTileEntity(TileEntityExplosive.class, "ICBMExplosive");
		GameRegistry.registerTileEntity(TileEntityDetector.class, "ICBMDetector");
		GameRegistry.registerTileEntity(TileEntityInvisibleBlock.class, "ICBMTileEntityInvisibleBlock");
		
		EntityRegistry.registerGlobalEntityID(EntityExplosive.class, "ICBMExplosive", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityMissile.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityProceduralExplosion.class, "ICBMProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityGravityBlock.class, "ICBMGravityBlock", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityLightBeam.class, "ICBMLightBeam", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityFragment.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityGrenade.class, "ICBMGrenade", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityRailgun.class, "ICBMRailgun", EntityRegistry.findGlobalUniqueEntityId());
		
		EntityRegistry.registerModEntity(EntityExplosive.class, "ICBMExplosive", ENTITY_ID_PREFIX, this, 50, 5, true);
		EntityRegistry.registerModEntity(EntityMissile.class, "ICBMMissile", ENTITY_ID_PREFIX+1, this, 100, 2, true);
		EntityRegistry.registerModEntity(EntityProceduralExplosion.class, "ICBMProceduralExplosion", ENTITY_ID_PREFIX+2, this, 100, 5, true);
		EntityRegistry.registerModEntity(EntityGravityBlock.class, "ICBMGravityBlock", ENTITY_ID_PREFIX+3, this, 50, 15, true);
		EntityRegistry.registerModEntity(EntityLightBeam.class, "ICBMLightBeam", ENTITY_ID_PREFIX+4, this, 80, 5, true);
		EntityRegistry.registerModEntity(EntityFragment.class, "ICBMFragment", ENTITY_ID_PREFIX+5, this, 40, 8, true);
		EntityRegistry.registerModEntity(EntityGrenade.class, "ICBMGrenade", ENTITY_ID_PREFIX+6, this, 50, 5, true);
		EntityRegistry.registerModEntity(EntityRailgun.class, "ICBMRailgun", ENTITY_ID_PREFIX+7, this, 50, 5, true);
		
  	    //Register potion effects
  	    ICBMPotion.init();
  	    
		this.proxy.init();
    }
	
	public static Vector3 getLook(float rotationYaw, float rotationPitch)
    {
        float var2;
        float var3;
        float var4;
        float var5;

        var2 = MathHelper.cos(-rotationYaw * 0.017453292F - (float)Math.PI);
        var3 = MathHelper.sin(-rotationYaw * 0.017453292F - (float)Math.PI);
        var4 = -MathHelper.cos(-rotationPitch * 0.017453292F);
        var5 = MathHelper.sin(-rotationPitch * 0.017453292F);
        return new Vector3(var3 * var4, var5, var2 * var4);
    }
    
    @Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		chunkX = chunkX << 4;
		chunkZ = chunkZ << 4;
		
		//Checks to make sure this is the normal world 
		if(chunkGenerator instanceof ChunkProviderGenerate)
		{
            if(sulfurGenData.shouldGenerate && sulfurGenData.generateSurface)
            {
            	WorldGenSulfur worldGenSulfur = new WorldGenSulfur(sulfurGenData.oreStack, sulfurGenData.amountPerBranch);

                for (int i = 0; i < sulfurGenData.amountPerChunk; i++)
                {
                    int x = chunkX + rand.nextInt(16);
                    int y = rand.nextInt(sulfurGenData.maxGenerateLevel) + sulfurGenData.minGenerateLevel;
                    int z = chunkZ + rand.nextInt(16);
                    
        			int randAmount = rand.nextInt(8);

                	worldGenSulfur.generate(world, rand, x, y, z);
                }
            }
     
        }
	}
	
	@Override
    public int dispense(double x, double y, double z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY, double entZ)
	{
		if(item.itemID == ICBM.itemGrenade.shiftedIndex)
		{
			EntityGrenade entity = new EntityGrenade(world, new Vector3(x, y, z), item.getItemDamage());
	        entity.setThrowableHeading(xVelocity, 0.10000000149011612D, zVelocity, 1.1F, 6.0F);
	        world.spawnEntityInWorld(entity);
			return -1;
		}
		
		return -1;
	}
	
	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager); 
		serverCommandManager.registerCommand(new CommandICBM());
	}
	
	public static boolean getBooleanConfig(String comment, boolean defaultValue)
	{
		boolean returnValue = defaultValue;
		
		CONFIGURATION.load();

        try
        {
        	returnValue = Boolean.parseBoolean(CONFIGURATION.getOrCreateBooleanProperty(comment, Configuration.CATEGORY_GENERAL, defaultValue).value);
        }
        catch(Exception e)
        {
        	returnValue = defaultValue;
        }
        
        CONFIGURATION.save();
        return returnValue;
	}
}
