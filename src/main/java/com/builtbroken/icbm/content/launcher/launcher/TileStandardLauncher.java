package com.builtbroken.icbm.content.launcher.launcher;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.launcher.block.LauncherPartListener;
import com.builtbroken.icbm.content.missile.data.missile.Missile;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.block.imp.IActivationListener;
import com.builtbroken.mc.framework.block.imp.IBlockStackListener;
import com.builtbroken.mc.framework.block.imp.IWrenchListener;
import com.builtbroken.mc.framework.multiblock.MultiBlockHelper;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
@TileWrapped(className = "TileWrapperStandardLauncher", wrappers = "MultiBlock")
public class TileStandardLauncher extends TileAbstractLauncher implements IRotation, IWrenchListener, IActivationListener, IBlockStackListener
{
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

    public MissileSize missileSize = MissileSize.STANDARD;

    public TileStandardLauncher()
    {
        super("launcher.standard", ICBM.DOMAIN);
    }

    @Override
    public ItemStack toStack()
    {
        Block blockDrop = InventoryUtility.getBlock("icbm:icbmLauncherParts");
        if (blockDrop != null)
        {
            return new ItemStack(blockDrop, 1, 0);
        }
        return null;
    }

    @Override
    public String uniqueContentID()
    {
        return "launcher." + missileSize.name().toLowerCase();
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
    public void update(long ticks)
    {
        super.update(ticks);
        if(isServer())
        {
            if (ticks == 1 || ticks % 20 == 0)
            {
                if (buildMissileBlocks)
                {
                    buildMissileBlocks = false;
                    MultiBlockHelper.buildMultiBlock(world().unwrap(), (IMultiTileHost) getHost(), true, true);
                }
                else if (destroyMissileBlocks)
                {
                    destroyMissileBlocks = false;
                    MultiBlockHelper.destroyMultiBlockStructure((IMultiTileHost) getHost(), false, true, false);
                }
            }
            //Check to ensure the frame is still intact
            if (ticks % frameUpdateCheckTick == 0)
            {
                //Check if broken by counting number of frames
                int count = LauncherPartListener.getFrameCount(world().unwrap(), new Pos(this).add(0, 1, 0));
                MissileSize size = LauncherPartListener.getLauncherSize(count);
                //If we do not have 5 blocks drop the missile and set the block back to CPU
                if (size != missileSize)
                {
                    dropItems();
                    Block blockDrop = InventoryUtility.getBlock("icbm:icbmLauncherParts");
                    if (blockDrop != null)
                    {
                        world().unwrap().setBlock(xi(), yi(), zi(), blockDrop);
                    }
                }
                else
                {
                    //Updates top block meta for older versions of ICBM
                    int meta = world().unwrap().getBlockMetadata(xi(), yi() + count, zi());
                    int dMeta = getMetaForDirection(getDirection());
                    if (meta != dMeta)
                    {
                        world().unwrap().setBlockMetadataWithNotify(xi(), yi() + count, zi(), dMeta, 3);
                    }
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
    public boolean onPlayerRightClickWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
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
                    getInventory().setInventorySlotContents(0, stack); //sends packet when slot is set
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
    public boolean onPlayerActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
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
                            getInventory().setInventorySlotContents(0, copy);
                            if (!player.capabilities.isCreativeMode)
                            {
                                player.getHeldItem().stackSize--;
                                if (player.getHeldItem().stackSize <= 0)
                                {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                                }
                                player.inventoryContainer.detectAndSendChanges();
                            }
                            //TODO add translation key
                            player.addChatComponentMessage(new ChatComponentText("* Missile added *"));
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
            //Recipe handler for standard missile
            if (missileSize == MissileSize.STANDARD && StandardMissileCrafting.isPartOfRecipe(player.getHeldItem()))
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
            dir_cache = ForgeDirection.getOrientation(host.getHostMeta());
        }
        return dir_cache;
    }

    @Override
    public Pos getMissileLaunchOffset()
    {
        switch (missileSize)
        {
            case MICRO:
                return new Pos(getDirection()).add(0, 3, 0);
            case SMALL:
                return new Pos(getDirection()).add(0, 5, 0);
            case STANDARD:
                return new Pos(getDirection()).add(0, 7, 0);
            case MEDIUM:
                return new Pos(getDirection()).multiply(2).add(0, 19, 0);
        }
        return new Pos(getDirection()).add(0, 20, 0);
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack stackPrev, ItemStack newStack)
    {
        if (slot == 0 && isServer())
        {
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
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        return super.read(buf, id, player, type);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        //Size
        buf.writeByte(missileSize.ordinal());

        //missile data
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
    public void readDescPacket(ByteBuf buf)
    {
        //Size
        missileSize = MissileSize.get(buf.readByte());

        //Missile data
        byte type = buf.readByte();
        if (type == 0)
        {
            ItemStack missileStack = ByteBufUtils.readItemStack(buf);
            if (missileStack.getItem() instanceof IMissileItem)
            {
                missile = ((IMissileItem) missileStack.getItem()).toMissile(missileStack);
            }
            else
            {
                missile = new Missile(missileStack);
            }
        }
        else if (type == 1)
        {
            this.missile = null;
            if (recipe == null)
            {
                recipe = new StandardMissileCrafting();
            }
            recipe.readBytes(buf);
        }
        else if (type == 2 || type == 3)
        {
            this.missile = null;
            this.recipe = null;
            this.isCrafting = false;
        }
    }

    @Override
    protected void onPostMissileFired(final Pos target, EntityMissile entity)
    {
        super.onPostMissileFired(target, entity);
        destroyMissileBlocks = true;
        //Set ground on fire for lolz
        if (entity.getMissile() != null && entity.getMissile().getEngine() != null && entity.getMissile().getEngine().generatesFire(entity, entity.getMissile()))
        {
            final Location center = toLocation().add(getDirection());

            //set location around launcher on fire
            for (int x = -1; x < 2; x++)
            {
                for (int z = -1; z < 2; z++)
                {
                    if (world().unwrap().rand.nextFloat() < 0.3f)
                    {
                        final Location pos = center.add(x, 0, z);

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

    @Override
    public boolean canFireMissile()
    {
        return !isCrafting;
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == missileSize.ordinal();
    }


    //public String getInventoryName()
    //{
    //    return "tile.icbm:standardLauncher.container";
    //}

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        if (nbt.hasKey("missileRecipe"))
        {
            triggerCraftingMode();
            recipe = new StandardMissileCrafting();
            recipe.load(nbt.getCompoundTag("missileRecipe"));
        }
        if (nbt.hasKey("missileSize"))
        {
            missileSize = MissileSize.get(nbt.getInteger("missileSize"));
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        if (recipe != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            recipe.save(nbt);
            nbt.setTag("missileRecipe", tag);
        }
        nbt.setInteger("missileSize", missileSize.ordinal());
        return nbt;
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

    protected final void dropItems()
    {
        if (getMissileItem() != null)
        {
            InventoryUtility.dropItemStack(toLocation(), getMissileItem());
            getInventory().setInventorySlotContents(0, null);
        }
        if (recipe != null)
        {
            recipe.dropItems(toLocation());
            recipe = null;
        }
    }
}
