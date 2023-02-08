package ac.artemis.checks.regular.v2;

import ac.artemis.checks.regular.v2.checks.impl.aim.*;
import ac.artemis.checks.regular.v2.checks.impl.aim.cinematic.Cinematic;
import ac.artemis.checks.regular.v2.checks.impl.aim.cinematic.Cinematic2;
import ac.artemis.checks.regular.v2.checks.impl.aura.AuraAcceleration;
import ac.artemis.checks.regular.v2.checks.impl.aura.AuraLock;
import ac.artemis.checks.regular.v2.checks.impl.aura.AuraMovement;
import ac.artemis.checks.regular.v2.checks.impl.aura.AuraMulti;
import ac.artemis.checks.regular.v2.checks.impl.aura.badpacket.*;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.*;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket.AutoClicker1;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket.AutoClicker2;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket.AutoClicker3;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket.AutoClicker4;
import ac.artemis.checks.regular.v2.checks.impl.badpackets.*;
import ac.artemis.checks.regular.v2.checks.impl.blink.BlinkA;
import ac.artemis.checks.regular.v2.checks.impl.emulator.Physics;
import ac.artemis.checks.regular.v2.checks.impl.emulator.PredictionSpeed;
import ac.artemis.checks.regular.v2.checks.impl.fastladder.FastLadderSimple;
import ac.artemis.checks.regular.v2.checks.impl.fly.*;
import ac.artemis.checks.regular.v2.checks.impl.hitbox.HitboxA;
import ac.artemis.checks.regular.v2.checks.impl.inventory.InventoryMoving;
import ac.artemis.checks.regular.v2.checks.impl.inventory.InventorySlotComplex;
import ac.artemis.checks.regular.v2.checks.impl.inventory.InventorySlotSimple;
import ac.artemis.checks.regular.v2.checks.impl.jesus.JesusSimple;
import ac.artemis.checks.regular.v2.checks.impl.omnisprint.OmniSprintComplex;
import ac.artemis.checks.regular.v2.checks.impl.omnisprint.OmniSprintSimple;
import ac.artemis.checks.regular.v2.checks.impl.phase.PhaseSimple;
import ac.artemis.checks.regular.v2.checks.impl.pingspoof.PingSpoofDuplicate;
import ac.artemis.checks.regular.v2.checks.impl.pingspoof.PingSpoofIdDupe;
import ac.artemis.checks.regular.v2.checks.impl.reach.Reach2;
import ac.artemis.checks.regular.v2.checks.impl.scaffold.ScaffoldA;
import ac.artemis.checks.regular.v2.checks.impl.speed.SpeedHorizontalSimple;
import ac.artemis.checks.regular.v2.checks.impl.speed.SpeedV;
import ac.artemis.checks.regular.v2.checks.impl.timer.TimerAverage;
import ac.artemis.checks.regular.v2.checks.impl.timer.TimerInflux;
import ac.artemis.checks.regular.v2.checks.impl.velocity.VelocityXZ;
import ac.artemis.checks.regular.v2.checks.impl.velocity.VelocityY;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.manager.AbstractCheckManager;
import ac.artemis.core.v4.data.PlayerData;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
public class ArtemisCheckLoader extends AbstractCheckManager {

