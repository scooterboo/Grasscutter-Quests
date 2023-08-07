package emu.grasscutter.game.props;

/**
 * TODO for now keep it in sync with EntityType, maybe replace this by EntityType
 */
public enum EntityIdType {
	NONE	(0x00),
	AVATAR	(0x01),
	MONSTER	(0x02),
	NPC		(0x0C),
	GADGET	(0x13),
    REGION	(0x05),
    WEAPON	(0x08),
	TEAM 	(0x1F),
	MPLEVEL	(0x2A);

	private final int id;

	private EntityIdType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
