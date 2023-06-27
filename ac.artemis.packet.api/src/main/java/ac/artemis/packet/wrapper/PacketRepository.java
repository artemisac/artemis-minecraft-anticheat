package ac.artemis.packet.wrapper;

import ac.artemis.packet.wrapper.client.*;
import ac.artemis.packet.wrapper.client.v1_8.*;
import ac.artemis.packet.wrapper.client.handshake.PacketHandshakeClientSetProtocol;
import ac.artemis.packet.wrapper.client.login.PacketLoginClientCustomPayload;
import ac.artemis.packet.wrapper.client.login.PacketLoginClientEncryptionBegin;
import ac.artemis.packet.wrapper.client.login.PacketLoginClientStart;
import ac.artemis.packet.wrapper.server.*;

import java.util.Map;

public class PacketRepository {
    private static final PacketCache repository = new PacketCache();

    private static final int HANDSHAKE = 0;
    private static final int CLIENT_LOGIN = 0;
    private static final int CLIENT_PLAY = 1000;
    private static final int CLIENT_STATUS = 2000;
    private static final int SERVER_LOGIN = 3000;
    private static final int SERVER_PLAY = 4000;
    private static final int SERVER_STATUS = 5000;

    public static Class<? extends Packet> getPacket(final int id) {
        return repository.get(id);
    }

    public static int getPacketId(final Class<? extends Packet> clazz) {
        return repository.entrySet().stream().filter(e -> e.getValue().equals(clazz)).map(Map.Entry::getKey).findFirst().orElse(-1);
    }

    static {
        init();
    }

