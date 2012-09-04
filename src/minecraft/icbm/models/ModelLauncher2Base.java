package icbm.models;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelLauncher2Base extends ModelBase
{
	//fields
    ModelRenderer Shape12;
    ModelRenderer Shape13;
  
  public ModelLauncher2Base()
  {
    textureWidth = 256;
    textureHeight = 256;
    
      Shape12 = new ModelRenderer(this, 0, 80);
      Shape12.addBox(0F, 0F, 0F, 11, 6, 6);
      Shape12.setRotationPoint(-5F, 18F, -6F);
      Shape12.setTextureSize(256, 256);
      Shape12.mirror = true;
      setRotation(Shape12, 0F, 0F, 0F);
      Shape13 = new ModelRenderer(this, 0, 80);
      Shape13.addBox(0F, 0F, 0F, 11, 6, 6);
      Shape13.setRotationPoint(-5F, 18F, 1F);
      Shape13.setTextureSize(256, 256);
      Shape13.mirror = true;
      setRotation(Shape13, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5);
    Shape12.render(f5);
    Shape13.render(f5);
  }
  
  public void render(float f5)
  {
    Shape12.render(f5);
    Shape13.render(f5);
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
