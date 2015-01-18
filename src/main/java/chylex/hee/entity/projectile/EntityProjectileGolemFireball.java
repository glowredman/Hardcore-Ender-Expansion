package chylex.hee.entity.projectile;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import chylex.hee.proxy.ModCommonProxy;

public class EntityProjectileGolemFireball extends EntityLargeFireball{
	public EntityProjectileGolemFireball(World world){
		super(world);
		setSize(0.2F,0.2F);
	}
	
	public EntityProjectileGolemFireball(World world, EntityLivingBase shooter, double x, double y, double z, double xDiff, double yDiff, double zDiff){
		super(world,shooter,xDiff,yDiff,zDiff);
		setPosition(x,y,z);
		setSize(0.2F,0.2F);
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			if (mop.entityHit != null)mop.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this,shootingEntity),ModCommonProxy.opMobs ? 8F : 4F);

			Explosion explosion = new FieryExplosion(worldObj,shootingEntity,posX,posY,posZ,ModCommonProxy.opMobs ? 3F : 2.35F);
			explosion.doExplosionA();
			explosion.doExplosionB(true);
			
			setDead();
		}
	}
	
	static class FieryExplosion extends Explosion{
		private final World world;
		
		public FieryExplosion(World world, Entity cause, double x, double y, double z, float strength){
			super(world,cause,x,y,z,strength,false,world.getGameRules().getGameRuleBooleanValue("mobGriefing"));
			this.world = world;
		}
		
		@Override
		public void doExplosionB(boolean doParticles){
			super.doExplosionB(doParticles);
			
			for(Iterator<BlockPos> iter = func_180343_e().iterator(); iter.hasNext();){
				BlockPos pos = iter.next();
				if (world.isAirBlock(pos) && world.rand.nextInt(9) == 0)world.setBlock(pos,Blocks.fire);
			}
		}
	}
}
