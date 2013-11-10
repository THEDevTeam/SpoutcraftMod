package org.spoutcraft.test.block;

import org.spoutcraft.api.block.BlockPrefab;
import org.spoutcraft.api.material.MapIndex;
import org.spoutcraft.api.material.MaterialPrefab;

public class TestSand extends BlockPrefab {
	public TestSand() {
		super("testsand", "Test Sand", new MaterialPrefab("testmaterial", MapIndex.DIRT), 0.5f);
	}
}