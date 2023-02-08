package ac.artemis.checks.regular.v2;

import ac.artemis.checks.regular.v2.checks.impl.aim.*;
import ac.artemis.checks.regular.v2.checks.impl.aim.cinematic.Cinematic;
import ac.artemis.checks.regular.v2.checks.impl.aim.cinematic.Cinematic2;
import ac.artemis.checks.regular.v2.checks.impl.aura.*;
import ac.artemis.checks.regular.v2.checks.impl.aura.badpacket.*;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.*;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket.AutoClicker1;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket.AutoClicker2;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket.AutoClicker3;
import ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket.AutoClicker4;
import ac.artemis.checks.regular.v2.checks.impl.badpackets.*;
import ac.artemis.checks.regular.v2.checks.impl.blink.BlinkA;
import ac.artemis.checks.regular.v2.checks.impl.disabler.DisablerRespawn;
import ac.artemis.checks.regular.v2.checks.impl.disabler.DisablerTransaction;
import ac.artemis.checks.regular.v2.checks.impl.emulator.Physics;
import ac.artemis.checks.regular.v2.checks.impl.emulator.PredictionSpeed;
import ac.artemis.checks.regular.v2.checks.impl.fastladder.FastLadderSimple;
import ac.artemis.checks.regular.v2.checks.impl.fly.*;
import ac.artemis.checks.regular.v2.checks.impl.hitbox.HitboxA;
import ac.artemis.checks.regular.v2.checks.impl.inventory.InventoryMoving;
import ac.artemis.checks.regular.v2.checks.impl.inventory.InventorySlotComplex;
import ac.artemis.checks.regular.v2.checks.impl.inventory.InventorySlotSimple;
import ac.artemis.checks.regular.v2.checks.impl.jesus.JesusSimple;
import ac.artemis.checks.regular.v2.checks.impl.nofall.NoFallComplex;
import ac.artemis.checks.regular.v2.checks.impl.nofall.NoFallSimple;
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
import ac.artemis.core.v4.check.settings.CheckInformation;


/**
 * @author Ghast
 * @since 24-Apr-20
 */
public final class Checks {

    /*
       ______                       __               _
     .' ___  |                     [  |             / |_
    / .'   \_|  .--.   _ .--..--.   | |.--.   ,--. `| |-'
    | |       / .'`\ \[ `.-. .-. |  | '/'`\ \`'_\ : | |
    \ `.___.'\| \__. | | | | | | |  |  \__/ |// | |,| |,
     `.____ .' '.__.' [___||__||__][__;.__.' \'-;__/\__/

     */


    /**
     * Aim checks which rely purely on mathematics
     */
    //public static final CheckInformation AIM_1337 = new CheckInformation(Aim0x1337.class);
    public static final CheckInformation AIM_DRIP = new CheckInformation(AimDrip.class);
    public static final CheckInformation AIM_IDENTICAL = new CheckInformation(AimIdentical.class);

    //public static final CheckInformation AIM_SENSITIVITY = new CheckInformation(AimSensitivity.class);
    // static final CheckInformation AIM_SMOOTHNESS = new CheckInformation(AimSmoothness.class);
    //public static final CheckInformation AIM_SMOOTHNESS_YAW = new CheckInformation(AimSmoothnessYaw.class);
    public static final CheckInformation AIM_CINEMATIC = new CheckInformation(Cinematic.class);
    public static final CheckInformation AIM_CINEMATIC2 = new CheckInformation(Cinematic2.class);
    public static final CheckInformation AIM_RATIO = new CheckInformation(AimRatio.class);
    public static final CheckInformation AIM_RANDOMIZED = new CheckInformation(AimRandomized.class);
    public static final CheckInformation AIM_INVALID_SMOOTH = new CheckInformation(AimInvalidSmooth.class);
    public static final CheckInformation AIM_GENERIC = new CheckInformation(AimGeneric.class);
    public static final CheckInformation AIM_LIQUID = new CheckInformation(AimLiquid.class);

