package emu.grasscutter.data.excels;

import java.util.List;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.data.common.BaseBlossomROSData;
import emu.grasscutter.game.managers.blossom.enums.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@ResourceType(name = "BlossomRefreshExcelConfigData.json")
@EqualsAndHashCode(callSuper = false)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@SuppressWarnings("SpellCheckingInspection")
public class BlossomRefreshData extends GameResource implements BaseBlossomROSData {
    @Getter(onMethod = @__(@Override))
    int id;
    BlossomClientShowType clientShowType;  // BLOSSOM_SHOWTYPE_CHALLENGE, BLOSSOM_SHOWTYPE_NPCTALK

    // Refresh details
    BlossomRefreshType refreshType;  // Leyline blossoms, magical ore outcrops
    int refreshCount;  // Number of entries to spawn at refresh (1 for each leyline type for each city, 4 for magical ore for each city)
    String refreshTime;  // Server time-of-day to refresh at
    List<RefreshCond> refreshCondVec;  // AR requirements etc.
    BlossomItemLimitType itemLimitType;
    BlossomRewardType rewardType;

    int cityId;
    int blossomChestId;  // 1 for mora, 2 for exp
    List<Drop> dropVec;

    int reviseLevel;
    int campUpdateNeedCount;  // Always 1 if specified

    @Override
    public void onLoad() {
        if (this.refreshType == null) {
            this.refreshType = BlossomRefreshType.BLOSSOM_REFRESH_NONE;
        }
        if (this.itemLimitType == null) {
            this.itemLimitType = BlossomItemLimitType.ITEM_LIMIT_NONE;
        }
        if (this.rewardType == null) {
            this.rewardType = BlossomRewardType.BLOSSOM_REWARD_TYPE_NONE;
        }
        this.refreshCondVec = this.refreshCondVec.stream().filter(cond -> !cond.getParam().isEmpty()).toList();
        this.refreshCondVec.forEach(RefreshCond::onLoad);
    }

    @Override
    public int getRefreshId() {
        return getId();
    }

    @Override
    public int getRewardId(int worldLevel) {
        return getDropVec().get(worldLevel).getPreviewReward();
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class Drop {
        int dropId;
        int previewReward;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class RefreshCond {
        BlossomRefreshCondType type; // null oe none type will be filtered out, so don't have to reassign
        List<Integer> param;

        public void onLoad(){
            this.param = this.param.stream().filter(x -> (x != null) && (x != 0)).toList();
        }
    }
}
