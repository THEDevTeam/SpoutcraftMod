package org.spoutcraft.mod.block;

import net.minecraft.block.Block;
import org.spoutcraft.api.block.BlockPrefab;
import org.spoutcraft.mod.SpoutcraftMod;
import org.spoutcraft.mod.material.CustomMaterial;

public class CustomBlock extends Block {
	private final BlockPrefab prefab;

	public CustomBlock(int id, BlockPrefab prefab, CustomMaterial material) {
		super(id, material);
		this.prefab = prefab;
		setUnlocalizedName(prefab.getIdentifier());
		setCreativeTab(SpoutcraftMod.getCustomTabs());
		setTextureName("spoutcraft:" + prefab.getIdentifier());
		setHardness(prefab.getHardness());
	}

	public BlockPrefab getPrefab() {
		return prefab;
	}
}