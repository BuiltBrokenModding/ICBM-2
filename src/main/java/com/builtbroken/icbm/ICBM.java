package com.builtbroken.icbm;

import com.builtbroken.icbm.content.blast.BlastEndoThermic;
import com.builtbroken.icbm.content.blast.BlastExoThermic;
import com.builtbroken.icbm.content.blast.entity.BlastSnowman;
import com.builtbroken.icbm.content.blast.fragment.BlastFragment;
import com.builtbroken.icbm.content.crafting.missile.ItemMissileModules;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.display.TileMissile;
import com.builtbroken.icbm.content.display.TileMissileDisplay;
import com.builtbroken.icbm.content.launcher.TileRotationTest;
import com.builtbroken.icbm.content.launcher.TileSmallLauncher;
import com.builtbroken.icbm.content.missile.MissileTracker;
import com.builtbroken.icbm.content.warhead.TileWarhead;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.builtbroken.mc.lib.mod.config.Config;
import com.builtbroken.mc.prefab.explosive.ExplosiveHandler;
import com.builtbroken.mc.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import com.builtbroken.icbm.content.BlockExplosiveMarker;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.icbm.content.rocketlauncher.ItemRocketLauncher;
import com.builtbroken.mc.prefab.explosive.blast.BlastBasic;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Main class for ICBM core to profile on. The core will need to be initialized by each ICBM module.
 *
 * @author Calclavia
 */
@Mod(modid = ICBM.DOMAIN, name = ICBM.NAME, version = ICBM.VERSION, dependencies = "required-after:VoltzEngine")
public final class ICBM extends AbstractMod
{
    /** Name of the channel and mod ID. */
    public static final String NAME = "ICBM";
    public static final String DOMAIN = "icbm";
    public static final String PREFIX = DOMAIN + ":";

    /** The version of ICBM. */
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    public static final String ASSETS_PATH = "/assets/icbm/";
    public static final String TEXTURE_PATH = "textures/";
    public static final String GUI_PATH = TEXTURE_PATH + "gui/";
    public static final String MODEL_PREFIX = "models/";
    public static final String MODEL_DIRECTORY = ASSETS_PATH + MODEL_PREFIX;

    public static final String MODEL_TEXTURE_PATH = TEXTURE_PATH + MODEL_PREFIX;
    public static final String BLOCK_PATH = TEXTURE_PATH + "blocks/";
    public static final String ITEM_PATH = TEXTURE_PATH + "items/";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Instance(DOMAIN)
    public static ICBM INSTANCE;

    @Metadata(DOMAIN)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = "com.builtbroken.icbm.ClientProxy", serverSide = "com.builtbroken.icbm.CommonProxy")
    public static CommonProxy proxy;

    @Config(key = "Creepers_Drop_Sulfur", category = "Extras")
    public static boolean CREEPER_DROP_SULFER = true;
    @Config(key = "Creepers_Blow_up_in_Fire", category = "Extras")
    public static boolean CREEPER_BLOW_UP_IN_FIRE = true;

    @Config(key = "EntityIDStart", category = "Extras")
    public static int ENTITY_ID_PREFIX = 50;

    // Blocks
    public static Block blockExplosive;
    public static Block blockExplosiveMarker;
    public static Block blockMissileDisplay;
    public static Block blockMissile;
    public static Block blockSmallLauncher;

    // Items
    public static Item itemMissile;
    public static Item itemRocketLauncher;
    public static ItemMissileModules itemMissileModules;

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
                    return stack.getDisplayName() + ex.getID();
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

        // Blocks
        blockExplosive = manager.newBlock(TileWarhead.class);
        blockMissileDisplay = manager.newBlock(TileMissileDisplay.class);
        blockMissile = manager.newBlock(TileMissile.class);
        blockSmallLauncher = manager.newBlock(TileSmallLauncher.class);
        if (Engine.runningAsDev)
        {
            blockExplosiveMarker = manager.newBlock(BlockExplosiveMarker.class, ItemBlockMetadata.class);
            manager.newBlock(TileRotationTest.class);
        }

        // ITEMS
        itemMissile = manager.newItem(ItemMissile.class);
        itemRocketLauncher = manager.newItem(ItemRocketLauncher.class);
        itemMissileModules = manager.newItem(ItemMissileModules.class);

        MissileCasings.register();
        WarheadCasings.register();
        Engines.register();

        CREATIVE_TAB.itemStack = new ItemStack(itemMissile);

        Engine.heatedRockRequested = true;
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        //Explosives
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Snowmen", new ExplosiveHandler("snowmen", BlastSnowman.class, 1));
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "ExoThermic", new ExplosiveHandler("ExoThermic", BlastExoThermic.class, 2));
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "EndoThermic", new ExplosiveHandler("EndoThermic", BlastEndoThermic.class, 2));
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "ArrowFragment", new ExplosiveHandler("ArrowFragment", BlastFragment.class, 2));

        //Entities
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
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[]{"dustSulfur", "dustSaltpeter", Items.coal}));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[]{"dustSulfur", "dustSaltpeter", new ItemStack(Items.coal, 1, 1)}));

        if (dustCharcoal != null && dustCharcoal.size() > 0)
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[]{"dustSulfur", "dustSaltpeter", "dustCharcoal"}));
        if (dustCoal != null && dustCoal.size() > 0)
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[]{"dustSulfur", "dustSaltpeter", "dustCoal"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.tnt, new Object[]{"@@@", "@R@", "@@@", '@', Items.gunpowder, 'R', Items.redstone}));
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
        if(evt.side == Side.SERVER && evt.phase == TickEvent.Phase.END)
        {
            MissileTracker tracker = MissileTracker.getTrackerForWorld(evt.world);
            if(tracker != null)
            {
                tracker.update(evt.world);
            }
        }
    }
}