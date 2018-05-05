package com.builtbroken.icbm.content.launcher.door;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.door.json.DoorData;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.client.json.IJsonRenderStateProvider;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.block.imp.IActivationListener;
import com.builtbroken.mc.framework.block.imp.IBoundListener;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.framework.multiblock.listeners.IMultiBlockNodeListener;
import com.builtbroken.mc.framework.multiblock.MultiBlockHelper;
import com.builtbroken.mc.framework.multiblock.TileMulti;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Simple door that can open or close to let missiles through
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/1/2018.
 */
@TileWrapped(className = "TileWrapperSiloDoor", wrappers = "MultiBlock")
public class TileSiloDoor extends TileNode implements IRotatable, IActivationListener, IBoundListener, IMultiBlockNodeListener, IJsonRenderStateProvider
{
    /** Default speed to open the door */
    public static float openingSpeed = 0.5f; //TODO config

    /** is the door fully open */
    public boolean isOpen = false;

    /** Should the door be open due to redstone */
    public boolean openViaRedstone = false;
    /** Should the door be forced open */
    public boolean forceOpen = false;

    /** Current rotation progress of the door (0-90) */
    public float doorRotation = 0f;
    /** Client cache: previous value of rotation to use for smoother animation */
    public float _prevDoorRotation = 0f;

    private ForgeDirection dir_cache;

    public String doorID = "3x3StarSeeker";
    private DoorData _doorData;

    public ItemStack[] mimic_blocks;

    public boolean sendSyncPacket = true;

    public TileSiloDoor()
    {
        super("door.silo", ICBM.DOMAIN);
    }

    @Override
    public void firstTick()
    {
        for (int i = 0; i < getMimicBlocks().length; i++)
        {
            if (getMimicBlocks()[i] == null || !(getMimicBlocks()[i].getItem() instanceof ItemBlock))
            {
                setFillerBlock(new ItemStack(Blocks.grass), i);
            }
        }
        sendDescPacket();
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);

        //Ensure mutliblock filler is set, done every tick until a better solution can be found
        if (ticks % 3 == 0)
        {
            for (int i = 0; i < getMimicBlocks().length; i++)
            {
                setFillerBlock(i);
            }
        }

