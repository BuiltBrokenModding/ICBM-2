package icbm.models;

import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelCruiseLauncherBase extends ModelBase
{
	//fields
    ModelRenderer LEG_SUPPORT_1;
    ModelRenderer LEG_SUPPORT_2;
    ModelRenderer TURRET_NECK;
    
  
  public ModelCruiseLauncherBase()
  {
    textureWidth = 128;
    textureHeight = 128;
    
      LEG_SUPPORT_1 = new ModelRenderer(this, 0, 0);
      LEG_SUPPORT_1.addBox(-17F, 0F, -1F, 34, 1, 2);
      LEG_SUPPORT_1.setRotationPoint(0F, 23F, 0F);
      LEG_SUPPORT_1.setTextureSize(128, 128);
      LEG_SUPPORT_1.mirror = true;
      setRotation(LEG_SUPPORT_1, 0F, 0.7853982F, 0F);
      LEG_SUPPORT_2 = new ModelRenderer(this, 0, 0);
      LEG_SUPPORT_2.addBox(-17F, 0F, -1F, 34, 1, 2);
      LEG_SUPPORT_2.setRotationPoint(0F, 23F, 0F);
      LEG_SUPPORT_2.setTextureSize(128, 128);
      LEG_SUPPORT_2.mirror = true;
      setRotation(LEG_SUPPORT_2, 0F, -0.7853982F, 0F);
      TURRET_NECK = new ModelRenderer(this, 0, 30);
      TURRET_NECK.addBox(0F, 0F, 0F, 4, 3, 4);
      TURRET_NECK.setRotationPoint(-2F, 21F, -2F);
      TURRET_NECK.setTextureSize(128, 128);
      TURRET_NECK.mirror = true;
      setRotation(TURRET_NECK, 0F, 0F, 0F);
      
  }
  
  public void render(float f5)
  {
    LEG_SUPPORT_1.render(f5);
    LEG_SUPPORT_2.render(f5);
    TURRET_NECK.render(f5);
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
