package com.builtbroken.test.icbm.content.crafting.station;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.station.TileSmallMissileWorkstation;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.multiblock.BlockMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.ItemBlockMulti;
import com.builtbroken.mc.testing.junit.ModRegistry;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.builtbroken.mc.testing.junit.world.FakeWorld;
import com.builtbroken.mc.testing.tile.AbstractTileTest;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;

/**
 * Test for small missile workstation. Creates an engine instance and loads multi block code.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/22/2015.
 */
@RunWith(VoltzTestRunner.class)
public class TestSmallMissileStation extends AbstractTileTest<TileSmallMissileWorkstation>
{
    public TestSmallMissileStation() throws IllegalAccessException, InstantiationException
    {
        super("testSmallMissileStation", TileSmallMissileWorkstation.class);
        //Init engine to prevent NPE when code is called
        if (Engine.instance == null)
        {
            Engine.instance = new Engine();
        }
        //Load multi block for the test
        if (Engine.multiBlock == null)
        {
            Engine.multiBlock = new BlockMultiblock();
            ModRegistry.registerBlock(Engine.multiBlock, ItemBlockMulti.class, "veMultiBlock");
            EnumMultiblock.register();
        }

        if (ICBM.itemMissile == null)
        {
            ICBM.itemMissile = new ItemMissile();
            GameRegistry.registerItem(ICBM.itemMissile, "missile");
        }
    }

    @Test
    public void testCoverage()
    {
        super.testCoverage();
        Method[] methods = TileSmallMissileWorkstation.class.getDeclaredMethods();
        if (methods.length != 22)
        {
            for (Method method : methods)
            {
                System.out.println(method.getName());
            }
            fail("There are " + methods.length + " but should be 22");
        }
    }

    @Test
    public void testStructureMap()
    {
        //Test to ensure that the maps contain the correct data for the multi block
        assertTrue(TileSmallMissileWorkstation.eastWestMap.containsKey(new Pos(1, 0, 0)));
        assertTrue(TileSmallMissileWorkstation.eastWestMap.containsKey(new Pos(-1, 0, 0)));

        assertTrue(TileSmallMissileWorkstation.upDownMap.containsKey(new Pos(0, 1, 0)));
        assertTrue(TileSmallMissileWorkstation.upDownMap.containsKey(new Pos(0, -1, 0)));

        assertTrue(TileSmallMissileWorkstation.northSouthMap.containsKey(new Pos(0, 0, 1)));
        assertTrue(TileSmallMissileWorkstation.northSouthMap.containsKey(new Pos(0, 0, -1)));
    }

    @Test
    public void testInit()
    {
        TileSmallMissileWorkstation station = new TileSmallMissileWorkstation();
        //Tested just to increase code coverage
        assertTrue(station.getDirection() == ForgeDirection.NORTH);
        assertTrue(station.newTile() instanceof TileSmallMissileWorkstation);
    }

    @Override
    @Test
    public void testFirstTick()
    {
        FakeWorld world = FakeWorld.newWorld("TestFirstTick");

        for (int i = 0; i < 6; i++)
        {
            world.setBlock(0, 10, 0, block);
            TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));
            world.setBlockMetadataWithNotify(0, 10, 0, i, 3);

            //Call first tick
            tile.firstTick();

            //Test that the correct side is set on first tick
            assertTrue(tile.connectedBlockSide == ForgeDirection.getOrientation(i));

