package com.deax.dev.kotlingradle.listeners

import com.deax.dev.kotlingradle.MainPlugin
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import kotlin.math.ceil

// Функция нанесения разрушение инструмента
fun ItemStack.damageDurability(dmg: Int) {
    itemMeta = (itemMeta as Damageable).apply {
        val k = getEnchantmentLevel(Enchantment.DURABILITY) + 1
        damage += ceil(dmg.toDouble() / k.toDouble()).toInt()
        if (type.maxDurability <= damage) {
            type = AIR
        }
    } as ItemMeta
}

// Функция определения оставшейся прочности у предмета
fun ItemStack.durability() = (type.maxDurability - (itemMeta as Damageable).damage) * (getEnchantmentLevel(
    Enchantment.DURABILITY
) + 1)

inline fun matrixLoop(range: Int, qux: (i: Int, j: Int) -> Boolean) {
    loop1@ for (i in -range..range) {
        for (j in -range..range) {
            when (qux(i, j)) {
                true -> break@loop1
                false -> {
                }
            }
        }
    }
}

object MainListener : Listener {
    private val plugin: MainPlugin = MainPlugin.instance;

    @EventHandler
    public fun onBlockPlaceEvent(e: BlockPlaceEvent) {
        with(e) {
            // Получаем данные игрока из памяти плагина
            val pdata = PlayerData[player]
            with(player) {
                // Включен ли режим посадки или засева у игрока?
                if (pdata.multiplaceEnabled) {
                    with(inventory) {
                        // Определяем действие игрока
                        when (itemInMainHand.type) {
                            // Для засева
                            WHEAT_SEEDS, PUMPKIN_SEEDS, MELON_SEEDS, BEETROOT_SEEDS, CARROT, POTATO -> {
                                // Получаем тип растения в виде блока из предмета семени
                                val cropType: Material = run {
                                    when (itemInMainHand.type) {
                                        WHEAT_SEEDS -> WHEAT
                                        PUMPKIN_SEEDS -> PUMPKIN_STEM
                                        MELON_SEEDS -> MELON_STEM
                                        BEETROOT_SEEDS -> BEETROOTS
                                        POTATO -> POTATOES
                                        CARROT -> CARROTS
                                        else -> AIR
                                    }
                                }
                                // Семена которые есть у игрока
                                val camount = itemInMainHand.amount - 1
                                // Требуемое количество семян
                                var cused = 0

                                matrixLoop(pdata.range) { i, j ->
                                    // Заканчиваем цикл когда семян становится маловато
                                    if (cused >= camount) return@matrixLoop true
                                    // Нижний блок
                                    val blkdown = world.getBlockAt(block.x + i, block.y - 1, block.z + j)
                                    // Верхний блок
                                    val blkup = world.getBlockAt(block.x + i, block.y, block.z + j)
                                    // Проверка того что внизу вспаханная земля, а сверху воздух
                                    if (blkdown.type == FARMLAND && blkup.type.isAir) {
                                        // Засеиваем верхний блок
                                        blkup.type = cropType
                                        // Добавляем использованные семена
                                        cused += 1;
                                    }
                                    // Продолжаем цикл
                                    return@matrixLoop false
                                }
                                // Применяем оставшееся количество семян
                                setItemInMainHand(itemInMainHand.apply { amount = camount - cused })
                            }
                            // Для вспахивания
                            DIAMOND_HOE, GOLDEN_HOE, IRON_HOE, STONE_HOE, WOODEN_HOE -> {
                                // Текущая прочность
                                val durability =
                                    itemInMainHand.durability()
                                // Прочность которая потребуется суммарно для этой работы
                                var dmg = 1
                                // Проходимся по квадрату range*2 + 1
                                matrixLoop(pdata.range) { i, j ->
                                    // Нижний блок
                                    val blkdown = world.getBlockAt(block.x + i, block.y, block.z + j)
                                    // Верхний блок
                                    val blkup = world.getBlockAt(block.x + i, block.y + 1, block.z + j)
                                    // Если верхний блок пустой
                                    if (blkup.type.isAir) {
                                        // Если прочности меньше или равно требуемой мы выходим из цикла
                                        if (durability <= dmg) return@matrixLoop true
                                        // Определяем наличие блока из подобранных 3 типов земли
                                        when (blkdown.type) {
                                            DIRT, GRASS_BLOCK, GRASS_PATH -> {
                                                // Превращаем землю в грядку
                                                blkdown.type = FARMLAND
                                                // Добавляем требуемую прочность
                                                dmg++
                                            }
                                            else -> {
                                            }
                                        }
                                    }
                                    // Продолжаем цикл
                                    return@matrixLoop false
                                }
                                // Наносим разрушение инструменту
                                setItemInMainHand(itemInMainHand.apply {
                                    damageDurability(dmg)
                                })
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        }
    }
}
