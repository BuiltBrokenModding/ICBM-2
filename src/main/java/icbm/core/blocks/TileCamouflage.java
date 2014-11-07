package icbm.core.blocks;

import icbm.core.ICBMCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.blocks.ICamouflageMaterial;
import resonant.content.prefab.java.TileAdvanced;

import com.google.common.io.ByteArrayDataInput;
import resonant.engine.ResonantEngine;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.transform.region.Cuboid;
import resonant.lib.transform.vector.Vector3;

public class TileCamouflage extends TileAdvanced implements IPacketReceiver
{
    // The block Id this block is trying to mimick
    private Block block = null;
    private int blockMeta = 0;
    private boolean isSolid = true;

    /** Bitmask **/
    private byte renderSides = 0;

    public TileCamouflage()
    {
        super(Material.cloth);
        this.blockHardness(0.3F);
        this.blockResistance(1F);
        this.isOpaqueCube(true);
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public boolean configure(EntityPlayer player, int side, Vector3 hit)
    {
        if(!player.isSneaking())
            toggleRenderSide(ForgeDirection.getOrientation(side));
        else
            toggleCollision();

        worldObj.markBlockForUpdate(xi(), yi(), zi());
        return true;
    }

    public boolean use(EntityPlayer player, int side, Vector3 hit)
    {
        if (player.getCurrentEquippedItem() != null)
        {
            Block block = Block.getBlockFromItem(player.getCurrentEquippedItem().getItem());

            if (block != null && block != this.getBlockType())
            {
                if (block instanceof ICamouflageMaterial || (block.isNormalCube() && (block.getRenderType() == 0 || block.getRenderType() == 31)))
                {
                    this.block = block;
                    this.blockMeta = player.getCurrentEquippedItem().getItemDamage();
                    if (!this.worldObj.isRemote)
                    {
                        ResonantEngine.instance.packetHandler.sendToAllInDimension(this.getDescPacket(), world());
                    }
                    world().markBlockForUpdate(xi(), yi(), zi());
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        if (canRenderSide(ForgeDirection.getOrientation(side)))
        {
            return Blocks.glass.getBlockTextureFromSide(side);
        }
        if(block != null)
        {
            return block.getIcon(side, meta);
        }
        return super.getIcon(side, meta);
    }


    ////////////////////////////////////
    // Packet Handling
    ////////////////////////////////////
    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType type)
    {
        try
        {
            this.block = Block.getBlockById(data.readInt());
            this.blockMeta = data.readInt();
            this.renderSides = data.readByte();
            this.isSolid = data.readBoolean();
            this.worldObj.markBlockRangeForRenderUpdate(this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, Block.getIdFromBlock(block), this.blockMeta, this.renderSides, this.isSolid);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.block = Block.getBlockById(nbt.getInteger("jiaHaoMa"));
        this.blockMeta = nbt.getInteger("jiaMetadata");
        this.renderSides = nbt.getByte("renderSides");
        this.isSolid = nbt.getBoolean("isYing");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setInteger("jiaHaoMa", Block.getIdFromBlock(block));
        nbt.setInteger("jiaMetadata", this.blockMeta);
        nbt.setByte("renderSides", renderSides);
        nbt.setBoolean("isYing", this.isSolid);
    }

    ////////////////////////////////////
    // Accessors and Setters
    ////////////////////////////////////

    public void setCanCollide(boolean isSolid)
    {
        this.isSolid = isSolid;

        if (!this.worldObj.isRemote)
        {
            ResonantEngine.instance.packetHandler.sendToAllInDimension(this.getDescPacket(), world());
        }
    }

    public void toggleCollision()
    {
        this.setCanCollide(!this.isSolid);
    }


    public boolean canRenderSide(ForgeDirection direction)
    {
        return (renderSides & (1 << direction.ordinal())) != 0;
    }

    public void setRenderSide(ForgeDirection direction, boolean isClear)
    {
        if (isClear)
        {
            renderSides = (byte) (renderSides | (1 << direction.ordinal()));
        }
        else
        {
            renderSides = (byte) (renderSides & ~(1 << direction.ordinal()));

        }

        if (!this.worldObj.isRemote)
        {
            ResonantEngine.instance.packetHandler.sendToAllInDimension(this.getDescPacket(), world());
        }
    }

    public void toggleRenderSide(ForgeDirection direction)
    {
        this.setRenderSide(direction, !canRenderSide(direction));
    }

    @Override
    public int colorMultiplier()
    {
        if (block != null)
        {
            return block.colorMultiplier(world(), xi(), yi(), xi());
        }

        return 16777215;
    }

    @Override
    public Cuboid getCollisionBounds()
    {
        return isSolid ?  super.getCollisionBounds() : null;
    }
}
