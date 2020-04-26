package com.deax.dev.kotlingradle.listeners

import com.deax.dev.kotlingradle.MainPlugin
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

object MainListener : Listener {
    private val plugin: MainPlugin = MainPlugin.instance;

    @EventHandler
    public fun onBlockPlaceEvent(e: BlockPlaceEvent) {
        with(e) {
            with(player) {
                // Получаем данные игрока из памяти плагина
                val pdata: PlayerData = PlayerData[uniqueId]!!
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

                                // Проходимся по квадрату range*2 + 1
                                loop@ for (i in -pdata.range..pdata.range) {
                                    for (j in -pdata.range..pdata.range) {
                                        if (cused >= camount) break@loop
                                        val blkdown = world.getBlockAt(block.x + i, block.y - 1, block.z + j);
                                        val blkup = world.getBlockAt(block.x + i, block.y, block.z + j)
                                        if (blkdown.type == FARMLAND && blkup.type.isAir) {
                                            blkup.type = cropType
                                            cused += 1;
                                        }
                                    }
                                }
                                // Применяем оставшееся количество семян
                                setItemInMainHand(itemInMainHand.apply { amount = camount - cused })
                            }
                            // Для вспахивания
                            DIAMOND_HOE, GOLDEN_HOE, IRON_HOE, STONE_HOE, WOODEN_HOE -> {
                                // Текущая прочность
                                val durability =
                                    itemInMainHand.type.maxDurability - (itemInMainHand.itemMeta as Damageable).damage
                                // Прочность за 1 использование с учетом зачарования прочности
                                val delt = 1.0 / (itemInMainHand.getEnchantmentLevel(Enchantment.DURABILITY) + 1.0)
                                // Прочность которая потребуется суммарно для этой работы
                                var dmg = delt
                                // Проходимся по квадрату range*2 + 1
                                loop@ for (i in -pdata.range..pdata.range) {
                                    for (j in -pdata.range..pdata.range) {
                                        // Нижний блок
                                        val blkdown = world.getBlockAt(block.x + i, block.y, block.z + j)
                                        // Верхний блок
                                        val blkup = world.getBlockAt(block.x + i, block.y + 1, block.z + j)
                                        // Если верхний блок пустой
                                        if (blkup.type.isAir) {
                                            // Если прочности меньше или равно требуемой мы выходим из цикла
                                            if (durability <= dmg) break@loop
                                            // Определяем наличие блока из подобранных 3 типов земли
                                            when (blkdown.type) {
                                                DIRT, GRASS_BLOCK, GRASS_PATH -> {
                                                    // Превращаем землю в грядку
                                                    blkdown.type = FARMLAND
                                                    // Добавляем требуемую прочность
                                                    dmg += delt
                                                }
                                                else -> {
                                                }
                                            }
                                        }
                                    }
                                }

                                // Задаем прочность предмету или обнуляем
                                setItemInMainHand(itemInMainHand.apply {
                                    // Применяем свойства предмета
                                    itemMeta = ((itemMeta as Damageable).apply {
                                        // Применяем уровень ломанности
                                        damage += Math.ceil(dmg).toInt()
                                        // Если единица ломанности больше максимальной прочности то обнуляем
                                        if (damage >= type.maxDurability) {
                                            type = AIR
                                        }
                                    }) as ItemMeta
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
