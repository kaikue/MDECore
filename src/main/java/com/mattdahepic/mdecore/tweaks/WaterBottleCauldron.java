package com.mattdahepic.mdecore.tweaks;

import com.mattdahepic.mdecore.MDECore;
import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WaterBottleCauldron {
    public WaterBottleCauldron () {
        MDECore.logger.info("Making water bottles fill cauldrons");
    }
    @SubscribeEvent
    public void playerInteract (PlayerInteractEvent e) {
        if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && e.world.getBlockState(e.pos).getBlock() == Blocks.cauldron) { //if rigth click on cauldron
            if (e.entityPlayer.inventory.getCurrentItem().getItem() == Items.potionitem && e.entityPlayer.inventory.getCurrentItem().getMetadata() == 0) { //if water bottle
                int currentLevel = ((Integer)e.world.getBlockState(e.pos).getValue(BlockCauldron.LEVEL));
                if (currentLevel < 3) { //if not full
                    e.world.setBlockState(e.pos,e.world.getBlockState(e.pos).withProperty(BlockCauldron.LEVEL,currentLevel+1)); //fill cauldron
                    if (!e.entityPlayer.capabilities.isCreativeMode) {
                        e.entityPlayer.inventory.getCurrentItem().stackSize--; //remove one water bottle
                        if (!e.entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle))) { //give the bottle back, but if they cant fit it...
                            e.world.spawnEntityInWorld(new EntityItem(e.world,e.pos.getX()+0.5D,e.pos.getY()+0.5D,e.pos.getZ()+0.5D,new ItemStack(Items.glass_bottle))); //...drop it!
                        }
                    }
                }
            }
        }
    }
}