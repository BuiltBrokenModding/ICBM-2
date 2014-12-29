package com.builtbroken.icbm;

import com.builtbroken.icbm.content.blast.entity.BlastSnowman;
import com.builtbroken.icbm.content.crafting.ItemMissileModules;
import com.builtbroken.icbm.content.crafting.MissileSizes;
import com.builtbroken.icbm.content.crafting.missile.EnumModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.warhead.TileWarhead;
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
import com.builtbroken.icbm.content.BlockExplosiveMarker;
import resonant.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.icbm.content.rocketlauncher.ItemRocketLauncher;
import resonant.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.icbm.content.blast.explosive.BlastBasic;
import com.builtbroken.icbm.content.blast.explosive.BlastInvert;
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
@ModstatInfo(prefix = "com/builtbroken/icbm", name = ICBM.NAME, version = ICBM.VERSION)
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

    // Items
    public static Item itemMissile;
    public static Item itemRocketLauncher;
    public static ItemMissileModules itemMissileModules;

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
        blockExplosive = manager.newBlock(TileWarhead.class);
        if (ResonantEngine.runningAsDev)
            blockExplosiveMarker = manager.newBlock(BlockExplosiveMarker.class, ItemBlockMetadata.class);

        // ITEMS
        itemMissile = manager.newItem(ItemMissile.class);
        itemRocketLauncher = manager.newItem(ItemRocketLauncher.class);
        itemMissileModules = manager.newItem(ItemMissileModules.class);

        for(MissileSizes size : MissileSizes.values())
        {
            MissileModuleBuilder.INSTANCE.register(DOMAIN, "warhead_"+size.name().toLowerCase(), size.warhead_clazz);
        }

        EnumModule.register();

        CREATIVE_TAB.itemStack = new ItemStack(itemMissile);


    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        //Explosives
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "Snowmen", new Explosive("snowmen", BlastSnowman.class, 1));
        ExplosiveRegistry.registerOrGetExplosive(DOMAIN, "TNT", new Explosive("tnt", BlastBasic.class, 1));

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

}