        doDoorMotion();
        if (isServer())
        {
            if (ticks % 2 == 0)
            {
                boolean prev = openViaRedstone;
                openViaRedstone = isStructureGettingRedstone() || forceOpen;
                if (prev != openViaRedstone)
                {
                    world().unwrap().playSoundEffect(xi() + 0.5D, yi() + 0.5D, zi() + 0.5D,
                            "tile.piston.out", 0.5F, world().unwrap().rand.nextFloat() * 0.25F + 0.6F);

                }
            }

            if (sendSyncPacket)
            {
                sendSyncPacket = false;
                sendDescPacket();
            }
        }
    }

    /**
     * Called to set the filler block
     *
     * @param stack - item stack, should be an ItemBlock
     * @param index - index
     */
    protected void setFillerBlock(ItemStack stack, int index)
    {
        if (index >= 0 && index < getMimicBlocks().length)
        {
            getMimicBlocks()[index] = stack;
            sendSyncPacket = true;
            setFillerBlock(index);
        }
    }

    /**
     * Called to update the multi-block in the layout with the filler block
     *
     * @param index - index in the layout, converted to x & z offset
     */
    protected void setFillerBlock(int index)
    {
        if (world().unwrap() != null)
        {
            ItemStack stack = getMimicBlocks()[index];
            if (stack == null || !(stack.getItem() instanceof ItemBlock))
            {
                stack = new ItemStack(Blocks.grass);
            }

            //Update multi-tile
            int x = (index / getDoorSize()) - getBlockArrayOffset();
            int z = (index % getDoorSize()) - getBlockArrayOffset();
            TileEntity tile = world().unwrap().getTileEntity(xi() + x, yi(), zi() + z);
            if (tile instanceof TileMulti && ((TileMulti) tile).getHost() == getHost())
            {
                ((TileMulti) tile).setBlockToFake(stack);
            }
        }
    }


    protected ItemStack[] getMimicBlocks()
    {
        if (mimic_blocks == null)
        {
            mimic_blocks = new ItemStack[getDoorSize() * getDoorSize()];
        }
        return mimic_blocks;
    }


    protected void doDoorMotion()
    {
        float prev = doorRotation;
        if (openViaRedstone)
        {
            if (doorRotation < 90)
            {
                doorRotation += openingSpeed;
            }
            else
            {
                isOpen = true;
            }
        }
        else
        {
            if (doorRotation > 0)
            {
                doorRotation -= openingSpeed;
            }
            else
            {
                isOpen = false;
            }
        }

        if (prev != doorRotation)
        {
            sendSyncPacket = true;
        }
    }

    @Override
    public boolean onPlayerActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.redstone)
        {
            if (isServer())
            {
                forceOpen = !forceOpen;
            }
            return true;
        }
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBounds()
    {
        return isOpen ? Cube.EMPTY.getAABB() : null;
    }

    public boolean isStructureGettingRedstone()
    {
        return getHost() instanceof IMultiTileHost ? MultiBlockHelper.getRedstoneLevel((IMultiTileHost) getHost(), true) > 0 : false;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        isOpen = buf.readBoolean();
        openViaRedstone = buf.readBoolean();
        doorRotation = buf.readFloat();
        doorID = ByteBufUtils.readUTF8String(buf);

        //Read mimic block array
        for (int i = 0; i < getMimicBlocks().length; i++)
        {
            setFillerBlock(ByteBufUtils.readItemStack(buf), i);
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeBoolean(isOpen);
        buf.writeBoolean(openViaRedstone);
        buf.writeFloat(doorRotation);
        ByteBufUtils.writeUTF8String(buf, doorID);

        //Write mimic block array
        for (int i = 0; i < getMimicBlocks().length; i++)
        {
            ItemStack stack = getMimicBlocks()[i];
            if (stack == null || stack.getItem() == null)
            {
                stack = new ItemStack(Blocks.grass);
            }
            ByteBufUtils.writeItemStack(buf, stack);
        }
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        forceOpen = nbt.getBoolean("playerSetOpen");
        isOpen = nbt.getBoolean("isOpen");
        openViaRedstone = nbt.getBoolean("shouldBeOpen");
        doorRotation = nbt.getFloat("doorRotation");
        doorID = nbt.getString("doorID");

        for (int i = 0; i < getMimicBlocks().length; i++)
        {
            String key = "mimic_block_" + i;
            if (nbt.hasKey(key))
            {
                NBTTagCompound tag = nbt.getCompoundTag(key);
                getMimicBlocks()[i] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setBoolean("playerSetOpen", forceOpen);
        nbt.setBoolean("isOpen", isOpen);
        nbt.setBoolean("shouldBeOpen", openViaRedstone);
        nbt.setFloat("doorRotation", doorRotation);
        nbt.setString("doorID", doorID);

        for (int i = 0; i < getMimicBlocks().length; i++)
        {
            ItemStack stack = getMimicBlocks()[i];
            if (stack != null && stack.getItem() != null)
            {
                String key = "mimic_block_" + i;
                nbt.setTag(key, stack.writeToNBT(new NBTTagCompound()));
            }
        }

        return super.save(nbt);
    }

    @Override
    public void setDirection(ForgeDirection direction)
    {

    }

    @Override
    public ForgeDirection getDirection()
    {
        if (dir_cache == null)
        {
            dir_cache = ForgeDirection.getOrientation(host.getHostMeta());
        }
        return dir_cache;
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti, Pos relativePos)
    {
        if (isServer())
        {
            //Get index from relative position, translate by 1 to remove negative
            int index = getBlockIndex(relativePos.xi(), relativePos.zi());
            if (index >= 0 && index < getMimicBlocks().length)
            {
                //Update filler block
                setFillerBlock(index);
            }
        }
    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, Pos relativePos, EntityPlayer player, int side, float xHit, float yHit, float zHit)
    {
        //TODO maybe add a locking feature to prevent mistakes? Wrench sneak + click maybe?
        if (isOwner(player) && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock)
        {
            //Get index from relative position, translate by 1 to remove negative
            int index = getBlockIndex(relativePos.xi(), relativePos.zi());
            if (index >= 0 && index < getMimicBlocks().length)
            {
                //Store new filler block, copy to prevent issues
                ItemStack copy = player.getHeldItem().copy();
                copy.stackSize = 1;
                setFillerBlock(copy, index);
                if (Engine.runningAsDev)
                {
                    player.addChatComponentMessage(new ChatComponentText("Setting fake block to '" + InventoryUtility.getDisplayName(copy) + "' for index '" + index + "' " + relativePos));
                }
            }
            return true;
        }
        return onPlayerActivated(player, side, xHit, yHit, zHit);
    }

    /**
     * Gets index from relative position.
     * relative -> distance from this block as whole numbers
     *
     * @param x - relative x position
     * @param z - relative z position
     * @return index in the block array
     */
    public int getBlockIndex(int x, int z)
    {
        return (x + getBlockArrayOffset()) * getDoorSize() + (z) + getBlockArrayOffset();
    }

    /**
     * Data that defines properties about the door
     *
     * @return door, or null placeholder
     */
    public DoorData getDoorData()
    {
        if (_doorData == null)
        {
            _doorData = DoorData.getDoorData(doorID);
        }
        return _doorData;
    }

    /**
     * Size (width & depth) of the door in blocks
     *
     * @return size
     */
    public int getDoorSize()
    {
        return getDoorData().blockSize;
    }

    /**
     * Gets the offset that needs to be used when converting
     * relative positions to index or back
     *
     * @return offset (if size 3 -> 1, size 5 -> 2)
     */
    public int getBlockArrayOffset()
    {
        return (getDoorSize() - 1) / 2; //In theory doors should always be odd in value (ex: (3 - 2) / 2 = 1)
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getRenderContentID()
    {
        return getDoorData().doorRender;
    }

    @Override
    public String getMultiBlockLayoutKey()
    {
        return getDoorData().doorLayout;
    }
}
