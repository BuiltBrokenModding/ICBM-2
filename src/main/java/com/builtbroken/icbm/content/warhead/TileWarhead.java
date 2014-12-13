package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.content.missile.RenderMissile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import resonant.api.items.ISimpleItemRenderer;
import resonant.api.tile.IRemovable;
import resonant.lib.world.explosive.ExplosiveItemUtility;
import resonant.lib.world.explosive.ExplosiveRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.explosive.IExplosive;
import resonant.api.explosive.IExplosiveContainer;
import resonant.api.TriggerCause;
import resonant.lib.prefab.tile.TileAdvanced;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.transform.region.Cuboid;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;
import resonant.lib.utility.WrenchUtility;
import resonant.lib.world.edit.WorldChangeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Block version of the warhead placed at the end of a missile
 * @author Darkguardsman
 */
public class TileWarhead extends TileAdvanced implements IExplosiveContainer, IPacketReceiver, IRemovable.ISneakPickup, ISimpleItemRenderer
{
    public String explosiveID = null;
    public boolean exploding = false;

    public TileWarhead()
    {
        super(Material.iron);
        this.setBlockHardness(100);
        this.normalRender(false);
        this.itemBlock(ItemBlockExplosive.class);
        this.bounds_$eq(new Cuboid(0.2, 0, 0.2, 0.8, 0.5, 0.8));
        this.isOpaqueCube(false);
    }

    @Override
    public void collide(Entity entity)
    {
        if(entity != null && entity.isBurning())
        {
            explode(new TriggerCause.TriggerCauseEntity(entity));
        }
    }

    @Override
    public boolean activate(EntityPlayer entityPlayer, int side, Vector3 hit)
    {
        if (entityPlayer.getCurrentEquippedItem() != null)
        {
            if (entityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel)
            {
                explode(new TriggerCause.TriggerCauseFire(ForgeDirection.getOrientation(side)));
                return true;
            }
            else if (WrenchUtility.isUsableWrench(entityPlayer, entityPlayer.getCurrentEquippedItem(), xi(), yi(), zi()))
            {
                byte change = 3;

                // Reorient the block
                switch (getBlockMetadata())
                {
                    case 0:
                        change = 2;
                        break;
                    case 2:
                        change = 5;
                        break;
                    case 5:
                        change = 3;
                        break;
                    case 3:
                        change = 4;
                        break;
                    case 4:
                        change = 1;
                        break;
                    case 1:
                        change = 0;
                        break;
                }

                setMeta(ForgeDirection.getOrientation(change).ordinal());

                world().notifyBlockChange(xi(), yi(), zi(), this.getBlockType());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPlaced(EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onPlaced(entityLiving, itemStack);
        if (!world().isRemote && ExplosiveItemUtility.getExplosive(itemStack) != null)
        {
            //Set explosive id
            this.explosiveID = ExplosiveItemUtility.getExplosive(itemStack).getID();

            //Set rotation for direction based explosives
            setMeta(determineOrientation(entityLiving));

            int power_side = -1;
            int powerMax = 0;
            // If anything can set it on fire blow up
            for (byte i = 0; i < 6; i++)
            {
                VectorWorld position = toVectorWorld();
                position.add(ForgeDirection.getOrientation(i));

                Block b = position.getBlock();

                if (world().getIndirectPowerLevelTo(xi(), yi(), zi(), i) > powerMax)
                {
                    powerMax = world().getIndirectPowerLevelTo(xi(), yi(), zi(), i);
                    power_side = i;
                }

                if (b == Blocks.fire || b == Blocks.lava || b == Blocks.flowing_lava)
                {
                    explode(new TriggerCause.TriggerCauseFire(ForgeDirection.getOrientation(i)));
                    return;
                }
            }

            if(power_side != -1)
                explode(new TriggerCause.TriggerCauseRedstone(ForgeDirection.UNKNOWN, powerMax));
        }
    }

    @Override
    public void onWorldJoin()
    {
        super.onWorldJoin();
        world().markBlockForUpdate(xi(), yi(), zi());
    }

    @Override
    public void onNeighborChanged(Block block)
    {
        super.onNeighborChanged(block);
        int power_side = -1;
        int powerMax = 0;
        // If anything can set it on fire blow up
        for (byte i = 0; i < 6; i++)
        {
            VectorWorld position = toVectorWorld();
            position.add(ForgeDirection.getOrientation(i));

            Block b = position.getBlock();

            if (world().getIndirectPowerLevelTo(xi(), yi(), zi(), i) > powerMax)
            {
                powerMax = world().getIndirectPowerLevelTo(xi(), yi(), zi(), i);
                power_side = i;
            }

            if (b == Blocks.fire || b == Blocks.lava || b == Blocks.flowing_lava)
            {
                explode(new TriggerCause.TriggerCauseFire(ForgeDirection.getOrientation(i)));
                return;
            }
        }

        if(power_side != -1)
            explode(new TriggerCause.TriggerCauseRedstone(ForgeDirection.UNKNOWN, powerMax));
    }

    /*
     * Called to detonate the TNT. Args: world, x, y, z, metaData, CauseOfExplosion (0, intentional,
     * 1, exploded, 2 burned)
     */
    public void explode(TriggerCause triggerCause)
    {
        if(!exploding)
        {
            exploding = true;
            //TODO add tier
            WorldChangeHelper.ChangeResult result = ExplosiveRegistry.triggerExplosive(world(), x(), y(), z(), ExplosiveRegistry.get(explosiveID), triggerCause, 1);
            if (result == WorldChangeHelper.ChangeResult.COMPLETED)
                world().setBlockToAir(xi(), yi(), zi());
            else
                exploding = false;
        }
    }

    @Override
    public void onDestroyedByExplosion(Explosion explosion)
    {
        explode(new TriggerCause.TriggerCauseExplosion(explosion));
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.explosiveID = par1NBTTagCompound.getString("explosiveString");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setString("explosiveString", this.explosiveID);
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType type)
    {
        final byte ID = data.readByte();

        if (ID == 1)
        {
            explosiveID = ByteBufUtils.readUTF8String(data);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, (byte) 1, this.explosiveID);
    }

    @Override
    public IExplosive getExplosive()
    {
        return ExplosiveRegistry.get(this.explosiveID);
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        ExplosiveItemUtility.getSubItems(par1, par3List);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target)
    {
        if(explosiveID != null)
        {
            ItemStack stack = new ItemStack(this.getBlockType());
            ExplosiveItemUtility.setExplosive(stack, explosiveID);
            return stack;
        }
        return null;
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entity)
    {
        List<ItemStack> list = new ArrayList();

        ItemStack stack = new ItemStack(this.getBlockType());
        ExplosiveItemUtility.setExplosive(stack, explosiveID);
        list.add(stack);

        return list;
    }

    @SideOnly(Side.CLIENT) @Override
    public void renderDynamic(Vector3 position, float frame, int pass)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(position.xf() + 0.5f, position.yf() - 2.5f, position.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderMissile.TEXTURE);
        RenderMissile.defaultMissile.renderOnly("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
        GL11.glPopMatrix();
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        //Translate and rotate
        if (type == IItemRenderer.ItemRenderType.EQUIPPED)
        {
            GL11.glTranslatef(1f, 0.3f, 0.5f);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(1.15f, 1f, 0.5f);
        }

        //Scale
        if (type == IItemRenderer.ItemRenderType.ENTITY)
        {
            GL11.glScalef(0.5f, 0.5f, 0.5f);
        }

        renderDynamic(new Vector3(), 0, 0);
    }
}
