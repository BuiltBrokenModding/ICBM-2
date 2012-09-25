package icbm;

import icbm.daodan.DaoDan;
import icbm.daodan.EDaoDan;
import icbm.daodan.ItDaoDan;
import icbm.daodan.ItTeBieDaoDan;
import icbm.dianqi.ItGenZongQi;
import icbm.dianqi.ItHuoLuanQi;
import icbm.dianqi.ItJieJa;
import icbm.dianqi.ItLeiDaQiang;
import icbm.dianqi.ItLeiShiZhiBiao;
import icbm.dianqi.ItYaoKong;
import icbm.jiqi.BJiQi;
import icbm.jiqi.BYinGanQi;
import icbm.jiqi.ECiGuiPao;
import icbm.jiqi.IBJiQi;
import icbm.jiqi.TYinGanQi;
import icbm.zhapin.BZhaDan;
import icbm.zhapin.EShouLiuDan;
import icbm.zhapin.EZhaDan;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.IBZhaDan;
import icbm.zhapin.TZhaDan;
import icbm.zhapin.ZhaPin;

import java.io.File;
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ICommandManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.BasicComponents;
import universalelectricity.UniversalElectricity;
import universalelectricity.ore.OreGenBase;
import universalelectricity.ore.OreGenerator;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;
import atomicscience.api.BlockRadioactive;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IDispenserHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "ICBM", name = "ICBM", version = ICBM.VERSION, dependencies = "after:UniversalElectricity;after:AtomicScience")
@NetworkMod(channels = { "ICBM" }, clientSideRequired = true, serverSideRequired = false, packetHandler = ICBMPacketManager.class)

public class ICBM
{
	@Instance("ICBM")
	public static ICBM instance;
	
	public static final String VERSION = "0.5.4";
	
	public static final String TEXTURE_FILE_PATH = "/icbm/textures/";
    public static final String BLOCK_TEXTURE_FILE = TEXTURE_FILE_PATH + "blocks.png";
    public static final String ITEM_TEXTURE_FILE = TEXTURE_FILE_PATH + "items.png";
    public static final String TRACKER_TEXTURE_FILE = TEXTURE_FILE_PATH + "tracker.png";
    
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/ICBM.cfg"));
    
	@SidedProxy(clientSide = "icbm.ICBMClientProxy", serverSide = "icbm.ICBMCommonProxy")
	public static ICBMCommonProxy proxy;
	
	//BLOCKS
	public static final int ENTITY_ID_PREFIX = 50;
	
