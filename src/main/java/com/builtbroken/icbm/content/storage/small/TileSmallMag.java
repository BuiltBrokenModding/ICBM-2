package com.builtbroken.icbm.content.storage.small;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.icbm.content.launcher.TileMissileContainer;
import com.builtbroken.icbm.content.storage.IMissileMag;
import com.builtbroken.icbm.content.storage.IMissileMagOutput;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.framework.block.imp.IDestroyedListener;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.codegen.annotations.ExternalInventoryWrapped;
import com.builtbroken.mc.codegen.annotations.MultiBlockWrapped;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Small 2 block tall self contained launcher for small missiles.
 * Created by robert on 3/28/2015.
 */
@TileWrapped(className = "TileEntityWrappedSmallMag")
@ExternalInventoryWrapped()
@MultiBlockWrapped()
public class TileSmallMag extends TileMissileContainer implements IMissileMag, IDestroyedListener, IRotatable
{
    protected static final ForgeDirection[] rotations = new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};

    public static final int MISSILE_MOTION_DELAY = 40; //2 seconds

    //TODO add custom delay to allow sorting of missile
    //TODO add animation for missile movement

    /** List of tiles to update */
    protected List<WeakReference<TileSmallMag>> updateList = new LinkedList();
    /** Tile to move the missile into */
    protected WeakReference<IMissileMagOutput> targetTile;

    /** Current tick delay for motion. */
    private int movementDelay = 0;

    /** Is motion queued to run. */
    private boolean doMotion = false;

    private ForgeDirection dirCache;

    public TileSmallMag()
    {
        super("mag.small", ICBM.DOMAIN);
    }

    @Override
    protected IInventory createInventory()
    {
        return new ExternalInventory(this, 1);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        if (isServer())
        {
            //TODO add cleanup tick
            if (ticks % 20 == 0)
            {
                queueMotion();
            }
            if (doMotion && movementDelay > -2 && movementDelay-- < 0)
            {
                movementDelay = -2;
                doMovement();
            }
        }
    }

    //TODO implement
    public void onNeighborChanged(Block block)
    {
        //super.onNeighborChanged(block);
        updateList.clear();
        final Location location = toLocation();
        for (ForgeDirection side : rotations)
        {
            final Location l = location.add(side);
            if (location.isChunkLoaded())
            {
                TileEntity tile = l.getTileEntity();
                if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileSmallMag)
                {
                    updateList.add(new WeakReference<TileSmallMag>((TileSmallMag) ((ITileNodeHost) tile).getTileNode()));
                }
            }
        }
        queueMotion();
    }

    /**
     * Called to move the missile
     */
    protected void doMovement()
    {
        doMotion = false;
        if (getMissileItem() != null)
        {
            if (targetTile == null
                    || targetTile.get() == null
                    || targetTile.get() instanceof TileEntity && ((TileEntity) targetTile.get()).isInvalid()
                    || targetTile.get() instanceof ITileNode && !((ITileNode) targetTile.get()).getHost().isHostValid())
            {
                Location location = toLocation().add(getDirection());
                TileEntity tile = location.getTileEntity();
                if (tile instanceof IMissileMagOutput)
                {
                    targetTile = new WeakReference<IMissileMagOutput>((IMissileMagOutput) tile);
                }
                else if (tile instanceof ITileNodeHost)
                {
                    ITileNode node = ((ITileNodeHost) tile).getTileNode();
                    if (node instanceof IMissileMagOutput)
                    {
                        targetTile = new WeakReference<IMissileMagOutput>((IMissileMagOutput) node);
                    }
                }
            }
            if (targetTile != null)
            {
                IMissile missile = getMissile();
                if (targetTile.get().canAcceptMissile(missile) && targetTile.get().storeMissile(missile))
                {
                    //Remove our missile copy
                    getInventory().setInventorySlotContents(0, null);

                    //Update nearby tiles
                    Iterator<WeakReference<TileSmallMag>> it = updateList.iterator();
                    while (it.hasNext())
                    {
                        WeakReference<TileSmallMag> ref = it.next();
                        if (ref == null || ref.get() == null || ref.get().isInvalid())
                        {
                            it.remove();
                        }
                        else
                        {
                            ref.get().queueMotion();
                        }
                    }
                }
            }
        }
    }

    public void queueMotion()
    {
        doMotion = true;
        if (movementDelay <= -2)
        {
            movementDelay = MISSILE_MOTION_DELAY;
        }
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileSize.SMALL.ordinal();
    }

    @Override
    public boolean removedByPlayer(EntityPlayer player, boolean willHarvest)
    {
        if (willHarvest && getMissileItem() != null)
        {
            InventoryUtility.dropItemStack(toLocation(), getMissileItem());
            getInventory().setInventorySlotContents(0, null);
        }
        return false;
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        //TODO change so inventory can only be accessed from front and back of the tile
        //  With front as output, back as input
        super.onInventoryChanged(slot, prev, item);
        if (slot == 0)
        {
            queueMotion();
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        oldWorld().markBlockRangeForRenderUpdate(xi(), yi(), zi(), xi(), yi(), zi());
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (dirCache == null)
        {
            dirCache = ForgeDirection.getOrientation(oldWorld().getBlockMetadata(xi(), yi(), zi()));
        }
        return dirCache;
    }

    @Override
    public void setDirection(ForgeDirection direction)
    {
        int meta = direction.ordinal();
        if (meta >= 2 && meta <= 5) //Restrict to side only rotations
        {
            dirCache = null;
            oldWorld().setBlockMetadataWithNotify(xi(), yi(), zi(), meta, 3);
        }
    }
}