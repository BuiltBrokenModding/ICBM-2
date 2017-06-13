package com.builtbroken.icbm;

import com.builtbroken.icbm.client.CreativeTabExplosives;
import com.builtbroken.icbm.client.CreativeTabMissiles;
import com.builtbroken.icbm.client.CreativeTabWarheads;
import com.builtbroken.icbm.client.ICBMCreativeTab;
import com.builtbroken.icbm.content.blast.biome.ExBiomeChange;
import com.builtbroken.icbm.content.blast.effect.ExAntiPlant;
import com.builtbroken.icbm.content.blast.effect.ExEnderBlocks;
import com.builtbroken.icbm.content.blast.effect.ExPlantLife;
import com.builtbroken.icbm.content.blast.effect.ExTorchEater;
import com.builtbroken.icbm.content.blast.entity.ExSpawn;
import com.builtbroken.icbm.content.blast.entity.slime.EntitySlimeRain;
import com.builtbroken.icbm.content.blast.entity.slime.ExSlimeRain;
import com.builtbroken.icbm.content.blast.explosive.BlastPathTester;
import com.builtbroken.icbm.content.blast.explosive.ExAntimatter;
import com.builtbroken.icbm.content.blast.explosive.ExMicroQuake;
import com.builtbroken.icbm.content.blast.fire.ExFireBomb;
import com.builtbroken.icbm.content.blast.fire.ExFlashFire;
import com.builtbroken.icbm.content.blast.fragment.ExFragment;
import com.builtbroken.icbm.content.blast.gravity.ExGravity;
import com.builtbroken.icbm.content.blast.item.BlockFakeCake;
import com.builtbroken.icbm.content.blast.item.ExCake;
import com.builtbroken.icbm.content.blast.nuke.ExNuke;
import com.builtbroken.icbm.content.blast.potion.ExFlash;
import com.builtbroken.icbm.content.blast.potion.ExRadiation;
import com.builtbroken.icbm.content.blast.power.ExEmp;
import com.builtbroken.icbm.content.blast.power.ExMicrowave;
import com.builtbroken.icbm.content.blast.temp.ExEndoThermic;
import com.builtbroken.icbm.content.blast.temp.ExExoThermic;
import com.builtbroken.icbm.content.blast.troll.BlastMidasOre;
import com.builtbroken.icbm.content.blast.util.ExOrePuller;
import com.builtbroken.icbm.content.blast.util.ExRegen;
import com.builtbroken.icbm.content.blast.util.ExRegenLocal;
import com.builtbroken.icbm.content.debug.BlockExplosiveMarker;
import com.builtbroken.icbm.content.fragments.EntityFragment;
import com.builtbroken.icbm.content.fragments.FragmentEventHandler;
import com.builtbroken.icbm.content.items.ItemExplosive;
import com.builtbroken.icbm.content.items.parts.ItemExplosiveParts;
import com.builtbroken.icbm.content.items.parts.ItemMissileParts;
import com.builtbroken.icbm.content.items.parts.MissileCraftingParts;
import com.builtbroken.icbm.content.launcher.block.LauncherPartListener;
import com.builtbroken.icbm.content.launcher.block.TileLauncherFrame;
import com.builtbroken.icbm.content.launcher.controller.direct.TileSiloController;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.BlockAntennaParts;
import com.builtbroken.icbm.content.launcher.controller.remote.central.TileCommandController;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileCommandSiloConnector;
import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.icbm.content.missile.item.ItemMissile;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.icbm.content.missile.json.MissileJsonProcessor;
import com.builtbroken.icbm.content.missile.parts.engine.Engines;
import com.builtbroken.icbm.content.missile.parts.engine.ItemEngineModules;
import com.builtbroken.icbm.content.missile.parts.guidance.GuidanceModules;
import com.builtbroken.icbm.content.missile.parts.guidance.ItemGuidanceModules;
import com.builtbroken.icbm.content.missile.parts.trigger.ItemTriggerModules;
import com.builtbroken.icbm.content.missile.parts.trigger.Triggers;
import com.builtbroken.icbm.content.missile.parts.warhead.WarheadCasings;
import com.builtbroken.icbm.content.missile.tile.TileCrashedMissile;
import com.builtbroken.icbm.content.missile.tracking.MissileTracker;
import com.builtbroken.icbm.content.rail.EntityMissileCart;
import com.builtbroken.icbm.content.rail.ItemMissileCart;
import com.builtbroken.icbm.content.rocketlauncher.ItemRocketLauncher;
import com.builtbroken.icbm.content.warhead.TileWarhead;
import com.builtbroken.icbm.mods.cc.CCProxyICBM;
import com.builtbroken.icbm.mods.oc.OCProxyICBM;
import com.builtbroken.icbm.mods.ve.EntityTypeCheckMissile;
import com.builtbroken.icbm.server.CommandICBM;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.lib.json.JsonContentLoader;
import com.builtbroken.mc.lib.json.processors.block.JsonBlockListenerProcessor;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.builtbroken.mc.lib.mod.Mods;
import com.builtbroken.mc.lib.recipe.item.sheetmetal.RecipeSheetMetal;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.mods.nei.NEIProxy;
import com.builtbroken.mc.prefab.entity.type.EntityTypeCheckRegistry;
import com.builtbroken.mc.prefab.explosive.ExplosiveHandlerGeneric;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Main Mod class for ICBM, Loads up everything needs when called by FML/Forge
 *
 * @author DarkGuardsman, [Original Author Calclavia]
 */
