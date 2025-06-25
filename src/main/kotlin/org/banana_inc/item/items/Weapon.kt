package org.banana_inc.item.items

import org.banana_inc.extensions.CP
import org.banana_inc.extensions.GP
import org.banana_inc.extensions.SP
import org.banana_inc.extensions.lb
import org.banana_inc.item.ItemMaterial
import org.banana_inc.item.attributes.Currency
import org.banana_inc.item.attributes.Weight
import org.banana_inc.item.attributes.weapon.*
import org.banana_inc.util.dnd.*


interface Magical: ItemData.Category

sealed class Weapon(
    cost: Currency,
    weight: Weight,
    val damageDice: Dice.Damage,
    val weaponProperties: Set<WeaponProperty>,
    val weaponMastery: WeaponMastery,
    val combatType: CombatType,
    val weaponProficiency: WeaponProficiency
): ItemData(cost,weight){

    override val stackSize = 1
    override val itemType = ItemMaterial.WEAPON

    sealed class Melee(cost: Currency, weight: Weight, damageDice: Dice.Damage, properties: Set<WeaponProperty>, weaponMastery: WeaponMastery, weaponProficiency: WeaponProficiency): Weapon(cost, weight, damageDice, properties, weaponMastery,
        CombatType.MELEE, weaponProficiency) {
        sealed class Simple(cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery, vararg properties: WeaponProperty) : Melee(cost,weight, damageDice, properties.toHashSet(), weaponMastery,
            WeaponProficiency.SIMPLE
        ) {
            data object Club : Simple(1.SP, 2.lb,
                Dice.Damage(d4 to DamageType.BLUDGEONING),
                WeaponMastery.SLOW,
                WeaponProperty.Light
            )
            data object Dagger : Simple(2.GP, 1.lb,
                Dice.Damage(d4 to DamageType.PIERCING),
                WeaponMastery.NICK,
                WeaponProperty.Finesse,
                WeaponProperty.Light,
                WeaponProperty.Thrown(WeaponRange(20, 60))
            )
            data object Greatclub : Simple(2.SP, 10.lb,
                Dice.Damage(d8 to DamageType.BLUDGEONING),
                WeaponMastery.PUSH,
                WeaponProperty.TwoHanded()
            )
            data object Handaxe : Simple(5.GP, 2.lb,
                Dice.Damage(d6 to DamageType.SLASHING),
                WeaponMastery.VEX,
                WeaponProperty.Light,
                WeaponProperty.Thrown(WeaponRange(20, 60))
            )
            data object Javelin : Simple(5.SP, 2.lb,
                Dice.Damage(d6 to DamageType.PIERCING),
                WeaponMastery.SLOW,
                WeaponProperty.Thrown(WeaponRange(30, 120))
            )
            data object LightHammer : Simple(2.GP, 2.lb,
                Dice.Damage(d4 to DamageType.BLUDGEONING),
                WeaponMastery.NICK,
                WeaponProperty.Light,
                WeaponProperty.Thrown(WeaponRange(20, 60))
            )
            data object Mace : Simple(5.GP, 4.lb, Dice.Damage(d6 to DamageType.BLUDGEONING), WeaponMastery.SAP)
            data object Quarterstaff : Simple(2.SP, 4.lb,
                Dice.Damage(d6 to DamageType.BLUDGEONING),
                WeaponMastery.TOPPLE,
                WeaponProperty.Versatile(d8)
            )
            data object Sickle : Simple(1.GP, 2.lb,
                Dice.Damage(d4 to DamageType.SLASHING),
                WeaponMastery.NICK,
                WeaponProperty.Light
            )
            data object Spear : Simple(1.GP, 3.lb,
                Dice.Damage(d6 to DamageType.PIERCING),
                WeaponMastery.SAP,
                WeaponProperty.Thrown(WeaponRange(20, 60)),
                WeaponProperty.Versatile(d8)
            )
        }

        sealed class Martial(
            cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery,
            vararg properties: WeaponProperty
        ) : Melee(cost, weight, damageDice, properties.toHashSet(), weaponMastery, WeaponProficiency.MARTIAL) {

            data object Battleaxe : Martial(10.GP, 4.lb,
                Dice.Damage(d8 to DamageType.SLASHING),
                WeaponMastery.TOPPLE,
                WeaponProperty.Versatile(d10)
            )
            data object Flail : Martial(10.GP, 2.lb, Dice.Damage(d8 to DamageType.BLUDGEONING), WeaponMastery.SAP)
            data object Glaive : Martial(20.GP, 6.lb,
                Dice.Damage(d10 to DamageType.SLASHING),
                WeaponMastery.GRAZE,
                WeaponProperty.Heavy,
                WeaponProperty.Reach,
                WeaponProperty.TwoHanded()
            )
            data object Greataxe : Martial(30.GP, 7.lb,
                Dice.Damage(d12 to DamageType.SLASHING),
                WeaponMastery.CLEAVE,
                WeaponProperty.Heavy,
                WeaponProperty.TwoHanded()
            )
            data object Greatsword : Martial(50.GP, 6.lb,
                Dice.Damage(2.d6 to DamageType.SLASHING),
                WeaponMastery.GRAZE,
                WeaponProperty.Heavy,
                WeaponProperty.TwoHanded()
            )
            data object Halberd : Martial(20.GP, 6.lb,
                Dice.Damage(d10 to DamageType.SLASHING),
                WeaponMastery.CLEAVE,
                WeaponProperty.Heavy,
                WeaponProperty.Reach,
                WeaponProperty.TwoHanded()
            )
            data object Lance : Martial(10.GP, 6.lb,
                Dice.Damage(d10 to DamageType.PIERCING),
                WeaponMastery.TOPPLE,
                WeaponProperty.Heavy,
                WeaponProperty.Reach,
                WeaponProperty.TwoHanded(unlessMounted = true)
            )
            data object Longsword : Martial(15.GP, 3.lb,
                Dice.Damage(d8 to DamageType.SLASHING),
                WeaponMastery.SAP,
                WeaponProperty.Versatile(d10)
            )
            data object Maul : Martial(10.GP, 10.lb,
                Dice.Damage(2.d6 to DamageType.BLUDGEONING),
                WeaponMastery.TOPPLE,
                WeaponProperty.Heavy,
                WeaponProperty.TwoHanded()
            )
            data object Morningstar : Martial(15.GP, 4.lb,
                Dice.Damage(d8 to DamageType.PIERCING),
                WeaponMastery.SAP
            )
            data object Pike : Martial(5.GP, 18.lb,
                Dice.Damage(d10 to DamageType.PIERCING),
                WeaponMastery.PUSH,
                WeaponProperty.Heavy,
                WeaponProperty.Reach,
                WeaponProperty.TwoHanded()
            )
            data object Rapier : Martial(25.GP, 2.lb,
                Dice.Damage(d8 to DamageType.PIERCING),
                WeaponMastery.VEX,
                WeaponProperty.Finesse
            )
            data object Scimitar : Martial(25.GP, 3.lb,
                Dice.Damage(d6 to DamageType.SLASHING),
                WeaponMastery.NICK,
                WeaponProperty.Finesse,
                WeaponProperty.Light
            )
            data object Shortsword : Martial(10.GP, 2.lb,
                Dice.Damage(d6 to DamageType.PIERCING),
                WeaponMastery.VEX,
                WeaponProperty.Finesse,
                WeaponProperty.Light
            )
            data object Trident : Martial(5.GP, 4.lb,
                Dice.Damage(d8 to DamageType.PIERCING),
                WeaponMastery.TOPPLE,
                WeaponProperty.Thrown(WeaponRange(20, 60)),
                WeaponProperty.Versatile(d10)
            )
            data object Warhammer : Martial(15.GP, 5.lb,
                Dice.Damage(d8 to DamageType.BLUDGEONING),
                WeaponMastery.PUSH,
                WeaponProperty.Versatile(d10)
            )
            data object WarPick : Martial(5.GP, 2.lb,
                Dice.Damage(d8 to DamageType.PIERCING),
                WeaponMastery.SAP,
                WeaponProperty.Versatile(d10)
            )
            data object Whip : Martial(2.GP, 3.lb,
                Dice.Damage(d4 to DamageType.SLASHING),
                WeaponMastery.SLOW,
                WeaponProperty.Finesse,
                WeaponProperty.Reach
            )
        }
    }

    sealed class Ranged(
        cost: Currency, weight: Weight, damageDice: Dice.Damage, properties: Set<WeaponProperty>,
        weaponMastery: WeaponMastery, weaponProficiency: WeaponProficiency
    ) : Weapon(cost, weight, damageDice, properties, weaponMastery, CombatType.RANGED, weaponProficiency) {

        sealed class Simple(
            cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery,
            vararg properties: WeaponProperty,
            override val itemType: ItemMaterial = ItemMaterial.CROSSBOW
        ) : Ranged(cost, weight, damageDice, properties.toHashSet(), weaponMastery, WeaponProficiency.SIMPLE) {

            data object Dart : Simple(5.CP, 0.25.lb,
                Dice.Damage(d4 to DamageType.PIERCING),
                WeaponMastery.VEX,
                WeaponProperty.Finesse,
                WeaponProperty.Thrown(WeaponRange(20, 60)), itemType= ItemMaterial.THROWABLE
            )
            data object LightCrossbow : Simple(25.GP, 5.lb,
                Dice.Damage(d8 to DamageType.PIERCING),
                WeaponMastery.SLOW,
                WeaponProperty.Ammunition(
                    WeaponRange(80, 320),
                    Ammunition.Bolt
                ),
                WeaponProperty.Loading,
                WeaponProperty.TwoHanded()
            )
            data object Shortbow : Simple(25.GP, 2.lb,
                Dice.Damage(d6 to DamageType.PIERCING),
                WeaponMastery.VEX,
                WeaponProperty.Ammunition(
                    WeaponRange(80, 320),
                    Ammunition.Arrow
                ),
                WeaponProperty.TwoHanded(),itemType= ItemMaterial.BOW
            )
            data object Sling : Simple(1.SP, 0.lb,
                Dice.Damage(d4 to DamageType.BLUDGEONING),
                WeaponMastery.SLOW,
                WeaponProperty.Ammunition(
                    WeaponRange(30, 120),
                    Ammunition.SlingBullet
                )
            )
        }

        sealed class Martial(
            cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery,
            vararg properties: WeaponProperty, override val itemType: ItemMaterial = ItemMaterial.CROSSBOW
        ): Ranged(cost, weight, damageDice, properties.toHashSet(), weaponMastery, WeaponProficiency.MARTIAL) {

            sealed class Gun(cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery,
                             vararg properties: WeaponProperty, override val itemType: ItemMaterial = ItemMaterial.GUN
            ): Martial(cost, weight, damageDice, weaponMastery, *properties,itemType= ItemMaterial.GUN){

                data object Blowgun : Gun(10.GP, 1.lb,
                    Dice.Damage(Dice(Dice.Sides.ONE) to DamageType.PIERCING),
                    WeaponMastery.VEX,
                    WeaponProperty.Ammunition(
                        WeaponRange(25, 100),
                        Ammunition.Needle
                    ),
                    WeaponProperty.Loading
                )
                data object Pistol : Gun(250.GP, 3.lb,
                    Dice.Damage(d10 to DamageType.PIERCING),
                    WeaponMastery.VEX,
                    WeaponProperty.Ammunition(
                        WeaponRange(30, 90),
                        Ammunition.FirearmBullet
                    ),
                    WeaponProperty.Loading
                )
                data object Musket : Gun(500.GP, 10.lb,
                    Dice.Damage(d12 to DamageType.PIERCING),
                    WeaponMastery.SLOW,
                    WeaponProperty.Ammunition(
                        WeaponRange(40, 120),
                        Ammunition.FirearmBullet
                    ),
                    WeaponProperty.Loading,
                    WeaponProperty.TwoHanded()
                )
            }

            data object HandCrossbow : Martial(75.GP, 3.lb,
                Dice.Damage(d6 to DamageType.PIERCING),
                WeaponMastery.VEX,
                WeaponProperty.Ammunition(
                    WeaponRange(30, 120),
                    Ammunition.Bolt
                ),
                WeaponProperty.Light,
                WeaponProperty.Loading
            )
            data object HeavyCrossbow : Martial(50.GP, 18.lb,
                Dice.Damage(d10 to DamageType.PIERCING),
                WeaponMastery.PUSH,
                WeaponProperty.Ammunition(
                    WeaponRange(100, 400),
                    Ammunition.Bolt
                ),
                WeaponProperty.Heavy,
                WeaponProperty.Loading,
                WeaponProperty.TwoHanded(),)
            data object Longbow : Martial(50.GP, 2.lb,
                Dice.Damage(d8 to DamageType.PIERCING),
                WeaponMastery.SLOW,
                WeaponProperty.Ammunition(
                    WeaponRange(150, 600),
                    Ammunition.Arrow
                ),
                WeaponProperty.Heavy,
                WeaponProperty.TwoHanded(),itemType= ItemMaterial.BOW
            )
        }
    }
}