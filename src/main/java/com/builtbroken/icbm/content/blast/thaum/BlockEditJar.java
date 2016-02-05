package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEditResult;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileJarNode;
import thaumcraft.common.tiles.TileNode;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/1/2016.
 */
public class BlockEditJar implements IWorldEdit
{
    Block oldBlock;
    int oldMeta;

    World world;
    int x, y, z;

    public BlockEditJar(Location location)
    {
        this.world = location.world;
        this.x = location.xi();
        this.y = location.yi();
        this.z = location.zi();
    }

    @Override
    public boolean hasChanged()
    {
        return oldBlock != ConfigBlocks.blockJar;
    }

    @Override
    public BlockEditResult place()
    {
        //We can not place a block without a world
        if (world != null)
        {
            //Check if the chunk exists and is loaded to prevent loading/creating new chunks
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            if (chunk != null && chunk.isChunkLoaded)
            {
                //Check if the prev_block still exists
                if (oldBlock != getBlock() && oldMeta != getBlockMetadata())
                {
                    return BlockEditResult.PREV_BLOCK_CHANGED;
                }

                //Check if it was already placed to prevent item lose if this is being used by a schematic
                if (getBlock() == getNewBlock() && getBlockMetadata() == getNewMeta())
                {
                    return BlockEditResult.ALREADY_PLACED;
                }

                //Place the block and check if the world says its placed
                if (jarNode(world, x, y, z))
                {
                    return BlockEditResult.PLACED;
                }
                return BlockEditResult.BLOCKED;
            }
            return BlockEditResult.CHUNK_UNLOADED;
        }
        return BlockEditResult.NULL_WORLD;
    }

    /**
     * Converts a node {@link TileNode} to a jar node {@link thaumcraft.common.tiles.TileJarNode}
     */
    public static boolean jarNode(World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof INode)
        {
            INode node = (INode) tile;

            //Get node data
            AspectList na = node.getAspects().copy();
            int nodeType = node.getNodeType().ordinal();
            int nodeModifier = -1;
            if (node.getNodeModifier() != null)
            {
                nodeModifier = node.getNodeModifier().ordinal();
            }

            //Apply chance to change modifier
            if (world.rand.nextFloat() < 0.75F)
            {
                if (node.getNodeModifier() == null)
                {
                    nodeModifier = NodeModifier.PALE.ordinal();
                }
                else if (node.getNodeModifier() == NodeModifier.BRIGHT)
                {
                    nodeModifier = -1;
                }
                else if (node.getNodeModifier() == NodeModifier.PALE)
                {
                    nodeModifier = NodeModifier.FADING.ordinal();
                }
            }

            //More data
            String nid = node.getId();
            node.setAspects(new AspectList());

            //Remove node and set block
            world.removeTileEntity(x, y, z); //TODO check if needed
            world.setBlock(x, y, z, ConfigBlocks.blockJar, 2, 3);

            //Get jar tile
            tile = world.getTileEntity(x, y, z);
            TileJarNode jar = (TileJarNode) tile;

            //Set jar data
            jar.setAspects(na);
            if (nodeModifier >= 0)
            {
                jar.setNodeModifier(NodeModifier.values()[nodeModifier]);
            }
            jar.setNodeType(NodeType.values()[nodeType]);
            jar.setId(nid);

            //Tick
            world.addBlockEvent(x, y, z, ConfigBlocks.blockJar, 9, 0);
            return true;
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBounds()
    {
        return null;
    }

    @Override
    public Block getNewBlock()
    {
        return ConfigBlocks.blockJar;
    }

    @Override
    public int getNewMeta()
    {
        return 2;
    }

    @Override
    public Block getBlock()
    {
        return oldBlock;
    }

    @Override
    public int getBlockMetadata()
    {
        return oldMeta;
    }

    @Override
    public TileEntity getTileEntity()
    {
        return world.getTileEntity(x, y, z);
    }

    @Override
    public World world()
    {
        return world;
    }

    @Override
    public double x()
    {
        return x;
    }

    @Override
    public double y()
    {
        return y;
    }

    @Override
    public double z()
    {
        return z;
    }
}
