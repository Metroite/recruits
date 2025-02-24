package com.talhanation.recruits.entities.ai;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

public class RecruitEatGoal extends Goal {

    AbstractRecruitEntity recruit;
    ItemStack foodItem;

    public RecruitEatGoal(AbstractRecruitEntity recruit) {
        this.recruit = recruit;
    }

    @Override
    public boolean canUse() {
        if (recruit.isUsingItem()) return false;
        if (recruit.beforeFoodItem != null) return false;

        float currentHealth = recruit.getHealth();
        float maxHealth = recruit.getMaxHealth();

        if (recruit.getTarget() != null)// || recruit.hasFoodItemInInv())
        {
            return (currentHealth <  maxHealth - maxHealth / 1.75);
        }
        else
            return (currentHealth <  maxHealth - (maxHealth / 5.0D));

    }

    @Override
    public void start() {
        if (hasFoodInInv()) {
            recruit.beforeFoodItem = recruit.getItemInHand(InteractionHand.OFF_HAND);

            recruit.setIsEating(true);
            recruit.setItemInHand(InteractionHand.OFF_HAND, foodItem);
            recruit.setSlot(10, foodItem);

            recruit.startUsingItem(InteractionHand.OFF_HAND);

            recruit.heal(foodItem.getItem().getFoodProperties().getSaturationModifier() * 10);

            recruit.eat(recruit.level, foodItem);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    private boolean hasFoodInInv(){
        SimpleContainer inventory = recruit.getInventory();

        for(int i = 0; i < inventory.getContainerSize(); i++){
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack.isEdible()){
                setFoodItem(itemStack);
                return true;
            }
        }
        return false;
    }


    private void setFoodItem(ItemStack itemStack){
        this.foodItem = itemStack.copy();
        this.foodItem.setCount(2);// fixes shield bug lul
        itemStack.shrink(1);
    }
}
