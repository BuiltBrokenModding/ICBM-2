package icbm.models;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class MLeiShePao extends ModelBase
{
  //fields
    ModelRenderer Turret_Base;
    ModelRenderer Support_1;
    ModelRenderer Support_2;
    ModelRenderer Support_3;
    ModelRenderer Support_4;
    ModelRenderer Turret_Neck;
    ModelRenderer Main_Turret_MOVES;
    ModelRenderer Back_Armor_MOVES;
    ModelRenderer Armor_1_MOVES;
    ModelRenderer Armor_2_MOVES;
    ModelRenderer Armor_3_MOVES;
    ModelRenderer Armor_4_MOVES;
    ModelRenderer Armor_5_MOVES;
    ModelRenderer Armor_6_MOVES;
    ModelRenderer Front_Armor_1_MOVES;
    ModelRenderer Front_Armor_2_MOVES;
    ModelRenderer Turret_Body;
    ModelRenderer Cannon_MOVES;
  
  public MLeiShePao()
  {
    textureWidth = 128;
    textureHeight = 128;
    
      Turret_Base = new ModelRenderer(this, 0, 0);
      Turret_Base.addBox(0F, 0F, 0F, 14, 14, 14);
      Turret_Base.setRotationPoint(-7F, 10F, -7F);
      Turret_Base.setTextureSize(128, 128);
      Turret_Base.mirror = true;
      setRotation(Turret_Base, 0F, 0F, 0F);
      Support_1 = new ModelRenderer(this, 0, 60);
      Support_1.addBox(0F, 0F, 0F, 2, 6, 2);
      Support_1.setRotationPoint(6F, 18F, 6F);
      Support_1.setTextureSize(128, 128);
      Support_1.mirror = true;
      setRotation(Support_1, 0F, 0F, 0F);
      Support_2 = new ModelRenderer(this, 0, 60);
      Support_2.addBox(0F, 0F, 0F, 2, 6, 2);
      Support_2.setRotationPoint(6F, 18F, -8F);
      Support_2.setTextureSize(128, 128);
      Support_2.mirror = true;
      setRotation(Support_2, 0F, 0F, 0F);
      Support_3 = new ModelRenderer(this, 0, 60);
      Support_3.addBox(0F, 0F, 0F, 2, 6, 2);
      Support_3.setRotationPoint(-8F, 18F, -8F);
      Support_3.setTextureSize(128, 128);
      Support_3.mirror = true;
      setRotation(Support_3, 0F, 0F, 0F);
      Support_4 = new ModelRenderer(this, 0, 60);
      Support_4.addBox(0F, 0F, 0F, 2, 6, 2);
      Support_4.setRotationPoint(-8F, 18F, 6F);
      Support_4.setTextureSize(128, 128);
      Support_4.mirror = true;
      setRotation(Support_4, 0F, 0F, 0F);
      Turret_Neck = new ModelRenderer(this, 10, 60);
      Turret_Neck.addBox(-1F, 0F, -1F, 2, 1, 2);
      Turret_Neck.setRotationPoint(0F, -1F, 0F);
      Turret_Neck.setTextureSize(128, 128);
      Turret_Neck.mirror = true;
      setRotation(Turret_Neck, 0F, 0F, 0F);
      Main_Turret_MOVES = new ModelRenderer(this, 60, 10);
      Main_Turret_MOVES.addBox(-5F, 0F, -5F, 10, 4, 14);
      Main_Turret_MOVES.setRotationPoint(0F, -5F, 0F);
      Main_Turret_MOVES.setTextureSize(128, 128);
      Main_Turret_MOVES.mirror = true;
      setRotation(Main_Turret_MOVES, 0F, 0F, 0F);
      Back_Armor_MOVES = new ModelRenderer(this, 60, 0);
      Back_Armor_MOVES.addBox(-5F, 5F, 5F, 10, 3, 3);
      Back_Armor_MOVES.setRotationPoint(0F, -3F, 0F);
      Back_Armor_MOVES.setTextureSize(128, 128);
      Back_Armor_MOVES.mirror = true;
      setRotation(Back_Armor_MOVES, 0.7853982F, 0F, 0F);
      Armor_1_MOVES = new ModelRenderer(this, 90, 0);
      Armor_1_MOVES.addBox(3F, 0F, 0F, 4, 4, 3);
      Armor_1_MOVES.setRotationPoint(0F, -5F, 0F);
      Armor_1_MOVES.setTextureSize(128, 128);
      Armor_1_MOVES.mirror = true;
      setRotation(Armor_1_MOVES, 0F, 0.7504916F, 0F);
      Armor_2_MOVES = new ModelRenderer(this, 60, 30);
      Armor_2_MOVES.addBox(4F, 0F, -2F, 4, 4, 8);
      Armor_2_MOVES.setRotationPoint(0F, -5F, 0F);
      Armor_2_MOVES.setTextureSize(128, 128);
      Armor_2_MOVES.mirror = true;
      setRotation(Armor_2_MOVES, 0F, -0.4363323F, 0F);
      Armor_3_MOVES = new ModelRenderer(this, 90, 30);
      Armor_3_MOVES.addBox(4F, 0F, -1F, 4, 4, 4);
      Armor_3_MOVES.setRotationPoint(0F, -5F, 0F);
      Armor_3_MOVES.setTextureSize(128, 128);
      Armor_3_MOVES.mirror = true;
      setRotation(Armor_3_MOVES, 0F, 0.1919862F, 0F);
      Armor_4_MOVES = new ModelRenderer(this, 90, 0);
      Armor_4_MOVES.addBox(3F, 0F, -3F, 4, 4, 3);
      Armor_4_MOVES.setRotationPoint(0F, -5F, 0F);
      Armor_4_MOVES.setTextureSize(128, 128);
      Armor_4_MOVES.mirror = true;
      setRotation(Armor_4_MOVES, 0F, 2.391101F, 0F);
      Armor_5_MOVES = new ModelRenderer(this, 90, 30);
      Armor_5_MOVES.addBox(-1F, 0F, 4F, 4, 4, 4);
      Armor_5_MOVES.setRotationPoint(0F, -5F, 0F);
      Armor_5_MOVES.setTextureSize(128, 128);
      Armor_5_MOVES.mirror = true;
      setRotation(Armor_5_MOVES, 0F, -1.780236F, 0F);
      Armor_6_MOVES = new ModelRenderer(this, 60, 30);
      Armor_6_MOVES.addBox(-8F, 0F, -2F, 4, 4, 8);
      Armor_6_MOVES.setRotationPoint(0F, -5F, 0F);
      Armor_6_MOVES.setTextureSize(128, 128);
      Armor_6_MOVES.mirror = true;
      setRotation(Armor_6_MOVES, 0F, 0.4363323F, 0F);
      Front_Armor_1_MOVES = new ModelRenderer(this, 110, 0);
      Front_Armor_1_MOVES.addBox(-5F, 3F, -4F, 4, 4, 2);
      Front_Armor_1_MOVES.setRotationPoint(0F, -5F, 0F);
      Front_Armor_1_MOVES.setTextureSize(128, 128);
      Front_Armor_1_MOVES.mirror = true;
      setRotation(Front_Armor_1_MOVES, -0.6457718F, 0F, 0F);
      Front_Armor_2_MOVES = new ModelRenderer(this, 110, 0);
      Front_Armor_2_MOVES.addBox(1F, 3F, -4F, 4, 4, 2);
      Front_Armor_2_MOVES.setRotationPoint(0F, -5F, 0F);
      Front_Armor_2_MOVES.setTextureSize(128, 128);
      Front_Armor_2_MOVES.mirror = true;
      setRotation(Front_Armor_2_MOVES, -0.6457718F, 0F, 0F);
      Turret_Body = new ModelRenderer(this, 0, 30);
      Turret_Body.addBox(0F, 0F, 0F, 10, 11, 10);
      Turret_Body.setRotationPoint(-5F, 0F, -5F);
      Turret_Body.setTextureSize(128, 128);
      Turret_Body.mirror = true;
      setRotation(Turret_Body, 0F, 0F, 0F);
      Cannon_MOVES = new ModelRenderer(this, 60, 50);
      Cannon_MOVES.addBox(-1F, 0F, -22F, 2, 2, 17);
      Cannon_MOVES.setRotationPoint(0F, -4F, 0F);
      Cannon_MOVES.setTextureSize(128, 128);
      Cannon_MOVES.mirror = true;
      setRotation(Cannon_MOVES, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5);
    Turret_Base.render(f5);
    Support_1.render(f5);
    Support_2.render(f5);
    Support_3.render(f5);
    Support_4.render(f5);
    Turret_Neck.render(f5);
    Main_Turret_MOVES.render(f5);
    Back_Armor_MOVES.render(f5);
    Armor_1_MOVES.render(f5);
    Armor_2_MOVES.render(f5);
    Armor_3_MOVES.render(f5);
    Armor_4_MOVES.render(f5);
    Armor_5_MOVES.render(f5);
    Armor_6_MOVES.render(f5);
    Front_Armor_1_MOVES.render(f5);
    Front_Armor_2_MOVES.render(f5);
    Turret_Body.render(f5);
    Cannon_MOVES.render(f5);
  }
  
  	public void render(float rotationYaw, float rotationPitch, float f5)
  	{
	    Turret_Body.render(f5);
	  	Turret_Base.render(f5);
	    Support_1.render(f5);
	    Support_2.render(f5);
	    Support_3.render(f5);
	    Support_4.render(f5);
	    Turret_Neck.render(f5);
	    
	    Main_Turret_MOVES.rotateAngleY = rotationYaw;
	    Main_Turret_MOVES.rotateAngleX = rotationYaw;
	    Main_Turret_MOVES.render(f5);

	    Back_Armor_MOVES.rotateAngleY = rotationYaw;
	    Back_Armor_MOVES.rotateAngleX = rotationYaw;
	    Back_Armor_MOVES.render(f5);
	    
	    Armor_1_MOVES.rotateAngleY = rotationYaw;
	    Armor_1_MOVES.rotateAngleX = rotationYaw;
	    Armor_1_MOVES.render(f5);
	    
	    Armor_2_MOVES.rotateAngleY = rotationYaw;
	    Armor_2_MOVES.rotateAngleX = rotationYaw;
	    Armor_2_MOVES.render(f5);
	    
	    Armor_3_MOVES.rotateAngleY = rotationYaw;
	    Armor_3_MOVES.rotateAngleX = rotationYaw;
	    Armor_3_MOVES.render(f5);
	    
	    Armor_4_MOVES.rotateAngleY = rotationYaw;
	    Armor_4_MOVES.rotateAngleX = rotationYaw;
	    Armor_4_MOVES.render(f5);
	    
	    Armor_5_MOVES.rotateAngleY = rotationYaw;
	    Armor_5_MOVES.rotateAngleX = rotationYaw;
	    Armor_5_MOVES.render(f5);
	    
	    Armor_6_MOVES.rotateAngleY = rotationYaw;
	    Armor_6_MOVES.rotateAngleX = rotationYaw;
	    Armor_6_MOVES.render(f5);
	    
	    Front_Armor_1_MOVES.rotateAngleY = rotationYaw;
	    Front_Armor_1_MOVES.rotateAngleX = rotationYaw;
	    Front_Armor_1_MOVES.render(f5);
	    
	    Front_Armor_2_MOVES.rotateAngleY = rotationYaw;
	    Front_Armor_2_MOVES.rotateAngleX = rotationYaw;
	    Front_Armor_2_MOVES.render(f5);
	    
	    Cannon_MOVES.rotateAngleY = rotationYaw;
	    Cannon_MOVES.rotateAngleX = rotationYaw;
	    Cannon_MOVES.render(f5);
  	}
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5);
  }

}
