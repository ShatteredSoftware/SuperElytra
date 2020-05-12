<p align="center"><img src="https://raw.githubusercontent.com/UberPilot/SuperElytra/master/image/SuperElytraBanner.png"></p>
<p align="center">
<a href="https://github.com/UberPilot/SuperElytra/releases"><img alt="GitHub Version" src="https://img.shields.io/github/release/UberPilot/SuperElytra?label=Github%20Version&logo=github&style=for-the-badge"></a>
<a href="https://discord.gg/zUbNX9t"><img alt="Discord" src="https://img.shields.io/badge/Get%20Help-On%20Discord-%237289DA?style=for-the-badge&logo=discord" /></a>
<a href="ko-fi.com/uberpilot"><img alt="Ko-Fi" src="https://img.shields.io/badge/Support-on%20Ko--fi-%23F16061?style=for-the-badge&logo=ko-fi" /></a>
</p>

----
This is my fork of SuperElytra. Check out the original by Eisenwave  [here]('https://www.spigotmc.org/resources/superelytra-improved-elytra-flight.19382/').

![https://i.imgur.com/R56HpBH.png](https://i.imgur.com/R56HpBH.png)   
SuperElytra makes elytra flight more exciting by allowing players to launch flight from the ground, and fly infinitely in the air by increasing your flying speed when flying downwards. 
**Original Features:**

* 1.13.2 Support
* Increased, configurable speed when flying downwards.
* Crouch to launch into the air, with configurable 

**New Features:**

* Cross-version compatibility.
* Fully configurable messages.
* Fully configurable sounds.
* Allow players to enable and disable specific features for themselves.
* Active support.

![https://i.imgur.com/ilMjPaM.png](https://i.imgur.com/ilMjPaM.png)  
`config.yml`
```yaml
config:
  ==: SuperElytraConfig # If you change this line, the plugin may not load properly.
  chargeup-ticks: 60 # How many ticks it takes to launch from the ground.
  speed-multiplier: 1.0 # How much to increase the flying speed each tick.
  launch-multiplier: 1.0 # How much to increase the boost amount.
  default: true # If flight/boosting should be enabled by default.
  charge-sound: FUSE
  ready-sound: BAT_TAKEOFF
  launch-sound: ENDERDRAGON_WINGS
  autosave-interval: 600 # Saves player preferences in this second interval.
```

`messages.yml`
```yaml
prefix: "&f[&bSuperElytra&f] "
no-console: >
  &cConsole can't use player commands.
reloaded: >
  &eReloaded.
no-permission: >
  &cYou don't have permission to use that.
not-enough-args: >
  &cNot enough arguments. Found %argc%, expected %argx%.
too-many-args: >
  &cToo many arguments. Found %argc%, expected %argx%.
launch-enabled: >
  Crouch to launch has been &aenabled&f.
launch-disabled: >
  Crouch to launch has been &cdisabled&f.
boost-enabled: >
  Boosted flight has been &aenabled&f.
boost-disabled: >
  Boosted flight has been &cdisabled&f.
all-enabled: >
  Boosting and launching have been &aenabled&f.
all-disabled: >
  Boosting and launching have been &cdisabled&f.
invalid-argument: >
  &cGot invalid argument '%invalid%.' Expected %expected%.
```

![https://i.imgur.com/zOLsv8g.png](https://i.imgur.com/zOLsv8g.png)  

* /elytramode - Change between normal and boosted flight.
* /elmode - Same as above.


* /elytrapreferences - Enable and disable boosting and launching separately.
* /elytraprefs - Same as above.
* /elprefs - Same as above.


* /elytrareload - Reload the plugin's configuration.
* /elreload - Same as above.

![https://i.imgur.com/5B3sOy3.png](https://i.imgur.com/5B3sOy3.png) 

* superelytra.glide - Allows for boosted flight.
* superelytra.launch - Allows for launching from the ground.
* superelytra.command.elytramode - Allows using /elytramode
* superelytra.command.elytrareload - Allows using /elytrareload
* superelytra.command.elytraprefs - Allows using /elytraprefs

![https://i.imgur.com/BqJR9lM.png](https://i.imgur.com/BqJR9lM.png)   
Need help? Found a bug? Join [our discord]('https://discord.gg/zUbNX9t') and get help quickly!