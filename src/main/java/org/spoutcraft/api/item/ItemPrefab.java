/**
 * This file is a part of Spoutcraft.
 *
 * Copyright (c) 2013 SpoutcraftDev <http://spoutcraft.org>
 * Spoutcraft is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spoutcraft.api.item;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spoutcraft.api.Prefab;

public class ItemPrefab extends Prefab {
	private final String displayName;
	private final int maxStackSize;
	private final boolean showInCreativeTab;

	public ItemPrefab(String identifier, String displayName, int maxStackSize, boolean showInCreativeTab) {
		super(identifier);
		this.displayName = displayName;
		this.maxStackSize = maxStackSize;
		this.showInCreativeTab = showInCreativeTab;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public boolean shouldShowInCreativeTab() {
		return showInCreativeTab;
	}

	public boolean onLeftClickEntity(Side side, ItemStack stack, EntityPlayer player, Entity entity) {
		return false;
	}

	public ItemStack onItemRightClick(Side side, ItemStack stack, World world, EntityPlayer player) {
		return stack;
	}

	public void onUpdate(Side side, ItemStack stack, World world, Entity entity, int slot, boolean isCurrentlyHeldItem) {

	}

	public void onCraftOrSmelt(Side side, ItemStack stack, World world, EntityPlayer player) {

	}

	public void onPlayerStoppedUsing(Side side, ItemStack stack, World world, EntityPlayer player, int ticksItemHasBeenUsed) {

	}

	@Override
	public String toString() {
		final String NEW_LINE = System.getProperty("line.separator");
		final String parent = super.toString();
		final StringBuilder builder = new StringBuilder(parent.substring(0, parent.length() - 1) + NEW_LINE);
		builder
				.append(" Display Name: " + displayName + NEW_LINE)
				.append(" Max Stack Size: " + maxStackSize + NEW_LINE)
				.append("}");
		return builder.toString();
	}
}
