package icbm.explosion.model.tiles;

import icbm.ModelICBM;
import icbm.Reference;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MDiLei extends ModelICBM
{
    public static final MDiLei INSTANCE = new MDiLei();
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "s-mine.png");

    // fields
    ModelRenderer A;
    ModelRenderer B;
    ModelRenderer C;

    public MDiLei()
    {
        textureWidth = 45;
        textureHeight = 26;

        A = new ModelRenderer(this, 0, 0);
        A.addBox(-5F, 0F, -5F, 10, 2, 10);
        A.setRotationPoint(0F, 22F, 0F);
        A.setTextureSize(45, 26);
        A.mirror = true;
        setRotation(A, 0F, 0.7853982F, 0F);
        B = new ModelRenderer(this, 0, 14);
        B.addBox(-8F, 0F, -2F, 16, 1, 4);
        B.setRotationPoint(0F, 23F, 0F);
        B.setTextureSize(45, 26);
        B.mirror = true;
        setRotation(B, 0F, 0.7853982F, 0F);
        C = new ModelRenderer(this, 0, 14);
        C.addBox(-8F, 0F, -2F, 16, 1, 4);
        C.setRotationPoint(0F, 23F, 0F);
        C.setTextureSize(45, 26);
        C.mirror = true;
        setRotation(C, 0F, 2.356194F, 0F);
    }

    @Override
    public void render(float f5)
    {
        A.render(f5);
        B.render(f5);
        C.render(f5);
    }

    @Override
    public void render(Entity entity, float x, float y, float z, float f1, float f2, float f3)
    {
        super.render(entity, x, y, z, f1, f2, f3);
        this.setRotationAngles(x, y, z, f1, f2, f3, entity);
        this.render(f3);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
