package me.daqem.xlifehealth.events;

import me.daqem.xlifehealth.XLifeHealth;
import me.daqem.xlifehealth.utils.SecurityCheck;
import me.daqem.xlifehealth.utils.SendMessage;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static me.daqem.xlifehealth.utils.XLifeModifiers.applyMaxHealthModifier;
import static me.daqem.xlifehealth.utils.XLifeModifiers.applyMaxHealthModifierDimension;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = XLifeHealth.MOD_ID)
public class HealthEvents {

    private static float maxHealth = 0;

    public static float getMaxHealth() {
        return maxHealth;
    }

    public static void setMaxHealth(float maxHealth) {
        HealthEvents.maxHealth = maxHealth;
    }

    @SubscribeEvent
    public void onPlayerDie(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            setMaxHealth(player.getMaxHealth());
        }
    }
    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        SecurityCheck.securityCheck(player);
        setHealthAfterDeath(player);
        if (player.getEntityWorld().dimension.getType().equals(DimensionType.OVERWORLD)) {
            UUID MAX_HEALTH_MODIFIER_DIMENSION = UUID.fromString("CA3F55D3-B35A-4FA8-A497-9C13A33DB5CD");
            IAttributeInstance attribute = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
            attribute.removeModifier(MAX_HEALTH_MODIFIER_DIMENSION);
        }
    }

    @SubscribeEvent
    public void onEnterNether(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerEntity player = event.getPlayer();
        SecurityCheck.securityCheck(player);
        applyMaxHealthModifierDimension(player);
    }

    @SubscribeEvent
    public void onFirstJoin(PlayerEvent.PlayerLoggedInEvent event){
        PlayerEntity player = event.getPlayer();
        CompoundNBT entityData = player.getPersistentData();
        setMaxHealth(player.getMaxHealth() - 2);
        if(!entityData.getBoolean("xlifehealth.firstJoin") && player.getMaxHealth() == 20f) {
            entityData.putBoolean("xlifehealth.firstJoin", true);
            applyMaxHealthModifier(player, -18f);
            SendMessage.sendMessage(player, TextFormatting.YELLOW + "=====================================");
            SendMessage.sendMessage(player, " ");
            SendMessage.sendMessage(player, TextFormatting.GOLD + "Welcome to X Life!");
            SendMessage.sendMessage(player, " ");
            SendMessage.sendMessage(player, TextFormatting.WHITE + "You'll start off with only one heart.");
            SendMessage.sendMessage(player, TextFormatting.WHITE + "Every time you die, a heart will be added.");
            SendMessage.sendMessage(player, TextFormatting.WHITE + "When you die with 10 hearts, you will be banned!");
            SendMessage.sendMessage(player, " ");
            SendMessage.sendMessage(player, TextFormatting.GOLD + "Good Luck!");
            SendMessage.sendMessage(player, " ");
            SendMessage.sendMessage(player, TextFormatting.YELLOW + "=====================================");
        }
    }

    public void setHealthAfterDeath(PlayerEntity player) {
        if (getMaxHealth() >= 2 && getMaxHealth() <= 18) {
            float maxHealth = getMaxHealth();
            int lives = (int) (10 - (maxHealth / 2));
            int modifierAmount = (lives * 2 - 2) - ((lives * 2 - 2) + (lives * 2 - 2));
            applyMaxHealthModifier(player, modifierAmount);
            if (lives < 10) {
                if (lives >= 2) {
                    SendMessage.sendMessage(player, TextFormatting.YELLOW + "Be careful, you only have " + TextFormatting.GOLD + lives + TextFormatting.YELLOW + " lives left.");
                }
                if (lives == 1) {
                    SendMessage.sendMessage(player, TextFormatting.DARK_RED + "Be careful, this is your last life!!!");
                }
            }
        } else if (getMaxHealth() == 20) {
            player.setGameType(GameType.SPECTATOR);
            SendMessage.sendMessage(player,TextFormatting.YELLOW + "You have died with 10 hearts remaining.");
            SendMessage.sendMessage(player,TextFormatting.YELLOW + "You have been put in spectator mode.");
            applyMaxHealthModifier(player, 20);
        } else {
            applyMaxHealthModifier(player, -18);
            XLifeHealth.LOGGER.info("[XLIFEHEALTHMOD] Error ID: 100");
            XLifeHealth.LOGGER.info("[XLIFEHEALTHMOD] Please report this error to DaqEm.");
            XLifeHealth.LOGGER.info("[XLIFEHEALTHMOD] Email: daqemyt@gmail.com");
        }
    }
}
