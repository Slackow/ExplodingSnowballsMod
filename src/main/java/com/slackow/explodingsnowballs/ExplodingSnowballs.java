package com.slackow.explodingsnowballs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

import static net.minecraft.block.Blocks.SNOW_BLOCK;
import static net.minecraft.entity.EntityType.LIGHTNING_BOLT;

public class ExplodingSnowballs implements ModInitializer {

    private static Consumer<ExplodingSnowballEntity> explodeAtSnowball(float power) {
        return snowball -> snowball.getWorld().createExplosion(snowball, snowball.getX(), snowball.getY(), snowball.getZ(), power, World.ExplosionSourceType.TNT);
    }

    private static final Consumer<ExplodingSnowballEntity> LIGHTNING_AT_SNOWBALL = snowball -> {
        var lightningEntity = LIGHTNING_BOLT.create(snowball.getWorld());
        if (lightningEntity != null) {
            var blockPos = snowball.getBlockPos();
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
            lightningEntity.setChanneler(snowball.getOwner() instanceof ServerPlayerEntity ? (ServerPlayerEntity)snowball.getOwner() : null);
            snowball.getWorld().spawnEntity(lightningEntity);
        }

    };

    public static final Item EXPLODING_SNOWBALL = new ExplodingSnowballItem(explodeAtSnowball(5.0f));
    public static final Item LIGHTNING_SNOWBALL = new ExplodingSnowballItem(LIGHTNING_AT_SNOWBALL);
    public static final Item LIGHTSPLODING_SNOWBALL = new ExplodingSnowballItem(explodeAtSnowball(3.0f).andThen(LIGHTNING_AT_SNOWBALL));
    public static final Block SNOW_SAND = new FallingBlock(FabricBlockSettings.of(Material.SNOW_BLOCK).strength(0.2f).sounds(BlockSoundGroup.SNOW));

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, path("exploding_snowball"), EXPLODING_SNOWBALL);
        Registry.register(Registries.ITEM, path("lightning_snowball"), LIGHTNING_SNOWBALL);
        Registry.register(Registries.ITEM, path("lightsploding_snowball"), LIGHTSPLODING_SNOWBALL);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x787874, EXPLODING_SNOWBALL);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xe8eb34, LIGHTNING_SNOWBALL);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xeb4034, LIGHTSPLODING_SNOWBALL);

        Registry.register(Registries.BLOCK, path("snow_sand"), SNOW_SAND);
        Registry.register(Registries.ITEM, path("snow_sand"), new BlockItem(SNOW_SAND, new FabricItemSettings()));
    }

    private static Identifier path(String path) {
        return new Identifier("exploding_snowballs", path);
    }
}