@Mod(modid = ICBM.DOMAIN, name = ICBM.NAME, version = ICBM.VERSION, dependencies = ICBM.DEPENDENCIES)
public final class ICBM extends AbstractMod
{
    //Meta
    public static final String NAME = "ICBM";
    public static final String DOMAIN = "icbm";
    public static final String PREFIX = DOMAIN + ":";

    // Version numbers
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;
    //http://www.minecraftforge.net/wiki/Developing_Addons_for_Existing_Mods
    public static final String DEPENDENCIES = "required-after:voltzengine;after:OpenComputers";


    @Instance(DOMAIN)
    public static ICBM INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.icbm.client.ClientProxy", serverSide = "com.builtbroken.icbm.server.ServerProxy")
    public static CommonProxy proxy;


    public static boolean ANTIMATTER_BREAK_UNBREAKABLE = true;
    public static boolean DEBUG_MISSILE_MANAGER = false;

    public static float missile_firing_volume = 1f;
    public static float missile_engine_volume = 1f;
    public static float ams_gun_volume = 1f;
    public static float ams_rotation_volume = 1f;

    public static int ENTITY_ID_PREFIX = 50;

    // Blocks
    public static Block blockWarhead;
    public static Block blockExplosiveMarker;

    public static Block blockMissileWorkstation;
    public static Block blockDirectSiloController;

    public static Block blockLauncherFrame;

    public static Block blockAntenna;
    public static Block blockCommandCentral;
    public static Block blockCommandSiloConnector;
    public static Block blockCommandSiloDisplay;

    public static Block blockCake;

    public static Block blockCrashMissile;

    // Items
    public static Item itemMissile;
    public static Item itemRocketLauncher;
    public static ItemEngineModules itemEngineModules;
    public static ItemGuidanceModules itemGuidanceModules;
    public static Item itemMissileParts;
    public static Item itemExplosive;
    public static Item itemExplosivePart;
    public static Item itemTrigger;

    public static Item itemMissileCart;

    public final ModCreativeTab CREATIVE_TAB;

    private static boolean registerExplosives;

    public static boolean APRIL_FIRST;

    public static CreativeTabMissiles[] missileTabs;
    public static CreativeTabWarheads warheadsTab;
    public static CreativeTabExplosives explosiveTab;

    public ICBM()
    {
        super(DOMAIN, "ICBM");
        CREATIVE_TAB = new ICBMCreativeTab();
        super.manager.setTab(CREATIVE_TAB);

        explosiveTab = new CreativeTabExplosives();
        missileTabs = new CreativeTabMissiles[]{
                new CreativeTabMissiles(MissileSize.MICRO),
                new CreativeTabMissiles(MissileSize.SMALL),
                new CreativeTabMissiles(MissileSize.STANDARD)
        };
        warheadsTab = new CreativeTabWarheads();

        fireProxyPreInit = false;

        Calendar now = Calendar.getInstance(); //TODO move to VE
        APRIL_FIRST = (now.get(Calendar.MONTH) + 1) == 4 && now.get(Calendar.DAY_OF_MONTH) == 1;
    }

