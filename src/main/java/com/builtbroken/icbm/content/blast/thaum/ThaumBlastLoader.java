package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.mod.loadable.AbstractLoadable;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.explosive.ExplosiveHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads thaumcraft support
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2015.
 */
public class ThaumBlastLoader extends AbstractLoadable
{
    private static Map<String, ResearchPage> researchPages = new HashMap();

    @Override
    public void init()
    {
        super.init();
        try
        {
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumTaint", new ExplosiveHandler("ThaumTaint", BlastTaint.class, 2));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumTaintBottle", new ExplosiveHandler("ThaumTaintBottle", BlastTaintBottle.class, 5));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumNode", new ExplosiveHandlerNode());
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ThaumJar", new ExplosiveHandler("ThaumJar", BlastNodeJar.class, 2));
        }
        catch (Exception e)
        {
            ICBM.INSTANCE.logger().error("Failed to load thaumcraft support", e);
        }

        ResearchCategories.registerCategory(ICBM.NAME, new ResourceLocation(ICBM.DOMAIN, "textures/blocks/warhead.micro.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));

    }

    @Override
    public void postInit()
    {
        super.postInit();
        taintWarheadRecipes();
        taintBottleWarheadRecipes();
        jarWarheadRecipes();
        nodeWarheadRecipes();
    }

    private void taintBottleWarheadRecipes()
    {
        ItemStack taintBottleMicroMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, ExplosiveRegistry.get("ThaumTaintBottle")).toStack();
        ItemStack taintBottleSmallMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, ExplosiveRegistry.get("ThaumTaintBottle")).toStack();
        ItemStack micro_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, (ItemStack)null).toStack();
        ItemStack small_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, (ItemStack)null).toStack();

        IArcaneRecipe taintBottleMicroMissileRecipe = ThaumcraftApi.addArcaneCraftingRecipe("MicroTaintBottleWarhead", taintBottleMicroMissile,
                new AspectList().add(Aspect.ORDER, 25).add(Aspect.FIRE, 25),
                "I",
                "M",
                "I",
                'I', ConfigItems.itemBottleTaint,
                'M', micro_warhead_empty);

        IArcaneRecipe taintBottleSmallMissileRecipe = ThaumcraftApi.addArcaneCraftingRecipe("SmallTaintBottleWarhead", taintBottleSmallMissile,
                new AspectList().add(Aspect.ORDER, 25).add(Aspect.FIRE, 25),
                " I ",
                "IMI",
                " I ",
                'I', ConfigItems.itemBottleTaint,
                'M', small_warhead_empty);

        AspectList aspects = new AspectList();
        aspects.add(Aspect.TOOL, 1).add(Aspect.MECHANISM, 2).add(Aspect.TRAVEL, 1).add(Aspect.TAINT, 5);

        ResearchItem taintBottleMissilePage = new ResearchItemICBM("BottledTaintWarhead", ICBM.NAME, aspects, 0, 2, 3, taintBottleMicroMissile);
        taintBottleMissilePage.setPages(new ResearchPage[]{getResearchPage("BottledTaintWarhead"), new ResearchPage(taintBottleMicroMissileRecipe), new ResearchPage(taintBottleSmallMissileRecipe)})
                .setParents("BOTTLETAINT", "ENTROPICPROCESSING", "TaintWarhead")
                .registerResearchItem();
    }

    private void jarWarheadRecipes()
    {
        ///Taint Warhead
        ItemStack jarMicroMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, ExplosiveRegistry.get("ThaumJar")).toStack();
        ItemStack jarSmallMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, ExplosiveRegistry.get("ThaumJar")).toStack();
        ItemStack micro_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, (ItemStack)null).toStack();
        ItemStack small_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, (ItemStack)null).toStack();

        AspectList list = new AspectList().add(Aspect.ORDER, 75).add(Aspect.FIRE, 75).add(Aspect.EARTH, 75).add(Aspect.WATER, 75).add(Aspect.AIR, 75).add(Aspect.ENTROPY, 75);
        IArcaneRecipe taintMicroMissileRecipe = ThaumcraftApi.addArcaneCraftingRecipe("JarWarhead", jarMicroMissile,
                list,
                "J",
                "M",
                'J', new ItemStack(ConfigBlocks.blockJar),
                'M', micro_warhead_empty);

        IArcaneRecipe taintSmallMissileRecipe = ThaumcraftApi.addArcaneCraftingRecipe("JarWarhead", jarSmallMissile,
                list,
                "J",
                "M",
                'J', new ItemStack(ConfigBlocks.blockJar),
                'M', small_warhead_empty);

        AspectList aspects = new AspectList();
        aspects.add(Aspect.TOOL, 1).add(Aspect.MECHANISM, 2).add(Aspect.TRAVEL, 1);

        ResearchItem taintMissilePage = new ResearchItemICBM("JarWarhead", ICBM.NAME, aspects, -1, 4, 3, jarMicroMissile);
        taintMissilePage.setPages(new ResearchPage[]{getResearchPage("JarWarhead"), new ResearchPage(taintMicroMissileRecipe), new ResearchPage(taintSmallMissileRecipe)})
                .setParents("NODEJAR")
                .registerResearchItem();
    }

    private void nodeWarheadRecipes()
    {
        ItemStack micro_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, (ItemStack)null).toStack();
        ItemStack small_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, (ItemStack)null).toStack();
        ItemStack taintBottleMicroMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, ExplosiveRegistry.get("ThaumNode")).toStack();

        AspectList aspects = new AspectList();
        aspects.add(Aspect.TOOL, 1).add(Aspect.MECHANISM, 2).add(Aspect.TRAVEL, 1);

        InfusionRecipeJar recipeJar = new InfusionRecipeJar("NodeWarhead", 0, new AspectList().add(Aspect.ORDER, 25).add(Aspect.FIRE, 25), new ItemStack[]{micro_warhead_empty});
        InfusionRecipeJar recipeJar1 = new InfusionRecipeJar("NodeWarhead", 0, new AspectList().add(Aspect.ORDER, 25).add(Aspect.FIRE, 25), new ItemStack[]{small_warhead_empty});

        ResearchItem researchPage = new ResearchItemICBM("NodeWarhead", ICBM.NAME, aspects, -3, 4, 3, taintBottleMicroMissile);
        researchPage.setPages(new ResearchPage[]{getResearchPage("NodeWarhead"), new ResearchPage(recipeJar), new ResearchPage(recipeJar1)})
                .setParents("JarWarhead")
                .registerResearchItem();
    }

    private void taintWarheadRecipes()
    {
        ///Taint Warhead
        ItemStack taintMicroMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, ExplosiveRegistry.get("ThaumTaint")).toStack();
        ItemStack taintSmallMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, ExplosiveRegistry.get("ThaumTaint")).toStack();
        ItemStack micro_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, (ItemStack)null).toStack();
        ItemStack small_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, (ItemStack)null).toStack();

        IArcaneRecipe taintMicroMissileRecipe = ThaumcraftApi.addArcaneCraftingRecipe("TaintWarhead", taintMicroMissile,
                new AspectList().add(Aspect.ORDER, 25).add(Aspect.FIRE, 25),
                "I",
                "M",
                "I",
                'I', new ItemStack(ConfigItems.itemResource, 1, 11),
                'M', micro_warhead_empty);

        IArcaneRecipe taintSmallMissileRecipe = ThaumcraftApi.addArcaneCraftingRecipe("TaintWarhead", taintSmallMissile,
                new AspectList().add(Aspect.ORDER, 25).add(Aspect.FIRE, 25),
                " I ",
                "IMI",
                " I ",
                'I', new ItemStack(ConfigItems.itemResource, 1, 11),
                'M', small_warhead_empty);

        AspectList aspects = new AspectList();
        aspects.add(Aspect.TOOL, 1).add(Aspect.MECHANISM, 2).add(Aspect.TRAVEL, 1).add(Aspect.TAINT, 2);

        ResearchItem taintMissilePage = new ResearchItemICBM("TaintWarhead", ICBM.NAME, aspects, 0, 0, 3, taintMicroMissile);
        taintMissilePage.setPages(new ResearchPage[]{getResearchPage("TaintWarhead"), new ResearchPage(taintMicroMissileRecipe), new ResearchPage(taintSmallMissileRecipe)})
                .setParents("ENTROPICPROCESSING")
                .registerResearchItem();
    }

    private static ResearchPage createResearchPage(String key, int pageNum)
    {
        return new ResearchPage(LanguageUtility.getLocal(String.format("thaumcraft.research.%s.page.%d", key, pageNum)).replace("\n", "<BR>").replace("---", "<LINE>").replace("{img}", "<IMG>").replace("{/img}", "</IMG>"));
    }

    public static ResearchPage getResearchPage(String researchTag)
    {
        ResearchPage page = researchPages.get(researchTag);
        if (page == null)
        {
            page = createResearchPage(researchTag, 1);
            researchPages.put(researchTag, page);
        }
        return page;
    }
}
