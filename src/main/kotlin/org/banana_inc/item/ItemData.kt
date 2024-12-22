package org.banana_inc.item

import com.google.common.collect.MutableClassToInstanceMap
import org.banana_inc.extensions.*
import org.banana_inc.item.attributes.Currency
import org.banana_inc.item.attributes.Weight
import org.banana_inc.item.attributes.weapon.*
import org.banana_inc.logger
import org.banana_inc.util.dnd.DamageType
import org.banana_inc.util.dnd.Dice
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.Material
import kotlin.reflect.KClass

@InitOnStartup
sealed class ItemData(
    val cost: Currency,
    val weight: Weight,
){
    open val itemMCType: Material = Material.NETHERITE_HOE
    open val stackAmount = 64
    var name: String
    interface Magical
    sealed class Ammunition(currency: Currency, weight: Weight): ItemData(currency, weight){
        data object Arrow : Ammunition(5.CP, 0.8.oz)
        data object Bolt : Ammunition(5.CP, 1.2.oz)
        data object FirearmBullet : Ammunition(3.SP, .5.lb)
        data object Needle : Ammunition(2.CP, .32.oz)
        data object SlingBullet : Ammunition(4.CP, 1.2.oz)
    }
    sealed class Coin(currency: Currency): ItemData(currency, .32.oz){
        data object CopperPiece : Coin(1.CP)
        data object SilverPiece : Coin(1.SP)
        data object ElectrumPiece : Coin(1.EP)
        data object GoldPiece : Coin(1.GP)
        data object PlatinumPiece : Coin(1.PP)
    }
    sealed class Weapon(cost: Currency, weight: Weight, val damageDice: Dice.Damage, val properties: Set<WeaponProperty>, val weaponMastery: WeaponMastery, val combatType: CombatType, val weaponProficiency: WeaponProficiency): ItemData(cost,weight){
        override val stackAmount = 1
        sealed class Melee(cost: Currency, weight: Weight, damageDice: Dice.Damage, properties: Set<WeaponProperty>, weaponMastery: WeaponMastery, weaponProficiency: WeaponProficiency): Weapon(cost, weight, damageDice, properties, weaponMastery, CombatType.MELEE, weaponProficiency) {
            sealed class Simple(cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery, vararg properties: WeaponProperty) : Melee(cost,weight, damageDice, properties.toHashSet(), weaponMastery, WeaponProficiency.SIMPLE) {
                data object Club : Simple(1.SP, 2.lb, Dice.Damage(Dice(4) to DamageType.BLUDGEONING), WeaponMastery.SLOW, WeaponProperty.Light)
                data object Dagger : Simple(2.GP, 1.lb, Dice.Damage(Dice(4) to DamageType.PIERCING), WeaponMastery.NICK, WeaponProperty.Finesse, WeaponProperty.Light, WeaponProperty.Thrown(WeaponRange(20,60)))
                data object Greatclub : Simple(2.SP, 10.lb, Dice.Damage(Dice( 8) to DamageType.BLUDGEONING), WeaponMastery.PUSH, WeaponProperty.TwoHanded())
                data object Handaxe : Simple(5.GP, 2.lb, Dice.Damage(Dice(6) to DamageType.SLASHING), WeaponMastery.VEX, WeaponProperty.Light, WeaponProperty.Thrown(WeaponRange(20,60)))
                data object Javelin : Simple(5.SP, 2.lb, Dice.Damage(Dice(6) to DamageType.PIERCING), WeaponMastery.SLOW, WeaponProperty.Thrown(WeaponRange(30,120)))
                data object LightHammer : Simple(2.GP, 2.lb, Dice.Damage(Dice(4) to DamageType.BLUDGEONING), WeaponMastery.NICK, WeaponProperty.Light, WeaponProperty.Thrown(WeaponRange(20,60)))
                data object Mace : Simple(5.GP, 4.lb, Dice.Damage(Dice(6) to DamageType.BLUDGEONING), WeaponMastery.SAP)
                data object Quarterstaff : Simple(2.SP, 4.lb, Dice.Damage(Dice(6) to DamageType.BLUDGEONING), WeaponMastery.TOPPLE, WeaponProperty.Versatile(Dice(8)))
                data object Sickle : Simple(1.GP, 2.lb, Dice.Damage(Dice(4) to DamageType.SLASHING), WeaponMastery.NICK, WeaponProperty.Light)
                data object Spear : Simple(1.GP, 3.lb, Dice.Damage(Dice(6) to DamageType.PIERCING), WeaponMastery.SAP, WeaponProperty.Thrown(WeaponRange(20,60)), WeaponProperty.Versatile(Dice(8)))
            }
            sealed class Martial(
                cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery,
                vararg properties: WeaponProperty
            ) : Melee(cost, weight, damageDice, properties.toHashSet(), weaponMastery, WeaponProficiency.MARTIAL) {

                data object Battleaxe : Martial(10.GP, 4.lb, Dice.Damage(Dice(8) to DamageType.SLASHING), WeaponMastery.TOPPLE, WeaponProperty.Versatile(Dice(10)))
                data object Flail : Martial(10.GP, 2.lb, Dice.Damage(Dice(8) to DamageType.BLUDGEONING), WeaponMastery.SAP)
                data object Glaive : Martial(20.GP, 6.lb, Dice.Damage(Dice(10) to DamageType.SLASHING), WeaponMastery.GRAZE, WeaponProperty.Heavy, WeaponProperty.Reach, WeaponProperty.TwoHanded())
                data object Greataxe : Martial(30.GP, 7.lb, Dice.Damage(Dice(12) to DamageType.SLASHING), WeaponMastery.CLEAVE, WeaponProperty.Heavy, WeaponProperty.TwoHanded())
                data object Greatsword : Martial(50.GP, 6.lb, Dice.Damage(Dice(2,6) to DamageType.SLASHING), WeaponMastery.GRAZE, WeaponProperty.Heavy, WeaponProperty.TwoHanded())
                data object Halberd : Martial(20.GP, 6.lb, Dice.Damage(Dice(10) to DamageType.SLASHING), WeaponMastery.CLEAVE, WeaponProperty.Heavy, WeaponProperty.Reach, WeaponProperty.TwoHanded())
                data object Lance : Martial(10.GP, 6.lb, Dice.Damage(Dice(10) to DamageType.PIERCING), WeaponMastery.TOPPLE, WeaponProperty.Heavy, WeaponProperty.Reach, WeaponProperty.TwoHanded(unlessMounted = true))
                data object Longsword : Martial(15.GP, 3.lb, Dice.Damage(Dice(8) to DamageType.SLASHING), WeaponMastery.SAP, WeaponProperty.Versatile(Dice(10)))
                data object Maul : Martial(10.GP, 10.lb, Dice.Damage(Dice(2,6) to DamageType.BLUDGEONING), WeaponMastery.TOPPLE, WeaponProperty.Heavy, WeaponProperty.TwoHanded())
                data object Morningstar : Martial(15.GP, 4.lb, Dice.Damage(Dice(8) to DamageType.PIERCING), WeaponMastery.SAP)
                data object Pike : Martial(5.GP, 18.lb, Dice.Damage(Dice(10) to DamageType.PIERCING), WeaponMastery.PUSH, WeaponProperty.Heavy, WeaponProperty.Reach, WeaponProperty.TwoHanded())
                data object Rapier : Martial(25.GP, 2.lb, Dice.Damage(Dice(8) to DamageType.PIERCING), WeaponMastery.VEX, WeaponProperty.Finesse)
                data object Scimitar : Martial(25.GP, 3.lb, Dice.Damage(Dice(6) to DamageType.SLASHING), WeaponMastery.NICK, WeaponProperty.Finesse, WeaponProperty.Light)
                data object Shortsword : Martial(10.GP, 2.lb, Dice.Damage(Dice(6) to DamageType.PIERCING), WeaponMastery.VEX, WeaponProperty.Finesse, WeaponProperty.Light)
                data object Trident : Martial(5.GP, 4.lb, Dice.Damage(Dice(8) to DamageType.PIERCING), WeaponMastery.TOPPLE, WeaponProperty.Thrown(WeaponRange(20, 60)), WeaponProperty.Versatile(Dice(10)))
                data object Warhammer : Martial(15.GP, 5.lb, Dice.Damage(Dice(8) to DamageType.BLUDGEONING), WeaponMastery.PUSH, WeaponProperty.Versatile(Dice(10)))
                data object WarPick : Martial(5.GP, 2.lb, Dice.Damage(Dice(8) to DamageType.PIERCING), WeaponMastery.SAP, WeaponProperty.Versatile(Dice(10)))
                data object Whip : Martial(2.GP, 3.lb, Dice.Damage(Dice(4) to DamageType.SLASHING), WeaponMastery.SLOW, WeaponProperty.Finesse, WeaponProperty.Reach)
            }
        }
        sealed class Ranged(
            cost: Currency, weight: Weight, damageDice: Dice.Damage, properties: Set<WeaponProperty>,
            weaponMastery: WeaponMastery, weaponProficiency: WeaponProficiency
        ) : Weapon(cost, weight, damageDice, properties, weaponMastery, CombatType.RANGED, weaponProficiency) {

            sealed class Simple(
                cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery,
                vararg properties: WeaponProperty
            ) : Ranged(cost, weight, damageDice, properties.toHashSet(), weaponMastery, WeaponProficiency.SIMPLE) {

                data object Dart : Simple(5.CP, 0.25.lb, Dice.Damage(Dice(4) to DamageType.PIERCING), WeaponMastery.VEX, WeaponProperty.Finesse, WeaponProperty.Thrown(WeaponRange(20, 60)))
                data object LightCrossbow : Simple(25.GP, 5.lb, Dice.Damage(Dice(8) to DamageType.PIERCING), WeaponMastery.SLOW, WeaponProperty.Ammunition(WeaponRange(80, 320), Ammunition.Bolt), WeaponProperty.Loading, WeaponProperty.TwoHanded())
                data object Shortbow : Simple(25.GP, 2.lb, Dice.Damage(Dice(6) to DamageType.PIERCING), WeaponMastery.VEX, WeaponProperty.Ammunition(WeaponRange(80, 320), Ammunition.Arrow), WeaponProperty.TwoHanded())
                data object Sling : Simple(1.SP, 0.lb, Dice.Damage(Dice(4) to DamageType.BLUDGEONING), WeaponMastery.SLOW, WeaponProperty.Ammunition(WeaponRange(30, 120), Ammunition.SlingBullet))
            }

            sealed class Martial(
                cost: Currency, weight: Weight, damageDice: Dice.Damage, weaponMastery: WeaponMastery,
                vararg properties: WeaponProperty
            ): Ranged(cost, weight, damageDice, properties.toHashSet(), weaponMastery, WeaponProficiency.MARTIAL) {
                data object Blowgun : Martial(10.GP, 1.lb, Dice.Damage(Dice(1) to DamageType.PIERCING), WeaponMastery.VEX, WeaponProperty.Ammunition(WeaponRange(25, 100), Ammunition.Needle), WeaponProperty.Loading)
                data object HandCrossbow : Martial(75.GP, 3.lb, Dice.Damage(Dice(6) to DamageType.PIERCING), WeaponMastery.VEX, WeaponProperty.Ammunition(WeaponRange(30, 120), Ammunition.Bolt), WeaponProperty.Light, WeaponProperty.Loading)
                data object HeavyCrossbow : Martial(50.GP, 18.lb, Dice.Damage(Dice(10) to DamageType.PIERCING), WeaponMastery.PUSH, WeaponProperty.Ammunition(WeaponRange(100, 400), Ammunition.Bolt), WeaponProperty.Heavy, WeaponProperty.Loading, WeaponProperty.TwoHanded())
                data object Longbow : Martial(50.GP, 2.lb, Dice.Damage(Dice(8) to DamageType.PIERCING), WeaponMastery.SLOW, WeaponProperty.Ammunition(WeaponRange(150, 600), Ammunition.Arrow), WeaponProperty.Heavy, WeaponProperty.TwoHanded())
                data object Musket : Martial(500.GP, 10.lb, Dice.Damage(Dice(12) to DamageType.PIERCING), WeaponMastery.SLOW, WeaponProperty.Ammunition(WeaponRange(40, 120), Ammunition.FirearmBullet), WeaponProperty.Loading, WeaponProperty.TwoHanded())
                data object Pistol : Martial(250.GP, 3.lb, Dice.Damage(Dice(10) to DamageType.PIERCING), WeaponMastery.VEX, WeaponProperty.Ammunition(WeaponRange(30, 90), Ammunition.FirearmBullet), WeaponProperty.Loading)
            }
        }
    }
    companion object {
        private val items = MutableClassToInstanceMap.create<ItemData>()

        val sortedItemClasses: List<Class<out ItemData>> by lazy {
            items.keys.sortedBy{ it.simpleName }
        }
        val sortedItemsByID: List<ItemData> by lazy {
            items.values.sortedBy { it.id }
        }

        init {
            logger.info("companion called")
        }

        operator fun <T: ItemData> get(id: Class<T>): T {
            return id.cast(items[id] ?: error("No such item: $id"))
        }

        operator fun get(id: Int): ItemData {
            return sortedItemsByID[id]
        }

        fun getAll(): Set<ItemData> {
            return items.values.toSet()
        }

        fun getClasses(): Set<Class<out ItemData>> {
            return items.keys
        }
        fun getKClasses(): Set<KClass<out ItemData>> {
            return getClasses().map{ a -> a.kotlin }.toSet()
        }
    }

    val id: Int
        get() = run { sortedItemClasses.indexOf(this.javaClass) }

    init {
        items[this.javaClass] = this
        this.name = this.javaClass.simpleName.replace(Regex("(?<=[a-zA-Z])(?=[A-Z])"), " ")

    }

    fun <T : ItemData> T.create(): Item<T> = Item(this)

}