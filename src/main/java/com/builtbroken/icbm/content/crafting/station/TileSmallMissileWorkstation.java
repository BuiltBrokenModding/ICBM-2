package com.builtbroken.icbm.content.crafting.station;

import com.builtbroken.icbm.api.IModuleItem;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.content.Assets;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadSmall;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * Workstation for the small missile
 * Created by DarkCow on 3/12/2015.
 */
public class TileSmallMissileWorkstation extends TileAbstractWorkstation implements IPacketIDReceiver, IRotatable, ISimpleItemRenderer
{
    //Static values
    public static final int INPUT_SLOT = 0;
    public static final int WARHEAD_SLOT = 1;
    public static final int ENGINE_SLOT = 2;
    public static final int GUIDANCE_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;

    public static final int[] INPUTS = new int[]{INPUT_SLOT, WARHEAD_SLOT, ENGINE_SLOT, GUIDANCE_SLOT};
    public static final int[] OUTPUTS = new int[]{OUTPUT_SLOT};

    public static final int MAX_PROGRESS = 200;

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

    //Machine vars
    protected boolean automated = false;
    protected boolean assemble = true;
    protected boolean enforce_complete = false;
    protected int progress = 0;
    protected ForgeDirection rotation = ForgeDirection.NORTH;
    protected ForgeDirection connectedBlockSide = ForgeDirection.UP;


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
    public void firstTick()
    {
        super.firstTick();
        this.connectedBlockSide = ForgeDirection.getOrientation(world().getBlockMetadata(xi(), yi(), zi()));
        System.out.println("First Tick");
        MultiBlockHelper.buildMultiBlock(world(), this, true, true);
    }

    @Override
    public void update()
    {
        super.update();
        //TODO add progress timer for manual building as well
        if (automated)
        {
            if (getMissileItem() != null && getStackInSlot(OUTPUT_SLOT) == null && getMissileItem().getItem() instanceof IModularMissileItem)
            {
                if (progress++ >= MAX_PROGRESS && assemble() == "")
                {
                    progress = 0;
                }
            }
        }
    }

