/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * 
 * File Created @ [Mar 13, 2014, 4:30:27 PM (GMT)]
 */
package vazkii.botania.common.item;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILens;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.item.recipe.ManaGunLensRecipe;
import vazkii.botania.common.lib.LibItemIDs;
import vazkii.botania.common.lib.LibItemNames;

public class ItemManaGun extends ItemMod {

	private static final String TAG_LENS = "lens";
	private static final int COOLDOWN = 30;
	
	public ItemManaGun() {
		super(LibItemIDs.idManaGun);
		setMaxDamage(COOLDOWN);
		setMaxStackSize(1);
		setNoRepair();
		setUnlocalizedName(LibItemNames.MANA_GUN);
		
		GameRegistry.addRecipe(new ManaGunLensRecipe());
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if(!par3EntityPlayer.isSneaking()) {
			if(par1ItemStack.getItemDamage() == 0) {
				EntityManaBurst burst = getBurst(par3EntityPlayer, par1ItemStack);
				if(burst != null && ManaItemHandler.requestManaExact(par1ItemStack, par3EntityPlayer, burst.getMana(), true)) {
					if(!par2World.isRemote) {
						par2World.playSoundAtEntity(par3EntityPlayer, "random.explode", 0.9F, 3F);
						par2World.spawnEntityInWorld(burst);
					} else par3EntityPlayer.swingItem();
					par1ItemStack.setItemDamage(COOLDOWN);
				} else if(!par2World.isRemote)
					par2World.playSoundAtEntity(par3EntityPlayer, "random.click", 0.6F, (1.0F + (par2World.rand.nextFloat() - par2World.rand.nextFloat()) * 0.2F) * 0.7F);

			}
		} else {
			ItemStack lens = getLens(par1ItemStack);
			if(lens != null) {
				if(!par3EntityPlayer.inventory.addItemStackToInventory(lens.copy()))
					par3EntityPlayer.addChatMessage("botaniamisc.invFull");
				else setLens(par1ItemStack, null);
			}
		}
	
		return par1ItemStack;
	}

	public EntityManaBurst getBurst(EntityPlayer player, ItemStack stack) {
		EntityManaBurst burst = new EntityManaBurst(player);

		int maxMana = 120;
		int color = 0x00FF00;
		int ticksBeforeManaLoss = 60;
		float manaLossPerTick = 4F;
		float motionModifier = 5F;
		float gravity = 0F;
		BurstProperties props = new BurstProperties(maxMana, ticksBeforeManaLoss, manaLossPerTick, gravity, motionModifier, color);

		ItemStack lens = getLens(stack);
		if(lens != null)
			((ILens) lens.getItem()).apply(lens, props);

		
		burst.setSourceLens(lens);
		if(ManaItemHandler.requestManaExact(stack, player, props.maxMana, false)) {
			burst.setColor(props.color);
			burst.setMana(props.maxMana);
			burst.setStartingMana(props.maxMana);
			burst.setMinManaLoss(props.ticksBeforeManaLoss);
			burst.setManaLossPerTick(props.manaLossPerTick);
			burst.setGravity(props.gravity);
			burst.setMotion(burst.motionX * props.motionModifier, burst.motionY * props.motionModifier, burst.motionZ * props.motionModifier);

			return burst;
		}
		return null;
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		ItemStack lens = getLens(par1ItemStack);
		if(lens != null) {
			List<String> tooltip = lens.getTooltip(par2EntityPlayer, false);
			if(tooltip.size() > 1)
				par3List.addAll(tooltip.subList(1, tooltip.size()));
		}
	}
	
	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		ItemStack lens = getLens(par1ItemStack);
		return super.getItemDisplayName(par1ItemStack) + (lens == null ? "" : " (" + EnumChatFormatting.GREEN + lens.getDisplayName() + EnumChatFormatting.RESET + ")");
	}
	
	public static void setLens(ItemStack stack, ItemStack lens) {
		NBTTagCompound cmp = new NBTTagCompound();
		if(lens != null)
			lens.writeToNBT(cmp);
		ItemNBTHelper.setCompound(stack, TAG_LENS, cmp);
	}
	
	public static ItemStack getLens(ItemStack stack) {
		NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, TAG_LENS, true);
		if(cmp != null) {
			ItemStack lens = ItemStack.loadItemStackFromNBT(cmp);
			return lens;
		}
		return null;
	}


	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		if(par1ItemStack.isItemDamaged())
			par1ItemStack.setItemDamage(par1ItemStack.getItemDamage() - 1);
	}
}
