package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.warhead.TileWarhead;
import com.builtbroken.icbm.content.warhead.WarheadRecipe;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.mod.loadable.AbstractLoadable;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.explosive.ExplosiveHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
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
        ItemStack micro_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, null).toStack();
        ItemStack small_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, null).toStack();

        TileWarhead.addMicroWarheadRecipe("ThaumTaintBottle", ConfigItems.itemBottleTaint, ConfigItems.itemBottleTaint);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "ThaumTaintBottle", ConfigItems.itemBottleTaint, ConfigItems.itemBottleTaint, ConfigItems.itemBottleTaint, ConfigItems.itemBottleTaint, small_warhead_empty));


        ItemStack taintMicroMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, ExplosiveRegistry.get("ThaumTaint")).toStack();
        ItemStack taintSmallMissile = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, ExplosiveRegistry.get("ThaumTaint")).toStack();

        IArcaneRecipe taintMicroMissileRecipe = ThaumcraftApi.addArcaneCraftingRecipe("MicroTaintWarhead", taintMicroMissile,
                new AspectList().add(Aspect.ORDER, 25).add(Aspect.FIRE, 25),
                "I",
                "M",
                "I",
                'I', new ItemStack(ConfigItems.itemResource, 1, 11),
                'R', micro_warhead_empty);

        IArcaneRecipe taintSmallMissileRecipe = ThaumcraftApi.addArcaneCraftingRecipe("SmallTaintWarhead", taintSmallMissile,
                new AspectList().add(Aspect.ORDER, 25).add(Aspect.FIRE, 25),
                " I ",
                "IMI",
                " I ",
                'I', new ItemStack(ConfigItems.itemResource, 1, 11),
                'R', small_warhead_empty);


        AspectList aspects = new AspectList();
        aspects.add(Aspect.TOOL, 1).add(Aspect.MECHANISM, 2).add(Aspect.TRAVEL, 1);

        ResearchItem taintMicroMissilePage = new ResearchItemICBM("MicroTaintWarhead", ICBM.NAME, aspects, 0, 0, 3, taintMicroMissile);
        taintMicroMissilePage.setPages(new ResearchPage[]{getResearchPage("MicroTaintWarhead"), new ResearchPage(taintMicroMissileRecipe)})
                .setParentsHidden("THAUMIUM")
                .registerResearchItem();

        ResearchItem taintSmallMissilePage = new ResearchItemICBM("SmallTaintWarhead", ICBM.NAME, aspects, 0, 1, 3, taintSmallMissile);
        taintSmallMissilePage.setPages(new ResearchPage[]{getResearchPage("SmallTaintWarhead"), new ResearchPage(taintSmallMissileRecipe)})
                .setParentsHidden("THAUMIUM")
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
