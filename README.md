<p align="center">
  <img width="50%" src="https://i.imgur.com/gpjLEmU.png" alt="Artemis">
  <br>
  <a href="https://github.com/artemisac/Artemis/issues"><img alt="GitHub versions" src="https://img.shields.io/badge/Bukkit%20Version-1.7--1.13.2-blue"></a>
  <a href="https://github.com/artemisac/Artemis/network"><img alt="GitHub forks" src="https://img.shields.io/spiget/tested-versions/73908?label=Compatible%20versions"></a>
  <a href="https://github.com/artemisac/Artemis/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/Artemis-Anticheat/Artemis?style=social"></a>
</p>

### Preface
Artemis Anticheat has possibly been the longest on-going project I had ever made. I'm happy to open-source this finally to the public. There's a couple things however I'd like to get out of the way right there and then. Please make sure to read everything if you wish to contribute/use this for commercial use.
- This is open sourced without a license. You are not eligible to use this for commercial use unless strictly allowed by myself (Shanyu Juneja)
- This uses code which I have been either granted permission to use either I've been sold rights to. If you believe this infringes on your copyright, you may be either wrong either a mistunderstanding must have happened. Anywhom, contact me on discord (Ghast#0001) if you believe that I use your code.
- This isn't entirely done. The base work and checks have been made. It's now up to the community to refine this to it's best potential level.
- I removed a sneaky check hehe

## Introduction

What is Artemis? Well... Imagine this: You run a Minecraft server. A cheater comes online. You're not online. So he proceeds to piss off your players and gain a competitive advantage. How to solve this? Very simple! You need an anticheat. However anticheats are tedious. This, by far to my belief, is the best solution to have been provided online as open source.

Here's an introductory video on Artemis

[Watch the video](https://youtu.be/NJY-iDrdSaQ)

### Features

Artemis is bundled with a variety of features which have been seen or not in the past. It's objective is to remain as simple as possible yet to have an important amount of API's and useful core tools for the easy creation of gameplay enforcement tactics. It's main focus is allowing the end consumer to modify everything to their liking yet to be able to enjoy basic configuration as simple as one could be.

**Artemis, at it's current stage, contains the following:**
- Prediction with E-8 precision of the player's position. Perfect for catching all movement cheats
- Extremely robust core, with all sorts of useful tools at the disposition of any developer
- 3.05 reach check. Extremely hard to false. Yet to be perfect, but yet not too far from it.
- +20 Aim, Aura and Autoclicker checks designed to flag a variety of cheats. Most are extremely thoroughly tested and near flawless
- Variety of supplementary fly, speed and misc checks for secondary opinion on top of the prediction system
- GUI designed with simplicity and efficiency in mind
- A fuck-load of documented code. Some of the code may be old and rubbish. I tried my best reworking everything in the nick of time
- New packet system soon to be implemented which injects itself before any other packet system to guarantee 100% accurate and precise data.
- Extremely extensive and easy-to-use configuration system for checks
- Mediocre theme configuration system. I'd suggest recoding it. Not useful for obfuscation
- Mediocre infrastructure but clear in how it functions
- Interesting exemption system which has yet to have it's potential exploited
- Interesting common-attribute check information system to dynamically update certain settings
- Java based encrypted loader made by Cg., all rights reserved to SoterDev.
- Proper Maven structure that I personally love
- Smart setback system with little to no issues. Can be modified in the code to be more or less strict.

**The detections currently catch:** 
- AntiKnockback
- AntiPotion
- AntiWaterPush
- AutoArmor
- AutoPotion
- AutoClicker
- Aimbot
- Blink (Very experimental)
- BoatFly (Need to finish the vehicle implementation)
- BowAimbot (I removed the aim check because no <3 )
- BunnyHop
- ClickAura
- Criticals
- Derp
- Dolphin
- FastBow (Majority)
- FastBreak (Experimental/Not finished)
- FastEat
- FastLadder
- FastPlace
- FightBot
- Flight
- Glide
- Headless
- HeadRoll
- HighJump
- Inventory Walk
- Jesus
- Jetpack
- Killaura
- Derp
- MultiAura
- NoClip
- NoFall
- NoSlowdown
- NoWeb
- Nuker
- Parkour (Heuristic/Experimental)
- Phase
- Reach
- Regen (Not active)
- SafeWalk (Heuristic/Experimental)
- ScaffoldWalk (Depends)
- ServerCrasher
- Sneak
- SpeedHack
- SpeedNuker
- Spider
- Step
- Timer
- TP-Aura
- TriggerBot (Depends)
- Velocity

### Credits

[FunkeMunky/Dawson](https://github.com/funkemunky) - Various Atlas/Fiona utils, more specifically the old yet still active packet api. Thank you Dawson for allowing me to use it in the meantime. Much appreciated (Authorization given in private)

[Sim0n](https://github.com/sim0n/) - GraphUtil. Thank you Sim0n for allowing me to use it. I really couldn't be asked to recode despite how simplistic it may be. (Authorization given in private)

[Elevated](https://github.com/ElevatedDev) - Cinematic util and various other things. Thank you Elevated. I couldn't be more grateful of all the wisdom you shared with me.

[ToonBasic](https://github.com/toonbasic) - 4-5 BadPackets checks from various things I bought out in the past. Thanks Toon. I couldn't be asked to reinvent the wheel.

## Sponsors

## Commercial License

You may buy a commercial license for this specific software by contacting me via Discord (Ghast#0001). Resell licenses will not be offered for below $xxxx.xx. Server usage licenses are available at the price of $97.00. 

As per your expectations:
- You may not distribute this software
- You may not use this for any use other than personal/educational without a commercial license.
- You may not use bits of this software to improve your own
- You may not copy this software's interface
- Blablabla just don't be a skid.
