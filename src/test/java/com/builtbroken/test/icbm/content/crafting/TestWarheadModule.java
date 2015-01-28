package com.builtbroken.test.icbm.content.crafting;

import com.builtbroken.mc.prefab.explosive.BlastBasic;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadMicro;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.lib.world.explosive.Explosive;
import com.builtbroken.mc.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import junit.framework.TestCase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/** Unit test for the warhead module class
 * Created by robert on 12/29/2014.
 */
public class TestWarheadModule extends TestCase
{
    private static boolean init = false;
    private static IExplosive ex = null;

    //Test to see if the warhead creates itself correctly
    public void testCreation()
    {
        Warhead warhead = createWarhead();
        assertEquals(true, warhead != null);
        assertEquals(true, warhead.toStack() != null);
    }

    //Creates the warhead
    private Warhead createWarhead()
    {
        ItemStack stack = new ItemStack((Block)null);
        Warhead warhead = new WarheadMicro(stack);

        if(!init)
        {
            WarheadCasings.register();
            ex = new Explosive(BlastBasic.class);
            ExplosiveRegistry.LOG_REGISTERING_EXPLOSIVES = false;
            ex = ExplosiveRegistry.registerOrGetExplosive("junit", "test", ex);
            init = true;
        }

        warhead.ex = ex;

        return warhead;
    }

    public void testBuild()
    {
        //setup
        Warhead warhead = createWarhead();
        Warhead loaded = MissileModuleBuilder.INSTANCE.buildWarhead(warhead.toStack());
        assertEquals("Failed to build warhead", true, loaded != null);
        assertEquals("Save items are not equal", warhead.toStack(), loaded.toStack());
        assertEquals("Explosives are not equal", warhead.ex, loaded.ex);
    }

    //Test to make sure that the save function for the module works correctly
    public void testItemSave()
    {
        //setup
        Warhead warhead = createWarhead();

        //tests
        ItemStack stack = warhead.toStack();

        //Check if save exists
        assertEquals("Warhead save is null", true, stack.getTagCompound() != null);

        //Check for ID uses to build from saves
        assertEquals("Warhead didn't save its class ID", true, stack.getTagCompound().hasKey(ModuleBuilder.SAVE_ID));
        String id = stack.getTagCompound().getString(ModuleBuilder.SAVE_ID);
        assertEquals("Warhead class ID is empty", true, id != null && !id.isEmpty());

        //Check for ex
        assertEquals("Warhead didn't save its explosive", true, stack.getTagCompound().hasKey(ExplosiveItemUtility.EXPLOSIVE_SAVE));
    }
}
