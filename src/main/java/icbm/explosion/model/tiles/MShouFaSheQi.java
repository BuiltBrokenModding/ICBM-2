package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MShouFaSheQi extends ModelBase
{
    // fields
    ModelRenderer a;
    ModelRenderer b;
    ModelRenderer c;
    ModelRenderer d;
    ModelRenderer e;
    ModelRenderer f;
    ModelRenderer g;
    ModelRenderer h;
    ModelRenderer i;
    ModelRenderer j;
    ModelRenderer k;
    ModelRenderer l;
    ModelRenderer m;
    ModelRenderer n;
    ModelRenderer o;
    ModelRenderer p;
    ModelRenderer q;
    ModelRenderer r;
    ModelRenderer s;
    ModelRenderer t;
    ModelRenderer u;

    public MShouFaSheQi()
    {
        textureWidth = 128;
        textureHeight = 128;

        a = new ModelRenderer(this, 0, 0);
        a.addBox(-2F, -2F, -16F, 4, 4, 34);
        a.setRotationPoint(0F, 19F, 0F);
        a.setTextureSize(128, 128);
        a.mirror = true;
        setRotation(a, 0F, 0F, 0F);
        b = new ModelRenderer(this, 0, 7);
        b.addBox(0F, 0F, 0F, 1, 3, 2);
        b.setRotationPoint(0F, 21F, -10F);
        b.setTextureSize(128, 128);
        b.mirror = true;
        setRotation(b, 0.2792527F, 0F, 0F);
        c = new ModelRenderer(this, 7, 6);
        c.addBox(0F, 0F, 0F, 1, 4, 2);
        c.setRotationPoint(0F, 21F, 2F);
        c.setTextureSize(128, 128);
        c.mirror = true;
        setRotation(c, 0F, 0F, 0F);
        d = new ModelRenderer(this, 14, 3);
        d.addBox(-1F, 0F, 0F, 2, 4, 0);
        d.setRotationPoint(0F, 11F, -14F);
        d.setTextureSize(128, 128);
        d.mirror = true;
        setRotation(d, 0F, 0F, 0F);
        e = new ModelRenderer(this, 0, 0);
        e.addBox(-1F, 0F, 0F, 2, 1, 4);
        e.setRotationPoint(0F, 16F, -15F);
        e.setTextureSize(128, 128);
        e.mirror = true;
        setRotation(e, 0F, 0F, 0F);
        f = new ModelRenderer(this, 14, 0);
        f.addBox(-2F, 0F, 0F, 4, 2, 0);
        f.setRotationPoint(0F, 15F, -14F);
        f.setTextureSize(128, 128);
        f.mirror = true;
        setRotation(f, 0F, 0F, 0F);
        g = new ModelRenderer(this, 0, 40);
        g.addBox(0F, -1F, 0F, 3, 1, 12);
        g.setRotationPoint(-3F, 20F, 9F);
        g.setTextureSize(128, 128);
        g.mirror = true;
        setRotation(g, 0F, 0F, 0.7853982F);
        h = new ModelRenderer(this, 0, 53);
        h.addBox(-3F, 0F, 0F, 3, 1, 8);
        h.setRotationPoint(-1F, 16F, 11F);
        h.setTextureSize(128, 128);
        h.mirror = true;
        setRotation(h, 0F, 0F, -0.7853982F);
        i = new ModelRenderer(this, 31, 40);
        i.addBox(0F, 0F, 0F, 2, 1, 12);
        i.setRotationPoint(-1F, 21F, 9F);
        i.setTextureSize(128, 128);
        i.mirror = true;
        setRotation(i, 0F, 0F, 0F);
        j = new ModelRenderer(this, 23, 53);
        j.addBox(0F, 0F, 0F, 2, 1, 8);
        j.setRotationPoint(-1F, 16F, 11F);
        j.setTextureSize(128, 128);
        j.mirror = true;
        setRotation(j, 0F, 0F, 0F);
        k = new ModelRenderer(this, 0, 53);
        k.addBox(0F, 0F, 0F, 3, 1, 8);
        k.setRotationPoint(1F, 16F, 11F);
        k.setTextureSize(128, 128);
        k.mirror = true;
        setRotation(k, 0F, 0F, 0.7853982F);
        l = new ModelRenderer(this, 0, 40);
        l.addBox(0F, -1F, 0F, 3, 1, 12);
        l.setRotationPoint(1F, 22F, 9F);
        l.setTextureSize(128, 128);
        l.mirror = true;
        setRotation(l, 0F, 0F, -0.7853982F);
        m = new ModelRenderer(this, 47, 0);
        m.addBox(0F, 0F, 0F, 2, 2, 6);
        m.setRotationPoint(2F, 15F, -10F);
        m.setTextureSize(128, 128);
        m.mirror = true;
        setRotation(m, 0F, 0F, 0F);
        n = new ModelRenderer(this, 47, 10);
        n.addBox(0F, 0F, 0F, 2, 3, 1);
        n.setRotationPoint(2F, 15F, -9F);
        n.setTextureSize(128, 128);
        n.mirror = true;
        setRotation(n, 0F, 0F, 0.7853982F);
        o = new ModelRenderer(this, 47, 10);
        o.addBox(0F, 0F, 0F, 2, 3, 1);
        o.setRotationPoint(2F, 15F, -6F);
        o.setTextureSize(128, 128);
        o.mirror = true;
        setRotation(o, 0F, 0F, 0.7853982F);
        p = new ModelRenderer(this, 0, 14);
        p.addBox(0F, 0F, 0F, 3, 1, 4);
        p.setRotationPoint(1F, 16F, -17F);
        p.setTextureSize(128, 128);
        p.mirror = true;
        setRotation(p, 0F, 0F, 0.7853982F);
        q = new ModelRenderer(this, 0, 14);
        q.addBox(0F, -1F, 0F, 3, 1, 4);
        q.setRotationPoint(1F, 22F, -17F);
        q.setTextureSize(128, 128);
        q.mirror = true;
        setRotation(q, 0F, 0F, -0.7853982F);
        r = new ModelRenderer(this, 0, 21);
        r.addBox(-1F, 0F, 0F, 1, 2, 4);
        r.setRotationPoint(3F, 18F, -17F);
        r.setTextureSize(128, 128);
        r.mirror = true;
        setRotation(r, 0F, 0F, 0F);
        s = new ModelRenderer(this, 0, 14);
        s.addBox(0F, 0F, 0F, 3, 1, 4);
        s.setRotationPoint(-3F, 18F, -17F);
        s.setTextureSize(128, 128);
        s.mirror = true;
        setRotation(s, 0F, 0F, -0.7853982F);
        t = new ModelRenderer(this, 0, 21);
        t.addBox(0F, -1F, 0F, 1, 2, 4);
        t.setRotationPoint(-3F, 19F, -17F);
        t.setTextureSize(128, 128);
        t.mirror = true;
        setRotation(t, 0F, 0F, 0F);
        u = new ModelRenderer(this, 0, 14);
        u.addBox(0F, -1F, 0F, 3, 1, 4);
        u.setRotationPoint(-3F, 20F, -17F);
        u.setTextureSize(128, 128);
        u.mirror = true;
        setRotation(u, 0F, 0F, 0.7853982F);
    }

    @Override
    public void render(Entity entity, float x, float y, float z, float f3, float f4, float f5)
    {
        super.render(entity, x, y, z, f3, f4, f5);
        this.setRotationAngles(x, y, z, f3, f4, f5, entity);
        a.render(f5);
        b.render(f5);
        c.render(f5);
        d.render(f5);
        e.render(f5);
        f.render(f5);
        g.render(f5);
        h.render(f5);
        i.render(f5);
        j.render(f5);
        k.render(f5);
        l.render(f5);
        m.render(f5);
        n.render(f5);
        o.render(f5);
        p.render(f5);
        q.render(f5);
        r.render(f5);
        s.render(f5);
        t.render(f5);
        u.render(f5);
    }

    public void render(float f5)
    {
        a.render(f5);
        b.render(f5);
        c.render(f5);
        d.render(f5);
        e.render(f5);
        f.render(f5);
        g.render(f5);
        h.render(f5);
        i.render(f5);
        j.render(f5);
        k.render(f5);
        l.render(f5);
        m.render(f5);
        n.render(f5);
        o.render(f5);
        p.render(f5);
        q.render(f5);
        r.render(f5);
        s.render(f5);
        t.render(f5);
        u.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
