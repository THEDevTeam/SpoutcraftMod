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
package org.spoutcraft.api.addon;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.relauncher.Side;
import org.spoutcraft.api.Spoutcraft;
import org.spoutcraft.api.exception.InvalidAddonException;
import org.spoutcraft.api.exception.InvalidPrefabException;

public class AddonLoader {
	private static final String ADDON_GSON = "addon.info";
	private final Side side;
	private final Map<String, AddonClassLoader> loaders = new HashMap<>();

	public AddonLoader(Side side) {
		this.side = side;
	}

	public void enable(Addon addon) {
		if (addon.isEnabled()) {
			throw new IllegalStateException("Cannot enable addon <" + addon.getPrefab().getIdentifier() + ">, it has already been enabled.");
		}

		try {
			final String nameVersion = addon.getPrefab().getName() + " v" + addon.getPrefab().getVersion();
			Spoutcraft.getLogger().info("Enabling <" + nameVersion + ">...");
			addon.onEnable(side);
			addon.enable();
			Spoutcraft.getLogger().info("<" + nameVersion + "> enabled");
		} catch (Throwable t) {
			Spoutcraft.getLogger().log(Level.SEVERE, "Exception caught while enabling addon <" + addon.getPrefab().getIdentifier() + "> -> " + t.getMessage(), t);
		}

		loaders.put(addon.getPrefab().getName(), addon.getClassLoader());
	}

	public void disable(Addon addon) {
		if (!addon.isEnabled()) {
			throw new IllegalStateException("Cannot disable addon <" + addon.getPrefab().getIdentifier() + ">, it has never been enabled.");
		}

		try {
			addon.disable();
			final String nameVersion = addon.getPrefab().getName() + " v" + addon.getPrefab().getVersion();
			Spoutcraft.getLogger().info("Disabling <" + nameVersion + ">...");
			addon.onDisable(side);
			Spoutcraft.getLogger().info("<" + nameVersion + "> disabled");
		} catch (Throwable t) {
			Spoutcraft.getLogger().log(Level.SEVERE, "Exception caught while disabling addon <" + addon.getPrefab().getIdentifier() + "> -> " + t.getMessage(), t);
		}
	}

	public Addon load(Path path) throws InvalidAddonException, InvalidPrefabException {
		final AddonPrefab prefab = create(path);
		Addon addon = null;
		AddonClassLoader loader;

		if (prefab.isValidMode(side)) {
			final Path dataPath = Paths.get(path.getParent().toString(), prefab.getIdentifier()); //TODO breakpoint this
			try {
				loader = new AddonClassLoader(this.getClass().getClassLoader(), this);
				loader.addURL(path.toUri().toURL());
				Class<?> addonMain = Class.forName(prefab.getMain(), true, loader);
				Class<? extends Addon> addonClass = addonMain.asSubclass(Addon.class);
				Constructor<? extends Addon> constructor = addonClass.getConstructor();
				addon = constructor.newInstance();
				addon.initialize(this, prefab, loader, dataPath, path);
			} catch (Exception e) {
				throw new InvalidAddonException(e);
			}
			loader.setAddon(addon);
			loaders.put(prefab.getIdentifier(), loader);
		}
		return addon;
	}

	protected static AddonPrefab create(Path path) throws InvalidAddonException, InvalidPrefabException {
		if (!Files.exists(path)) {
			throw new InvalidAddonException(path.getFileName() + " does not exist!");
		}

		AddonPrefab prefab;
		JarFile jar = null;
		try {
			jar = new JarFile(path.toFile());
			JarEntry entry = jar.getJarEntry(ADDON_GSON);

			if (entry == null) {
				throw new InvalidPrefabException("Attempt to create an addon prefab failed because " + ADDON_GSON + " was not found in " + jar.getName());
			}

			final GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(AddonPrefab.class, new AddonJsonDeserializer());
			final Gson gson = builder.create();
			prefab = gson.fromJson(new InputStreamReader(jar.getInputStream(entry)), AddonPrefab.class);
			if (prefab == null) {
				throw new InvalidPrefabException("Failed to parse " + entry + " as addon prefab");
			}
		} catch (IOException e) {
			throw new InvalidAddonException(e);
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					Spoutcraft.getLogger().log(Level.WARNING, "Problem closing jar input stream", e);
				}
			}
		}
		return prefab;
	}

	protected Class<?> getClassByName(final String name, final AddonClassLoader commonLoader) {
		for (String current : loaders.keySet()) {
			AddonClassLoader loader = loaders.get(current);
			if (loader == commonLoader) {
				continue;
			}
			try {
				Class<?> clazz = loader.findClass(name, false);
				if (clazz != null) {
					return clazz;
				}
			} catch (ClassNotFoundException ignored) {
			}
		}
		return null;
	}
}
