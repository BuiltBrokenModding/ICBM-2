package icbm.jiqi;

import net.minecraft.src.Entity;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class ECiGuiPao extends Entity implements IEntityAdditionalSpawnData
{
    private TileEntity controller;

	public ECiGuiPao(World par1World)
    {
        super(par1World);
        this.setSize(1F, 0.5F);
        this.yOffset = this.height / 2.0F;
    }
    
    public ECiGuiPao(World par1World, Vector3 position, TileEntity controller)
    {
        this(par1World);
        this.isImmuneToFire = true;
        this.setPosition(position.x, position.y, position.z);
        this.controller = controller;
    }
    
    @Override
    public String getEntityName()
    {
    	return "Railgun Seat";
    }
    
	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeInt(this.controller.xCoord);
		data.writeInt(this.controller.yCoord);
		data.writeInt(this.controller.zCoord);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.controller = this.worldObj.getBlockTileEntity(data.readInt(), data.readInt(), data.readInt());
	}
    
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if(this.controller == null)
        {
        	this.setDead();
        }
        else if(this.controller.isInvalid())
        {
        	this.setDead();
        }        
    }
    
    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
	protected void entityInit()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound var1)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1)
	{
		// TODO Auto-generated method stub
		
	}
}
