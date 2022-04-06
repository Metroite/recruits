package com.talhanation.recruits.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.Main;
import com.talhanation.recruits.inventory.CommandContainer;
import com.talhanation.recruits.network.*;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class CommandScreen extends ScreenBase<CommandContainer> {

    private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Main.MOD_ID,"textures/gui/command_gui.png");


    private static final ITextComponent TEXT_GROUP = new TranslationTextComponent("gui.recruits.command.group");

    private static final ITextComponent TEXT_FOLLOW = new TranslationTextComponent("gui.recruits.command.follow");
    private static final ITextComponent TEXT_WANDER = new TranslationTextComponent("gui.recruits.command.wander");
    private static final ITextComponent TEXT_HOLD_MY_POS = new TranslationTextComponent("gui.recruits.command.holdMyPos");
    private static final ITextComponent TEXT_HOLD_POS = new TranslationTextComponent("gui.recruits.command.holdPos");
    private static final ITextComponent TEXT_BACK_TO_POS = new TranslationTextComponent("gui.recruits.command.backToPos");

    private static final ITextComponent TEXT_PASSIVE = new TranslationTextComponent("gui.recruits.command.passive");
    private static final ITextComponent TEXT_NEUTRAL = new TranslationTextComponent("gui.recruits.command.neutral");
    private static final ITextComponent TEXT_AGGRESSIVE = new TranslationTextComponent("gui.recruits.command.aggressive");
    private static final ITextComponent TEXT_RAID = new TranslationTextComponent("gui.recruits.command.raid");

    private static final ITextComponent TEXT_CLEAR_TARGET = new TranslationTextComponent("gui.recruits.command.clearTargets");

    private static final int fontColor = 16250871;
    private PlayerEntity player;
    private int group;
    private int recCount;

    public CommandScreen(CommandContainer commandContainer, PlayerInventory playerInventory, ITextComponent title) {
        super(RESOURCE_LOCATION, commandContainer, playerInventory, title);
        imageWidth = 201;
        imageHeight = 170;
        player = playerInventory.player;

    }

    @Override
    public boolean keyReleased(int x, int y, int z) {
        super.keyReleased(x,y,z);
        this.onClose();
        return true;
    }

    @Override
    protected void init() {
        super.init();
        //WANDER
        addButton(new Button(leftPos - 70 - 40 + imageWidth / 2, topPos + 20 + 30, 81, 20, new StringTextComponent("Release!"), button -> {
            CommandEvents.sendFollowCommandInChat(0, player);
            Main.SIMPLE_CHANNEL.sendToServer(new MessageFollow(player.getUUID(), 0, group));

        },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_WANDER, c, d);
        }));

        //FOLLOW
        addButton(new Button(leftPos - 40 - 50 + imageWidth / 2, topPos + 10, 81, 20, new StringTextComponent("Follow me!"), button -> {
            CommandEvents.sendFollowCommandInChat(1, player);
            Main.SIMPLE_CHANNEL.sendToServer(new MessageFollow(player.getUUID(), 1, group));

        },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_FOLLOW, c, d);
        }));

        //TO PLAYER POS
        addButton(new Button(leftPos + 10 + imageWidth / 2, topPos + 10, 81, 20, new StringTextComponent("Hold here!"), button -> {
            CommandEvents.sendFollowCommandInChat(4, player);
            Main.SIMPLE_CHANNEL.sendToServer(new MessageFollow(player.getUUID(), 4, group));

        },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_HOLD_MY_POS, c, d);
        }));

        //HOLD POS
        addButton(new Button(leftPos + 71 + imageWidth / 2, topPos + 20 + 30, 40, 20, new StringTextComponent("Hold!"), button -> {
            CommandEvents.sendFollowCommandInChat(2, player);
            Main.SIMPLE_CHANNEL.sendToServer(new MessageFollow(player.getUUID(), 2, group));

            },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_HOLD_POS, c, d);
        }));

        //Back to POS
        addButton(new Button(leftPos + 30 + imageWidth / 2, topPos + 20 + 30, 40, 20, new StringTextComponent("Back!"), button -> {
            CommandEvents.sendFollowCommandInChat(3, player);
            Main.SIMPLE_CHANNEL.sendToServer(new MessageFollow(player.getUUID(), 3, group));

        },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_BACK_TO_POS, c, d);
        }));

        //AGGRESSIVE
        addButton(new Button(leftPos - 40 - 70 + imageWidth / 2, topPos + 20 + 30 + 30, 81, 20, new StringTextComponent("Stay Aggressive!"), button -> {
            CommandEvents.sendAggroCommandInChat(1, player);
            Main.SIMPLE_CHANNEL.sendToServer(new MessageAggro(player.getUUID(), 1, group));

            },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_AGGRESSIVE, c, d);
        }));

        //RAID
        addButton(new Button(leftPos + 30 + imageWidth / 2, topPos + 20 + 30 + 30, 81, 20, new StringTextComponent("Raid!"), button -> {
            CommandEvents.sendAggroCommandInChat(2, player);
            Main.SIMPLE_CHANNEL.sendToServer(new MessageAggro(player.getUUID(), 2, group));

        },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_RAID, c, d);
        }));

        /*
        //ATTACK TARGET
        addButton(new Button(leftPos + 30 + imageWidth / 2, topPos + 150, 81, 20, new StringTextComponent("Attack!"), button -> {
            this.onAttackButton();
        }));
        */

        //NEUTRAL
        addButton(new Button(leftPos - 40 - 50 + imageWidth / 2, topPos + 120, 81, 20, new StringTextComponent("Stay Neutral!"), button -> {
            CommandEvents.sendAggroCommandInChat(0, player);
            Main.SIMPLE_CHANNEL.sendToServer(new MessageAggro(player.getUUID(), 0, group));

        },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_NEUTRAL, c, d);
        }));

        //CLEAR TARGET
        addButton(new Button(leftPos + 10 + imageWidth / 2, topPos + 120, 81, 20, new StringTextComponent("Clear Targets!"), button -> {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageClearTarget(player.getUUID(), group));

        },  (a, b, c, d) -> {
            this.renderTooltip(b, TEXT_CLEAR_TARGET, c, d);
        }));

        //GROUP
        addButton(new Button(leftPos - 5 + imageWidth / 2, topPos - 40 + imageHeight / 2, 11, 20, new StringTextComponent("+"), button -> {
            this.group = getSavedCurrentGroup(player);

            if (this.group != 9) {
                this.group ++;

                this.saveCurrentGroup(player);
            }
        }));

        addButton(new Button(leftPos - 5 + imageWidth / 2, topPos + imageHeight / 2, 11, 20, new StringTextComponent("-"), button -> {
            this.group = getSavedCurrentGroup(player);

            if (this.group != 0) {
                this.group --;

                this.saveCurrentGroup(player);
            }
        }));
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);

        //Main.SIMPLE_CHANNEL.sendToServer(new MessageRecruitsInCommand(player.getUUID()));

        //this.recCount = getSavedRecruitCount(player);
        this.group = getSavedCurrentGroup(player);
        //player.sendMessage(new StringTextComponent("SCREEN int: " + recCount), player.getUUID());

        int k = 78;//rechst links
        int l = 71;//höhe

        font.draw(matrixStack, "" +  handleGroupText(this.group), k , l, fontColor);
        //font.draw(matrixStack, "" +  handleRecruitCountText(currentRecruits), k - 30 , 0, fontColor);
    }

    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static String handleGroupText(int group){
        if (group == 0){
            return "Everyone";
        }
        else
            return (TEXT_GROUP.getString()+ " " + group);
    }

    public static String handleRecruitCountText(int recCount){
        return ("Recruits in Command: " + recCount);
    }
    /*
    public void onAttackButton(){
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraft.player;

        RayTraceResult rayTraceResult = minecraft.hitResult;
        if (rayTraceResult != null) {
            if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if (entityRayTraceResult.getEntity() instanceof LivingEntity && clientPlayerEntity != null) {
                    LivingEntity living = (LivingEntity) entityRayTraceResult.getEntity();
                    Main.SIMPLE_CHANNEL.sendToServer(new MessageAttackEntity(clientPlayerEntity.getUUID(), living.getUUID()));
                }
            }
        }
    }

     */

    public int getSavedCurrentGroup(PlayerEntity player) {
        CompoundNBT playerNBT = player.getPersistentData();
        CompoundNBT nbt = playerNBT.getCompound(PlayerEntity.PERSISTED_NBT_TAG);

        return nbt.getInt("CommandingGroup");
    }

    public void saveCurrentGroup(PlayerEntity player) {
        CompoundNBT playerNBT = player.getPersistentData();
        CompoundNBT nbt = playerNBT.getCompound(PlayerEntity.PERSISTED_NBT_TAG);

        nbt.putInt( "CommandingGroup", this.group);
        playerNBT.put(PlayerEntity.PERSISTED_NBT_TAG, nbt);
    }

}
