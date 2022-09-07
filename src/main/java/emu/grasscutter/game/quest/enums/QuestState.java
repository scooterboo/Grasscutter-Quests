package emu.grasscutter.game.quest.enums;

public enum QuestState {
    QUEST_STATE_NONE (0),
    QUEST_STATE_UNSTARTED (1),
    QUEST_STATE_UNFINISHED (2),
    QUEST_STATE_FINISHED (3),
    QUEST_STATE_FAILED (4),

    // Used by lua
    None (0),
    UnStarted(1),
    UnFinished(2),
    Finished(3),
    FAILED(4);

    private final int value;

    QuestState(int id) {
        this.value = id;
    }

    public int getValue() {
        return value;
    }
}
