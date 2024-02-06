package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import lombok.val;

import java.util.List;

import static emu.grasscutter.utils.Language.translate;

@Command(label = "enter_dungeon", aliases = {"enterdungeon", "dungeon"}, usage = {"<dungeonId>"}, permission = "player.enterdungeon", permissionTargeted = "player.enterdungeon.others")
public final class EnterDungeonCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1) {
            sendUsageMessage(sender);
            return;
        }

        try {
            int dungeonId = Integer.parseInt(args.get(0));
            val dungeonData = GameData.getDungeonDataMap().get(dungeonId);
            if(dungeonData == null) {
                CommandHandler.sendMessage(sender, translate(sender, "commands.enter_dungeon.not_found_error"));
                return;
            }
            if (dungeonData.getSceneId() == targetPlayer.getSceneId() && targetPlayer.getScene().getCurDungeonId() == dungeonId) {
                CommandHandler.sendMessage(sender, translate(sender, "commands.enter_dungeon.in_dungeon_error"));
                return;
            }

            boolean result = targetPlayer.getServer().getDungeonSystem().enterDungeon(targetPlayer.getSession().getPlayer(), 0, dungeonId);

            if (!result) {
                CommandHandler.sendMessage(sender, translate(sender, "commands.enter_dungeon.not_found_error"));
            } else {
                CommandHandler.sendMessage(sender, translate(sender, "commands.enter_dungeon.changed", dungeonId));
            }
        } catch (Exception e) {
            sendUsageMessage(sender);
        }
    }
}
