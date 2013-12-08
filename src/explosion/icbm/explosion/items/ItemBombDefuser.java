package icbm.explosion.items;

import icbm.core.base.ItemICBMElectricBase;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.cart.EntityBombCart;
import icbm.explosion.missile.EntityExplosive;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

//Explosive Defuser
public class ItemBombDefuser extends ItemICBMElectricBase
{
    private static final int YONG_DIAN_LIANG = 2000;

    public ItemBombDefuser(int id)
    {
        super(id, "defuser");
    }

    /** Called when the player Left Clicks (attacks) an entity. Processed before damage is done, if
     * return value is true further processing is canceled and the entity is not attacked.
     * 
     * @param itemStack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction. */
    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity)
    {
        if (this.getElectricityStored(itemStack) > YONG_DIAN_LIANG)
        {
            if (entity instanceof EntityExplosive)
            {
                if (!entity.worldObj.isRemote)
                {
                    EntityExplosive entityTNT = (EntityExplosive) entity;
                    EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(ICBMExplosion.blockExplosive, 1, entityTNT.haoMa));
                    float var13 = 0.05F;
                    Random random = new Random();
                    entityItem.motionX = ((float) random.nextGaussian() * var13);
                    entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
                    entityItem.motionZ = ((float) random.nextGaussian() * var13);
                    entity.worldObj.spawnEntityInWorld(entityItem);
                }
                entity.setDead();
            }
            else if (entity instanceof EntityTNTPrimed)
            {
                if (!entity.worldObj.isRemote)
                {
                    EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Block.tnt));
                    float var13 = 0.05F;
                    Random random = new Random();
                    entityItem.motionX = ((float) random.nextGaussian() * var13);
                    entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
                    entityItem.motionZ = ((float) random.nextGaussian() * var13);
                    entity.worldObj.spawnEntityInWorld(entityItem);
                }
                entity.setDead();
            }
            else if (entity instanceof EntityBombCart)
            {
                ((EntityBombCart) entity).killMinecart(DamageSource.generic);
            }

            this.discharge(itemStack, YONG_DIAN_LIANG, true);
            return true;
        }
        else
        {
            player.addChatMessage("Defuser out of electricity!");
        }

        return false;
    }

    @Override
    public float getVoltage(ItemStack itemStack)
    {
        return 20;
    }

    @Override
    public float getMaxElectricityStored(ItemStack itemStack)
    {
        return 80000;
    }
}
