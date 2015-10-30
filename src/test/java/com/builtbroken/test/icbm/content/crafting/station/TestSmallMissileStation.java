package com.builtbroken.test.icbm.content.crafting.station;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.station.TileSmallMissileWorkstation;
import com.builtbroken.icbm.content.crafting.station.TileSmallMissileWorkstationClient;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.tool.ItemScrewdriver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.transform.vector.Location;
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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    @Test
    public void testGetLayoutOfMultiBlock()
    {
        //25 test cases needed... best estimate
        FakeWorld world = FakeWorld.newWorld("TestGetLayout");
        world.setBlock(0, 10, 0, block);
        TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));


        //Test layout for side down
        tile.connectedBlockSide = ForgeDirection.DOWN;
        tile.rotation = ForgeDirection.UP;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.DOWN;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.NORTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.SOUTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.EAST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
        tile.rotation = ForgeDirection.WEST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);

        //Test layout for side up
        tile.connectedBlockSide = ForgeDirection.UP;
        tile.rotation = ForgeDirection.UP;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.DOWN;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.NORTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.SOUTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.EAST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
        tile.rotation = ForgeDirection.WEST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);

        //Test layout for side north
        tile.connectedBlockSide = ForgeDirection.NORTH;
        tile.rotation = ForgeDirection.UP;
        assertSame(tile.getLayoutOfMultiBlock(), tile.upDownMap);
        tile.rotation = ForgeDirection.DOWN;
        assertSame(tile.getLayoutOfMultiBlock(), tile.upDownMap);
        tile.rotation = ForgeDirection.NORTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
        tile.rotation = ForgeDirection.SOUTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
        tile.rotation = ForgeDirection.EAST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
        tile.rotation = ForgeDirection.WEST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);

        //Test layout for side south
        tile.connectedBlockSide = ForgeDirection.SOUTH;
        tile.rotation = ForgeDirection.UP;
        assertSame(tile.getLayoutOfMultiBlock(), tile.upDownMap);
        tile.rotation = ForgeDirection.DOWN;
        assertSame(tile.getLayoutOfMultiBlock(), tile.upDownMap);
        tile.rotation = ForgeDirection.NORTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
        tile.rotation = ForgeDirection.SOUTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
        tile.rotation = ForgeDirection.EAST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
        tile.rotation = ForgeDirection.WEST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);

        //Test layout for side east
        tile.connectedBlockSide = ForgeDirection.EAST;
        tile.rotation = ForgeDirection.UP;
        assertSame(tile.getLayoutOfMultiBlock(), tile.upDownMap);
        tile.rotation = ForgeDirection.DOWN;
        assertSame(tile.getLayoutOfMultiBlock(), tile.upDownMap);
        tile.rotation = ForgeDirection.NORTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.SOUTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.EAST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.WEST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);

        //Test layout for side west
        tile.connectedBlockSide = ForgeDirection.WEST;
        tile.rotation = ForgeDirection.UP;
        assertSame(tile.getLayoutOfMultiBlock(), tile.upDownMap);
        tile.rotation = ForgeDirection.DOWN;
        assertSame(tile.getLayoutOfMultiBlock(), tile.upDownMap);
        tile.rotation = ForgeDirection.NORTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.SOUTH;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.EAST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);
        tile.rotation = ForgeDirection.WEST;
        assertSame(tile.getLayoutOfMultiBlock(), tile.northSouthMap);

        //Test unknown... this should never happen
        tile.connectedBlockSide = ForgeDirection.UNKNOWN;
        assertSame(tile.getLayoutOfMultiBlock(), tile.eastWestMap);
    }

    @Test
    public void testIsRotationBlocked()
    {
        FakeWorld world = FakeWorld.newWorld("TestIsRotationBlocked");
        TileSmallMissileWorkstation workstation = new TileSmallMissileWorkstation();
        workstation.setWorldObj(world);
        workstation.yCoord = 10;
        for (ForgeDirection connectedSide : ForgeDirection.VALID_DIRECTIONS)
        {
            workstation.connectedBlockSide = connectedSide;
            for (ForgeDirection rotation : ForgeDirection.VALID_DIRECTIONS)
            {
                //Only test valid rotation as the invalid will not work no matter what
                if (workstation.isValidRotation(rotation))
                {
                    //Test rotation with no blocks
                    assertFalse(workstation.isRotationBlocked(rotation));

                    //Test rotation with blocks
                    Location pos = workstation.toLocation().add(rotation);
                    pos.setBlock(Blocks.dirt);
                    assertTrue(workstation.isRotationBlocked(rotation));
                    pos.setBlockToAir();
                }
            }
        }
    }

    @Test
    public void testGetNextRotation()
    {
        TileSmallMissileWorkstation workstation = new TileSmallMissileWorkstation();

        ////Test down
        workstation.connectedBlockSide = ForgeDirection.DOWN;
        workstation.rotation = ForgeDirection.NORTH;
        assertTrue(workstation.getNextRotation() == ForgeDirection.EAST);
        workstation.rotation = ForgeDirection.EAST;
        assertTrue(workstation.getNextRotation() == ForgeDirection.SOUTH);
        workstation.rotation = ForgeDirection.SOUTH;
        assertTrue(workstation.getNextRotation() == ForgeDirection.WEST);
        workstation.rotation = ForgeDirection.WEST;


        ////Test up
        workstation.connectedBlockSide = ForgeDirection.UP;
        workstation.rotation = ForgeDirection.NORTH;
        assertTrue(workstation.getNextRotation() == ForgeDirection.EAST);
        workstation.rotation = ForgeDirection.EAST;
        assertTrue(workstation.getNextRotation() == ForgeDirection.SOUTH);
        workstation.rotation = ForgeDirection.SOUTH;
        assertTrue(workstation.getNextRotation() == ForgeDirection.WEST);
        workstation.rotation = ForgeDirection.WEST;
        assertTrue(workstation.getNextRotation() == ForgeDirection.NORTH);

        ////Test east
        workstation.connectedBlockSide = ForgeDirection.EAST;
        workstation.rotation = ForgeDirection.NORTH;
        assertTrue(workstation.getNextRotation() == ForgeDirection.DOWN);
        workstation.rotation = ForgeDirection.DOWN;
        assertTrue(workstation.getNextRotation() == ForgeDirection.SOUTH);
        workstation.rotation = ForgeDirection.SOUTH;
        assertTrue(workstation.getNextRotation() == ForgeDirection.UP);
        workstation.rotation = ForgeDirection.UP;
        assertTrue(workstation.getNextRotation() == ForgeDirection.NORTH);

        ////Test west
        workstation.connectedBlockSide = ForgeDirection.EAST;
        workstation.rotation = ForgeDirection.NORTH;
        assertTrue(workstation.getNextRotation() == ForgeDirection.DOWN);
        workstation.rotation = ForgeDirection.DOWN;
        assertTrue(workstation.getNextRotation() == ForgeDirection.SOUTH);
        workstation.rotation = ForgeDirection.SOUTH;
        assertTrue(workstation.getNextRotation() == ForgeDirection.UP);
        workstation.rotation = ForgeDirection.UP;
        assertTrue(workstation.getNextRotation() == ForgeDirection.NORTH);

        ////Test north
        workstation.connectedBlockSide = ForgeDirection.NORTH;
        workstation.rotation = ForgeDirection.EAST;
        assertTrue(workstation.getNextRotation() == ForgeDirection.DOWN);
        workstation.rotation = ForgeDirection.DOWN;
        assertTrue(workstation.getNextRotation() == ForgeDirection.WEST);
        workstation.rotation = ForgeDirection.WEST;
        assertTrue(workstation.getNextRotation() == ForgeDirection.UP);
        workstation.rotation = ForgeDirection.UP;
        assertTrue(workstation.getNextRotation() == ForgeDirection.EAST);

        ////Test south
        workstation.connectedBlockSide = ForgeDirection.SOUTH;
        workstation.rotation = ForgeDirection.EAST;
        assertTrue(workstation.getNextRotation() == ForgeDirection.DOWN);
        workstation.rotation = ForgeDirection.DOWN;
        assertTrue(workstation.getNextRotation() == ForgeDirection.WEST);
        workstation.rotation = ForgeDirection.WEST;
        assertTrue(workstation.getNextRotation() == ForgeDirection.UP);
        workstation.rotation = ForgeDirection.UP;
        assertTrue(workstation.getNextRotation() == ForgeDirection.EAST);
    }

    @Test
    public void testSetRotation()
    {
        //idea 12 test cases by isServer is not checked removing two
        FakeWorld world = FakeWorld.newWorld("TestGetLayout");
        world.setBlock(0, 10, 0, block);
        TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));

        tile.connectedBlockSide = ForgeDirection.UP;
        tile.rotation = ForgeDirection.NORTH;
        //Init tile as we want the structure to generate
        tile.firstTick();

        //Run just for code coverage
        tile.setDirection(ForgeDirection.NORTH);

        //Test invalid rotation, same dir
        assertFalse(tile.setDirectionDO(ForgeDirection.NORTH, false));

        //Test invalid rotation, connected side
        assertFalse(tile.setDirectionDO(ForgeDirection.UP, false));

        //Test invalid rotation, inverted connected side
        assertFalse(tile.setDirectionDO(ForgeDirection.DOWN, false));

        //Test valid rotation, with no block updates
        assertTrue(tile.setDirectionDO(ForgeDirection.SOUTH, false));
        assertTrue(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
        assertTrue(world.getBlock(0, 10, -1) instanceof BlockMultiblock);
        tile.rotation = ForgeDirection.NORTH;

        //Test valid rotation, with 90 degree change, block updates, nothing blocked
        assertTrue(tile.setDirectionDO(ForgeDirection.EAST, false));
        assertTrue(world.getBlock(1, 10, 0) instanceof BlockMultiblock);
        assertTrue(world.getBlock(-1, 10, 0) instanceof BlockMultiblock);
        tile.invalidate();
        tile.rotation = ForgeDirection.NORTH;

        //Test valid rotation, with 90 degree change, block updates, with some stuff blocked
        world.setBlock(1, 10, 0, Blocks.dirt);
        world.setBlock(-1, 10, 0, Blocks.dirt);
        assertFalse(tile.setDirectionDO(ForgeDirection.EAST, false));
        tile.invalidate();
        tile.rotation = ForgeDirection.NORTH;
    }

    @Test
    public void testOnPlayerRightClickWrench()
    {
        //Init wrench if missing
        if (Engine.itemWrench == null)
        {
            Engine.itemWrench = new ItemScrewdriver();
            GameRegistry.registerItem(Engine.itemWrench, "ve.screwdriver");
        }

        //Set player held item to wrench
        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Engine.itemWrench));

        for (ForgeDirection dir : new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.DOWN})
        {
            world.setBlock(0, 10, 0, block, dir.ordinal(), 3);
            TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));
            tile.firstTick();

            //First rotation from north to east
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.EAST);
            assertTrue(world.getBlock(1, 10, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(-1, 10, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, -1) instanceof BlockMultiblock);

            //First rotation from east to south
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.SOUTH);
            assertFalse(world.getBlock(1, 10, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(-1, 10, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 10, -1) instanceof BlockMultiblock);

            //First rotation from south to west
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.WEST);
            assertTrue(world.getBlock(1, 10, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(-1, 10, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, -1) instanceof BlockMultiblock);

            //First rotation from west to north
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.NORTH);
            assertFalse(world.getBlock(1, 10, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(-1, 10, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 10, -1) instanceof BlockMultiblock);

            //Clean up
            world.setBlockToAir(0, 10, 0);
        }

        for (ForgeDirection dir : new ForgeDirection[]{ForgeDirection.EAST, ForgeDirection.WEST})
        {
            world.setBlock(0, 10, 0, block, dir.ordinal(), 3);
            TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));
            tile.firstTick();

            //First rotation from north to east
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.DOWN);
            assertTrue(world.getBlock(0, 11, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 9, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, -1) instanceof BlockMultiblock);

            //First rotation from east to south
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.SOUTH);
            assertFalse(world.getBlock(0, 11, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 9, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 10, -1) instanceof BlockMultiblock);

            //First rotation from south to west
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.UP);
            assertTrue(world.getBlock(0, 9, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 11, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, -1) instanceof BlockMultiblock);

            //First rotation from west to north
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.NORTH);
            assertFalse(world.getBlock(0, 11, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 9, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 10, 1) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 10, -1) instanceof BlockMultiblock);

            //Clean up
            world.setBlockToAir(0, 10, 0);
        }

        for (ForgeDirection dir : new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.SOUTH})
        {
            world.setBlock(0, 10, 0, block, dir.ordinal(), 3);
            TileSmallMissileWorkstation tile = ((TileSmallMissileWorkstation) world.getTileEntity(0, 10, 0));
            tile.firstTick();

            //First rotation from north to east
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.DOWN);
            assertTrue(world.getBlock(0, 11, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 9, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(1, 10, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(-1, 10, 0) instanceof BlockMultiblock);

            //First rotation from east to south
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.WEST);
            assertFalse(world.getBlock(0, 11, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 9, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(1, 10, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(-1, 10, 0) instanceof BlockMultiblock);

            //First rotation from south to west
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.UP);
            assertTrue(world.getBlock(0, 9, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(0, 11, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 10, 0) instanceof BlockMultiblock);

            //First rotation from west to north
            assertTrue(tile.onPlayerActivated(player, 0, new Pos()));
            assertTrue("" + tile.getDirection(), tile.getDirection() == ForgeDirection.EAST);
            assertFalse(world.getBlock(0, 11, 0) instanceof BlockMultiblock);
            assertFalse(world.getBlock(0, 9, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(1, 10, 0) instanceof BlockMultiblock);
            assertTrue(world.getBlock(-1, 10, 0) instanceof BlockMultiblock);

            //Clean up
            world.setBlockToAir(0, 10, 0);
        }
    }

    @Test
    public void testPacketCode()
    {
        for (ItemStack item : new ItemStack[]{null, new ItemStack(ICBM.itemMissile)})
        {
            TileSmallMissileWorkstation sender = new TileSmallMissileWorkstation();
            sender.setWorldObj(world);
            sender.rotation = ForgeDirection.EAST;
            sender.setInventorySlotContents(sender.INPUT_SLOT, item);


            TileSmallMissileWorkstationClient receiver = new TileSmallMissileWorkstationClient()
            {
                @Override
                public boolean isClient()
                {
                    return true;
                }
            };
            sender.setWorldObj(world);

            //Create and encode packet
            PacketTile packet = sender.getDescPacket();

            packet.handle(player, receiver);
            assertTrue(receiver.rotation == ForgeDirection.EAST);
            assertTrue(InventoryUtility.stacksMatchExact(receiver.getStackInSlot(receiver.INPUT_SLOT), item));
        }
    }
}
