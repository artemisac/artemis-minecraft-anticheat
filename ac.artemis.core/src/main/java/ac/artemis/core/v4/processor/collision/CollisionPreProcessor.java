package ac.artemis.core.v4.processor.collision;

import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.Vehicle;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.utils.PlayerEnums;
import ac.artemis.core.v4.emulator.entity.utils.EntityUtil;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.core.v5.collision.BlockCollisionProvider;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ghast
 * @since 09-May-20
 */
public class CollisionPreProcessor extends AbstractHandler {

    public CollisionPreProcessor(PlayerData data) {
        super("Collision [0x01]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            this.checkInvalidMotion();
            this.checkBlockCollision((PacketPlayClientFlying) packet);
            this.checkEntityCollision();
        }

    }

    private void checkInvalidMotion() {
        if (!TimeUtil.elapsed(data.combat.getLastAttack(), 250L)) {
            data.movement.setInvalidNMSMotion(true);
        } else if (!data.user.isSprinting()) {
            data.movement.setInvalidNMSMotion(false);
        }
    }

    private void checkEntityCollision() {
        if (data.movement.getLocation() == null) return;
        BoundingBox boundingBox = data.movement.getLocation().getBox();
        List<Entity> list = NMSManager.getInms()
                .getEntitiesInAABBexcluding(data.getPlayer(),
                boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D),
                EntityUtil::canBePushed);
        data.collision.setCollidesCollideable(!list.isEmpty());
    }

    private void checkBlockCollision(PacketPlayClientFlying packet) {

        if (!packet.isPos()) return;

        PlayerPosition pos = data.movement.location;

        World world = data.getPlayer().getWorld();

        if (world.isChunkLoaded((int) Math.floor(pos.getX()) >> 4, (int) Math.floor(pos.getZ()) >> 4)
                && data.movement.lastLocation != null && data.movement.getMovement() != null) {

            BoundingBox interactBig = pos.getBox().cloneBB().expand(1.5, 1.5, 1.5);
            Set<Material> mats = NMSManager.getInms().getCollidingBlocks(interactBig, world);
            data.collision.setCollidingBlocks1(mats);

            BoundingBox interactBelow = pos.getBox().cloneBB().subtract(0, 1, 0);
            Set<Material> matsBelow = NMSManager.getInms().getCollidingBlocks(interactBelow, world);

            BoundingBox interactTop = pos.getBox().cloneBB().add(0, 2, 0).expand(0.1, 0, 0.1);
            Set<Material> matsTop = NMSManager.getInms().getCollidingBlocks(interactTop, world);
            matsTop.remove(NMSMaterial.AIR.getMaterial());

            BoundingBox interact = pos.getBox().cloneBB();
            Set<Material> selfMats = NMSManager.getInms().getCollidingBlocks(interact, world);

            data.collision.setCollidingBlocks0(selfMats.stream().map(NMSMaterial::matchXMaterial).collect(Collectors.toSet()));

            data.collision.setCollidingBlocksY1(matsBelow);

            data.collision.setCollidingBlocksY1NMS(matsBelow.stream().map(NMSMaterial::matchXMaterial).collect(Collectors.toSet()));

            data.collision.setCollidingMaterials(NMSManager.getInms().getCollidingBlocks(
                    data.movement.getLocation().getBox(), data.getPlayer().getWorld())
                    .stream()
                    .map(NMSMaterial::matchXMaterial)
                    .collect(Collectors.toSet()));

            data.collision.setCollidingMaterials1(NMSManager.getInms().getCollidingBlocks(
                    data.movement.getLocation().getBox().cloneBB()
                            .expand(0.5, 0.5, 0.5), data.getPlayer().getWorld())
                    .stream()
                    .map(NMSMaterial::matchXMaterial)
                    .collect(Collectors.toSet()));

            BoundingBox interactGround = pos.getBox().cloneBB().expand(0.2, 1, 0.2).add(0,-1,0);

            boolean ground = BlockCollisionProvider.PROVIDER.getCollidingBlocks(interactGround.cloneBB(), data.entity)
                    .stream()
                    .anyMatch(e -> !NMSMaterial.AIR.equals(e.getMaterial()));

            boolean ground2 = data.user.isOnFakeGround(); //BlockUtil.isOnGround(pos.toBukkitLocation(), 1);


            Set<Material> finalMats = NMSManager.getInms().getCollidingBlocks(interact.cloneBB()
                    .subtractMax(0,1.8,0), world);

            boolean ground4 = finalMats.contains(NMSMaterial.AIR.getMaterial()) && finalMats.size() > 1;

            boolean inVClip = interact.shrink(0.2, 0.2, 0.2).checkCollision(data.getPlayer(), Material::isBlock);

            data.movement.setStuck(inVClip);

            boolean boatFlag = interactBig.checkCollision(data.getPlayer(), e -> e.toString().contains("BOAT"));
            boolean boatEntityFlag = NMSManager.getInms()
                    .getEntitiesInAABBexcluding(data.getPlayer(), interactBig, entity -> entity instanceof Vehicle).size() > 0;

            data.collision.setCollidesBoat(boatFlag || boatEntityFlag);
            data.collision.setWasGroundCollide(data.collision.isGroundCollide());
            data.collision.setGroundCollide(ground);

            data.user.setOnGround(ground2);
            data.movement.setOnGroundX(ground);


            if (ground2) data.user.setLastGround(System.currentTimeMillis());


            Block air = pos.toBukkitLocation().getBlock();
            Block below = pos.toBukkitLocation().subtract(0, 1, 0).getBlock();
            Block bottom = pos.toBukkitLocation().subtract(0, 2, 0).getBlock();
            Block between = pos.toBukkitLocation().add(0, 1, 0).getBlock();
            Block above = pos.toBukkitLocation().add(0, 2, 0).getBlock();

            data.movement.setOnSlime(below.getType().toString().contains("SLIME"),
                    System.currentTimeMillis());

            if (between != null && air != null) {
                // -- TRAPDOOR --
                data.movement.setInTrapdoor((between.getType().equals(NMSMaterial.OAK_TRAPDOOR.getMaterial())
                                || between.getType().name().equalsIgnoreCase("IRON_TRAPDOOR")),
                        //
                        System.currentTimeMillis());

                data.movement.setOnTrapdoor((air.getType().equals(NMSMaterial.IRON_TRAPDOOR.getMaterial())
                        || air.getType().name().equalsIgnoreCase("IRON_TRAPDOOR")),
                        //
                        System.currentTimeMillis());
            }






            // -- STAIRS --
            data.movement.setOnStair((air.getType().toString().toUpperCase().contains("STAIRS") ||
                            below.getType().toString().toUpperCase().contains("STAIRS")
                    ),
                    //
                    System.currentTimeMillis());

            // -- SLAB --
            data.movement.setOnSlab(air.getType().toString().contains("STEP"),
                    //
                    System.currentTimeMillis());

            // -- LIQUID --
            data.movement.setInLiquid(air.getType().toString().contains("WATER")
                            || air.getType().toString().contains("LAVA")
                            || between.getType().toString().contains("WATER")
                            || between.getType().toString().contains("LAVA")
                            || interactBig.cloneBB().shrink(0.95, 0.95, 0.95)
                            .checkCollision(data.getPlayer(), e -> e.toString().contains("WATER")
                                    || e.toString().contains("LAVA")),
                    //
                    System.currentTimeMillis());

            // -- LADDER
            data.movement.setOnLadder(mats.contains(NMSMaterial.LADDER.getMaterial())
                            || mats.contains(NMSMaterial.VINE.getMaterial()),
                    //
                    System.currentTimeMillis());

            // -- WEB --
            data.movement.setInWeb((air.getType().equals(NMSMaterial.COBWEB.getMaterial())
                            || above.getType().equals(NMSMaterial.COBWEB.getMaterial())
                            || mats.contains(NMSMaterial.COBWEB.getMaterial())
                    ),
                    //
                    System.currentTimeMillis());

            // -- UNDER BLOCK
            data.movement.setUnderBlock(matsTop.size() > 0,
                    //
                    System.currentTimeMillis());

            // -- ICE --
            data.movement.setOnIce((below.getType().equals(NMSMaterial.ICE.getMaterial())
                            || below.getType().equals(NMSMaterial.PACKED_ICE.getMaterial())
                            || air.getType().equals(NMSMaterial.ICE.getMaterial())
                            || air.getType().equals(NMSMaterial.PACKED_ICE.getMaterial())
                            || bottom.getType().equals(NMSMaterial.ICE.getMaterial())
                            || bottom.getType().equals(NMSMaterial.PACKED_ICE.getMaterial())),
                    //
                    System.currentTimeMillis());


            data.movement.setOnBed(below.getType().toString().contains("BED"));

            // -- SOUL SAND --
            data.movement.setOnSoulSand(below.getType().equals(NMSMaterial.SOUL_SAND.getMaterial()),
                    //
                    System.currentTimeMillis());

            // -- LILY PAD --
            data.user.setOnLilyPad(mats.contains(NMSMaterial.LILY_PAD.getMaterial()));

            final double deltaV = pos.getY() - data.movement.lastLocation.getY();
            final double deltaY = Math.abs(data.movement.lastLocation.getY() - pos.getY());

            if (!data.movement.isOnSlab() && !data.movement.isOnStair() && !data.user.isOnFakeGround()) {
                data.user.setOnGroundAir(Math.abs(deltaY) > 0);
            }


            PlayerEnums.AirType airType = null;

            if (Math.abs(deltaV) > 0) {

                // If is on stairs and matches the stairs velocity, set it on ground since it's a ground movement
                if ((data.movement.isOnStair() || data.movement.isOnSlab())) {
                    airType = PlayerEnums.AirType.GROUND;
                }

                // If user has recent velocity equal or superior to the vertical, set air type to velocity
                else if ((data.movement.getVelocity() != null
                        && data.movement.isHasVelocity())
                        && data.movement.getVelocity().getVertical() > 0
                ) {
                    airType = PlayerEnums.AirType.VELOCITY;
                }

                // If user is in slime velocity, self explanatory
                else if (data.movement.isSlimeVelocity()) {
                    airType = PlayerEnums.AirType.SLIME;
                }
                // Liquid interaction! My favourite (not)
                else if (data.movement.isInLiquid()) {

                    // Check if the user is fully submerged or on high levels. Important!
                    if (interact.checkCollision(data.getPlayer(), e -> e.equals(NMSMaterial.AIR.getMaterial()))) {
                        airType = PlayerEnums.AirType.LIQUID_JUMP;
                    }
                    // If not submerged, it can be quite regular
                    else {
                        airType = PlayerEnums.AirType.LIQUID;
                    }
                }

                // Finally, if a user is in a web, welp
                else if (data.movement.isInWeb()) {
                    airType = PlayerEnums.AirType.COBWEB;
                }

                // Finally, if it's below 0, user is obviously falling
                else if (deltaV < -0.1
                        && !data.user.isOnFakeGround()
                        && data.movement.getAirType() != null
                        && !data.movement.getAirType().equals(PlayerEnums.AirType.JUMP)) {
                    airType = PlayerEnums.AirType.FALLING;
                }

                // If user wasn't jumping or whatnot, he's obviously flying. Cheeky bastard
                else if (data.movement.getAirType() != null
                        && !data.movement.getAirType().equals(PlayerEnums.AirType.JUMP)
                        && !data.movement.getAirType().equals(PlayerEnums.AirType.GROUND)

                ) {
                    airType = PlayerEnums.AirType.FLY;
                }

                // Most likely one out of the lot.
                else {
                    airType = PlayerEnums.AirType.JUMP;
                }
            } else {
                airType = PlayerEnums.AirType.GROUND;
            }

            data.movement.setOnGround(ground2, airType, System.currentTimeMillis());
            data.movement.setOnGroundCollision(ground4);
        }
    }

    private int dcrBy1(int i) {
        return Math.max(0, i - 1);
    }
}
