package io.anuke.ld42.entities.traits;

import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.entities.trait.HealthTrait;

public interface EnemyTrait extends HealthTrait, Entity{
    boolean isActive();
}