    public static void init() {
        // Base packet
        repository.put(HANDSHAKE, PacketHandshakeClientSetProtocol.class);

        // Client bound login packets
        repository.put(CLIENT_LOGIN + 1, PacketLoginClientEncryptionBegin.class);
        repository.put(CLIENT_LOGIN + 2, PacketLoginClientStart.class);
        repository.put(CLIENT_LOGIN + 3, PacketLoginClientCustomPayload.class);

        // Regular packets (starts at 3 don't ask why)
        repository.put(CLIENT_PLAY + 1, PacketPlayClientAbilities.class);
        repository.put(CLIENT_PLAY + 2, PacketPlayClientArmAnimation.class);
        repository.put(CLIENT_PLAY + 3, PacketPlayClientBlockMetadata.class);
        repository.put(CLIENT_PLAY + 4, PacketPlayClientBlockPlace.class);
        repository.put(CLIENT_PLAY + 5, PacketPlayClientBoatMove.class);
        repository.put(CLIENT_PLAY + 6, PacketPlayClientBookEdit.class);
        repository.put(CLIENT_PLAY + 7, PacketPlayClientChatMessage.class);
        repository.put(CLIENT_PLAY + 8, PacketPlayClientCommand.class);
        repository.put(CLIENT_PLAY + 9, PacketPlayClientConfirmTeleport.class);
        repository.put(CLIENT_PLAY + 10, PacketPlayClientCustomPayload.class);
        repository.put(CLIENT_PLAY + 11, PacketPlayClientDifficultyLock.class);
        repository.put(CLIENT_PLAY + 12, PacketPlayClientDifficultySet.class);
        repository.put(CLIENT_PLAY + 13, PacketPlayClientEffectBeaconSet.class);
        repository.put(CLIENT_PLAY + 14, PacketPlayClientEnchantItem.class);
        repository.put(CLIENT_PLAY + 15, PacketPlayClientEntityAction.class);
        repository.put(CLIENT_PLAY + 16, PacketPlayClientEntityMetadata.class);
        repository.put(CLIENT_PLAY + 17, PacketPlayClientLoc.class);
        repository.put(CLIENT_PLAY + 18, PacketPlayClientGenerateStructure.class);
        repository.put(CLIENT_PLAY + 19, PacketPlayClientItemHeldSlot.class);
        repository.put(CLIENT_PLAY + 20, PacketPlayClientItemName.class);
        repository.put(CLIENT_PLAY + 21, PacketPlayClientItemPick.class);
        repository.put(CLIENT_PLAY + 22, PacketPlayClientKeepAlive.class);
        repository.put(CLIENT_PLAY + 23, PacketPlayClientLocLook.class);
        repository.put(CLIENT_PLAY + 24, PacketPlayClientLocPosition.class);
        repository.put(CLIENT_PLAY + 25, PacketPlayClientLocPositionLook.class);
        repository.put(CLIENT_PLAY + 26, PacketPlayClientRecipeBookData.class);
        repository.put(CLIENT_PLAY + 27, PacketPlayClientRecipeBookState.class);
        repository.put(CLIENT_PLAY + 28, PacketPlayClientRecipeDisplay.class);
        repository.put(CLIENT_PLAY + 29, PacketPlayClientRecipePrepareGrid.class);
        repository.put(CLIENT_PLAY + 30, PacketPlayClientResourcePackStatus.class);
        repository.put(CLIENT_PLAY + 31, PacketPlayClientSetCreativeSlot.class);
        repository.put(CLIENT_PLAY + 32, PacketPlayClientSettings.class);
        repository.put(CLIENT_PLAY + 33, PacketPlayClientSpectate.class);
        repository.put(CLIENT_PLAY + 34, PacketPlayClientSteerVehicle.class);
        repository.put(CLIENT_PLAY + 35, PacketPlayClientTabAdvancement.class);
        repository.put(CLIENT_PLAY + 36, PacketPlayClientTabComplete.class);
        repository.put(CLIENT_PLAY + 37, PacketPlayClientTradeSelect.class);
        repository.put(CLIENT_PLAY + 38, PacketPlayClientTransaction.class);
        repository.put(CLIENT_PLAY + 39, PacketPlayClientUpdateCommandBlock.class);
        repository.put(CLIENT_PLAY + 40, PacketPlayClientUpdateJigsawBlock.class);
        repository.put(CLIENT_PLAY + 41, PacketPlayClientUpdateMinecart.class);
        repository.put(CLIENT_PLAY + 42, PacketPlayClientUpdateSign.class);
        repository.put(CLIENT_PLAY + 43, PacketPlayClientUpdateStructureBlock.class);
        repository.put(CLIENT_PLAY + 44, PacketPlayClientUseEntity.class);
        repository.put(CLIENT_PLAY + 45, PacketPlayClientVehicleMove.class);
        repository.put(CLIENT_PLAY + 46, PacketPlayClientWindowClick.class);
        repository.put(CLIENT_PLAY + 47, PacketPlayClientWindowClose.class);
        repository.put(CLIENT_PLAY + 48, PacketPlayClientWindowHorse.class);
        repository.put(CLIENT_PLAY + 49, PacketPlayClientBlockDig.class);
        repository.put(CLIENT_PLAY + 50, PacketPlayClientItemUse.class);


        // Client bound status
        repository.put(CLIENT_STATUS + 1, PacketStatusClientPing.class);
        repository.put(CLIENT_STATUS + 2, PacketStatusClientStart.class);


        // Server bound login
        repository.put(SERVER_LOGIN + 1, PacketLoginOutDisconnect.class);
        repository.put(SERVER_LOGIN + 2, PacketLoginOutEncryptionBegin.class);
        repository.put(SERVER_LOGIN + 3, PacketLoginOutSetCompression.class);
        repository.put(SERVER_LOGIN + 4, PacketLoginOutSuccess.class);
        repository.put(SERVER_LOGIN + 5, PacketLoginServerCustomPayload.class);

        repository.put(SERVER_PLAY + 1, PacketPlayServerAbilities.class);
        repository.put(SERVER_PLAY + 2, PacketPlayServerAdvancement.class);
        repository.put(SERVER_PLAY + 3, PacketPlayServerAdvancementProgress.class);
        repository.put(SERVER_PLAY + 4, PacketPlayServerAnimation.class);
        repository.put(SERVER_PLAY + 5, PacketPlayServerBed.class);
        repository.put(SERVER_PLAY + 6, PacketPlayServerBlockAction.class);
        repository.put(SERVER_PLAY + 7, PacketPlayServerBlockBreakAnimation.class);
        repository.put(SERVER_PLAY + 8, PacketPlayServerBlockChange.class);
        repository.put(SERVER_PLAY + 9, PacketPlayServerBlockChangeMulti.class);
        repository.put(SERVER_PLAY + 10, PacketPlayServerBookOpen.class);
        repository.put(SERVER_PLAY + 11, PacketPlayServerBossBar.class);
        repository.put(SERVER_PLAY + 12, PacketPlayServerCamera.class);
        repository.put(SERVER_PLAY + 13, PacketPlayServerChat.class);
        repository.put(SERVER_PLAY + 14, PacketPlayServerChunkLoad.class);
        repository.put(SERVER_PLAY + 15, PacketPlayServerChunkLoadBulk.class);
        repository.put(SERVER_PLAY + 16, PacketPlayServerChunkUnload.class);
        repository.put(SERVER_PLAY + 17, PacketPlayServerCollect.class);
        repository.put(SERVER_PLAY + 18, PacketPlayServerCombatEvent.class);
        repository.put(SERVER_PLAY + 19, PacketPlayServerCommandsDeclare.class);
        repository.put(SERVER_PLAY + 20, PacketPlayServerCompression.class);
        repository.put(SERVER_PLAY + 21, PacketPlayServerCustomPayload.class);
        repository.put(SERVER_PLAY + 22, PacketPlayServerDifficulty.class);
        repository.put(SERVER_PLAY + 23, PacketPlayServerEntityEffectRemove.class);
        repository.put(SERVER_PLAY + 24, PacketPlayServerEntityEquipment.class);
        repository.put(SERVER_PLAY + 25, PacketPlayServerEntityHeadRotation.class);
        repository.put(SERVER_PLAY + 26, PacketPlayServerEntityMetadata.class);
        repository.put(SERVER_PLAY + 27, PacketPlayServerEntityMount.class);
        repository.put(SERVER_PLAY + 28, PacketPlayServerEntityRelLook.class);
        repository.put(SERVER_PLAY + 29, PacketPlayServerEntityRelMove.class);
        repository.put(SERVER_PLAY + 30, PacketPlayServerEntityRelMoveLook.class);
        repository.put(SERVER_PLAY + 31, PacketPlayServerEntityStatus.class);
        repository.put(SERVER_PLAY + 32, PacketPlayServerEntityTeleport.class);
        repository.put(SERVER_PLAY + 33, PacketPlayServerEntityVelocity.class);
        repository.put(SERVER_PLAY + 34, PacketPlayServerExperience.class);
        repository.put(SERVER_PLAY + 35, PacketPlayServerExplosion.class);
        repository.put(SERVER_PLAY + 36, PacketPlayServerGameStateChange.class);
        repository.put(SERVER_PLAY + 37, PacketPlayServerItemHeldSlot.class);
        repository.put(SERVER_PLAY + 38, PacketPlayServerKeepAlive.class);
        repository.put(SERVER_PLAY + 39, PacketPlayServerKickDisconnect.class);
        repository.put(SERVER_PLAY + 40, PacketPlayServerLogin.class);
        repository.put(SERVER_PLAY + 41, PacketPlayServerMap.class);
        repository.put(SERVER_PLAY + 42, PacketPlayServerMetadata.class);
        repository.put(SERVER_PLAY + 43, PacketPlayServerMetadataResponse.class);
        repository.put(SERVER_PLAY + 44, PacketPlayServerOpenSignEditor.class);
        repository.put(SERVER_PLAY + 45, PacketPlayServerPlayerDig.class);
        repository.put(SERVER_PLAY + 46, PacketPlayServerPlayerFace.class);
        repository.put(SERVER_PLAY + 47, PacketPlayServerPlayerInfo.class);
        repository.put(SERVER_PLAY + 48, PacketPlayServerPlayerListHeaderFooter.class);
        repository.put(SERVER_PLAY + 49, PacketPlayServerPosition.class);
        repository.put(SERVER_PLAY + 50, PacketPlayServerRecipeDeclare.class);
        repository.put(SERVER_PLAY + 51, PacketPlayServerRecipeResponse.class);
        repository.put(SERVER_PLAY + 52, PacketPlayServerRecipeUnlock.class);
        repository.put(SERVER_PLAY + 53, PacketPlayServerResourcePackSend.class);
        repository.put(SERVER_PLAY + 54, PacketPlayServerRespawn.class);
        repository.put(SERVER_PLAY + 55, PacketPlayServerScoreboardObjective.class);
        repository.put(SERVER_PLAY + 56, PacketPlayServerScoreboardObjectiveDisplay.class);
        repository.put(SERVER_PLAY + 57, PacketPlayServerScoreboardScore.class);
        repository.put(SERVER_PLAY + 58, PacketPlayServerScoreboardTeam.class);
        repository.put(SERVER_PLAY + 59, PacketPlayServerSetCooldown.class);
        repository.put(SERVER_PLAY + 60, PacketPlayServerSetPassengers.class);
        repository.put(SERVER_PLAY + 61, PacketPlayServerSetSlot.class);
        repository.put(SERVER_PLAY + 62, PacketPlayServerSoundEffectCustom.class);
        repository.put(SERVER_PLAY + 63, PacketPlayServerSoundEffectEntity.class);
        repository.put(SERVER_PLAY + 64, PacketPlayServerSoundEffectNamed.class);
        repository.put(SERVER_PLAY + 65, PacketPlayServerSoundEffectStop.class);
        repository.put(SERVER_PLAY + 66, PacketPlayServerSpawnEntityExperienceOrb.class);
        repository.put(SERVER_PLAY + 67, PacketPlayServerSpawnEntityLiving.class);
        repository.put(SERVER_PLAY + 68, PacketPlayServerSpawnEntityPainting.class);
        repository.put(SERVER_PLAY + 69, PacketPlayServerSpawnEntityWeather.class);
        repository.put(SERVER_PLAY + 70, PacketPlayServerSpawnObject.class);
        repository.put(SERVER_PLAY + 71, PacketPlayServerSpawnPosition.class);
        repository.put(SERVER_PLAY + 72, PacketPlayServerStatistic.class);
        repository.put(SERVER_PLAY + 73, PacketPlayServerTabComplete.class);
        repository.put(SERVER_PLAY + 74, PacketPlayServerTileEntityData.class);
        repository.put(SERVER_PLAY + 75, PacketPlayServerTitle.class);
        repository.put(SERVER_PLAY + 76, PacketPlayServerTradeList.class);
        repository.put(SERVER_PLAY + 77, PacketPlayServerTransaction.class);
        repository.put(SERVER_PLAY + 78, PacketPlayServerUpdateAttributes.class);
        repository.put(SERVER_PLAY + 79, PacketPlayServerUpdateHealth.class);
        repository.put(SERVER_PLAY + 80, PacketPlayServerUpdateLight.class);
        repository.put(SERVER_PLAY + 81, PacketPlayServerUpdateMetadata.class);
        repository.put(SERVER_PLAY + 82, PacketPlayServerUpdateSign.class);
        repository.put(SERVER_PLAY + 83, PacketPlayServerUpdateViewDistance.class);
        repository.put(SERVER_PLAY + 84, PacketPlayServerUpdateViewPosition.class);
        repository.put(SERVER_PLAY + 85, PacketPlayServerUpdateTime.class);
        repository.put(SERVER_PLAY + 86, PacketPlayServerVehicleMove.class);
        repository.put(SERVER_PLAY + 87, PacketPlayServerWindowClose.class);
        repository.put(SERVER_PLAY + 88, PacketPlayServerWindowData.class);
        repository.put(SERVER_PLAY + 89, PacketPlayServerWindowItems.class);
        repository.put(SERVER_PLAY + 90, PacketPlayServerWindowOpen.class);
        repository.put(SERVER_PLAY + 91, PacketPlayServerWorldBorder.class);
        repository.put(SERVER_PLAY + 92, PacketPlayServerWorldEvent.class);
        repository.put(SERVER_PLAY + 93, PacketPlayServerWorldParticles.class);
        repository.put(SERVER_PLAY + 94, PacketPlayServerEntity.class);
        repository.put(SERVER_PLAY + 95, PacketPlayServerEntityDestroy.class);
        repository.put(SERVER_PLAY + 96, PacketPlayServerSpawnEntityNamed.class);
        repository.put(SERVER_PLAY + 97, PacketPlayServerEntityAttach.class);
        repository.put(SERVER_PLAY + 98, PacketPlayServerEntityEffect.class);
        repository.put(SERVER_PLAY + 99, PacketPlayServerWindowHorse.class);

        repository.put(SERVER_STATUS + 1, PacketStatusServerInfo.class);
        repository.put(SERVER_STATUS + 2, PacketStatusServerPong.class);
    }
}
