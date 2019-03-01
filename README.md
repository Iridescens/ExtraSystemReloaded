# ExtraSystemReloaded

*The ShipLevel System* is the only working as intended for now. And may be that for long time (till I get better at Java and SS' guts).
Each ship has a somewhat-random quality ranging 0.5~1.5 based on it's in-game ID. Quality decides the cost and the degree of upgrades.
+You can toggle "RNG" in settings and adjust base quality if toggled off.

Upgrade has about 40% (configurable) chance of success at the minimum.
+You can turn off failures in settings.json in data\config
+baseFailureMinFactor is now there to lessen your losses.

+Upgrade subsystems on non-hostile planets with market.

---Mechanics---

The efficiency step of upgrades are:
   Frigate:10% Destroyer:8% Cruiser:6% Capital Ship:4%
With a total of steps:
   Frigate:10 Destroyer:15 Cruiser:20 Capital Ship:25
Which means that all sizes will receive full (or a little bit more) bonus mentioned below.
+You can control how many resources you wish to spend with dividingRatio parameter (higher is cheaper). 'Cause u know, the original costs are outrageous.
+upgradeCostMaxFactor and upgradeCostMinFactor (the latter will have much more impact on the cost to upgrade)

Durability:
Hullpoints, armor, weapon health, engine health up to 50%
  +Now with EMP resistance up to 40% on Q=1
Weaponry:
Weapon range, weapon damage, rate of fire up to 15%
Logistics:
CR per deployment, weapon ammo, crew, ship repair rate, CR recovery, fuel use
  +Now less SuppliesPerMonth and SuppliesToRecover up to 15% at Q=1
Mobility:
Max speed up to 33%; acceleration, deceleration, max turn rate, turn acceleration up to 50%
  +Burn level up to +20% at Q=1
Technology:
Flux capacity and disspation up to 33%; weapon flux cost, shield efficiency up to 15%
  +Now with PhaseCloak improvements up to 25% flux savings!

+Ship's quality can be upgraded! Seek markets with better industries.

Coming next:
-Adjustable efficiency steps and number of levels
-Fighter-hull upgrades (maybe, unlikely)
-More achievements
-Make it possible to just buy upgrades without materials/commodities