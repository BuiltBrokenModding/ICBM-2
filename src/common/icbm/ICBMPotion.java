package icbm;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPig;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Potion;

public class ICBMPotion extends Potion
{
    public static final int idPrefix = 20;
    /** The poison Potion object poisons entities until death. */
    public static final Potion extendedPoison = new ICBMPotion(idPrefix, false, 5149489, "potion.poison", 6, 0);
    public static final Potion contagiousPoison = new ICBMPotion(idPrefix+1, false, 5149489, "potion.poison", 6, 0);
    public static final Potion frostBite = new ICBMPotion(idPrefix+2, false, 5149489, "Frost Bite", 5, 0);

    public ICBMPotion(int id, boolean effectiveness, int color, String name, int indexX, int indexY)
    {
        super(id, effectiveness, color);
        this.setPotionName(name);
        this.setIconIndex(indexX, indexY);
    }
    
    public static void init()
	{
    	Potion.potionTypes[20] = extendedPoison;
    	Potion.potionTypes[21] = contagiousPoison;
    	Potion.potionTypes[22] = frostBite;
	}

    /**
     * Checks if Potion effect is ready to be applied this tick.
     */
    @Override
	public boolean isReady(int duration, int amplifier)
    {
    	if(this.id == extendedPoison.id || this.id == contagiousPoison.id)
        {
            return isSecond(duration, 2);
        }
    	else if(this.id == frostBite.id)
        {
            return isSecond(duration);
        }

        return false;
    }
    
    /**
     * Called whenever the potion effect is to be applied.
     */
    @Override
	public void performEffect(EntityLiving par1EntityLiving, int amplifier)
    {
        if (this.id == ICBMPotion.extendedPoison.id)
        {
        	if(!(par1EntityLiving instanceof EntityZombie) && !(par1EntityLiving instanceof EntityPigZombie))
        	{
            	par1EntityLiving.attackEntityFrom(DamageSource.magic, 1);
        	}
        }
        else if (this.id == ICBMPotion.contagiousPoison.id)
        {
        	if(!(par1EntityLiving instanceof EntityZombie) && !(par1EntityLiving instanceof EntityPigZombie))
        	{
            	par1EntityLiving.attackEntityFrom(DamageSource.magic, 1);
        	}
        	
        	//Poison things around it
        	int r = 13;
        	AxisAlignedBB entitySurroundings = AxisAlignedBB.getBoundingBox(par1EntityLiving.posX-r, par1EntityLiving.posY-r, par1EntityLiving.posZ-r, par1EntityLiving.posX+r, par1EntityLiving.posY+r, par1EntityLiving.posZ+r);
        	EntityLiving nearestEntity = (EntityLiving)par1EntityLiving.worldObj.findNearestEntityWithinAABB(EntityLiving.class, entitySurroundings, par1EntityLiving);
        	
        	if(nearestEntity != null)
        	{
        		if(nearestEntity instanceof EntityPig)
                {
            		EntityPigZombie var2 = new EntityPigZombie(nearestEntity.worldObj);
                    var2.setLocationAndAngles(nearestEntity.posX, nearestEntity.posY, nearestEntity.posZ, nearestEntity.rotationYaw, nearestEntity.rotationPitch);
                    nearestEntity.worldObj.spawnEntityInWorld(var2);
                    nearestEntity.setDead();
                }
            	else if(nearestEntity instanceof EntityVillager)
                {
            		EntityZombie var2 = new EntityZombie(nearestEntity.worldObj);
                    var2.setLocationAndAngles(nearestEntity.posX, nearestEntity.posY, nearestEntity.posZ, nearestEntity.rotationYaw, nearestEntity.rotationPitch);
                    nearestEntity.worldObj.spawnEntityInWorld(var2);
                    nearestEntity.setDead();
                }
        		
		        ICBM.DU_YI_CHUAN.poisonEntity(nearestEntity);
        	}
        }
        else if (this.id == frostBite.id)
        {
        	if(par1EntityLiving instanceof EntityPlayer)
        	{
        		((EntityPlayer)par1EntityLiving).addExhaustion(3F * (float)(amplifier + 1));
        	}
            
        	if(par1EntityLiving.isBurning())
        	{
        		par1EntityLiving.extinguish();
        		par1EntityLiving.removePotionEffect(this.id);
        	}
        	
        	//Check to see if it's on ice
        	if(par1EntityLiving.worldObj.getBlockId(MathHelper.floor_double(par1EntityLiving.posX), MathHelper.floor_double(par1EntityLiving.posY)-1, MathHelper.floor_double(par1EntityLiving.posZ)) == Block.ice.blockID)
        	{
            	par1EntityLiving.attackEntityFrom(DamageSource.magic, 2);
        	}
        		
        	//Shatter enemy if health is too low
        	if(par1EntityLiving.getHealth() < 6)
        	{
        		par1EntityLiving.attackEntityFrom(DamageSource.magic, 999999999);
        	}
        }
    }

    //Check if the ticks given are divisible by 20 (which is a second)
    private boolean isSecond(int ticks, int seconds)
    {
        if (ticks % (20 * seconds) == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isSecond(int ticks)
    {
        return this.isSecond(ticks, 1);
    }
}
