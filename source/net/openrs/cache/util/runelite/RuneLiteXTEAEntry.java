package net.openrs.cache.util.runelite;

/** @author Sino */
public final class RuneLiteXTEAEntry {
	private final int region;
	private final int[] keys;

	public RuneLiteXTEAEntry(int region, int[] keys) {
		this.region = region;
		this.keys = keys;
	}

	public int getRegion() {
		return region;
	}

	public int[] getKeys() {
		return keys;
	}
}