    @Override
    public void loadJsonContentHandlers()
    {
        super.loadJsonContentHandlers();

        //JSON processors
        JsonContentLoader.INSTANCE.add(new MissileJsonProcessor());

        //Listeners
        JsonBlockListenerProcessor.addBuilder(new LauncherPartListener.Builder());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(FragmentEventHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(this);


        //Request Engine to load items for use
        Engine.heatedRockRequested = true;
        Engine.requestOres();
        Engine.requestResources();
        Engine.requestSheetMetalContent();
        Engine.requestMultiBlock();
        Engine.requestSimpleTools();
        Engine.requestCircuits();
        Engine.requestCraftingParts();

        loader.applyModule(OCProxyICBM.class, Mods.OC.isLoaded());
        loader.applyModule(CCProxyICBM.class, Mods.CC.isLoaded());
        //Loads thaumcraft support
        if (Loader.isModLoaded("Thaumcraft") && !getConfig().getBoolean("DisableThaumSupport", "ModSupport", false, "Allows disabling thaumcraft support, if issues arise or game play balance is required."))
        {
            //loader.applyModule(ThaumBlastLoader.class);
        }

        // Configs TODO load up using config system, and separate file
        ANTIMATTER_BREAK_UNBREAKABLE = getConfig().getBoolean("Antimatter_Destroy_Unbreakable", Configuration.CATEGORY_GENERAL, true, "Allows antimatter to break blocks that are unbreakable, bedrock for example.");
        DEBUG_MISSILE_MANAGER = getConfig().getBoolean("Missile_Manager", "Debug", Engine.runningAsDev, "Adds additional info to the console");
        missile_firing_volume = getConfig().getFloat("missile_firing", "volume", 1.0F, 0, 1, "How loud the missile is when fired from launchers");
        missile_engine_volume = getConfig().getFloat("missile_engine", "volume", 1.0F, 0, 1, "How loud the missile engine is while running");
        ams_gun_volume = getConfig().getFloat("ams_gun", "volume", 1.0F, 0, 4, "How loud the gun firing is for the AMS turret");
        ams_rotation_volume = getConfig().getFloat("ams_rotation", "volume", 1.0F, 0, 1, "How loud the rotation audio is for the AMS turret");

        //Fire loader late to allow configs and loaders to setup
        loader.preInit();

        // Functional Blocks
        blockWarhead = manager.newBlock(TileWarhead.class);
        blockCrashMissile = manager.newBlock("icbmCrashedMissile", TileCrashedMissile.class);
        //blockMissileDisplay = manager.newBlock(TileMissileDisplay.class);
        blockLauncherFrame = manager.newBlock("icbmLauncherFrame", TileLauncherFrame.class);
        if (blockDirectSiloController == null)
        {
            blockDirectSiloController = manager.newBlock("icbmDirectSiloConnector", TileSiloController.class);
        }
        blockAntenna = manager.newBlock("icbmAntenna", BlockAntennaParts.class, ItemBlockMetadata.class);
        blockCommandCentral = manager.newBlock("icbmCommandCentral", TileCommandController.class);
        blockCommandSiloConnector = manager.newBlock("icbmCommandSiloController", TileCommandSiloConnector.class);

        //Launchers
        //blockSmallPortableLauncher = manager.newBlock(TileSmallLauncher.class);
        //blockSmallSilo = manager.newBlock(TileSmallSilo.class);
        //blockStandardSilo = manager.newBlock(TileStandardSilo.class);

        //Troll blocks
        blockCake = manager.newBlock("ICBMxFakeCake", BlockFakeCake.class);


        //Clear launcher creative tab to prevent placement by user by mistake
        NEIProxy.hideItem(blockCrashMissile);

        // Debug Only blocks
        if (Engine.runningAsDev)
        {
            blockExplosiveMarker = manager.newBlock(BlockExplosiveMarker.class, ItemBlockMetadata.class);
        }

        // ITEMS

        if (Loader.isModLoaded("assemblyline"))
        {
            itemMissileCart = manager.newItem("icbmMissileCart", ItemMissileCart.class);
        }
        itemMissile = manager.newItem("missile", ItemMissile.class);
        itemRocketLauncher = manager.newItem("rocketLauncher", ItemRocketLauncher.class);
        itemEngineModules = manager.newItem("engineModules", ItemEngineModules.class);
        itemGuidanceModules = manager.newItem("guidanceModules", ItemGuidanceModules.class);
        itemMissileParts = manager.newItem("missileParts", ItemMissileParts.class);
        itemExplosive = manager.newItem("explosiveUnit", ItemExplosive.class);
        itemExplosivePart = manager.newItem("explosiveUnitParts", ItemExplosiveParts.class);
        itemTrigger = manager.newItem("icbmTriggers", ItemTriggerModules.class);
        NEIProxy.hideItem(ItemExplosive.ExplosiveItems.NBT.newItem());

        // Register modules, need to do this or they will not build from ItemStacks
        WarheadCasings.register();
        Engines.register();
        GuidanceModules.register();
        Triggers.register();

        //Set tab item last so to avoid NPE
        CREATIVE_TAB.itemStack = new ItemStack(InventoryUtility.getItem("icbm:icbmRemoteDet"));
        warheadsTab.itemStack = new ItemStack(blockWarhead);
        explosiveTab.itemStack = ItemExplosiveParts.ExplosiveParts.GUNPOWDER_CHARGE.newItem();
        getProxy().registerExplosives();
    }

    /**
     * Registers the explosives. Under normal runtime never call
     * this method outside of the ICBM.class.
     */
    public static void registerExplosives()
    {
        if (!registerExplosives)
        {
            registerExplosives = true;
            //Create Explosives TODO move to JSON
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "EntitySpawn", new ExSpawn());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "ExoThermic", new ExExoThermic());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "EndoThermic", new ExEndoThermic());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Fragment", new ExFragment());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "ArrowFragment", ExplosiveRegistry.get("Fragment"));
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Antimatter", new ExAntimatter());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "FireBomb", new ExFireBomb());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "FlashFire", new ExFlashFire());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "EnderBlocks", new ExEnderBlocks());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "TorchEater", new ExTorchEater());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "AntiPlant", new ExAntiPlant());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "PlantLife", new ExPlantLife());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Regen", new ExRegen());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "RegenLocal", new ExRegenLocal());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "MicroQuake", new ExMicroQuake());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Cake", new ExCake());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "BiomeChange", new ExBiomeChange());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "OrePuller", new ExOrePuller());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "SlimeRain", new ExSlimeRain());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Emp", new ExEmp());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Gravity", new ExGravity());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Microwave", new ExMicrowave());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Nuke", new ExNuke());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Flash", new ExFlash());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Radiation", new ExRadiation());
            ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "MidasOre", new BlastMidasOre.ExMidasOre());
            if (Engine.runningAsDev)
            {
                ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "SimplePathTest1", new ExplosiveHandlerGeneric("SimplePathTest1", BlastPathTester.class, 1));
                ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "SimplePathTest2", new ExplosiveHandlerGeneric("SimplePathTest2", BlastPathTester.class, 2));
                ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "SimplePathTest3", new ExplosiveHandlerGeneric("SimplePathTest3", BlastPathTester.class, 3));
                ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "SimplePathTest10", new ExplosiveHandlerGeneric("SimplePathTest10", BlastPathTester.class, 10));
                ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "SimplePathTest20", new ExplosiveHandlerGeneric("SimplePathTest20", BlastPathTester.class, 20));
            }
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        EntityTypeCheckRegistry.register(new EntityTypeCheckMissile());

        //Register Entities
        EntityRegistry.registerModEntity(EntityMissile.class, "ICBMMissile", getConfig().getInt("missile", "entityIDs", ENTITY_ID_PREFIX + 3, 0, 1000, "Internal mod entity ID to use"), this, 500, 1, true);
        EntityRegistry.registerModEntity(EntityFragment.class, "ICBMFragment", getConfig().getInt("fragments", "entityIDs", ENTITY_ID_PREFIX + 5, 0, 1000, "Internal mod entity ID to use"), this, 500, 1, true);
        EntityRegistry.registerModEntity(EntityMissileCart.class, "ICBMMissileCart", getConfig().getInt("missile_cart", "entityIDs", ENTITY_ID_PREFIX + 5, 0, 1000, "Internal mod entity ID to use"), this, 500, 1, true);
        EntityRegistry.registerModEntity(EntitySlimeRain.class, "ICBMSlime", getConfig().getInt("slime", "entityIDs", ENTITY_ID_PREFIX + 6, 0, 1000, "Internal mod entity ID to use"), this, 500, 1, true);

        if (getConfig().getBoolean("registerEntitiesGlobally", "legacy", false, "Only enable this if there are issues loading older worlds containing entities from this mod."))
        {
            EntityRegistry.registerGlobalEntityID(EntityMissile.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerGlobalEntityID(EntityFragment.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerGlobalEntityID(EntityMissileCart.class, "ICBMMissileCart", EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerGlobalEntityID(EntitySlimeRain.class, "ICBMSlime", EntityRegistry.findGlobalUniqueEntityId());
        }
        super.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        /** LOAD. */
        ArrayList dustCharcoal = OreDictionary.getOres("dustCharcoal");
        ArrayList dustCoal = OreDictionary.getOres("dustCoal");
        // Sulfur
        if (getConfig().getBoolean("Charcoal_gunpowder_recipe", "Extra", true, "Enables a dust recipe of sulfur, saltpeter, and charcoal to make gunpowder"))
        {
            //TODO see about ore dictionary for coal
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSulfur", "dustSaltpeter", Items.coal));
        }
        if (getConfig().getBoolean("Charcoal_gunpowder_recipe", "Extra", true, "Enables a recipe of sulfur, saltpeter, and charcoal to make gunpowder"))
        {
            //TODO see about ore dictionary for charcoal
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSulfur", "dustSaltpeter", new ItemStack(Items.coal, 1, 1)));
        }
        if (getConfig().getBoolean("Charcoal_dust_gunpowder_recipe", "Extra", true, "Enables a recipe of sulfur, saltpeter, and charcoal to make gunpowder") && dustCharcoal != null && dustCharcoal.size() > 0)
        {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSulfur", "dustSaltpeter", "dustCharcoal"));
        }
        if (getConfig().getBoolean("Coal_dust_gunpowder_recipe", "Extra", true, "Enables a dust recipe of sulfur, saltpeter, and coal to make gunpowder") && dustCoal != null && dustCoal.size() > 0)
        {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSulfur", "dustSaltpeter", "dustCoal"));
        }
        if (getConfig().getBoolean("Redstone_TNT_recipe", "Extra", true, "Enables a cheaper/easier tnt recipe using gunpowder surrounding a redstone dust. This recipe is designed to make it easier to craft tnt without mining a lot of material."))
        {
            //TODO see about ore dictionary for redstone, and gunpowder
            GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.tnt, "@@@", "@R@", "@@@", '@', Items.gunpowder, 'R', Items.redstone));
        }

        //Sheet metal crafting recipes
        if (Engine.itemSheetMetal != null && Engine.itemSheetMetalTools != null)
        {
            GameRegistry.addRecipe(new RecipeSheetMetal(MissileCraftingParts.SMALL_MISSILE_CASE.stack(),
                    "CRC",
                    " H ",
                    'C', ItemSheetMetal.SheetMetal.CYLINDER.stack(),
                    'R', ItemSheetMetal.SheetMetal.RIVETS.stack(),
                    'H', Engine.itemSheetMetalTools.getHammer()));
        }
        else
        {
            logger().error("In order to craft the missile casing you need to enable sheet metal tools and parts in Voltz Engine");
        }
        super.postInit(event);
    }

    @Override
    public CommonProxy getProxy()
    {
        return proxy;
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // Setup command
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandICBM());

    }

    @SubscribeEvent
    public void onWorldTickEnd(TickEvent.WorldTickEvent evt)
    {
        if (evt.side == Side.SERVER && evt.phase == TickEvent.Phase.END)
        {
            MissileTracker tracker = MissileTracker.getTrackerForWorld(evt.world);
            if (tracker != null)
            {
                tracker.update(evt.world);
            }
        }
    }
}
