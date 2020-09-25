package me.daqem.xlifehealth.utils;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;

public class XLifeModifiers {

    public static final UUID MAX_HEALTH_MODIFIER = UUID.fromString("CA3F55D3-645A-4FA8-A497-9C13A33DB5CD");
    public static final UUID MAX_HEALTH_MODIFIER_DIMENSION = UUID.fromString("CA3F55D3-B35A-4FA8-A497-9C13A33DB5CD");



    public static void applyMaxHealthModifier(PlayerEntity player, float amount) {
        IAttributeInstance attribute = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
        attribute.removeModifier(MAX_HEALTH_MODIFIER);
        if (player.getEntityWorld().dimension.getType().equals(DimensionType.OVERWORLD)) {
            attribute.removeModifier(MAX_HEALTH_MODIFIER_DIMENSION);
        }
        attribute.applyModifier(new AttributeModifier(MAX_HEALTH_MODIFIER, "MaxHealth", amount, AttributeModifier.Operation.ADDITION));
    }

    public static void applyMaxHealthModifierDimension(PlayerEntity player) {
        IAttributeInstance attribute = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (player.getEntityWorld().dimension.getType().equals(DimensionType.OVERWORLD)) {
            attribute.removeModifier(MAX_HEALTH_MODIFIER_DIMENSION);
        } else {
            attribute.applyModifier(new AttributeModifier(MAX_HEALTH_MODIFIER_DIMENSION, "MaxHealth", 0, AttributeModifier.Operation.ADDITION));
        }
    }

}
