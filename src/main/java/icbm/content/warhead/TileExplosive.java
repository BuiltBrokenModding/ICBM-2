package icbm.content.warhead;

import cpw.mods.fml.common.network.ByteBufUtils;
import icbm.Reference;
import icbm.content.ItemSaveUtil;
import icbm.explosion.ExplosiveRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
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
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import icbm.api.explosion.TriggerCause;
import resonant.content.prefab.java.TileAdvanced;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;
import resonant.lib.utility.WrenchUtility;

import java.util.List;

public class TileExplosive extends TileAdvanced implements IExplosiveContainer, IPacketReceiver
{
    public String explosiveID = null;

    public TileExplosive()
    {
        super(Material.cloth);
        this.normalRender(true);
        this.itemBlock(ItemBlockExplosive.class);
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
        if (!world().isRemote && ItemSaveUtil.getExplosive(itemStack) != null)
        {
            //Set explosive id
            this.explosiveID = ItemSaveUtil.getExplosive(itemStack).getUnlocalizedName();

            //Set rotation for direction based explosives
            setMeta(determineOrientation(entityLiving));

            //Log the placement for Anti-Grief TODO add config to reduce console spam, add support for Anti-Grief mods/plugins
            if (entityLiving != null)
            {
                Reference.LOGGER.info(entityLiving.getCommandSenderName() + " placed " + ExplosiveRegistry.get(explosiveID) + " in: " + xi() + ", " + yi() + ", " + zi() + ".");
            }

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
        //TODO add tier
        ExplosiveRegistry.TriggerResult result = ExplosiveRegistry.triggerExplosive(world(), x(), y(), z(), ExplosiveRegistry.get(explosiveID), triggerCause, 1);
        if (result == ExplosiveRegistry.TriggerResult.TRIGGERED)
            world().setBlockToAir(xi(), yi(), zi());
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
        for(IExplosive ex : ExplosiveRegistry.getExplosives())
        {
            ItemStack stack = new ItemStack(this.getBlockType());
            ItemSaveUtil.setExplosive(stack, ex);
            par3List.add(stack);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target)
    {
        if(explosiveID != null)
        {
            ItemStack stack = new ItemStack(this.getBlockType());
            ItemSaveUtil.setExplosive(stack, explosiveID);
            return stack;
        }
        return null;
    }
}