    public ImmutableClassToInstanceMap<ArtemisCheck> newInstance(PlayerData data) {
        final ImmutableClassToInstanceMap<ArtemisCheck> build = ImmutableClassToInstanceMap.<ArtemisCheck>builder()


                /*/
                    COMBAT
                /*/

                // AIM
                .put(Cinematic.class, new Cinematic(data, Checks.AIM_CINEMATIC))
                .put(Cinematic2.class, new Cinematic2(data, Checks.AIM_CINEMATIC2))
                //.put(Aim0x1337.class, new Aim0x1337(data, Checks.AIM_1337))
                .put(AimRatio.class, new AimRatio(data, Checks.AIM_RATIO))
                //.put(AimSmoothness.class, new AimSmoothness(data, Checks.AIM_SMOOTHNESS))
                .put(AimDrip.class, new AimDrip(data, Checks.AIM_DRIP))
                //.put(AimSmoothnessYaw.class, new AimSmoothnessYaw(data, Checks.AIM_SMOOTHNESS_YAW))
                .put(AimIdentical.class, new AimIdentical(data, Checks.AIM_IDENTICAL))
                .put(AimLiquid.class, new AimLiquid(data, Checks.AIM_LIQUID))
                //.put(AimSensitivity.class, new AimSensitivity(data, Checks.AIM_SENSITIVITY))
                .put(AimRandomized.class, new AimRandomized(data, Checks.AIM_RANDOMIZED))
                .put(AimInvalidSmooth.class, new AimInvalidSmooth(data, Checks.AIM_INVALID_SMOOTH))
                .put(AimGeneric.class, new AimGeneric(data, Checks.AIM_GENERIC))
                //.put(AimFuckHecate.class, new AimFuckHecate(data, Checks.AIM_FUCKHECATE))

                // Frap's checks
                .put(AimDivisor.class, new AimDivisor(data, Checks.AIM_DIVISOR))
                .put(AimKitten.class, new AimKitten(data, Checks.AIM_MOON))
                .put(AimWater.class, new AimWater(data, Checks.AIM_WATER))

                // Tecnio's checks
                .put(AimCope.class, new AimCope(data, Checks.AIM_COPE))

                //.put()

                // AURA
                .put(AuraMovement.class, new AuraMovement(data, Checks.AURA_MOVEMENT))
                .put(AuraMulti.class, new AuraMulti(data, Checks.AURA_MULTI))
                .put(AuraAcceleration.class, new AuraAcceleration(data, Checks.AURA_ACCELERATION))
                //.put(AuraNMS.class, new AuraNMS(data, Checks.AURA_NMS))
                .put(AuraLock.class, new AuraLock(data, Checks.AURA_LOCK))
                //.put(AuraInconsistent.class, new AuraInconsistent(data, Checks.AURA_INCONSISTENT))
                //.put(AuraFakeMiss.class, new AuraFakeMiss(processor, ArtemisChecks.getChecksInfo().get(AuraFakeMiss.class)))

                // AURAS : BAD PACKETS
                .put(AuraBadPacketA.class, new AuraBadPacketA(data, Checks.AURA_A))
                .put(AuraBadPacketB.class, new AuraBadPacketB(data, Checks.AURA_B))
                .put(AuraBadPacketC.class, new AuraBadPacketC(data, Checks.AURA_C))
                .put(AuraBadPacketD.class, new AuraBadPacketD(data, Checks.AURA_D))
                .put(AuraBadPacketE.class, new AuraBadPacketE(data, Checks.AURA_E))
                .put(AuraBadPacketF.class, new AuraBadPacketF(data, Checks.AURA_F))
                .put(AuraBadPacketG.class, new AuraBadPacketG(data, Checks.AURA_G))
                .put(AuraBadPacketH.class, new AuraBadPacketH(data, Checks.AURA_H))
                .put(AuraBadPacketI.class, new AuraBadPacketI(data, Checks.AURA_I))
                .put(AuraBadPacketJ.class, new AuraBadPacketJ(data, Checks.AURA_J))
                .put(AuraBadPacketK.class, new AuraBadPacketK(data, Checks.AURA_K))
                .put(AuraBadPacketL.class, new AuraBadPacketL(data, Checks.AURA_L))
                .put(AuraBadPacketM.class, new AuraBadPacketM(data, Checks.AURA_M))
                .put(AuraBadPacketN.class, new AuraBadPacketN(data, Checks.AURA_N))
                .put(AuraBadPacketO.class, new AuraBadPacketO(data, Checks.AURA_O))


                // AUTOCLICKER
                .put(AutoClickerA.class, new AutoClickerA(data, Checks.AUTOCLICKER_A))
                .put(AutoClickerB.class, new AutoClickerB(data, Checks.AUTOCLICKER_B))
                .put(AutoClickerC.class, new AutoClickerC(data, Checks.AUTOCLICKER_C))
                .put(AutoClickerD.class, new AutoClickerD(data, Checks.AUTOCLICKER_D))
                .put(AutoClickerE.class, new AutoClickerE(data, Checks.AUTOCLICKER_E))
                .put(AutoClickerF.class, new AutoClickerF(data, Checks.AUTOCLICKER_F))
                .put(AutoClickerG.class, new AutoClickerG(data, Checks.AUTOCLICKER_G))
                .put(AutoClickerH.class, new AutoClickerH(data, Checks.AUTOCLICKER_H))
                .put(AutoClickerI.class, new AutoClickerI(data, Checks.AUTOCLICKER_I))
                .put(AutoClickerJ.class, new AutoClickerJ(data, Checks.AUTOCLICKER_J))
                .put(AutoClickerK.class, new AutoClickerK(data, Checks.AUTOCLICKER_K))
                .put(AutoClickerL.class, new AutoClickerL(data, Checks.AUTOCLICKER_L))
                .put(AutoClickerM.class, new AutoClickerM(data, Checks.AUTOCLICKER_M))
                // BAD CLICKER
                .put(AutoClicker1.class, new AutoClicker1(data, Checks.BADCLICKER_1))
                .put(AutoClicker2.class, new AutoClicker2(data, Checks.BADCLICKER_2))
                .put(AutoClicker3.class, new AutoClicker3(data, Checks.BADCLICKER_3))
                .put(AutoClicker4.class, new AutoClicker4(data, Checks.BADCLICKER_4))

                // REACH
                .put(Reach2.class, new Reach2(data, Checks.REACH_GHAST2))

                .put(HitboxA.class, new HitboxA(data, Checks.HITBOX_A))

                .put(VelocityXZ.class, new VelocityXZ(data, Checks.VELOCITY_XZ))
                .put(VelocityY.class, new VelocityY(data, Checks.VELOCITY_Y))
                /*/
                    MOVEMENT
                /*/

                // SPEEDS - FRICTION
                //.put(SpeedFrictionSimple.class, new SpeedFrictionSimple(data, Checks.SPEED_FRICTION_SIMPLE))
                // SPEEDS - GROUND
                // -> Speed
                .put(PredictionSpeed.class, new PredictionSpeed(this.getData(), Checks.PREDICTION_SPEED))
                .put(SpeedHorizontalSimple.class, new SpeedHorizontalSimple(this.getData(), Checks.SPEED_H))
                .put(SpeedV.class, new SpeedV(this.getData(), Checks.SPEED_V))
                // SPEEDS - OMNI
                .put(OmniSprintSimple.class, new OmniSprintSimple(data, Checks.SPEED_OMNI))
                .put(OmniSprintComplex.class, new OmniSprintComplex(data, Checks.SPEED_OMNI_COMPLEX))
                //.put(SpeedMotionComplex.class, new SpeedMotionComplex(data, Checks.SPEED_MOTION_COMPLEX))

                // FLY - MOTION
                .put(FlyGroundSpoofComplex.class, new FlyGroundSpoofComplex(data, Checks.FLY_BAD_GROUND_COMPLEX))
                .put(FlyGroundSpoofBad.class, new FlyGroundSpoofBad(data, Checks.FLY_BAD_GROUND))
                .put(FlyAccelerationSimple.class, new FlyAccelerationSimple(data, Checks.FLY_ACCELERATION))
                .put(FlyDeltaSimple.class, new FlyDeltaSimple(data, Checks.FLY_DELTA_SIMPLE))
                .put(FlyVanilla.class, new FlyVanilla(data, Checks.FLY_VANILLA))
                //.put(FlyInvalid.class, new FlyInvalid(data, Checks.FLY_INVALID))
                .put(FlyHover.class, new FlyHover(data, Checks.FLY_HOVER))
                //.put(FlyDeltaComplex.class, new FlyDeltaComplex(data, Checks.FLY_DELTA_COMPLEX))

                .put(FastLadderSimple.class, new FastLadderSimple(data, Checks.FAST_LADDER_SIMPLE))

                //.put(NoFallSimple.class, new NoFallSimple(data, Checks.NOFALL_SIMPLE))
                //.put(NoFallComplex.class, new NoFallComplex(data, Checks.NOFALL_COMPLEX))
                .put(JesusSimple.class, new JesusSimple(data, Checks.JESUS_SIMPLE))
                .put(BlinkA.class, new BlinkA(data, Checks.BLINK_A))

                .put(Physics.class, new Physics(data, Checks.PREDICTION_PHYSICS))
                //.put(SpeedFriction.class, new SpeedFriction(data, Checks.PREDICTION_FRICTION))

                // scaffold
                .put(ScaffoldA.class, new ScaffoldA(data, Checks.SCAFFOLD_A))

                /*/
                    MISC
                /*/

                .put(PhaseSimple.class, new PhaseSimple(data, Checks.PHASE_SIMPLE))

                // BAD PACKETS
                .put(BadPacketsA.class, new BadPacketsA(data, Checks.BAD_PACKET_A))
                .put(BadPacketsB.class, new BadPacketsB(data, Checks.BAD_PACKET_B))
                .put(BadPacketsC.class, new BadPacketsC(data, Checks.BAD_PACKET_C))
                .put(BadPacketsD.class, new BadPacketsD(data, Checks.BAD_PACKET_D))
                .put(BadPacketsE.class, new BadPacketsE(data, Checks.BAD_PACKET_E))
                .put(BadPacketsG.class, new BadPacketsG(data, Checks.BAD_PACKET_G))
                .put(BadPacketsH.class, new BadPacketsH(data, Checks.BAD_PACKET_H))
                .put(BadPacketsI.class, new BadPacketsI(data, Checks.BAD_PACKET_I))
                .put(BadPacketsJ.class, new BadPacketsJ(data, Checks.BAD_PACKET_J))
                .put(BadPacketsK.class, new BadPacketsK(data, Checks.BAD_PACKET_K))
                .put(BadPacketsL.class, new BadPacketsL(data, Checks.BAD_PACKET_L))
                .put(BadPacketsM.class, new BadPacketsM(data, Checks.BAD_PACKET_M))
                .put(BadPacketsN.class, new BadPacketsN(data, Checks.BAD_PACKET_N))
                .put(BadPacketsP.class, new BadPacketsP(data, Checks.BAD_PACKET_P))
                .put(BadPacketsQ.class, new BadPacketsQ(data, Checks.BAD_PACKET_Q))

                // Protocol

                /*/
                    PLAYER
                 */
                .put(InventorySlotSimple.class, new InventorySlotSimple(data, Checks.INVENTORY_SLOT_SIMPLE))
                .put(InventoryMoving.class, new InventoryMoving(data, Checks.INVENTORY_MOVING))
                .put(InventorySlotComplex.class, new InventorySlotComplex(data, Checks.INVENTORY_A))

                // TIMER
                .put(TimerAverage.class, new TimerAverage(data, Checks.TIMER_AVERAGE))
                .put(TimerInflux.class, new TimerInflux(data, Checks.TIMER_INFLUX))

                // PING SPOOF
                //.put(PingSpoofDelay.class, new PingSpoofDelay(data, Checks.PING_SPOOF_DELAY))
                .put(PingSpoofDuplicate.class, new PingSpoofDuplicate(data, Checks.PING_SPOOF_DUPLICATE))
                .put(PingSpoofIdDupe.class, new PingSpoofIdDupe(data, Checks.PING_SPOOF_ID))

                .build();
        return build;
    }

    public ArtemisCheckLoader(PlayerData data) {
        super(data);
        //checks = newInstance(processor);
    }

    @Override
    public void initChecks() {
        checks = newInstance(getData());
    }
}
