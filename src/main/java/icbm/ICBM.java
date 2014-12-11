package icbm;

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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import icbm.content.BlockExplosiveMarker;
import resonant.lib.world.explosive.ExplosiveItemUtility;
import icbm.content.missile.EntityMissile;
import icbm.content.missile.ItemMissile;
import icbm.content.rocketlauncher.ItemRocketLauncher;
import icbm.content.warhead.TileExplosive;
import resonant.lib.world.explosive.ExplosiveRegistry;
import icbm.content.blast.BlastBasic;
import icbm.content.blast.BlastInvert;
import resonant.lib.world.explosive.Explosive;
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
import org.modstats.ModstatInfo;
import org.modstats.Modstats;
import resonant.api.explosive.IExplosive;
import resonant.lib.prefab.tile.item.ItemBlockMetadata;
import resonant.engine.ResonantEngine;
import resonant.lib.mod.AbstractMod;
import resonant.lib.mod.AbstractProxy;
import resonant.lib.mod.ModCreativeTab;
import resonant.lib.mod.config.Config;

import java.util.ArrayList;

/**
 * Main class for ICBM core to profile on. The core will need to be initialized by each ICBM module.
 *
 * @author Calclavia
 */
@Mod(modid = ICBM.DOMAIN, name = ICBM.NAME, version = ICBM.VERSION, dependencies = "required-after:ResonantEngine")
@ModstatInfo(prefix = "icbm", name = ICBM.NAME, version = ICBM.VERSION)
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

    @SidedProxy(clientSide = "icbm.ClientProxy", serverSide = "icbm.CommonProxy")
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

    // Items
    public static Item itemMissile;
    public static Item itemRocketLauncher;
    public final ModCreativeTab CREATIVE_TAB;

    public ICBM()
    {
        super(DOMAIN);
        CREATIVE_TAB = new ModCreativeTab("ICBM");
        CREATIVE_TAB.itemSorter = new ModCreativeTab.NameSorter()
        {
            @Override
            public String getLabel(ItemStack stack)
            {
                IExplosive ex = ExplosiveItemUtility.getExplosive(stack);
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
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        Modstats.instance().getReporter().registerMod(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(proxy);

        // Blocks
        blockExplosive = manager.newBlock(TileExplosive.class);
        if (ResonantEngine.runningAsDev)
            blockExplosiveMarker = manager.newBlock(BlockExplosiveMarker.class, ItemBlockMetadata.class);

        // ITEMS
        itemMissile = manager.newItem(ItemMissile.class);
        itemRocketLauncher = manager.newItem(ItemRocketLauncher.class);

        //Explosives
        ExplosiveRegistry.registerOrGetExplosive(NAME,"testx01", new Explosive("testx01", BlastBasic.class));
        ExplosiveRegistry.registerOrGetExplosive(NAME, "testx05", new Explosive("testx05", BlastBasic.class, 5));
        ExplosiveRegistry.registerOrGetExplosive(NAME, "testx10", new Explosive("testx10", BlastBasic.class, 10));
        ExplosiveRegistry.registerOrGetExplosive(NAME, "testx50", new Explosive("testx50", BlastBasic.class, 50));

        ExplosiveRegistry.registerOrGetExplosive(NAME," test_invertedx01", new Explosive("test_invertedx01", BlastInvert.class));

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
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

}