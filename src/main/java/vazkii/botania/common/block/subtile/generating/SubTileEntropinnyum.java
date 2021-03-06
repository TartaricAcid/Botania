/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jun 7, 2014, 10:59:44 PM (GMT)]
 */
package vazkii.botania.common.block.subtile.generating;

import java.util.List;

import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.network.PacketBotaniaEffect;
import vazkii.botania.common.network.PacketHandler;

public class SubTileEntropinnyum extends SubTileGenerating {

	private static final int RANGE = 12;

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!supertile.getWorld().isRemote && mana == 0) {
			List<EntityTNTPrimed> tnts = supertile.getWorld().getEntitiesWithinAABB(EntityTNTPrimed.class, new AxisAlignedBB(supertile.getPos().add(-RANGE, -RANGE, -RANGE), supertile.getPos().add(RANGE + 1, RANGE + 1, RANGE + 1)));
			for(EntityTNTPrimed tnt : tnts) {
				if(tnt.getFuse() == 1 && !tnt.isDead && !supertile.getWorld().getBlockState(new BlockPos(tnt)).getMaterial().isLiquid()) {
					tnt.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.2F, (1F + (supertile.getWorld().rand.nextFloat() - supertile.getWorld().rand.nextFloat()) * 0.2F) * 0.7F);
					tnt.setDead();
					mana += getMaxMana();
					sync();

					PacketHandler.sendToNearby(supertile.getWorld(), supertile.getPos(), new PacketBotaniaEffect(PacketBotaniaEffect.EffectType.ENTROPINNYUM, tnt.posX, tnt.posY, tnt.posZ));
				}
			}
		}
	}

	@Override
	public int getColor() {
		return 0xcb0000;
	}

	@Override
	public int getMaxMana() {
		return 6500;
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(toBlockPos(), RANGE);
	}

	@Override
	public LexiconEntry getEntry() {
		return LexiconData.entropinnyum;
	}

}
