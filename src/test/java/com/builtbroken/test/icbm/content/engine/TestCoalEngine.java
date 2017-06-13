package com.builtbroken.test.icbm.content.engine;

import com.builtbroken.icbm.content.missile.parts.engine.solid.RocketEngineCoalPowered;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/27/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestCoalEngine extends AbstractTest
{
    @Override
    public void setUpForEntireClass()
    {
        RocketEngineCoalPowered.loadFuelValues();
        for (ItemStackWrapper wrapper : RocketEngineCoalPowered.FUEL_DISTANCE_VALUE.keySet())
        {
            System.out.println(wrapper);
        }
    }

    @Test
    public void testCoal()
    {
        RocketEngineCoalPowered engine = new RocketEngineCoalPowered(new ItemStack(Items.blaze_powder));
        ItemStack stack = new ItemStack(Items.coal, 10);
        engine.getInventory().setInventorySlotContents(0, stack);
        //Ensure new wrapper references work regardless of stack size change
        assertTrue(RocketEngineCoalPowered.FUEL_DISTANCE_VALUE.containsKey(new ItemStackWrapper(stack)));
        //Ensure stored value is greater than zero
        assertTrue(RocketEngineCoalPowered.FUEL_DISTANCE_VALUE.get(new ItemStackWrapper(stack)) > 0);
    }


    @Test
    public void testFuelDistance()
    {
        RocketEngineCoalPowered engine = new RocketEngineCoalPowered(new ItemStack(Items.blaze_powder));
        for (ItemStackWrapper wrapper : RocketEngineCoalPowered.FUEL_DISTANCE_VALUE.keySet())
        {
            //Ensure we store no null values
            assertNotNull(wrapper.itemStack);

            ItemStack stack = wrapper.itemStack.copy();
            stack.stackSize = stack.getMaxStackSize();
            //Ensure new wrapper references work regardless of stack size change
            assertTrue(RocketEngineCoalPowered.FUEL_DISTANCE_VALUE.containsKey(new ItemStackWrapper(stack)));
            //Ensure stored value is greater than zero
            assertTrue(RocketEngineCoalPowered.FUEL_DISTANCE_VALUE.get(new ItemStackWrapper(stack)) > 0);


            engine.getInventory().setInventorySlotContents(0, stack);
            float fuelDistance = engine.getMaxDistance(null);
            //Ensure distance is greater than zero
            assertTrue(fuelDistance > 0);
            //Ensure distance matches expected value
            assertTrue(fuelDistance == (RocketEngineCoalPowered.FUEL_DISTANCE_VALUE.get(new ItemStackWrapper(stack)) * stack.stackSize));
        }
    }

    @Test
    public void testFuelSpeed()
    {
        RocketEngineCoalPowered engine = new RocketEngineCoalPowered(new ItemStack(Items.blaze_powder));
        for (ItemStackWrapper wrapper : RocketEngineCoalPowered.FUEL_SPEED_VALUE.keySet())
        {
            //Ensure we store no null values
            assertNotNull(wrapper.itemStack);

            ItemStack stack = wrapper.itemStack.copy();
            stack.stackSize = stack.getMaxStackSize();
            //Ensure new wrapper references work regardless of stack size change
            assertTrue(RocketEngineCoalPowered.FUEL_SPEED_VALUE.containsKey(new ItemStackWrapper(stack)));
            //Ensure stored value is greater than zero
            assertTrue(RocketEngineCoalPowered.FUEL_SPEED_VALUE.get(new ItemStackWrapper(stack)) > 0);


            engine.getInventory().setInventorySlotContents(0, stack);
            float fuelDistance = engine.getSpeed(null);
            //Ensure distance is greater than zero
            assertTrue(fuelDistance > 0);
            //Ensure distance matches expected value
            assertTrue(stack.toString(), fuelDistance == (RocketEngineCoalPowered.FUEL_SPEED_VALUE.get(new ItemStackWrapper(stack))));
        }
    }
}
