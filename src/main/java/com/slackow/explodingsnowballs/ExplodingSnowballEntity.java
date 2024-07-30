package com.slackow.explodingsnowballs;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class ExplodingSnowballEntity extends SnowballEntity {
    private final Consumer<ExplodingSnowballEntity> action;

    public static TrackedData<Boolean> CONTINUES = DataTracker.registerData(ExplodingSnowballEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static TrackedData<Float> POWER = DataTracker.registerData(ExplodingSnowballEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static TrackedData<Float> PURITY = DataTracker.registerData(ExplodingSnowballEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CONTINUES, false);
        this.dataTracker.startTracking(POWER, 5.0F);
        this.dataTracker.startTracking(PURITY, 1.0F);
    }

    public ExplodingSnowballEntity(World world, LivingEntity owner, Consumer<ExplodingSnowballEntity> action) {
        super(world, owner);
        this.action = action;
    }

    public ExplodingSnowballEntity(World world, Position position, Consumer<ExplodingSnowballEntity> action) {
        super(world, position.getX(), position.getY(), position.getZ());
        this.action = action;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!getWorld().isClient) {
            action.accept(this);
        }
        if (!dataTracker.get(CONTINUES)) {
            super.onCollision(hitResult);
        }
    }
}
