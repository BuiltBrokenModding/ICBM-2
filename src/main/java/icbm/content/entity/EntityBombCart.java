package icbm.content.entity;

import icbm.ICBM;
import icbm.explosion.ExplosiveRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import resonant.api.explosion.IExplosive;
import resonant.api.explosion.IExplosiveContainer;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBombCart extends EntityMinecartTNT implements IExplosiveContainer, IEntityAdditionalSpawnData
{
    public int explosiveID = 0;
    public NBTTagCompound nbtData = new NBTTagCompound();

    public EntityBombCart(World par1World)
    {
        super(par1World);
    }

    public EntityBombCart(World par1World, double x, double y, double z, int explosiveID)
    {
        super(par1World, x, y, z);
        this.explosiveID = explosiveID;
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        data.writeInt(this.explosiveID);
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        this.explosiveID = data.readInt();
    }

    @Override
    protected void explodeCart(double par1)
    {
        // TODO add event
        this.worldObj.spawnParticle("hugeexplosion", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        this.getExplosiveType().createExplosion(this.worldObj, this.posX, this.posY, this.posZ, this);
        this.setDead();
    }

    public boolean interact(EntityPlayer player)
    {
        if (player.getCurrentEquippedItem() != null)
        {
            if (player.getCurrentEquippedItem().getItem() == Items.flint_and_steel)
            {
                this.ignite();
                return true;
            }
        }
        return false;
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(Items.minecart, 1);

        this.entityDropItem(itemstack, 0.0F);

        double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;

        if (!par1DamageSource.isExplosion())
        {
            this.entityDropItem(new ItemStack(ICBM.blockExplosive, 1, this.explosiveID), 0.0F);
        }

        if (par1DamageSource.isFireDamage() || par1DamageSource.isExplosion() || d0 >= 0.009999999776482582D)
        {
            this.explodeCart(d0);
        }
    }

    @Override
    public ItemStack getCartItem()
    {
        return new ItemStack(ICBM.itemBombCart, 1, this.explosiveID);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("explosiveID", this.explosiveID);
        this.nbtData = nbt.getCompoundTag("data");

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.explosiveID = nbt.getInteger("explosiveID");
        nbt.setTag("data", this.nbtData);

    }

    @Override
    public IExplosive getExplosiveType()
    {
        return ExplosiveRegistry.get(this.explosiveID);
    }

    @Override
    public Block func_145817_o()
    {
        return ICBM.blockExplosive;
    }

    @Override
    public int getDefaultDisplayTileData()
    {
        return this.explosiveID;
    }

    @Override
    public NBTTagCompound getTagCompound()
    {
        return this.nbtData;
    }

}
