package ac.artemis.packet.generator.comparison;

import ac.artemis.packet.generator.util.Pair;
import ac.artemis.packet.wrapper.Packet;
import ac.artemis.packet.wrapper.client.*;
import ac.artemis.packet.wrapper.client.PacketPlayClientChatAck;
import ac.artemis.packet.wrapper.client.handshake.PacketHandshakeClientSetProtocol;
import ac.artemis.packet.wrapper.client.login.PacketLoginClientCustomPayload;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientCommand;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientItemHeldSlot;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientWindowClose;
import ac.artemis.packet.wrapper.server.*;

import java.util.Arrays;
import java.util.List;

public class HardcodeMap {
    public static List<Pair<String, Class<? extends Packet>>> knownMaps = Arrays.asList(
            // Client
            wrap("PacketPlayInHeldItemSlot", PacketPlayClientItemHeldSlot.class),
            wrap("PacketPlayInCloseWindow", PacketPlayClientWindowClose.class),
            wrap("PacketPlayInClientCommand", PacketPlayClientCommand.class),
            wrap("PacketPlayInTeleportAccept", PacketPlayClientConfirmTeleport.class),
            wrap("PacketPlayInTileNBTQuery", PacketPlayClientBlockMetadata.class),
            wrap("PacketPlayInDifficultyChange", PacketPlayClientDifficultySet.class),
            wrap("PacketPlayInBEdit", PacketPlayClientBookEdit.class),
            wrap("PacketPlayInEntityNBTQuery", PacketPlayClientEntityMetadata.class),
            wrap("PacketPlayInJigsawGenerate", PacketPlayClientGenerateStructure.class),
            wrap("PacketPlayInPickItem", PacketPlayClientItemPick.class),
            wrap("PacketPlayInAutoRecipe", PacketPlayClientRecipePrepareGrid.class), // TODO CHECK IF THIS IS VALID
            wrap("PacketPlayInRecipeSettings", PacketPlayClientRecipeBookState.class),
            wrap("PacketPlayInAdvancements", PacketPlayClientTabAdvancement.class),
            wrap("PacketPlayInTrSel", PacketPlayClientTradeSelect.class),
            wrap("PacketPlayInBeacon", PacketPlayClientEffectBeaconSet.class),
            wrap("PacketPlayInSetCommandBlock", PacketPlayClientUpdateCommandBlock.class),
            wrap("PacketPlayInSetCommandMinecart", PacketPlayClientUpdateMinecart.class),
            wrap("PacketPlayInSetJigsaw", PacketPlayClientUpdateJigsawBlock.class),
            wrap("PacketPlayInStruct", PacketPlayClientUpdateStructureBlock.class),
            wrap("PacketPlayInUseItem", PacketPlayClientItemUse.class),
            wrap("PacketHandshakingInSetProtocol", PacketHandshakeClientSetProtocol.class),

            // Server
            wrap("PacketPlayOutHeldItemSlot", PacketPlayServerItemHeldSlot.class),
            wrap("PacketPlayOutNamedEntitySpawn", PacketPlayServerSpawnEntityNamed.class),
            wrap("PacketPlayOutEntity", PacketPlayServerEntity.class),
            wrap("PacketPlayOutRelEntityMove", PacketPlayServerEntityRelMove.class),
            wrap("PacketPlayOutEntityLook", PacketPlayServerEntityRelLook.class),
            wrap("PacketPlayOutRelEntityMoveLook", PacketPlayServerEntityRelMoveLook.class),
            wrap("PacketPlayOutAttachEntity", PacketPlayServerEntityAttach.class),
            wrap("PacketPlayOutRemoveEntityEffect", PacketPlayServerEntityEffectRemove.class),
            wrap("PacketPlayOutMapChunk", PacketPlayServerChunkLoad.class),
            wrap("PacketPlayOutUnloadChunk", PacketPlayServerChunkUnload.class),
            wrap("PacketPlayOutMultiBlockChange", PacketPlayServerBlockChangeMulti.class),
            wrap("PacketPlayOutBlockChange", PacketPlayServerBlockChange.class),
            wrap("PacketPlayOutMapChunkBulk", PacketPlayServerChunkLoadBulk.class),
            wrap("PacketPlayOutNamedSoundEffect", PacketPlayServerSoundEffectNamed.class),
            wrap("PacketPlayOutOpenWindow", PacketPlayServerWindowOpen.class),
            wrap("PacketPlayOutCloseWindow", PacketPlayServerWindowClose.class),
            wrap("PacketPlayOutScoreboardDisplayObjective", PacketPlayServerScoreboardObjectiveDisplay.class),
            wrap("PacketPlayOutServerDifficulty", PacketPlayServerDifficulty.class),
            wrap("PacketPlayOutSetCompression", PacketPlayServerCompression.class),
            wrap("PacketPlayOutUpdateEntityNBT", PacketPlayServerUpdateMetadata.class),
            wrap("PacketPlayOutLightUpdate", PacketPlayServerUpdateLight.class),
            wrap("PacketPlayOutOpenWindowMerchant", PacketPlayServerTradeList.class),
            wrap("PacketPlayOutOpenBook", PacketPlayServerBookOpen.class),
            wrap("PacketPlayOutAutoRecipe", PacketPlayServerRecipeResponse.class),
            wrap("PacketPlayOutLookAt", PacketPlayServerEntityHeadRotation.class),
            wrap("PacketPlayOutRecipes", PacketPlayServerRecipeDeclare.class),
            wrap("PacketPlayOutSelectAdvancementTab", PacketPlayServerAdvancement.class),
            wrap("PacketPlayOutViewCentre", PacketPlayServerUpdateViewPosition.class),
            wrap("PacketPlayOutViewDistance", PacketPlayServerUpdateViewDistance.class),
            wrap("PacketPlayOutMount", PacketPlayServerEntityMount.class),
            wrap("PacketPlayOutEntitySound", PacketPlayServerSoundEffectEntity.class),
            wrap("PacketPlayOutStopSound", PacketPlayServerSoundEffectStop.class),
            wrap("PacketPlayOutNBTQuery", PacketPlayServerMetadataResponse.class),
            wrap("PacketPlayOutRecipeUpdate", PacketPlayServerRecipeUnlock.class),
            wrap("PacketPlayOutTags", PacketPlayServerMetadata.class),
            wrap("PacketPlayOutCustomSoundEffect", PacketPlayServerSoundEffectCustom.class),
            wrap("PacketStatusOutServerInfo", PacketStatusServerInfo.class),
            wrap("PacketPlayOutUpdateTime", PacketPlayServerUpdateTime.class),
            wrap("PacketPlayOutSpawnEntity", PacketPlayServerSpawnObject.class),
            wrap("PacketPlayOutEntityEffect", PacketPlayServerEntityEffect.class),
            wrap("PacketPlayOutOpenWindowHorse", PacketPlayServerWindowHorse.class),

            wrap("PacketLoginInCustomPayload", PacketLoginClientCustomPayload.class),
            wrap("PacketLoginOutCustomPayload", PacketLoginServerCustomPayload.class),

            wrap("ServerboundPongPacket", PacketPlayClientPing.class),
            wrap("ServerboundChatAckPacket", PacketPlayClientChatAck.class)


    );

    public static Pair<String, Class<? extends Packet>> wrap(String string, Class<? extends Packet> packet) {
        return new Pair<>(string, packet);
    }
}
