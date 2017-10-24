# ExtraSystemReloaded

*The ShipLevel System* is the only working for now. And may be that for ever.
Each ship has a somewhat-random quality ranging 0.5~1.5 based on it's in-game ID. Quality decides the cost and the degree of upgrades.

Upgrade has about 40% (configurable) chance of success at the minimum.
+You can turn off failures in settings.json in data\config
+baseFailureMinFactor is now there to lessen your losses.

-What is decided by the abilities-

The efficiency-
		Frigate:100% Destroyer:80% Cruiser:60% Capital Ship:40%
+You can control how many resources you wish to spend with dividingRatio parameter (Better is cheaper). 'Cause u know, the original costs are outrageous.
+upgradeCostMaxFactor and upgradeCostMinFactor (the latter will have much more impact on the cost to upgrade)

Durability:
	Hullpoints, armor, weapon health, engine health
  +Now with EMP resistance up to 40% on Q=1
WeaponProficiency:
	Weapon range, weapon damage, rate of fire
Logistics:
	CR per deployment, weapon ammo, crew, ship repair rate, CR recovery, fuel use
  +Now less SuppliesPerMonth and SuppliesToRecover up to 15% at Q=1
Mobility:
	Max speed, acceleration, deceleration, max turn rate, turn acceleration
  +Burn level up to +20% at Q=1
Technology:
	Flux capacity and disspation, weapon flux cost, shield efficiency
  +Now with PhaseCloak improvements!
