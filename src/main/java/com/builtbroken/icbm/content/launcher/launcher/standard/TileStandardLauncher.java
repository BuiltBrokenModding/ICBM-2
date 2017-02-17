package com.builtbroken.icbm.content.launcher.launcher.standard;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.launcher.TileAbstractLauncherPad;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
public class TileStandardLauncher extends TileAbstractLauncherPad implements IMultiTileHost
{
    private static final HashMap[] STR_MAPS = new HashMap[4];
    /** Is the silo in crafting mode. */
    protected boolean isCrafting = false;
    /** Places blocks near the missile so to create collisions */
    protected boolean buildMissileBlocks = false;
    /** Called to remove blocks near the missile to remove collision */
    protected boolean destroyMissileBlocks = false;
    /** Current recipe and progress. */
    protected StandardMissileCrafting recipe;

    private ForgeDirection dir_cache;
    private int frameUpdateCheckTick = 1;

    static
    {
        for (ForgeDirection direction : new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST})
        {
            HashMap<IPos3D, String> map = new HashMap();
            for (int i = 0; i < 5; i++)
            {
                map.put(new Pos(direction.offsetX, i, direction.offsetZ), EnumMultiblock.TILE.getTileName() + "#bounds={0.3,0,0.3,0.7,1,0.7}");
            }

            STR_MAPS[direction.ordinal() - 2] = map;
        }
    }


    public TileStandardLauncher()
    {
        super("standardlauncher");
        this.hardness = 10f;
        this.resistance = 10f;
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        frameUpdateCheckTick = 10 + (int) (30 * Math.random());
        if (isCrafting || getMissileItem() != null)
        {
            buildMissileBlocks = true;
        }
    }

    @Override
    public void update()
    {
        super.update();
        if (ticks % 20 == 0)
        {
            if (buildMissileBlocks)
            {
                buildMissileBlocks = false;
                MultiBlockHelper.buildMultiBlock(world(), this, true, true);
            }
            else if (destroyMissileBlocks)
            {
                destroyMissileBlocks = false;
                MultiBlockHelper.destroyMultiBlockStructure(this, false, true, false);
            }
        }
        //Check to ensure the frame is still intact
        if (ticks % frameUpdateCheckTick == 0)
        {
            //Check if broken by counting number of frames
            int count = 0;
            Block block = world().getBlock(xi(), yi() + 1, zi());
            while (count < 6 && block == ICBM.blockLauncherFrame)
            {
                //Increase count
                count++;
                //Get next block above last
                block = world().getBlock(xi(), yi() + count, zi());
            }
            //If we do not have 5 blocks drop the missile and set the block back to CPU
            if (count != 6)
            {
                dropItems();
                world().setBlock(xi(), yi(), zi(), ICBM.blockLauncherParts);
            }
            else
            {
                //Updates top block meta for older versions of ICBM
                int meta = world().getBlockMetadata(xi(), yi() + 6, zi());
                int dMeta = getMetaForDirection(getDirection());
                if (meta != dMeta)
                {
                    worldObj.setBlockMetadataWithNotify(xi(), yi() + 6, zi(), dMeta, 3);
                }
            }
        }
    }

    private static int getMetaForDirection(ForgeDirection dir)
    {
        switch (dir)
        {
            case NORTH:
                return 1;
            case SOUTH:
                return 2;
            case EAST:
                return 3;
            case WEST:
                return 4;
        }
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target)
    {
        return new ItemStack(ICBM.blockLauncherParts, 1, 0);
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (recipe != null && isServer())
        {
            if (recipe.isFinished())
            {
                ItemStack stack = recipe.getMissileAsItem();
                if (stack != null)
                {
                    //Make sure to disable crafting before setting slot
                    disableCraftingMode();
                    setInventorySlotContents(0, stack); //sends packet when slot is set
                }
                else
                {
                    //TODO add more detailed error report and eject invalid parts
                    player.addChatComponentMessage(new ChatComponentText("Error missile stack is null"));
                }
            }
            else
            {
                //Output missing recipe items
                player.addChatComponentMessage(recipe.getCurrentRecipeChat());
            }
        }
        return true;
    }


    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        //Debug code
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.stick)
        {
            if (Engine.runningAsDev)
            {
                if (isServer())
                {
                    player.addChatComponentMessage(new ChatComponentText("Missile: " + getMissile()));
                }
                return true;
            }
        }

        if (getMissile() == null)
        {
            //Insert missile item if the player has one, normally should only work in creative mode
            if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IModularMissileItem)
            {
                if (isServer())
                {
                    IMissile missile = ((IModularMissileItem) player.getHeldItem().getItem()).toMissile(player.getHeldItem());
                    if (missile != null)
                    {
                        if (canAcceptMissile(missile))
                        {
                            ItemStack copy = player.getHeldItem().copy();
                            copy.stackSize = 1;
                            this.setInventorySlotContents(0, copy);
                            if (!player.capabilities.isCreativeMode)
                            {
                                player.getHeldItem().stackSize--;
                                if (player.getHeldItem().stackSize <= 0)
                                {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                                }
                                player.inventoryContainer.detectAndSendChanges();
                            }
                        }
                        else
                        {
                            //TODO add translation key
                            player.addChatComponentMessage(new ChatComponentText("Invalid missile size or type"));
                        }
                    }
                    else
                    {
                        //TODO add translation key
                        player.addChatComponentMessage(new ChatComponentText("Invalid missile item"));
                    }
                }
                return true;
            }
            if (StandardMissileCrafting.isPartOfRecipe(player.getHeldItem()))
            {
                //TODO implement recipe insertion or selection system
                if (recipe == null)
                {
                    triggerCraftingMode();
                    this.recipe = new StandardMissileCrafting();
                    player.addChatComponentMessage(new ChatComponentText("Starting crafting for new missile"));
                }

                //Insert recipe items
                ItemStack heldItem = player.getHeldItem();
                if (heldItem != null)
                {
                    if (isServer())
                    {
                        heldItem = heldItem.copy();
                        if (recipe.canAddItem(heldItem))
                        {
                            //Add item to recipe
                            if (!recipe.addItem(heldItem))
                            {
                                if (!recipe.isFinished())
                                {
                                    //TODO add translation key
                                    player.addChatComponentMessage(new ChatComponentText("Odd that should fit..."));
                                }
                                else
                                {
                                    //TODO add random finish messages
                                    player.addChatComponentMessage(new ChatComponentText("Recipe finished, click with wrench or screwdriver."));
                                }
                            }
                            //Check match to prevent ghost updates( updates when nothing happens)
                            else if (!InventoryUtility.stacksMatchExact(player.getHeldItem(), heldItem))
                            {
                                //Null item if stackSize is zero
                                if (heldItem.stackSize <= 0)
                                {
                                    heldItem = null;
                                }
                                //Update inventory
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem);
                                player.inventoryContainer.detectAndSendChanges();
                                sendDescPacket();
                            }
                        }
                        else
                        {
                            player.addChatComponentMessage(recipe.getCurrentRecipeChat());
                        }
                        return true;
                    }
                    //If not a block output missing crafting items
                    else if (Block.getBlockFromItem(player.getHeldItem().getItem()) == null)
                    {
                        if (isServer())
                        {
                            player.addChatComponentMessage(recipe.getCurrentRecipeChat());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (dir_cache == null)
        {
            dir_cache = ForgeDirection.getOrientation(toPos().getBlockMetadata(world()));
        }
        return dir_cache;
    }

    @Override
    public Pos getMissileLaunchOffset()
    {
        return new Pos(getDirection()).add(0.5, 7, 0.5);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (slot == 0 && isServer())
        {
            ItemStack stackPrev = getStackInSlot(slot);
            super.setInventorySlotContents(slot, stack);
            ItemStack newStack = getStackInSlot(slot);
            if (isCrafting && recipe != null)
            {
                //If something sets the missile while we are crafting, eject all items
                //TODO drop all crafting items
                recipe.dropItems(this.toLocation().add(getMissileLaunchOffset()));
            }
            //Update client if missile changes
            else if (!InventoryUtility.stacksMatch(stackPrev, newStack))
            {
                //TODO if called to often add delay or move to update loop to prevent spam
                sendDescPacket();
            }

            if (getMissileItem() != null)
            {
                buildMissileBlocks = true;
                destroyMissileBlocks = false;
                disableCraftingMode();
            }
            else
            {
                buildMissileBlocks = false;
                destroyMissileBlocks = true;
            }
        }
        else
        {
            super.setInventorySlotContents(slot, stack);
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        return super.read(buf, id, player, type);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        if (getMissileItem() != null)
        {
            buf.writeByte(0);
            ByteBufUtils.writeItemStack(buf, getMissileItem());
        }
        else if (isCrafting)
        {
            if (recipe != null)
            {
                buf.writeByte(1);
                recipe.writeBytes(buf);
            }
            else
            {
                buf.writeByte(2);
            }
        }
        else
        {
            buf.writeByte(3);
        }
    }

    @Override
    protected void onPostMissileFired(final Pos target, EntityMissile entity)
    {
        super.onPostMissileFired(target, entity);
        //clear missile collision box
        MultiBlockHelper.destroyMultiBlockStructure(this, false, true, false);

        //Set ground on fire for lolz
        if (entity.getMissile() != null && entity.getMissile().getEngine() != null && entity.getMissile().getEngine().generatesFire(entity, entity.getMissile()))
        {
            final Location center = toLocation().add(getDirection());

            //set location around launcher on fire
            for (int x = -1; x < 2; x++)
            {
                for (int z = -1; z < 2; z++)
                {
                    if (world().rand.nextFloat() < 0.3f)
                    {
                        final Location pos = center.add(x, 0, z);
                        Block block = pos.getBlock();

                        //Do not set fire to frame or CPU
                        if (block != ICBM.blockStandardLauncher)
                        {
                            if (pos.isAirBlock())
                            {
                                if (pos.sub(0, 1, 0).isSideSolid(ForgeDirection.UP))
                                {
                                    pos.setBlock(Blocks.fire);
                                }
                                else
                                {
                                    for (int i = 0; i < 3; i++)
                                    {
                                        Location posBellow = pos.add(0, -i, 0);
                                        if (posBellow.isAirBlock())
                                        {
                                            if (posBellow.sub(0, 1, 0).isSideSolid(ForgeDirection.UP))
                                            {
                                                posBellow.setBlock(Blocks.fire);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                Location posBellow = center;
                                for (int i = 0; i < 3; i++)
                                {
                                    Location posUp = pos.add(0, i, 0);
                                    if (posUp.isAirBlock())
                                    {
                                        if (posBellow.isSideSolid(ForgeDirection.UP))
                                        {
                                            posUp.setBlock(Blocks.fire);
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }
                                    posBellow = posUp;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canFireMissile()
    {
        return !isCrafting;
    }

    @Override
    public Tile newTile()
    {
        return new TileStandardLauncher();
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileCasings.STANDARD.ordinal();
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:standardLauncher.container";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("missileRecipe"))
        {
            triggerCraftingMode();
            recipe = new StandardMissileCrafting();
            recipe.load(nbt.getCompoundTag("missileRecipe"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (recipe != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            recipe.save(nbt);
            nbt.setTag("missileRecipe", tag);
        }
    }

    protected void triggerCraftingMode()
    {
        this.isCrafting = true;
        buildMissileBlocks = true;
    }

    protected void disableCraftingMode()
    {
        this.isCrafting = false;
        this.recipe = null;
        if (getMissileItem() == null)
        {
            destroyMissileBlocks = true;
            buildMissileBlocks = false;
        }
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (tileMulti instanceof TileEntity)
        {
            if (getLayoutOfMultiBlock().containsKey(new Pos((TileEntity)this).sub(new Pos((TileEntity) tileMulti))))
            {
                tileMulti.setHost(this);
            }
        }
    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (isCrafting || getMissileItem() != null)
        {
            this.buildMissileBlocks = true;
        }
        return false;
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, IPos3D hit)
    {
        return onPlayerActivated(player, side, new Pos(hit));
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        if (getDirection().ordinal() > 1 && getDirection() != ForgeDirection.UNKNOWN)
        {
            return STR_MAPS[getDirection().ordinal() - 2];
        }
        return new HashMap();
    }

    @Override
    public void onRemove(Block block, int par6)
    {
        super.onRemove(block, par6);
        dropItems();
        MultiBlockHelper.destroyMultiBlockStructure(this, false, true, false);
    }

    @Override
    public boolean removeByPlayer(EntityPlayer player, boolean willHarvest)
    {
        MultiBlockHelper.destroyMultiBlockStructure(this, false, true, false);
        dropItems();
        return super.removeByPlayer(player, willHarvest);
    }

    protected final void dropItems()
    {
        if (getMissileItem() != null)
        {
            InventoryUtility.dropItemStack(toLocation(), getMissileItem());
            setInventorySlotContents(0, null);
        }
        if (recipe != null)
        {
            recipe.dropItems(toLocation());
            recipe = null;
        }
    }
}
