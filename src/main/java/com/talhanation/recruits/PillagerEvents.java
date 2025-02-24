package com.talhanation.recruits;

import com.talhanation.recruits.config.RecruitsModConfig;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.ai.pillager.PillagerMeleeAttackGoal;
import com.talhanation.recruits.entities.ai.pillager.PillagerUseShield;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.datafix.fixes.OminousBannerRenameFix;
import net.minecraft.util.datafix.fixes.OminousBannerTileEntityRenameFix;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.village.VillageSiegeEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.raid.Raider;

public class PillagerEvents {
    protected final Random random = new Random();

    @SubscribeEvent
    public void attackRecruit(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Pillager) {
            Pillager pillager = (Pillager) entity;
            if(RecruitsModConfig.PillagerIncreasedCombatRange.get()) {
                pillager.goalSelector.addGoal(2, new FindTargetGoal(pillager, 24.0F));
                pillager.goalSelector.addGoal(2, new RangedCrossbowAttackGoal<>(pillager, 1.0D, 24.0F));
            }
        }

        if (entity instanceof AbstractIllager) {
            AbstractIllager illager = (AbstractIllager) entity;
            illager.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(illager, AbstractRecruitEntity.class, true));
            if (RecruitsModConfig.PillagerAttackMonsters.get()){
                illager.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(illager, Zombie.class, true));
                illager.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(illager, AbstractSkeleton.class, true));
                illager.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(illager, Spider.class, true));
            }
            if (RecruitsModConfig.ShouldPillagersRaidNaturally.get()){
                illager.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(illager, Animal.class, true));
            }
        }

        if (entity instanceof Monster) {
            Monster monster = (Monster) entity;
            if (!(monster instanceof Creeper))
            monster.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(monster, AbstractRecruitEntity.class, true));
        }

        if (entity instanceof Zombie){
            if (RecruitsModConfig.MonstersAttackPillagers.get()) {
                Zombie monster = (Zombie) entity;
                monster.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(monster, AbstractIllager.class, true));
            }
        }
        if (entity instanceof AbstractSkeleton){
            if (RecruitsModConfig.MonstersAttackPillagers.get()) {
                AbstractSkeleton monster = (AbstractSkeleton) entity;
                monster.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(monster, AbstractIllager.class, true));
            }
        }
        if (entity instanceof Spider){
            if (RecruitsModConfig.MonstersAttackPillagers.get()) {
                Spider monster = (Spider) entity;
                monster.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(monster, AbstractIllager.class, true));
            }
        }

        if (entity instanceof Vindicator && RecruitsModConfig.VindicatorSpawnItems.get()) {
            Vindicator vindicator = (Vindicator) entity;
            vindicator.goalSelector.addGoal(0, new PillagerUseShield(vindicator));
            vindicator.setPersistenceRequired();

            int i = this.random.nextInt(3);
            if (i == 2) vindicator.setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_AXE.getDefaultInstance());
            else {
                vindicator.setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_SWORD.getDefaultInstance());
                vindicator.setItemInHand(InteractionHand.OFF_HAND, Items.SHIELD.getDefaultInstance());
            }
        }

        if (entity instanceof Pillager && RecruitsModConfig.PillagerSpawnItems.get()) {
            Pillager pillager = (Pillager) entity;
            pillager.goalSelector.addGoal(0, new PillagerMeleeAttackGoal(pillager, 1.15D, true));
            pillager.goalSelector.addGoal(0, new PillagerUseShield(pillager));
            pillager.setPersistenceRequired();

            int i = this.random.nextInt(6);
            switch (i){
                case 1:
                    pillager.setItemInHand(InteractionHand.MAIN_HAND, Items.CROSSBOW.getDefaultInstance());
                    break;
                case 2:
                    pillager.setItemInHand(InteractionHand.MAIN_HAND, Items.CROSSBOW.getDefaultInstance());
                    break;
                case 3:
                    pillager.setItemInHand(InteractionHand.MAIN_HAND, Items.CROSSBOW.getDefaultInstance());
                    break;
                case 4:
                    pillager.setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_AXE.getDefaultInstance());
                    pillager.setItemInHand(InteractionHand.OFF_HAND, Items.SHIELD.getDefaultInstance());
                    break;
                case 5:
                    pillager.setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_SWORD.getDefaultInstance());
                    pillager.setItemInHand(InteractionHand.OFF_HAND, Items.SHIELD.getDefaultInstance());
                    break;
                case 0:
                    pillager.setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_SWORD.getDefaultInstance());
                    pillager.setItemInHand(InteractionHand.OFF_HAND, Items.SHIELD.getDefaultInstance());
                    break;
            }
        }
        /*
        if (entity instanceof VindicatorEntity) {
            VindicatorEntity vindicator = (VindicatorEntity) entity;

            List<PillagerEntity> list1 = entity.level.getEntitiesOfClass(PillagerEntity.class, vindicator.getBoundingBox().inflate(64));
            int max = 2 + random.nextInt(10);
            if (list1.size() > 1) {
                vindicator.remove();
            }
            else {
                for (int k = 0; k < max; k++) createPillager(vindicator);
            }
        }
        */
    }

    @SubscribeEvent
    public void onBiomeLoadingPillager(BiomeLoadingEvent event) {
        Biome.BiomeCategory category = event.getCategory();
        if (RecruitsModConfig.PillagerSpawn.get()) {
            if (category != Biome.BiomeCategory.NETHER && category != Biome.BiomeCategory.THEEND && category != Biome.BiomeCategory.NONE && category != Biome.BiomeCategory.OCEAN && category != Biome.BiomeCategory.RIVER) {
                event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1, 2));
            }
        }
    }

    private void createPillager(LivingEntity entity){
        Pillager pillager = EntityType.PILLAGER.create(entity.level);
        pillager.copyPosition(entity);
        entity.level.addFreshEntity(pillager);
    }

    @SubscribeEvent
    public void raidStartOnBurningOminus(EntityEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) event.getEntity();
            ItemStack itemStack = itemEntity.getItem();

            Level level = entity.level;
            if (itemStack.getItem().equals(Items.WHITE_BANNER)) {

                if (entity.isOnFire() && ItemStack.matches(itemStack, Raid.getLeaderBannerInstance())) {
                    Player player = level.getNearestPlayer(entity, 16D);
                    if (player != null) {
                        MobEffectInstance effectinstance1 = player.getEffect(MobEffects.BAD_OMEN);
                        int i = 1;
                        if (effectinstance1 != null) {
                            i += effectinstance1.getAmplifier();
                            player.removeEffectNoUpdate(MobEffects.BAD_OMEN);
                        } else {
                            --i;
                        }
                        i = Mth.clamp(i, 0, 4);
                        MobEffectInstance effectinstance = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, i, false, false, true);
                        if (!player.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                            player.addEffect(effectinstance);
                        }
                        level.explode(entity, entity.getX(), entity.getY(), entity.getZ(), 0.5F, Explosion.BlockInteraction.BREAK);
                        entity.remove();
                    }
                }
            }
        }
    }
}