    // Adds all components to the missile and sends it to the output slot
    public String assemble()
    {
        if (getStackInSlot(OUTPUT_SLOT) == null)
        {
            ItemStack missile_stack = getMissileItem();
            if (missile_stack == null)
                return "slot.input.empty";
            if (getStackInSlot(OUTPUT_SLOT) != null)
                return "slot.output.full";
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
                if (enforce_complete)
                {
                    if (missile.getEngine(missile_stack) == null || missile.getWarhead(missile_stack) == null || missile.getGuidance(missile_stack) == null)
                        return "slot.output.incomplete";
                }
                //Move missile to output slot even if not finished
                setInventorySlotContents(OUTPUT_SLOT, missile_stack);
                setInventorySlotContents(INPUT_SLOT, null);
                return "";
            }
            return "slot.input.invalid";
        }
        return "slot.output.full";
    }

    /**
     * Breaks down the missile into its parts. Called from the GUI or automation.
     *
     * @return error message
     */
    public String disassemble()
    {
        ItemStack missile_stack = getMissileItem();
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
            if (getStackInSlot(OUTPUT_SLOT) != null)
                return "slot.output.full";

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
            setInventorySlotContents(OUTPUT_SLOT, missile_stack);
            setInventorySlotContents(INPUT_SLOT, null);
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
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isClient())
        {
            if (id == 0)
            {
                this.automated = buf.readBoolean();
                this.enforce_complete = buf.readBoolean();
                return true;
            }
            else if (id == 5)
            {
                this.rotation = ForgeDirection.getOrientation(Math.min(0, Math.max(buf.readByte(), 5)));
                return true;
            }
        }
        return false;
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, automated, enforce_complete);
    }

    public void setAutomated(boolean b)
    {
        this.automated = b;
    }

    public void setEnforceComplete(boolean b)
    {
        this.enforce_complete = b;
    }

    public void setAssemble(boolean b)
    {
        this.assemble = b;
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
                addItemToSlot(player, INPUT_SLOT);
            }
            else if (player.isSneaking())
            {
                removeItemFromSlot(player, OUTPUT_SLOT);
            }
            else
            {
                handleSlot(player, GUIDANCE_SLOT);
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

    private void handleSlot(EntityPlayer player, int slot)
    {
        if (!addItemToSlot(player, slot))
        {
            removeItemFromSlot(player, slot);
        }
    }

    private boolean addItemToSlot(EntityPlayer player, int slot)
    {
        if (slot >= 0 && slot < getSizeInventory())
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
        }
        return false;
    }

    private boolean removeItemFromSlot(EntityPlayer player, int slot)
    {
        if (slot >= 0 && slot < getSizeInventory())
        {
            boolean itemsMatch = InventoryUtility.stacksMatch(player.getHeldItem(), getStackInSlot(slot));
            boolean spaceInHand = player.getHeldItem() == null || player.getHeldItem().stackSize < player.getHeldItem().getItem().getItemStackLimit(player.getHeldItem());
            if (getStackInSlot(slot) != null && (player.getHeldItem() == null || itemsMatch && spaceInHand))
            {
                player.getHeldItem().stackSize++;
                getStackInSlot(slot).stackSize--;
                if (getStackInSlot(slot).stackSize <= 0)
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
            if (stack.getItem() instanceof IModularMissileItem && (slot == INPUT_SLOT || slot == OUTPUT_SLOT))
            {
                Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
                return missile.casing == MissileCasings.SMALL;
            }
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
                setDirection(getDirection().getOpposite());
            }
            else
            {
                switch (this.connectedBlockSide)
                {
                    case UP:
                    case DOWN:
                        if (getDirection() == ForgeDirection.NORTH)
                            setDirection(ForgeDirection.EAST);
                        else if (getDirection() == ForgeDirection.SOUTH)
                            setDirection(ForgeDirection.WEST);
                        else if (getDirection() == ForgeDirection.EAST)
                            setDirection(ForgeDirection.SOUTH);
                        else
                            setDirection(ForgeDirection.NORTH);
                    case EAST:
                    case WEST:
                        if (getDirection() == ForgeDirection.NORTH)
                            setDirection(ForgeDirection.DOWN);
                        else if (getDirection() == ForgeDirection.SOUTH)
                            setDirection(ForgeDirection.UP);
                        else if (getDirection() == ForgeDirection.DOWN)
                            setDirection(ForgeDirection.SOUTH);
                        else
                            setDirection(ForgeDirection.UP);
                    case NORTH:
                    case SOUTH:
                        if (getDirection() == ForgeDirection.EAST)
                            setDirection(ForgeDirection.DOWN);
                        else if (getDirection() == ForgeDirection.WEST)
                            setDirection(ForgeDirection.UP);
                        else if (getDirection() == ForgeDirection.DOWN)
                            setDirection(ForgeDirection.WEST);
                        else
                            setDirection(ForgeDirection.UP);
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
    public void setDirection(ForgeDirection direction)
    {
        //Get the new rotation, and limit it to valid rotations
        ForgeDirection prev = direction;
        switch (connectedBlockSide)
        {
            case UP:
            case DOWN:
                if (direction != ForgeDirection.DOWN && direction != ForgeDirection.UP)
                {
                    rotation = direction;
                }
                break;
            case EAST:
            case WEST:
                if (direction != ForgeDirection.EAST && direction != ForgeDirection.WEST)
                {
                    rotation = direction;
                }
                break;
            case NORTH:
            case SOUTH:
                if (direction != ForgeDirection.NORTH && direction != ForgeDirection.SOUTH)
                {
                    rotation = direction;
                }
                break;
        }

        //Check if the rotation was valid
        if (isServer() && prev != direction)
        {
            //Rotate 90 degrees meaning we need a structure update
            if (prev != rotation.getOpposite())
            {
                //TODO check if it can rotate without clipping blocks
                for (IPos3D pos : getLayoutOfMultiBlock().keySet())
                {
                    Block block = world().getBlock((int) pos.x(), (int) pos.y(), (int) pos.z());
                    if (!block.isAir(world(), (int) pos.x(), (int) pos.y(), (int) pos.z()))
                    {
                        this.rotation = prev;
                    }
                }
            }

        }

        //Checked again as the above code can change the data
        if (isServer() && prev != direction)
        {
            sendPacket(new PacketTile(this, 5, (byte) connectedBlockSide.ordinal()));
        }
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.7f, .7f, .7f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.SMALL_MISSILE_STATION_MODEL.renderAll();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.53f, pos.yf(), pos.zf() + 0.5f);
        GL11.glRotated(90, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);

        //Keep in mind the directions are of the facing block
        switch (connectedBlockSide)
        {

            case UP:
                if (getDirection() == ForgeDirection.WEST || getDirection() == ForgeDirection.EAST)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-0.0255f, 0f, 0.033f);
                }
                break;
            case DOWN:
                GL11.glRotated(180, 1, 0, 0);
                // z y x
                GL11.glTranslatef(0.005f, -1f, 0.055f);
                if (getDirection() == ForgeDirection.WEST || getDirection() == ForgeDirection.EAST)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-0.0255f, 0f, 0.033f);
                }
                break;
            case EAST:
                GL11.glRotated(90, 1, 0, 0);
                // z x y
                GL11.glTranslatef(0.007f, -0.536f, -0.475f);
                if (getDirection() == ForgeDirection.UP || getDirection() == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-0.02f, 0f, 0.038f);
                }
                break;
            case WEST:
                GL11.glRotated(-90, 1, 0, 0);
                // z x y
                GL11.glTranslatef(0.015f, -0.47f, 0.52f);
                this.rotation = ForgeDirection.EAST;
                if (getDirection() == ForgeDirection.UP || getDirection() == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-0.02f, -0.01f, 0.05f);
                }
                break;
            case NORTH:
                //GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() - 0.1f, pos.zf() + 0.5f);
                GL11.glRotated(90, 0, 1, 0);
                GL11.glRotated(90, 1, 0, 0);
                //GL11.glRotated(1.1, 1, 0, 0);
                // y x z
                GL11.glTranslatef(0.0355f, -0.5f, -0.47f);
                if (getDirection() == ForgeDirection.UP || getDirection() == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y z x
                    GL11.glTranslatef(-0.02f, -0.01f, 0.04f);
                }
                break;
            case SOUTH:
                GL11.glRotated(90, 0, 1, 0);
                GL11.glRotated(-90, 1, 0, 0);
                // x z y
                GL11.glTranslatef(0.0355f, -0.5f, 0.53f);
                if (getDirection() == ForgeDirection.UP || getDirection() == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y z x
                    GL11.glTranslatef(-0.02f, -0.01f, 0.04f);
                }
                break;

        }
        Assets.SMALL_MISSILE_STATION_MODEL.renderAll();
        GL11.glPopMatrix();

        //Render missile
        if (getMissileItem() != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
            GL11.glScaled(.0015625f, .0015625f, .0015625f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
            Assets.SMALL_MISSILE_MODEL.renderAll();
            GL11.glPopMatrix();
        }
    }
}
