package fr.buffy.aura;

public enum AuraTheme {
	AURORE(0x8B7BFF, 0x6FE3FF),
	OCEAN(0x38BDF8, 0x22D3EE),
	EMERAUDE(0x34D399, 0xC7F9CC);

	private static final AuraTheme[] VALUES = values();

	public final int primary;
	public final int secondary;

	AuraTheme(int primary, int secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	public static int count() {
		return VALUES.length;
	}

	public static AuraTheme byIndex(int index) {
		return VALUES[Math.floorMod(index, VALUES.length)];
	}

	public int accent() {
		return 0xFF000000 | primary;
	}

	public int accentAlt() {
		return 0xFF000000 | secondary;
	}
}
