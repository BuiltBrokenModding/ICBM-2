package icbm;

import net.minecraft.src.Entity;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import universalelectricity.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityLightBeam extends Entity implements IEntityAdditionalSpawnData
{
	private int life;
	public float red, green, blue;
	
    public EntityLightBeam(World world)
    {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(1F, 1F);
    }

    public EntityLightBeam(World world, Vector3 position, int life, float red, float green, float blue)
    {
        super(world);
        this.setPosition(position.x, position.y, position.z);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.life = life;
    }
    
    @Override
    public String getEntityName()
    {
    	return "Light Beam";
    }
    
    @Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeInt(this.life);
		data.writeFloat(this.red);
		data.writeFloat(this.green);
		data.writeFloat(this.blue);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.life = data.readInt();
		this.red = data.readFloat();
		this.green = data.readFloat();
		this.blue = data.readFloat();
	}
    
    @Override
    protected void entityInit() { }
    
    public void onUpdate()
    {
    	if(this.life > 0)
    	{
    		this.life --;
    	}
    	else
    	{
    		this.setDead();
    	}
    }


    @Override
    public float getShadowSize() { return 0F; }
    
    @Override
	public boolean canBePushed() { return false; }
    
    @Override
    protected boolean canTriggerWalking() {  return false; }
    
    @Override
    public boolean canBeCollidedWith() { return false; }

	@Override
	protected void readEntityFromNBT(NBTTagCompound var1)
	{
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1)
	{
		
	}
}