    // Frep's checks
    public static final CheckInformation AIM_DIVISOR = new CheckInformation(AimDivisor.class);
    public static final CheckInformation AIM_MOON = new CheckInformation(AimKitten.class);
    public static final CheckInformation AIM_WATER = new CheckInformation(AimWater.class);

    // Tecnio's checks
    public static final CheckInformation AIM_COPE = new CheckInformation(AimCope.class);

    /**
     * Aura checks which rely on mathematics
     */
    public static final CheckInformation AURA_MOVEMENT = new CheckInformation(AuraMovement.class);
    public static final CheckInformation AURA_MULTI = new CheckInformation(AuraMulti.class);
    public static final CheckInformation AURA_ACCELERATION = new CheckInformation(AuraAcceleration.class);
    public static final CheckInformation AURA_LOCK = new CheckInformation(AuraLock.class);
    public static final CheckInformation AURA_INCONSISTENT = new CheckInformation(AuraInconsistent.class);
    public static final CheckInformation AURA_NMS = new CheckInformation(AuraNMS.class);

    /**
     * Aura checks which rely on bad packets
     */
    public static final CheckInformation AURA_A = new CheckInformation(AuraBadPacketA.class);
    public static final CheckInformation AURA_B = new CheckInformation(AuraBadPacketA.class);
    public static final CheckInformation AURA_C = new CheckInformation(AuraBadPacketC.class);
    public static final CheckInformation AURA_D = new CheckInformation(AuraBadPacketD.class);
    public static final CheckInformation AURA_E = new CheckInformation(AuraBadPacketE.class);
    public static final CheckInformation AURA_F = new CheckInformation(AuraBadPacketF.class);
    public static final CheckInformation AURA_G = new CheckInformation(AuraBadPacketG.class);
    public static final CheckInformation AURA_H = new CheckInformation(AuraBadPacketH.class);
    public static final CheckInformation AURA_I = new CheckInformation(AuraBadPacketI.class);
    public static final CheckInformation AURA_J = new CheckInformation(AuraBadPacketJ.class);
    public static final CheckInformation AURA_K = new CheckInformation(AuraBadPacketK.class);
    public static final CheckInformation AURA_L = new CheckInformation(AuraBadPacketL.class);
    public static final CheckInformation AURA_M = new CheckInformation(AuraBadPacketM.class);
    public static final CheckInformation AURA_N = new CheckInformation(AuraBadPacketN.class);
    public static final CheckInformation AURA_O = new CheckInformation(AuraBadPacketO.class);

    /**
     * Autoclicker checks which rely on mathematics and stats
     */
    public static final CheckInformation AUTOCLICKER_A = new CheckInformation(AutoClickerA.class);
    public static final CheckInformation AUTOCLICKER_B = new CheckInformation(AutoClickerB.class);
    public static final CheckInformation AUTOCLICKER_C = new CheckInformation(AutoClickerC.class);
    public static final CheckInformation AUTOCLICKER_D = new CheckInformation(AutoClickerD.class);
    public static final CheckInformation AUTOCLICKER_E = new CheckInformation(AutoClickerE.class);
    public static final CheckInformation AUTOCLICKER_F = new CheckInformation(AutoClickerF.class);
    public static final CheckInformation AUTOCLICKER_G = new CheckInformation(AutoClickerG.class);
    public static final CheckInformation AUTOCLICKER_H = new CheckInformation(AutoClickerH.class);
    public static final CheckInformation AUTOCLICKER_I = new CheckInformation(AutoClickerI.class);
    public static final CheckInformation AUTOCLICKER_J = new CheckInformation(AutoClickerJ.class);
    public static final CheckInformation AUTOCLICKER_K = new CheckInformation(AutoClickerK.class);
    public static final CheckInformation AUTOCLICKER_L = new CheckInformation(AutoClickerL.class);
    public static final CheckInformation AUTOCLICKER_M = new CheckInformation(AutoClickerM.class);

