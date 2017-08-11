package com.builtbroken.test.icbm.content.crafting;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.items.ItemExplosive;
import com.builtbroken.mc.core.content.blast.tnt.ExplosiveHandlerTNT;
import com.builtbroken.mc.framework.explosive.ExplosiveRegistry;
import com.builtbroken.mc.lib.data.item.ItemStackWrapper;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestExplosiveItem extends AbstractTest
{
    public static ItemExplosive item;

    @Override
    public void setUpForEntireClass()
    {
        if(item == null)
        {
            item = new ItemExplosive();
            GameRegistry.registerItem(item, "testExplosiveItemII");
        }
        ICBM_API.itemExplosive = item;
        ICBM.registerExplosives();
    }

    @Override
    public void tearDownForEntireClass()
    {
        ICBM_API.itemExplosive = null;
    }

    @Test
    public void testNewItem()
    {
        for (ItemExplosive.ExplosiveItems item : ItemExplosive.ExplosiveItems.values())
        {
            if (item.ex_name != null && item != ItemExplosive.ExplosiveItems.EMP)
            {
                ItemStack stack = item.newItem();
                assertTrue(stack != null);
                assertTrue(stack.getItem() == this.item);
            }
        }
    }

    @Test
    public void testExplosiveRegistry()
    {
        for (ItemExplosive.ExplosiveItems item : ItemExplosive.ExplosiveItems.values())
        {
            if (item.ex_name != null && item != ItemExplosive.ExplosiveItems.EMP)
            {
                ItemStack stack = item.newItem();

                //Cleanup
                ExplosiveRegistry.unregisterExplosiveItem(stack);

                //Test explosive handler
                ExplosiveRegistry.registerExplosive("mod", item.ex_name, new ExplosiveHandlerTNT());
                assertNotNull(item.ex_name, item.getExplosive());


                ExplosiveRegistry.registerExplosiveItem(item.newItem());


                assertNotNull(item.ex_name, ExplosiveRegistry.get(stack));
                assertTrue(item.ex_name, ExplosiveRegistry.getItems(item.getExplosive()).contains(new ItemStackWrapper(stack)));

                ExplosiveRegistry.unregisterExplosiveItem(stack);
                ExplosiveRegistry.unregisterExplosive(item.ex_name);
            }
        }
    }
}
