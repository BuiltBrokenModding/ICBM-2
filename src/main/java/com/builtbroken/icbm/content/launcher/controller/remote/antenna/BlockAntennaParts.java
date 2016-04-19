package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.core.registry.implement.IRegistryInit;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/28/2016.
 */
public class BlockAntennaParts extends BlockContainer implements IPostInit, IRegistryInit
{
    public BlockAntennaParts()
    {
        super(Material.iron);
        this.setBlockName(ICBM.PREFIX + "antennaParts");
        this.blockHardness = 10;
        this.blockResistance = 10;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy, float zz)
    {
        int meta = world.getBlockMetadata(x, y, z);
        //Antenna for linking missile areas
        if (meta == 0 && WrenchUtility.isHoldingWrench(player) && player.isSneaking() && side != 0 && side != 1)
        {
            if (!world.isRemote)
            {
                ArrayList<Pos> setToOne = new ArrayList();

                boolean foundLowest = false;
                int lowestY = y;
                //Path down to find lowest point
                for (; lowestY >= 0; lowestY--)
                {
                    Block block = world.getBlock(x, lowestY, z);
                    if (block instanceof BlockAntennaParts)
                    {
                        int m = world.getBlockMetadata(x, lowestY, z);
                        if (m == 0 || m == 1)
                        {
                            setToOne.add(new Pos(x, lowestY, z));
                            foundLowest = true;
                        }
                        else if (m == 2)
                        {
                            player.addChatComponentMessage(new ChatComponentText("Seems there is already a base block for this antenna! Break it to allow a new block to be set."));
                            return true;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                if (foundLowest)
                {
                    for (Pos pos : setToOne)
                    {
                        if (pos.yi() == lowestY)
                        {
                            if (!pos.setBlock(world, this, 2) && pos.getBlockMetadata(world) != 2)
                            {
                                ICBM.INSTANCE.logger().error("Failed to update antenna rod to base " + pos);
                            }
                        }
                        else
                        {
                            if (!pos.setBlock(world, this, 1) && pos.getBlockMetadata(world) != 1)
                            {
                                ICBM.INSTANCE.logger().error("Failed to update antenna rod to active rod " + pos);
                            }
                        }
                    }
                    TileEntity tile = world.getTileEntity(x, lowestY, z);
                    if (tile instanceof TileAntenna)
                    {
                        ((TileAntenna) tile).doInitScan();
                    }
                    player.addChatComponentMessage(new ChatComponentText("Scan completed and antenna setup, access based block for additional settings."));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
        return meta;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int side)
    {

    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileAntennaPart && ((TileAntennaPart) tile).network != null)
        {
            ((TileAntennaPart) tile).network.split((TileAntennaPart)tile);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB bounds, List boxes, Entity entity)
    {
        AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(world, x, y, z);

        if (axisalignedbb1 != null && bounds.intersectsWith(axisalignedbb1))
        {
            boxes.add(axisalignedbb1);

            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 2)
            {
                boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3, y + 0.3, z + 0.3, x + 0.7, y + 1, z + 0.7));
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 || meta == 1)
        {
            return AxisAlignedBB.getBoundingBox(x + 0.3, y, z + 0.3, x + 0.7, y + 1, z + 0.7);
        }
        else if (meta == 2)
        {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.4, z + 1);
        }
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return Blocks.hopper.getIcon(p_149691_1_, p_149691_2_);
    }

    @Override
    public int getRenderType()
    {
        return ISBRAntennaFrame.INSTANCE.ID;
    }

    @Override
    public void onPostInit()
    {

    }

    @Override
    public void onRegistered()
    {
        GameRegistry.registerTileEntity(TileAntenna.class, "ICBMxTileAntenna");
    }

    @Override
    public void onClientRegistered()
    {
        RenderingRegistry.registerBlockHandler(ISBRAntennaFrame.INSTANCE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return meta == 2 ? new TileAntenna() : new TileAntennaPart();
    }
}