	public static final int BLOCK_ID_PREFIX = 3880;
    public static final Block blockLiu = new BLiu(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Sulfur Ores", BLOCK_ID_PREFIX-1));
	public static final Block blockBuo1LiPan = new BBuo1Li4Pan2(UniversalElectricity.getBlockConfigID(CONFIGURATION, "GlassPressurePlate", BLOCK_ID_PREFIX+0), 0);
	public static final Block blockZha4Dan4 = new BZhaDan(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Explosives", BLOCK_ID_PREFIX+1), 16);
	public static final Block blockJiQi = new BJiQi(UniversalElectricity.getBlockConfigID(CONFIGURATION, "BlockMachine", BLOCK_ID_PREFIX+3));
	public static final Block blockYinXing = new BYinXing(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Invisible Block", BLOCK_ID_PREFIX+4), 255);
	public static Block blockFuShe;
	public static final Block blockYinGanQi = new BYinGanQi(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Proximity Detector", BLOCK_ID_PREFIX+6), 7);

	//ITEMS
	public static final int itemIDprefix = 3900;
	public static final Item itemLiu = new ICBMItem("Sulfur", UniversalElectricity.getItemConfigID(CONFIGURATION, "Sulfur", itemIDprefix-2), 3, CreativeTabs.tabMaterials);
	public static final Item itemDu = new ICBMItem("Poison Powder", UniversalElectricity.getItemConfigID(CONFIGURATION, "Poison Powder", itemIDprefix), 0, CreativeTabs.tabMaterials);
	public static final Item itemYao = new ItYao("Antidote", UniversalElectricity.getItemConfigID(CONFIGURATION, "Antidote", itemIDprefix+1), 5);
	public static final Item itemDaoDan = new ItDaoDan("Missile", UniversalElectricity.getItemConfigID(CONFIGURATION, "Missile", itemIDprefix+2), 32);
	public static final Item itemTeBieDaoDan = new ItTeBieDaoDan("Special Missile", UniversalElectricity.getItemConfigID(CONFIGURATION, "Special Missile", itemIDprefix+3), 32);
	
	public static final ItemElectric itemJieJa = new ItJieJa("Defuser", UniversalElectricity.getItemConfigID(CONFIGURATION, "Explosive Defuser", itemIDprefix+4), 21);
	public static final ItemElectric itemLeiDaQiang = new ItLeiDaQiang("Radar Gun", UniversalElectricity.getItemConfigID(CONFIGURATION, "RadarGun", itemIDprefix+5), 19);
	public static final ItemElectric itemYaoKong = new ItYaoKong("Remote", UniversalElectricity.getItemConfigID(CONFIGURATION, "Remote", itemIDprefix+6), 20);
	public static final ItemElectric itemLeiSheZhiBiao = new ItLeiShiZhiBiao("Laser Designator", UniversalElectricity.getItemConfigID(CONFIGURATION, "Laser Designator", itemIDprefix+7), 22);
	public static final ItemElectric itemHuoLaunQi = new ItHuoLuanQi("Signal Disruptor", UniversalElectricity.getItemConfigID(CONFIGURATION, "Signal Disruptor", itemIDprefix+9), 23);
	public static final ItemElectric itemGenZongQi = new ItGenZongQi("Tracker", UniversalElectricity.getItemConfigID(CONFIGURATION, "Tracker", itemIDprefix+11), 0);

	public static final Item itemShouLiuDan = new ItShouLiuDan("Grenade", UniversalElectricity.getItemConfigID(CONFIGURATION, "Grenade", itemIDprefix+8), 64);
	public static final Item itemZiDan = new ItZiDan("Bullet", UniversalElectricity.getItemConfigID(CONFIGURATION, "Bullet", itemIDprefix+10), 80);

	public static final Du DU_DU = new Du("Chemical", 1, false);
	public static final Du DU_YI_CHUAN = new Du("Contagious", 1, true);
	
    public static final OreGenBase liuGenData = new GenLiu("Sulfur Ore", "oreSulfur", new ItemStack(blockLiu), 0, 40, 25, 15).enable();
		
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
    {		
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);
		
		GameRegistry.registerDispenserHandler(
			new IDispenserHandler()
	        {
	            @Override
	            public int dispense(int x, int y, int z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY, double entZ)
	            {
	            	if(!world.isRemote)
	            	{
		            	if(item.itemID == ICBM.itemShouLiuDan.shiftedIndex)
		        		{
		        			EShouLiuDan entity = new EShouLiuDan(world, new Vector3(x, y, z), item.getItemDamage());
		        	        entity.setThrowableHeading(xVelocity, 0.10000000149011612D, zVelocity, 1.1F, 6.0F);
		        	        world.spawnEntityInWorld(entity);
		        	        return 1;
		        		}
	            	}
	        		
	        		return -1;
	            }
	        }
		);
		
		//-- Registering Blocks
		GameRegistry.registerBlock(blockLiu);
		GameRegistry.registerBlock(blockBuo1LiPan);
		GameRegistry.registerBlock(blockZha4Dan4, IBZhaDan.class);
		GameRegistry.registerBlock(blockJiQi, IBJiQi.class);
		GameRegistry.registerBlock(blockYinGanQi);
		
		if(OreDictionary.getOres("blockRadioactive").size() > 0)
		{
			blockFuShe = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
			System.out.println("Detected radioative block from another mod.");
		}
		
		OreDictionary.registerOre("dustSulfur", itemLiu);
		
		OreGenerator.addOre(liuGenData);
		   		
		this.proxy.preInit();
    }
	
	@Init
	public void load(FMLInitializationEvent evt)
    {
		if(blockFuShe == null)
		{
			blockFuShe = new BlockRadioactive(UniversalElectricity.getBlockConfigID(CONFIGURATION, "Radioactive Block", BLOCK_ID_PREFIX+5), 4, ICBM.BLOCK_TEXTURE_FILE);
			GameRegistry.registerBlock(blockFuShe);
		}

		//-- Add Names
		LanguageRegistry.addName(blockLiu, "Sulfur Ore");
		
		LanguageRegistry.addName(itemLiu, "Sulfur Dust");
		LanguageRegistry.addName(itemDu, "Poison Powder");
		
		LanguageRegistry.addName(ICBM.itemLeiDaQiang, "Radar Gun");
		LanguageRegistry.addName(ICBM.itemYaoKong, "Remote Detonator");
		LanguageRegistry.addName(ICBM.itemLeiSheZhiBiao, "Laser Designator");
		LanguageRegistry.addName(ICBM.itemJieJa, "Explosive Defuser");
		LanguageRegistry.addName(ICBM.itemGenZongQi, "Tracker");
		LanguageRegistry.addName(ICBM.itemHuoLaunQi, "Signal Disruptor");
		LanguageRegistry.addName(new ItemStack(ICBM.itemZiDan, 1, 0), "Conventional Bullet");
		LanguageRegistry.addName(new ItemStack(ICBM.itemZiDan, 1, 1), "Antimatter Bullet");
				
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 0), "Launcher Platform T1");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 1), "Launcher Platform T2");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 2), "Launcher Platform T3");
		
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 3), "Launcher Control Panel T1");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 4), "Launcher Control Panel T2");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 5), "Launcher Control Panel T3");
		
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 6), "Launcher Support Frame T1");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 7), "Launcher Support Frame T2");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 8), "Launcher Support Frame T3");
		
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 9), "Radar Station");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 10), "EMP Tower");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 11), "Railgun");
		LanguageRegistry.addName(new ItemStack(ICBM.blockJiQi, 1, 12), "Cruise Launcher");
		
		LanguageRegistry.addName(ICBM.itemYao, "Antidote");
		
		LanguageRegistry.addName(ICBM.blockBuo1LiPan, "Glass Pressure Plate");
		
		LanguageRegistry.addName(ICBM.blockYinGanQi, "Proximity Detector");
		
		for(int i = 0; i < ((ItTeBieDaoDan)ICBM.itemTeBieDaoDan).names.length; i++)
		{
		    LanguageRegistry.addName(new ItemStack(ICBM.itemTeBieDaoDan, 1, i), ((ItTeBieDaoDan)ICBM.itemTeBieDaoDan).names[i]);
		}
		
		//Explosives and missile recipe
		for(int i = 0; i < ZhaPin.MAX_EXPLOSIVE_ID; i++)
		{
			if(i == 0)
			{	
				LanguageRegistry.addName(new ItemStack(ICBM.itemDaoDan, 1, i), "Conventional Missile");
				LanguageRegistry.addName(new ItemStack(ICBM.itemShouLiuDan, 1, i), "Conventional Grenade");
			}
			else
			{
				LanguageRegistry.addName(new ItemStack(ICBM.itemDaoDan, 1, i), ZhaPin.list[i].getMing()+" Missile");
			
				if(i < 4)
				{
					LanguageRegistry.addName(new ItemStack(ICBM.itemShouLiuDan, 1, i), ZhaPin.list[i].getMing()+" Grenade");
				}
			}
		
			LanguageRegistry.addName(new ItemStack(ICBM.blockZha4Dan4, 1, i), ZhaPin.list[i].getMing()+" Explosives");
		}
		 
	    //-- Recipes
		RecipeManager.addRecipe(new ItemStack(ICBM.itemZiDan, 16, 0), new Object [] {"@", "!", "!", '@', Item.diamond, '!', BasicComponents.itemBronzeIngot});
		RecipeManager.addRecipe(new ItemStack(ICBM.itemZiDan, 1, 1), new Object [] {"@", "!", "!", '@', new ItemStack(ICBM.blockZha4Dan4, 1, ZhaPin.Antimatter.getID()), '!', ICBM.itemZiDan});
		
		//Poison Powder
		RecipeManager.addShapelessRecipe(new ItemStack(itemDu), new Object [] {Item.fermentedSpiderEye, Item.rottenFlesh});
		
		//Sulfur
		RecipeManager.addSmelting(new ItemStack(blockLiu, 1, 0), new ItemStack(itemLiu));
		RecipeManager.addRecipe(new ItemStack(Item.gunpowder, 5), new Object [] {"@@@", "@?@", "@@@", '@', itemLiu, '?', Item.coal});
		RecipeManager.addRecipe(new ItemStack(Item.gunpowder, 5), new Object [] {"@@@", "@?@", "@@@", '@', itemLiu, '?', new ItemStack(Item.coal, 1, 1)});
		
		//Radar Gun
		RecipeManager.addRecipe(new ItemStack(ICBM.itemLeiDaQiang), new Object [] {"@#!", " $!", "  !", '@', Block.glass, '!', BasicComponents.itemSteelPlate, '#', BasicComponents.itemCircuit, '$', Block.button});
		//Remote
		RecipeManager.addRecipe(new ItemStack(ICBM.itemYaoKong), new Object [] {"?@@", "@#$", "@@@", '@', BasicComponents.itemSteelIngot, '?', Item.redstone, '#', new ItemStack(BasicComponents.itemCircuit, 1, 1), '$', Block.button});
		//Laser Designator
		RecipeManager.addRecipe(new ItemStack(ICBM.itemLeiSheZhiBiao), new Object [] {"!  ", " ? ", "  @", '@', ICBM.itemYaoKong.getUnchargedItemStack(), '?', new ItemStack(BasicComponents.itemCircuit, 1, 2), '!', ICBM.itemLeiDaQiang.getUnchargedItemStack()});
		//Proximity Detector
		RecipeManager.addRecipe(new ItemStack(ICBM.blockYinGanQi), new Object [] {" ! ", "!?!", " ! ", '!', BasicComponents.itemSteelPlate, '?', new ItemStack(BasicComponents.itemCircuit, 1, 2)});
		//Signal Disrupter
		RecipeManager.addRecipe(new ItemStack(ICBM.itemHuoLaunQi), new Object [] {"!", "?", '!', ICBM.itemYaoKong.getUnchargedItemStack(), '?', ICBM.blockYinGanQi});
		
		//Antidote
		RecipeManager.addRecipe(new ItemStack(ICBM.itemYao, 2), new Object [] {"@@@", "@@@", "@@@", '@', Item.pumpkinSeeds});
		//Defuser
		RecipeManager.addRecipe(new ItemStack(ICBM.itemJieJa), new Object [] {"?  ", " @ ", "  !", '@', new ItemStack(BasicComponents.itemCircuit, 1, 1), '!', BasicComponents.itemSteelPlate, '?', BasicComponents.blockCopperWire});
		//Missile Launcher Platform
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 0), new Object [] {"! !", "!@!", "!!!", '!', "ingotBronze", '@', BasicComponents.itemSteelPlate});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 1), new Object [] {"! !", "! !", "!@!", '@', new ItemStack(ICBM.blockJiQi, 1, 0), '!', "ingotSteel"});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 2), new Object [] {"! !", "! !", "!@!", '@', new ItemStack(ICBM.blockJiQi, 1, 1), '!', BasicComponents.itemSteelPlate});
		//Missile Launcher Computer
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 3), new Object [] {"!!!", "!#!", "!?!", '#', BasicComponents.itemCircuit, '!', Block.glass, '?', BasicComponents.blockCopperWire});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 4), new Object [] {"!$!", "!#!", "!?!", '#', new ItemStack(BasicComponents.itemCircuit, 1, 1), '!', BasicComponents.itemSteelIngot, '?', BasicComponents.blockCopperWire, '$', new ItemStack(ICBM.blockJiQi, 1, 3)});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 5), new Object [] {"!$!", "!#!", "!?!", '#', new ItemStack(BasicComponents.itemCircuit, 1, 2), '!', Item.ingotGold, '?', BasicComponents.blockCopperWire, '$', new ItemStack(ICBM.blockJiQi, 1, 4)});
		//Missile Launcher Frame
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 6), new Object [] {"! !", "!!!", "! !", '!', "ingotBronze"});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 7), new Object [] {"! !", "!@!", "! !", '!', BasicComponents.itemSteelIngot, '@', new ItemStack(ICBM.blockJiQi, 1, 6)});
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 8), new Object [] {"! !", "!@!", "! !", '!', BasicComponents.itemSteelPlate, '@', new ItemStack(ICBM.blockJiQi, 1, 7)});
		//Radar Station
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 9), new Object [] {"?@?", " ! ", "!#!", '@', ICBM.itemLeiDaQiang.getUnchargedItemStack(), '!', BasicComponents.itemSteelIngot, '#', new ItemStack(BasicComponents.itemCircuit, 1, 2), '?', Item.ingotGold});
		//EMP Tower
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 10), new Object [] {"???", "@!@", "?#?", '?', BasicComponents.itemSteelPlate, '!', new ItemStack(BasicComponents.itemCircuit, 1, 2), '@', BasicComponents.batteryBox, '#', BasicComponents.itemMotor});
		//Railgun
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 11), new Object [] {"?!#", "@@@", '@', BasicComponents.itemSteelPlate, '!', ICBM.itemLeiDaQiang.getUnchargedItemStack(), '#', Item.diamond, '?', new ItemStack(BasicComponents.itemCircuit, 1, 2)});
		//Cruise Launcher
		RecipeManager.addRecipe(new ItemStack(ICBM.blockJiQi, 1, 12), new Object [] {"?! ", "@@@", '@', BasicComponents.itemSteelPlate, '!', new ItemStack(ICBM.blockJiQi, 1, 2), '?', new ItemStack(ICBM.blockJiQi, 1, 8)});
		//Glass Pressure Plate
		RecipeManager.addRecipe(new ItemStack(ICBM.blockBuo1LiPan, 1, 0), new Object [] {"##", '#', Block.glass});
		    
		//Missiles
		RecipeManager.addRecipe(new ItemStack(ICBM.itemTeBieDaoDan, 1, 0), new Object [] {" @ ", "@#@", "@?@", '@', "ingotSteel", '?', BasicComponents.itemOilBucket, '#', BasicComponents.itemCircuit});
		RecipeManager.addRecipe(new ItemStack(ICBM.itemTeBieDaoDan, 1, 1), new Object [] {"!", "?", "@", '@', new ItemStack(ICBM.itemTeBieDaoDan, 1, 0), '?', new ItemStack(ICBM.blockZha4Dan4, 1, 0), '!', BasicComponents.itemCircuit});
		RecipeManager.addRecipe(new ItemStack(ICBM.itemTeBieDaoDan, 1, 2), new Object [] {" ! ", " ? ", "!@!", '@', new ItemStack(ICBM.itemTeBieDaoDan, 1, 0), '?', DaoDan.list[ZhaPin.Fragmentation.getID()].getItemStack(), '!', new ItemStack(ICBM.itemDaoDan, 1, 0)});
		
		for(int i = 0; i < ZhaPin.MAX_EXPLOSIVE_ID; i++)
		{
			ZhaPin.list[i].init();
				
	    	//Missile
			RecipeManager.addShapelessRecipe(new ItemStack(ICBM.itemDaoDan, 1, i), new Object [] {new ItemStack(ICBM.itemTeBieDaoDan, 1, 0), new ItemStack(ICBM.blockZha4Dan4, 1, i)}, ZhaPin.list[i].getDaoDanMing(), CONFIGURATION, true);        
	
			if(i < 4)
			{
				//Grenade
			    RecipeManager.addRecipe(new ItemStack(ICBM.itemShouLiuDan, 1, i), new Object [] {"?", "@", "@", '@', new ItemStack(ICBM.blockZha4Dan4, 1, i), '?', Item.silk}, CONFIGURATION, true);
	        }
		}
		
		GameRegistry.registerTileEntity(TZhaDan.class, "ICBMExplosive");
		GameRegistry.registerTileEntity(TYinGanQi.class, "ICBMDetector");
		GameRegistry.registerTileEntity(TYinXing.class, "ICBMTileEntityInvisibleBlock");
		
		EntityRegistry.registerGlobalEntityID(EZhaDan.class, "ICBMExplosive", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EDaoDan.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EZhaPin.class, "ICBMProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityGravityBlock.class, "ICBMGravityBlock", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EGuang.class, "ICBMLightBeam", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(ESuiPian.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EShouLiuDan.class, "ICBMGrenade", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(ECiGuiPao.class, "ICBMRailgun", EntityRegistry.findGlobalUniqueEntityId());
		
		EntityRegistry.registerModEntity(EZhaDan.class, "ICBMExplosive", ENTITY_ID_PREFIX, this, 50, 5, true);
		EntityRegistry.registerModEntity(EDaoDan.class, "ICBMMissile", ENTITY_ID_PREFIX+1, this, 100, 2, true);
		EntityRegistry.registerModEntity(EZhaPin.class, "ICBMProceduralExplosion", ENTITY_ID_PREFIX+2, this, 100, 5, true);
		EntityRegistry.registerModEntity(EntityGravityBlock.class, "ICBMGravityBlock", ENTITY_ID_PREFIX+3, this, 50, 15, true);
		EntityRegistry.registerModEntity(EGuang.class, "ICBMLightBeam", ENTITY_ID_PREFIX+4, this, 80, 5, true);
		EntityRegistry.registerModEntity(ESuiPian.class, "ICBMFragment", ENTITY_ID_PREFIX+5, this, 40, 8, true);
		EntityRegistry.registerModEntity(EShouLiuDan.class, "ICBMGrenade", ENTITY_ID_PREFIX+6, this, 50, 5, true);
		EntityRegistry.registerModEntity(ECiGuiPao.class, "ICBMRailgun", ENTITY_ID_PREFIX+7, this, 50, 5, true);
		
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

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager); 
		serverCommandManager.registerCommand(new CommandICBM());
	}
}
