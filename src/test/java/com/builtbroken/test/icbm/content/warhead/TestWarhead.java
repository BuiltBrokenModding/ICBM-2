package com.builtbroken.test.icbm.content.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.warhead.Warhead;
import com.builtbroken.icbm.content.missile.parts.warhead.WarheadCasings;
import com.builtbroken.icbm.content.items.ItemExplosive;
import com.builtbroken.icbm.content.warhead.TileWarhead;
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
        ICBM_API.blockWarhead = this.block;
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
        ICBM_API.itemExplosive = TestExplosiveItem.item;
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
        ICBM_API.blockWarhead = null;
        ICBM_API.itemExplosive = null;
    }

    @Test
    public void testWarheadCasings()
    {
        for (WarheadCasings casing : WarheadCasings.values())
        {
            Warhead warhead = MissileModuleBuilder.INSTANCE.buildWarhead(casing, (ItemStack)null);

            //Test default casing creation & init values
            assertNotNull(warhead);
            assertNotNull(warhead.toStack());
            assertNotNull(warhead.toStack().getItem());
            assertNull(warhead.getExplosive());
            assertNull(warhead.explosive);
            assertTrue(warhead.getAdditionalExplosiveData() == null || warhead.getAdditionalExplosiveData().hasNoTags());

            //Test warhead creation with explosive items
            for (ItemExplosive.ExplosiveItems exItem : ItemExplosive.ExplosiveItems.values())
            {
                if (exItem.ex_name != null)
                {
                    assertTrue(exItem.ex_name, exItem.getExplosive() != null);
                    warhead = MissileModuleBuilder.INSTANCE.buildWarhead(casing, exItem.newItem());
                    assertNotNull(warhead);
                    assertNotNull(warhead.toStack());
                    assertNotNull(warhead.toStack().getItem());
                    assertEquals(warhead.getExplosive(), exItem.getExplosive());
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
    }
}
