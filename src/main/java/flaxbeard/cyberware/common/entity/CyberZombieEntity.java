package flaxbeard.cyberware.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CyberZombieEntity extends ZombieEntity {
    public CyberZombieEntity(EntityType<? extends ZombieEntity> entityType, World level) {
        super(entityType, level);
    }

    @Nonnull
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return ZombieEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }
}
