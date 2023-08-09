package emu.grasscutter.game.dungeons.challenge;

import lombok.Getter;

public record ChallengeInfo(@Getter int challengeIndex, @Getter int challengeId, @Getter int fatherChallengeIndex) {
}