    /**
     * Autoclicker checks which rely on bad packets
     */
    public static final CheckInformation BADCLICKER_1 = new CheckInformation(AutoClicker1.class);
    public static final CheckInformation BADCLICKER_2 = new CheckInformation(AutoClicker2.class);
    public static final CheckInformation BADCLICKER_3 = new CheckInformation(AutoClicker3.class);
    public static final CheckInformation BADCLICKER_4 = new CheckInformation(AutoClicker4.class);

    /**
     * Hitbox checks which rely on NMS values
     */
    public static final CheckInformation HITBOX_A = new CheckInformation(HitboxA.class);

    /**
     * Reach checks which rely on various mathematical and game development technics to properly rollback
     */
    public static final CheckInformation REACH_GHAST2 = new CheckInformation(Reach2.class);

    /**
     * Velocity checks based on heuristic and physics approach
     */

    public static final CheckInformation VELOCITY_XZ = new CheckInformation(VelocityXZ.class);
    public static final CheckInformation VELOCITY_Y = new CheckInformation(VelocityY.class);

    /*
     ____    ____   _
    |_   \  /   _| (_)
      |   \/   |   __   .--.   .---.
      | |\  /| |  [  | ( (`\] / /'`\]
     _| |_\/_| |_  | |  `'.'. | \__.
    |_____||_____|[___][\__) )'.___.'

     */

    /**
     * Bad Packet checks which are common invalid patterns which are native to certain versions
     */
    public static final CheckInformation BAD_PACKET_A = new CheckInformation(BadPacketsA.class);
    public static final CheckInformation BAD_PACKET_B = new CheckInformation(BadPacketsB.class);
    public static final CheckInformation BAD_PACKET_C = new CheckInformation(BadPacketsC.class);
    public static final CheckInformation BAD_PACKET_D = new CheckInformation(BadPacketsD.class);
    public static final CheckInformation BAD_PACKET_E = new CheckInformation(BadPacketsE.class);
    public static final CheckInformation BAD_PACKET_G = new CheckInformation(BadPacketsG.class);
    public static final CheckInformation BAD_PACKET_H = new CheckInformation(BadPacketsH.class);
    public static final CheckInformation BAD_PACKET_I = new CheckInformation(BadPacketsI.class);
    public static final CheckInformation BAD_PACKET_J = new CheckInformation(BadPacketsJ.class);
    public static final CheckInformation BAD_PACKET_K = new CheckInformation(BadPacketsK.class);
    public static final CheckInformation BAD_PACKET_L = new CheckInformation(BadPacketsL.class);
    public static final CheckInformation BAD_PACKET_M = new CheckInformation(BadPacketsM.class);
    public static final CheckInformation BAD_PACKET_N = new CheckInformation(BadPacketsN.class);
    public static final CheckInformation BAD_PACKET_P = new CheckInformation(BadPacketsP.class);
    public static final CheckInformation BAD_PACKET_Q = new CheckInformation(BadPacketsQ.class);

    /**
     * Common disablers used to well... screw with anticheats
     */
    public static final CheckInformation DISABLER_TRANSACTION = new CheckInformation(DisablerTransaction.class);
    public static final CheckInformation DISABLER_RESPAWN = new CheckInformation(DisablerRespawn.class);


    /*
     ____    ____                                                    _
    |_   \  /   _|                                                  / |_
      |   \/   |   .--.   _   __  .---.  _ .--..--.  .---.  _ .--. `| |-'
      | |\  /| | / .'`\ \[ \ [  ]/ /__\\[ `.-. .-. |/ /__\\[ `.-. | | |
     _| |_\/_| |_| \__. | \ \/ / | \__., | | | | | || \__., | | | | | |,
    |_____||_____|'.__.'   \__/   '.__.'[___||__||__]'.__.'[___||__]\__/

     */

    /**
     * Fly checks which rely purely on movement packets
     */

    // Air
    public static final CheckInformation FLY_ACCELERATION = new CheckInformation(FlyAccelerationSimple.class);
    public static final CheckInformation FLY_HOVER = new CheckInformation(FlyHover.class);
    public static final CheckInformation FLY_INVALID = new CheckInformation(FlyInvalid.class);

