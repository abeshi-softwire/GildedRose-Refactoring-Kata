package com.gildedrose

import kotlin.math.max
import kotlin.math.min

class GildedRose(var items: Array<Item>) {

    private val MIN_QUALITY = 0
    private val MAX_QUALITY = 50
    private val AGED_BRIE_NAME = "Aged Brie"
    private val SULFURAS_NAME = "Sulfuras, Hand of Ragnaros"
    private val BACKSTAGE_PASS_NAME = "Backstage passes to a TAFKAL80ETC concert"
    private val CONJURED_NAME = "Conjured "

    private fun addQuality(item : Item, isConjured: Boolean, qualityChange: Int) {
        // Adds quality to item, then ensures MIN_QUALITY <= item.quality <= MAX_QUALITY
        // QualityChange is doubled if it is conjured and quality is decreasing
        // Also doubled if it is past the sell by date
        var updatedQualityChange = qualityChange
        if (isConjured && qualityChange < 0) updatedQualityChange *= 2
        if (item.sellIn < 0) updatedQualityChange *= 2

        item.quality = max(min(item.quality + updatedQualityChange, MAX_QUALITY), MIN_QUALITY)
    }

    data class Name(val itemName: String, val isConjured: Boolean)

    private fun getConjuredAndName(name: String) : Name {
        val isConjured: Boolean
        val itemName: String
        if (name.startsWith(CONJURED_NAME)) {
            isConjured = true
            itemName = name.substring(CONJURED_NAME.length)
        } else {
            isConjured = false
            itemName = name
        }
        return Name(itemName, isConjured)
    }

    fun updateQuality() {
        for (item in items) {

            val (itemName, isConjured) = getConjuredAndName(item.name)

            // Sulfuruas is never changed and should be skipped
            if (itemName == SULFURAS_NAME) continue

            item.sellIn -= 1

            when (itemName) {
                AGED_BRIE_NAME -> addQuality(item, isConjured, 1)
                BACKSTAGE_PASS_NAME ->
                    // The legacy code functionality has been mimicked here
                    // Check the range of sellIn before it has been decremented and update quality accordingly
                    // The range has been adjusted to compensate for sellIn being decremented before
                    when (item.sellIn) {
                        in Int.MIN_VALUE..0 -> item.quality = 0
                        in 0..4 -> addQuality(item, isConjured, 3)
                        in 5..9 -> addQuality(item, isConjured, 2)
                        else -> addQuality(item, isConjured, 1)
                    }
                else -> addQuality(item, isConjured, -1)
            }
        }
    }
}