            //Clear block between cycles
            world.setBlockToAir(0, 10, 0);
        }
    }

    @Override
    @Test
    public void testUpdate()
    {
        FakeWorld world = FakeWorld.newWorld("TestUpdate");
        world.setBlock(0, 10, 0, block);
        TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));
        tile.ticks = 5;

        //Test eject when missile is missing
        tile.setInventorySlotContents(tile.GUIDANCE_SLOT, new ItemStack(Items.apple));
        tile.update();
        assertTrue(tile.getStackInSlot(tile.GUIDANCE_SLOT) == null);

        //Test eject when missile is not missing, item type doesn't matter
        tile.setInventorySlotContents(tile.INPUT_SLOT, new ItemStack(Items.stick));
        tile.setInventorySlotContents(tile.GUIDANCE_SLOT, new ItemStack(Items.apple));
        tile.update();
        assertTrue(tile.getStackInSlot(tile.GUIDANCE_SLOT) != null);
        assertTrue(tile.getStackInSlot(tile.INPUT_SLOT) != null);
    }

    @Test
    public void testEject()
    {
        FakeWorld world = FakeWorld.newWorld("TestEject");
        world.setBlock(0, 10, 0, block);
        TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));

        //Test eject with items
        tile.setInventorySlotContents(tile.GUIDANCE_SLOT, new ItemStack(Items.apple));
        tile.setInventorySlotContents(tile.ENGINE_SLOT, new ItemStack(Items.apple));
        tile.setInventorySlotContents(tile.WARHEAD_SLOT, new ItemStack(Items.apple));
        tile.ejectCraftingItems();
        assertTrue(tile.getStackInSlot(tile.GUIDANCE_SLOT) == null);
        assertTrue(tile.getStackInSlot(tile.ENGINE_SLOT) == null);
        assertTrue(tile.getStackInSlot(tile.WARHEAD_SLOT) == null);

        //Test eject with out items
        tile.ejectCraftingItems();
        assertTrue(tile.getStackInSlot(tile.GUIDANCE_SLOT) == null);
        assertTrue(tile.getStackInSlot(tile.ENGINE_SLOT) == null);
        assertTrue(tile.getStackInSlot(tile.WARHEAD_SLOT) == null);
    }

    @Test
    public void testAssemble()
    {
        FakeWorld world = FakeWorld.newWorld("TestEject");
        world.setBlock(0, 10, 0, block);
        TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));

        String result;

        //Test empty input slot
        result = tile.assemble();
        assertTrue(result.equals("slot.input.empty"));

        //Test invalid input slot
        tile.setInventorySlotContents(tile.INPUT_SLOT, new ItemStack(Items.apple));
        result = tile.assemble();
        assertTrue(result.equals("slot.input.invalid"));

        //Valid input slot
        tile.setInventorySlotContents(tile.INPUT_SLOT, MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.SMALL, null, null, null).toStack());
        result = tile.assemble();
        assertTrue(result.equals(""));

        //Test warhead insert, with warhead in slot, with out warhead in missile, with invalid item
        tile.setInventorySlotContents(tile.INPUT_SLOT, MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.SMALL, null, null, null).toStack());
        tile.setInventorySlotContents(tile.WARHEAD_SLOT, new ItemStack(Items.apple));
        result = tile.assemble();
        assertTrue(InventoryUtility.stacksMatchExact(tile.getWarheadItem(), new ItemStack(Items.apple)));
        assertTrue(tile.getMissileItem() != null);
        assertTrue(((ItemMissile) tile.getMissileItem().getItem()).getWarhead(tile.getMissileItem()) == null);
        assertTrue(tile.getEngineItem() == null);
        assertTrue(tile.getGuidanceItem() == null);
        assertTrue(result.equals(""));
        tile.setInventorySlotContents(tile.WARHEAD_SLOT, null);

        //Test guidance insert, with item in slot, with out guidance in missile, with invalid item
        tile.setInventorySlotContents(tile.INPUT_SLOT, MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.SMALL, null, null, null).toStack());
        tile.setInventorySlotContents(tile.GUIDANCE_SLOT, new ItemStack(Items.apple));
        result = tile.assemble();
        assertTrue(InventoryUtility.stacksMatchExact(tile.getGuidanceItem(), new ItemStack(Items.apple)));
        assertTrue(tile.getMissileItem() != null);
        assertTrue(((ItemMissile) tile.getMissileItem().getItem()).getWarhead(tile.getMissileItem()) == null);
        assertTrue(tile.getEngineItem() == null);
        assertTrue(tile.getWarheadItem() == null);
        assertTrue(result.equals(""));
        tile.setInventorySlotContents(tile.GUIDANCE_SLOT, null);

        //Test engine insert, with item in slot, with out engine in missile, with invalid item
        tile.setInventorySlotContents(tile.INPUT_SLOT, MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.SMALL, null, null, null).toStack());
        tile.setInventorySlotContents(tile.ENGINE_SLOT, new ItemStack(Items.apple));
        result = tile.assemble();
        assertTrue(InventoryUtility.stacksMatchExact(tile.getEngineItem(), new ItemStack(Items.apple)));
        assertTrue(tile.getMissileItem() != null);
        assertTrue(((ItemMissile) tile.getMissileItem().getItem()).getWarhead(tile.getMissileItem()) == null);
        assertTrue(tile.getWarheadItem() == null);
        assertTrue(tile.getGuidanceItem() == null);
        assertTrue(result.equals(""));
        tile.setInventorySlotContents(tile.GUIDANCE_SLOT, null);

        //TODO test valid item insert, valid item with item in missile, empty slot with item in missile... later
        //Basic another 20 tests for this one method meh
    }
}
