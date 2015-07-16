package com.builtbroken.icbm;

import com.builtbroken.icbm.content.debug.BlockExplosiveMarker;
import com.builtbroken.icbm.content.blast.BlastEndoThermic;
import com.builtbroken.icbm.content.blast.BlastExoThermic;
import com.builtbroken.icbm.content.blast.entity.BlastSnowman;
import com.builtbroken.icbm.content.blast.explosive.BlastAntimatter;
import com.builtbroken.icbm.content.blast.fragment.BlastFragment;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.icbm.content.crafting.missile.engine.ItemEngineModules;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.crafting.station.TileMissileWorkstation;
import com.builtbroken.icbm.content.display.TileMissile;
import com.builtbroken.icbm.content.display.TileMissileDisplay;
import com.builtbroken.icbm.content.debug.TileRotationTest;
import com.builtbroken.icbm.content.launcher.controller.TileController;
import com.builtbroken.icbm.content.launcher.items.ItemGPSFlag;
import com.builtbroken.icbm.content.launcher.items.ItemLinkTool;
import com.builtbroken.icbm.content.launcher.launcher.TileSmallLauncher;
import com.builtbroken.icbm.content.launcher.silo.TileSmallSilo;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.icbm.content.missile.tracking.MissileTracker;
import com.builtbroken.icbm.content.rocketlauncher.ItemRocketLauncher;
import com.builtbroken.icbm.content.warhead.TileWarhead;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.builtbroken.mc.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.explosive.ExplosiveHandler;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.common.FMLCommonHandler;
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
    //public static final String ENGINE_VERSION =  "@MAJOR@.@MINOR@.@REVIS@.@BUILD@"; TODO get version working
    public static final String DEPENDENCIES = "required-after:VoltzEngine";


    @Instance(DOMAIN)
    public static ICBM INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.icbm.ClientProxy", serverSide = "com.builtbroken.icbm.CommonProxy")
    public static CommonProxy proxy;


    public static boolean ANTIMATTER_BREAK_UNBREAKABLE = true;
    public static boolean DEBUG_MISSILE_MANAGER = false;

    public static float missile_firing_volume = 1f;

    public static int ENTITY_ID_PREFIX = 50;

    // Blocks
    public static Block blockWarhead;
    public static Block blockExplosiveMarker;
    public static Block blockMissileDisplay;
    public static Block blockMissile;
    public static Block blockSmallLauncher;
    public static Block blockSmallSilo;
    public static Block blockSiloController;
    public static Block blockMissileWorkstation;

    // Items
    public static Item itemMissile;
    public static Item itemRocketLauncher;
    public static Item itemLinkTool;
    public static Item itemGPSTool;
    public static ItemEngineModules itemEngineModules;

    public final ModCreativeTab CREATIVE_TAB;

    public ICBM()
    {
        super(DOMAIN, "ICBM");
        CREATIVE_TAB = new ModCreativeTab("ICBM");
        CREATIVE_TAB.itemSorter = new ModCreativeTab.NameSorter()
        {
            @Override
            public String getLabel(ItemStack stack)
            {
                IExplosiveHandler ex = ExplosiveItemUtility.getExplosive(stack);
                if (ex != null)
                {
                    return ex.getID() + stack.getDisplayName();
                }
                return stack.getDisplayName();
            }
        };
        super.manager.setTab(CREATIVE_TAB);
    }


    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(proxy);
        FMLCommonHandler.instance().bus().register(this);

        //Request Engine to load items for use
        Engine.heatedRockRequested = true;

        // Configs TODO load up using config system, and separate file
        ANTIMATTER_BREAK_UNBREAKABLE = getConfig().getBoolean("Antimatter_Destroy_Unbreakable", Configuration.CATEGORY_GENERAL, true, "Allows antimatter to break blocks that are unbreakable, bedrock for example.");
        DEBUG_MISSILE_MANAGER = getConfig().getBoolean("Missile_Manager", "Debug", Engine.runningAsDev, "Adds additional info to the console");
        missile_firing_volume = getConfig().getFloat("missile_firing_volume", "volume", 1.0F, 0, 4, "How loud the missile is when fired from launchers");


        // Functional Blocks
        blockWarhead = manager.newBlock(TileWarhead.class);
        blockMissileDisplay = manager.newBlock(TileMissileDisplay.class);
        blockSmallLauncher = manager.newBlock(TileSmallLauncher.class);
        blockSmallSilo = manager.newBlock(TileSmallSilo.class);
        blockMissileWorkstation = manager.newBlock(TileMissileWorkstation.class);
        blockSiloController = manager.newBlock("SiloController", TileController.class);

        // Decor Blocks
        blockMissile = manager.newBlock(TileMissile.class);

        // Debug Only blocks
        if (Engine.runningAsDev)
        {
            blockExplosiveMarker = manager.newBlock(BlockExplosiveMarker.class, ItemBlockMetadata.class);
            manager.newBlock(TileRotationTest.class);
        }

        // ITEMS
        itemMissile = manager.newItem(ItemMissile.class);
        itemRocketLauncher = manager.newItem(ItemRocketLauncher.class);
        itemEngineModules = manager.newItem(ItemEngineModules.class);
        itemLinkTool = manager.newItem("siloLinker", ItemLinkTool.class);
        itemGPSTool = manager.newItem("gpsFlag", ItemGPSFlag.class);

        // Register modules, need to do this or they will not build from ItemStacks
        MissileCasings.register();
        WarheadCasings.register();
        Engines.register();

        //Set tab item last so to avoid NPE
        CREATIVE_TAB.itemStack = new ItemStack(itemMissile);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

        //Create Explosives
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Snowmen", new ExplosiveHandler("snowmen", BlastSnowman.class, 1));
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "ExoThermic", new ExplosiveHandler("ExoThermic", BlastExoThermic.class, 2));
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "EndoThermic", new ExplosiveHandler("EndoThermic", BlastEndoThermic.class, 2));
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "ArrowFragment", new ExplosiveHandler("ArrowFragment", BlastFragment.class, 2));
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Antimatter", new ExplosiveHandler("Antimatter", BlastAntimatter.class, 2));

        //Register Entities
        EntityRegistry.registerGlobalEntityID(EntityMissile.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityMissile.class, "ICBMMissile", ENTITY_ID_PREFIX + 3, this, 500, 1, true);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        /** LOAD. */
        ArrayList dustCharcoal = OreDictionary.getOres("dustCharcoal");
        ArrayList dustCoal = OreDictionary.getOres("dustCoal");
        // Sulfur
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSulfur", "dustSaltpeter", Items.coal));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSulfur", "dustSaltpeter", new ItemStack(Items.coal, 1, 1)));

        if (dustCharcoal != null && dustCharcoal.size() > 0)
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSulfur", "dustSaltpeter", "dustCharcoal"));
        if (dustCoal != null && dustCoal.size() > 0)
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), "dustSulfur", "dustSaltpeter", "dustCoal"));

        GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.tnt, "@@@", "@R@", "@@@", '@', Items.gunpowder, 'R', Items.redstone));
    }

    @Override
    public AbstractProxy getProxy()
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
