package com.builtbroken.test.icbm.content.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.parts.ItemExplosive;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.warhead.TileWarhead;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.builtbroken.mc.testing.tile.AbstractTileTest;
import com.builtbroken.test.icbm.content.crafting.TestExplosiveItem;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestWarhead extends AbstractTileTest<TileWarhead>
{
    public TestWarhead() throws IllegalAccessException, InstantiationException
    {
        super("TileTestWarhead", TileWarhead.class);
        ICBM.registerExplosives();
        WarheadCasings.register();
        ICBM.blockWarhead = this.block;
    }

    @Override
    public void setUpForEntireClass()
    {
        super.setUpForEntireClass();

        if (TestExplosiveItem.item == null)
        {
            TestExplosiveItem.item = new ItemExplosive();
            GameRegistry.registerItem(TestExplosiveItem.item, "testExplosiveItemII");
        }
        ICBM.itemExplosive = TestExplosiveItem.item;
        for (ItemExplosive.ExplosiveItems exItem : ItemExplosive.ExplosiveItems.values())
        {
            ExplosiveRegistry.unregisterExplosiveItem(exItem.newItem());
            if (exItem.ex_name != null)
            {
                assertTrue(ExplosiveRegistry.registerExplosiveItem(exItem.newItem()));
                List<ItemStackWrapper> list = ExplosiveRegistry.getItems(exItem.getExplosive());
                assertTrue(list.contains(new ItemStackWrapper(exItem.newItem())));
            }
        }
    }

    @Override
    public void tearDownForEntireClass()
    {
        super.tearDownForEntireClass();
        for (ItemExplosive.ExplosiveItems exItem : ItemExplosive.ExplosiveItems.values())
        {
            ExplosiveRegistry.unregisterExplosiveItem(exItem.newItem());
        }
        ICBM.blockWarhead = null;
        ICBM.itemExplosive = null;
    }

    @Test
    public void testWarheadCasings()
    {
        for (WarheadCasings casing : WarheadCasings.values())
        {
            Warhead warhead = MissileModuleBuilder.INSTANCE.buildWarhead(casing, null);
            assertNotNull(warhead);
            assertNotNull(warhead.toStack());
            assertNotNull(warhead.toStack().getItem());
            assertNull(warhead.ex);
            assertNull(warhead.explosive);
            assertTrue(warhead.additionalExData.hasNoTags());
            for (IExplosiveHandler ex : ExplosiveRegistry.getExplosives())
            {
                warhead = MissileModuleBuilder.INSTANCE.buildWarhead(casing, ex);
                assertNotNull(warhead);
                assertNotNull(warhead.toStack());
                assertNotNull(warhead.toStack().getItem());
                assertEquals(warhead.ex, ex);
            }
            for (ItemExplosive.ExplosiveItems exItem : ItemExplosive.ExplosiveItems.values())
            {
                if (exItem.ex_name != null)
                {
                    assertTrue(exItem.ex_name, exItem.getExplosive() != null);
                    warhead = MissileModuleBuilder.INSTANCE.buildWarhead(casing, exItem.getExplosive());
                    assertNotNull(warhead);
                    assertNotNull(warhead.toStack());
                    assertNotNull(warhead.toStack().getItem());
                    assertEquals(warhead.ex, exItem.getExplosive());
                }
            }
        }
    }

    @Test
    public void testRecipes()
    {
        assertTrue(new ItemStack(block).getItem() != null);

        List<IRecipe> recipes = new ArrayList();
        TileWarhead.getRecipes(recipes);
        for (IRecipe recipe : recipes)
        {
            assertNotNull(recipe);
            assertNotNull(recipe.getRecipeOutput());
        }

        for (ItemExplosive.ExplosiveItems exItem : ItemExplosive.ExplosiveItems.values())
        {
            if (exItem.ex_name != null)
            {
                List<IRecipe> exRecipes = new ArrayList();
                TileWarhead.getRecipes(exItem.getExplosive(), exRecipes);
                assertTrue(exItem.getExplosive().toString(), exRecipes.size() == 4);
            }
        }
    }
}
