package com.builtbroken.icbm.content.crafting.station;

import com.builtbroken.icbm.api.IModuleItem;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadSmall;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * Workstation for the small missile
 * Created by DarkCow on 3/12/2015.
 */
public class TileSmallMissileWorkstation extends TileAbstractWorkstation implements IPacketIDReceiver, IRotatable
{
    //Static values
    public static final int INPUT_SLOT = 0;
    public static final int WARHEAD_SLOT = 1;
    public static final int ENGINE_SLOT = 2;
    public static final int GUIDANCE_SLOT = 3;

    public static final int[] INPUTS = new int[]{INPUT_SLOT, WARHEAD_SLOT, ENGINE_SLOT, GUIDANCE_SLOT};
    public static final int[] OUTPUTS = new int[]{INPUT_SLOT};

    /** Multi-block set for up-down row */
    public static HashMap<IPos3D, String> upDownMap;
    /** Multi-block set for east-west row */
    public static HashMap<IPos3D, String> eastWestMap;
    /** Multi-block set for north-south row */
    public static HashMap<IPos3D, String> northSouthMap;

    static
    {
        //Only 3 actual multi-block sets used 4 times each to create the 12 different rotation cases
        upDownMap = new HashMap();
        upDownMap.put(new Pos(0, 1, 0), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");
        upDownMap.put(new Pos(0, -1, 0), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");

        eastWestMap = new HashMap();
        eastWestMap.put(new Pos(1, 0, 0), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");
        eastWestMap.put(new Pos(-1, 0, 0), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");

        northSouthMap = new HashMap();
        northSouthMap.put(new Pos(0, 0, 1), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");
        northSouthMap.put(new Pos(0, 0, -1), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");
    }

    /** Rotation of the block, not all rotation are valid, do not set directly see {@link TileSmallMissileWorkstation#setDirection(ForgeDirection)}. */
    public ForgeDirection rotation = ForgeDirection.NORTH;
    /** Connected side of the block, if set reset mutli block structure to avoid ghost blocks & invalid renders. */
    public ForgeDirection connectedBlockSide = ForgeDirection.UP;


    public TileSmallMissileWorkstation()
    {
        super("missileworkstation", Material.iron);
        this.addInventoryModule(5);
        this.itemBlock = ItemBlockMissileStation.class;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
        this.isOpaque = false;
    }

    @Override
    public Tile newTile()
    {
        return new TileSmallMissileWorkstation();
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        this.connectedBlockSide = ForgeDirection.getOrientation(world().getBlockMetadata(xi(), yi(), zi()));
        MultiBlockHelper.buildMultiBlock(world(), this, true, true);
        //TODO validate rotation so data is not confused
    }

    @Override
    public void update()
    {
        super.update();
        //TODO add progress timer for manual building as well
        //TODO re-add automation

        //Eject items if not missile is in the slot
        if (getMissileItem() == null && ticks % 5 == 0)
        {
            ejectCraftingItems();
        }
    }

    /**
     * Called to eject crafting items
     */
    public void ejectCraftingItems()
    {
        for (int i = 1; i <= 3; i++)
        {
            InventoryUtility.dropItemStack(toLocation(), getStackInSlot(i));
            setInventorySlotContents(i, null);
        }
    }


    // Adds all components to the missile and sends it to the output slot
    public String assemble()
    {
        ItemStack missile_stack = getMissileItem();
        if (missile_stack == null)
            return "slot.input.empty";
        if (missile_stack.getItem() instanceof IModularMissileItem)
        {
            IModularMissileItem missile = (IModularMissileItem) missile_stack.getItem();

            //Insert engine
            if (getEngineItem() != null && missile.getEngine(missile_stack) == null)
                if (missile.setEngine(missile_stack, getEngineItem(), true))
                {
                    missile.setEngine(missile_stack, getEngineItem(), false);
                    setInventorySlotContents(ENGINE_SLOT, null);
                }

            //Insert warhead
            if (getWarheadItem() != null && missile.getWarhead(missile_stack) == null)
                if (missile.setWarhead(missile_stack, getWarheadItem(), true))
                {
                    missile.setWarhead(missile_stack, getWarheadItem(), false);
                    setInventorySlotContents(WARHEAD_SLOT, null);
                }

            //Insert guidance
            if (getGuidanceItem() != null && missile.getGuidance(missile_stack) == null)
                if (missile.setGuidance(missile_stack, getGuidanceItem(), true))
                {
                    missile.setGuidance(missile_stack, getGuidanceItem(), false);
                    setInventorySlotContents(GUIDANCE_SLOT, null);
                }
            //Move missile to output slot even if not finished
            setInventorySlotContents(INPUT_SLOT, missile_stack);
            return "";
        }
        return "slot.input.invalid";
    }

    /**
     * Breaks down the missile into its parts. Called from the GUI or automation.
     *
     * @return error message
     */
    public String disassemble()
    {
        ItemStack missile_stack = getMissileItem();
        ejectCraftingItems();
        if (missile_stack != null && missile_stack.getItem() instanceof IModularMissileItem)
        {
            IModularMissileItem missile = (IModularMissileItem) missile_stack.getItem();

            //Check if output slots are empty
            if (missile.getEngine(missile_stack) != null && getEngineItem() != null)
                return "slot.engine.full";
            if (missile.getWarhead(missile_stack) != null && getWarheadItem() != null)
                return "slot.warhead.full";
            if (missile.getGuidance(missile_stack) != null && getGuidanceItem() != null)
                return "slot.guidance.full";

            //Remove items from missile
            if (getWarheadItem() == null)
                setInventorySlotContents(WARHEAD_SLOT, missile.getWarhead(missile_stack));
            if (getEngineItem() == null)
                setInventorySlotContents(ENGINE_SLOT, missile.getEngine(missile_stack));
            if (getGuidanceItem() == null)
                setInventorySlotContents(GUIDANCE_SLOT, missile.getGuidance(missile_stack));

            //Clear items off of the missile
            if (missile.getEngine(missile_stack) != null && !missile.setEngine(missile_stack, null, false))
                return "missile.engine.error.set";
            if (missile.getWarhead(missile_stack) != null && !missile.setWarhead(missile_stack, null, false))
                return "missile.warhead.error.set";
            if (missile.getGuidance(missile_stack) != null && !missile.setGuidance(missile_stack, null, false))
                return "missile.guidance.error.set";

            //Move missile to output slot
            setInventorySlotContents(INPUT_SLOT, missile_stack);
            return "";
        }
        return "slot.input.invalid";
    }

    public ItemStack getWarheadItem()
    {
        return getStackInSlot(WARHEAD_SLOT);
    }

    public ItemStack getEngineItem()
    {
        return getStackInSlot(ENGINE_SLOT);
    }

    public ItemStack getGuidanceItem()
    {
        return getStackInSlot(GUIDANCE_SLOT);
    }

    public ItemStack getMissileItem()
    {
        return getStackInSlot(INPUT_SLOT);
    }

    @Override
    public PacketTile getDescPacket()
    {
        if (getMissileItem() != null)
        {
            return new PacketTile(this, 0, rotation, getMissileItem());
        }
        return new PacketTile(this, 0, rotation, new ItemStack(Items.apple));
    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        switch (connectedBlockSide)
        {
            case UP:
            case DOWN:
                if (getDirection() == ForgeDirection.EAST || getDirection() == ForgeDirection.WEST)
                {
                    return eastWestMap;
                }
                else
                {
                    return northSouthMap;
                }
            case EAST:
            case WEST:
                if (getDirection() == ForgeDirection.DOWN || getDirection() == ForgeDirection.UP)
                {
                    return upDownMap;
                }
                else
                {
                    return northSouthMap;
                }
            case NORTH:
            case SOUTH:
                if (getDirection() == ForgeDirection.DOWN || getDirection() == ForgeDirection.UP)
                {
                    return upDownMap;
                }
                else
                {
                    return eastWestMap;
                }
        }
        return eastWestMap;
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            //Find slot to place or removes items from
            if (getMissileItem() == null)
            {
                if (addItemToSlot(player, INPUT_SLOT))
                {
                    disassemble();
                    player.addChatComponentMessage(new ChatComponentText("Missile added, and broken down"));
                }
                else if (getMissileItem() != null)
                {
                    player.addChatComponentMessage(new ChatComponentText("Slot already filled"));
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText("Invalid input"));
                }
            }
            else if (player.isSneaking())
            {
                assemble();
                removeItemFromSlot(player, INPUT_SLOT);
                player.addChatComponentMessage(new ChatComponentText("Missile remove, and assembled"));
            }
            else
            {
                if (handleSlot(player, GUIDANCE_SLOT))
                {

                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText("Invalid input"));
                }
            }
        }
        return true;
    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, IPos3D hit)
    {
        if (isServer())
        {
            //Get offset from center
            Pos pos = new Pos((TileEntity) tile).sub(xi(), yi(), zi());

            //Ensure pos is in our layout
            if (getLayoutOfMultiBlock().containsKey(pos) || pos.equals(Pos.zero))
            {
                int slot = -1;

                //Find slot to place or removes items from
                if (getMissileItem() != null)
                {
                    if (pos.equals(new Pos(getDirection())))
                    {
                        slot = WARHEAD_SLOT;
                    }
                    else if (pos.equals(new Pos(getDirection().getOpposite())))
                    {
                        slot = ENGINE_SLOT;
                    }
                }
                else
                {
                    slot = INPUT_SLOT;
                }

                //If we have a slot do action
                handleSlot(player, slot);
            }
        }
        return true;
    }

    private boolean handleSlot(EntityPlayer player, int slot)
    {
        if (!addItemToSlot(player, slot))
        {
            return removeItemFromSlot(player, slot);
        }
        return true;
    }

    private boolean addItemToSlot(EntityPlayer player, int slot)
    {
        if (getStackInSlot(slot) == null && isItemValidForSlot(slot, player.getHeldItem()))
        {
            setInventorySlotContents(slot, player.getHeldItem().copy());
            getStackInSlot(slot).stackSize = 1;
            if (!player.capabilities.isCreativeMode)
            {
                player.getHeldItem().stackSize--;
                if (player.getHeldItem().stackSize <= 0)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
            player.inventoryContainer.detectAndSendChanges();
            return true;
        }
        return false;
    }

    private boolean removeItemFromSlot(EntityPlayer player, int slot)
    {
        if (slot >= 0 && slot < getSizeInventory())
        {
            boolean itemsMatch = InventoryUtility.stacksMatch(player.getHeldItem(), getStackInSlot(slot));
            boolean spaceInHand = player.getHeldItem() == null || player.getHeldItem().stackSize < player.getHeldItem().getItem().getItemStackLimit(player.getHeldItem());
            if (getStackInSlot(slot) != null)
            {
                if(player.getHeldItem() == null)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, getStackInSlot(slot));
                    setInventorySlotContents(slot, null);
                }
                else if(itemsMatch && spaceInHand)
                {
                    player.getHeldItem().stackSize++;
                    getStackInSlot(slot).stackSize--;
                    if (getStackInSlot(slot).stackSize <= 0)
                    {
                        setInventorySlotContents(slot, null);
                    }
                }
                else if(!player.inventory.addItemStackToInventory(getStackInSlot(slot)))
                {
                    InventoryUtility.dropItemStack(new Location(player), getStackInSlot(slot));
                    setInventorySlotContents(slot, null);
                }
                else
                {
                    setInventorySlotContents(slot, null);
                }

                player.inventoryContainer.detectAndSendChanges();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack != null)
        {
            //Missile slots
            if (stack.getItem() instanceof IModularMissileItem && slot == INPUT_SLOT)
            {
                Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
                return missile.casing == MissileCasings.SMALL;
            }
            //Modules slots
            else if (stack.getItem() instanceof IModuleItem)
            {
                AbstractModule module = ((IModuleItem) stack.getItem()).getModule(stack);
                if (module instanceof RocketEngine && slot == ENGINE_SLOT)
                {
                    return true;
                }
                else if (module instanceof Guidance && slot == GUIDANCE_SLOT)
                {
                    return true;
                }
                else if (module instanceof WarheadSmall && slot == WARHEAD_SLOT)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            if (player.isSneaking())
            {
                //Simple rotation so not extra checks are needed
                player.addChatComponentMessage(new ChatComponentText("Inverted station rotation"));
                setDirection(getDirection().getOpposite());
            }
            else
            {
                ForgeDirection newDir = ForgeDirection.UNKNOWN;
                switch (this.connectedBlockSide)
                {
                    case UP:
                    case DOWN:
                        if (getDirection() == ForgeDirection.NORTH)
                            newDir = ForgeDirection.EAST;
                        else if (getDirection() == ForgeDirection.SOUTH)
                            newDir = ForgeDirection.WEST;
                        else if (getDirection() == ForgeDirection.EAST)
                            newDir = ForgeDirection.SOUTH;
                        else
                            newDir = ForgeDirection.NORTH;
                        break;
                    case EAST:
                    case WEST:
                        if (getDirection() == ForgeDirection.NORTH)
                            newDir = ForgeDirection.DOWN;
                        else if (getDirection() == ForgeDirection.SOUTH)
                            newDir = ForgeDirection.UP;
                        else if (getDirection() == ForgeDirection.DOWN)
                            newDir = ForgeDirection.SOUTH;
                        else
                            newDir = ForgeDirection.NORTH;
                        break;
                    case NORTH:
                    case SOUTH:
                        if (getDirection() == ForgeDirection.EAST)
                            newDir = ForgeDirection.DOWN;
                        else if (getDirection() == ForgeDirection.WEST)
                            newDir = ForgeDirection.UP;
                        else if (getDirection() == ForgeDirection.DOWN)
                            newDir = ForgeDirection.WEST;
                        else
                            newDir = ForgeDirection.EAST;
                        break;
                }
                if (newDir != ForgeDirection.UNKNOWN && isRotationBlocked(newDir))
                {
                    player.addChatComponentMessage(new ChatComponentText("Rotated to face " + newDir));
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText("Can't rotate " + newDir + " as there are blocks in the way"));
                }
            }
        }
        return true;
    }

    @Override
    public ForgeDirection getDirection()
    {
        return rotation;
    }

    @Override
    public void setDirection(ForgeDirection newDir)
    {
        //Rotation can't equal the connected side or it's opposite as this will place it into a wall
        if (rotation != newDir && newDir != connectedBlockSide && newDir != connectedBlockSide.getOpposite())
        {
            //Check if the rotation was valid
            if (isServer())
            {
                //Only run placement and structure checks if rotated by 90 degrees
                if (rotation != rotation.getOpposite())
                {
                    if (!isRotationBlocked(rotation))
                    {

                        //Clear and rebuild multi block
                        rotating = true;
                        breakDownStructure(false);
                        //Change rotation after breaking down the structure and before making the new structure
                        rotation = newDir;
                        MultiBlockHelper.buildMultiBlock(world(), this, true, true);
                        rotating = false;
                    }
                }
                else
                {
                    rotation = newDir;
                }
                sendPacket(new PacketTile(this, 5, (byte) rotation.ordinal()));
            }
        }
    }
}
