package icbm.explosion.items;

import icbm.core.prefab.item.ItemICBMElectrical;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.entities.EntityBombCart;
import icbm.explosion.entities.EntityExplosive;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import resonant.lib.utility.LanguageUtility;

//Explosive Defuser
public class ItemDefuser extends ItemICBMElectrical
{
    private static final int energyCost = 100000;

    public ItemDefuser(int id)
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
        System.out.println("stackCharge " + this.getEnergy(itemStack));
        if (this.getEnergy(itemStack) >= energyCost)
        {
            if (entity instanceof EntityExplosive)
            {
                if (!entity.worldObj.isRemote)
                {
                    EntityExplosive entityTNT = (EntityExplosive) entity;
                    EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(ICBMExplosion.blockExplosive, 1, entityTNT.explosiveID));
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

            this.setEnergy(itemStack, this.getEnergy(itemStack) - energyCost);
            return true;
        }
        else
        {
            player.addChatMessage(LanguageUtility.getLocal("message.defuser.nopower"));
        }

        return false;
    }

    @Override
    public long getVoltage(ItemStack itemStack)
    {
        return 20;
    }

    @Override
    public long getEnergyCapacity(ItemStack itemStack)
    {
        return energyCost * 10;
    }
}
