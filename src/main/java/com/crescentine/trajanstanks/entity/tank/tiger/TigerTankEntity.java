package com.crescentine.trajanstanks.entity.tank.tiger;

import com.crescentine.trajanstanks.TankModClient;
import com.crescentine.trajanstanks.config.TankModConfig;
import com.crescentine.trajanstanks.entity.BaseTankEntity;
import com.crescentine.trajanstanks.entity.shell.ShellEntity;
import com.crescentine.trajanstanks.item.TankModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TigerTankEntity extends BaseTankEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private final int cooldown = TankModConfig.heavy_tank_shot_cooldown.get();
    private int time = cooldown;
    public TigerTankEntity(EntityType<?> entityType, Level world) {
        super((EntityType<? extends Pig>) entityType, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Pig.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 250.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10.0D)
                .add(Attributes.ARMOR, 5.0f)
                .add(Attributes.FOLLOW_RANGE, 0.0D);
    }
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.tank Movement", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    double speed = TankModConfig.heavy_tank_speed.get();
    float speedFloat = (float)speed;
    @Override
    public float getSteeringSpeed() {
        if (TankModClient.STARTMOVING.isDown()) {
            return speedFloat;
        }
        return 0.0f;
    }

    @Override
    public boolean canBeControlledByRider() {
        return true;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));

    }
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
    @Override
    protected boolean isImmobile() {
        return false;
    }
    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        player.startRiding(this, true);
        return InteractionResult.SUCCESS;
    }
    static int shellsUsed = 1;
    public void tick() {
        super.tick();

        if (time < cooldown) time++;
    }
    public boolean shoot(Player player, Level world) {
        ItemStack itemStack = ItemStack.EMPTY;
        Player playerEntity = (Player) player;
        for (int i = 0; i < playerEntity.getInventory().getContainerSize(); ++i) {
            ItemStack stack = playerEntity.getInventory().getItem(i);
            if (stack.getItem() == TankModItems.SHELL_ITEM.get() && stack.getCount() >= shellsUsed) {
                itemStack = stack;
                break;
            }
        }

        if (time < cooldown) {
            player.sendMessage(new TextComponent("Please wait " + (cooldown - time) / 20 + " s !").withStyle(ChatFormatting.AQUA), Util.NIL_UUID);
            world.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f);
            return false;
        }
        if (itemStack.isEmpty()) {
            player.sendMessage(new TextComponent("You don't have any ammo!" ).withStyle(ChatFormatting.RED), Util.NIL_UUID);
            world.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f);
            return false;
        }
        if (!itemStack.isEmpty()) {
            ShellEntity shellEntity = new ShellEntity(player, world);
            shellEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 0F);

            double distance = 1.0D;

            double x = -Mth.sin((float) (player.getEyeY() / 180.0F * (float) Math.PI)) * distance;
            double z = -Mth.cos((float) (player.getEyeY() / 180.0F * (float) Math.PI)) * distance;


            shellEntity.setPos(player.getEyePosition());
            world.addFreshEntity(shellEntity);

            itemStack.shrink(shellsUsed);
        }
        time = 0;
        return true;
    }
}