    // Ascension
    public static final CheckInformation FLY_DELTA_SIMPLE = new CheckInformation(FlyDeltaSimple.class);
    public static final CheckInformation FLY_DELTA_COMPLEX = new CheckInformation(FlyDeltaComplex.class);
    public static final CheckInformation FLY_VANILLA = new CheckInformation(FlyVanilla.class);

    // Bad Packet

    /**
     * Fly checks which rely on bad packets
     */
    public static final CheckInformation FLY_BAD_GROUND = new CheckInformation(FlyGroundSpoofBad.class);
    public static final CheckInformation FLY_BAD_GROUND_COMPLEX = new CheckInformation(FlyGroundSpoofComplex.class);

    /**
     * Phase checks which use the environment to evaluate if it's an invalid movement
     */
    public static final CheckInformation PHASE_SIMPLE = new CheckInformation(PhaseSimple.class);

    /**
     * Safe walk checks based on a heuristic approach
     */

    /**
     * Speed checks based on NMS values and reversal of the game function
     */
    //public static final CheckInformation SPEED_MOTION_COMPLEX = new CheckInformation(SpeedMotionComplex.class);
    public static final CheckInformation SPEED_H = new CheckInformation(SpeedHorizontalSimple.class);
    public static final CheckInformation SPEED_V = new CheckInformation(SpeedV.class);
    public static final CheckInformation SPEED_OMNI = new CheckInformation(OmniSprintSimple.class);
    public static final CheckInformation SPEED_OMNI_COMPLEX = new CheckInformation(OmniSprintComplex.class);

    /**
     * Jesus checks based on collision analysis
     */
    public static final CheckInformation JESUS_SIMPLE = new CheckInformation(JesusSimple.class);

    /**
     * NoFall checks based on both Fall Distance and MotionY
     */
    public static final CheckInformation NOFALL_SIMPLE = new CheckInformation(NoFallSimple.class);
    public static final CheckInformation NOFALL_COMPLEX = new CheckInformation(NoFallComplex.class);

    /**
     * Prediction checks based on NMS
     */
    public static final CheckInformation PREDICTION_PHYSICS = new CheckInformation(Physics.class);
    public static final CheckInformation PREDICTION_SPEED = new CheckInformation(PredictionSpeed.class);

    /**
     * Fast Ladder check based on autistic shit
     */
    public static final CheckInformation FAST_LADDER_SIMPLE = new CheckInformation(FastLadderSimple.class);

    /**
     * ok
     */
    public static final CheckInformation SCAFFOLD_A = new CheckInformation(ScaffoldA.class);

    /*

     _______  __
    |_   __ \[  |
      | |__) || |  ,--.    _   __  .---.  _ .--.
      |  ___/ | | `'_\ :  [ \ [  ]/ /__\\[ `/'`\]
     _| |_    | | // | |,  \ '/ / | \__., | |
    |_____|  [___]\'-;__/[\_:  /   '.__.'[___]
                          \__.'

     */

    /**
     * Inventory heuristic based checks
     */
    public static final CheckInformation INVENTORY_SLOT_SIMPLE = new CheckInformation(InventorySlotSimple.class);
    public static final CheckInformation INVENTORY_MOVING = new CheckInformation(InventoryMoving.class);

    /**
     * Inventory bad packet based checks
     */
    public static final CheckInformation INVENTORY_A = new CheckInformation(InventorySlotComplex.class);

    /**
     * Regen bad packet based checks
     */


    /**
     * Impossible based ping spoof checks
     */
    public static final CheckInformation PING_SPOOF_DUPLICATE = new CheckInformation(PingSpoofDuplicate.class);
    public static final CheckInformation PING_SPOOF_ID = new CheckInformation(PingSpoofIdDupe.class);

    /**
     * Statistically based timer checks
     */
    public static final CheckInformation TIMER_AVERAGE = new CheckInformation(TimerAverage.class);
    public static final CheckInformation TIMER_INFLUX = new CheckInformation(TimerInflux.class);

    /**
     * Heuristical approach to flagging blink cheats
     */
    public static final CheckInformation BLINK_A = new CheckInformation(BlinkA.class);

}