class FindTargetGoal extends Goal {
    private final Raider mob;
    private final float hostileRadiusSqr;
    private final Random random = new Random();
    public final TargetingConditions shoutTargeting = (new TargetingConditions()).range(8.0D).allowNonAttackable().allowInvulnerable().allowSameTeam().allowUnseeable().ignoreInvisibilityTesting();

    public FindTargetGoal(AbstractIllager p_i50573_2_, float p_i50573_3_) {
        this.mob = p_i50573_2_;
        this.hostileRadiusSqr = p_i50573_3_ * p_i50573_3_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        return this.mob.getCurrentRaid() == null && this.mob.getTarget() != null && !this.mob.isAggressive() && (livingentity == null || livingentity.getType() != EntityType.PLAYER);
    }

    public void start() {
        super.start();
        this.mob.getNavigation().stop();

        for(Raider abstractraiderentity : this.mob.level.getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D))) {
            abstractraiderentity.setTarget(this.mob.getTarget());
        }

    }

    public void stop() {
        super.stop();
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            for(Raider abstractraiderentity : this.mob.level.getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D))) {
                abstractraiderentity.setTarget(livingentity);
                abstractraiderentity.setAggressive(true);
            }

            this.mob.setAggressive(true);
        }

    }

    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            if (this.mob.distanceToSqr(livingentity) > (double)this.hostileRadiusSqr) {
                this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                if (this.random.nextInt(50) == 0) {
                    this.mob.playAmbientSound();
                }
            } else {
                this.mob.setAggressive(true);
            }

            super.tick();
        }
    }
}