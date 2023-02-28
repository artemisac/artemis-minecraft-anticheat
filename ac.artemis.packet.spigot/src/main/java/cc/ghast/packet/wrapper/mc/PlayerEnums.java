package cc.ghast.packet.wrapper.mc;

import lombok.Getter;

/**
 * @author Ghast
 * @since 31/10/2020
 * ArtemisPacket © 2020
 */
public class PlayerEnums {
    public enum DigType {
        // Start digging getX block
        START_DESTROY_BLOCK,
        // Cancel the process of digging getX block
        ABORT_DESTROY_BLOCK,
        // Finish digging the block
        STOP_DESTROY_BLOCK,
        // Drop item as getX stack
        DROP_ALL_ITEMS,
        // Drop item as getX singular
        DROP_ITEM,
        // Shoot arrow / finish eating
        RELEASE_USE_ITEM,
        // Swap from main-hand to off-hand
        SWAP_HELD_ITEMS;
    }

    public enum UseType {
        INTERACT,
        ATTACK,
        INTERACT_AT;
    }

    public enum Hand {
        MAIN_HAND,
        OFF_HAND;
    }

    public enum ClientCommand {
        PERFORM_RESPAWN, REQUEST_STATS, OPEN_INVENTORY_ACHIEVEMENT;
    }

    public enum PlayerAction {
        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        START_RIDING_JUMP,
        STOP_RIDING_JUMP,
        OPEN_INVENTORY,
        START_FALL_FLYING;
    }

    public enum ResourcePackStatus {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED;
    }

    @Getter
    public enum ChatVisibility {

        FULL(0, "options.chat.visibility.full"),
        SYSTEM(1, "options.chat.visibility.system"),
        HIDDEN(2, "options.chat.visibility.hidden");

        private final int settingId;
        private final String settingPath;

        ChatVisibility(int i, String s) {
            this.settingId = i;
            this.settingPath = s;
        }
    }
}
