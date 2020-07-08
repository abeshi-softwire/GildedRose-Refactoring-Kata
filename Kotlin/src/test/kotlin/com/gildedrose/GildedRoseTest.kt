package com.gildedrose

import org.junit.Assert.*
import org.junit.Test

class GildedRoseTest {

    private val aged_brie_name = "Aged Brie"
    private val sulfuras_name = "Sulfuras, Hand of Ragnaros"
    private val backstage_pass_name = "Backstage passes to a TAFKAL80ETC concert"

    private fun GetApp(vararg items : Item): GildedRose {
        return GildedRose(arrayOf(*items))
    }

    @Test
    fun quality_never_negative() {
        val app = GetApp(Item("foo", 0, 0))
        app.updateQuality()
        assertTrue("Quality can't be negative", app.items.all { it.quality >= 0 })
    }

    @Test
    fun quality_decreases_over_time() {
        val startQuality = 10
        val app = GetApp(Item("foo", 10, startQuality))
        app.updateQuality()
        assertTrue("Quality must decrease", startQuality > app.items[0].quality)

    }

    @Test
    fun sellin_negative_doubles_quality_loss() {
        val startQuality = 10
        val app = GetApp(Item("foo", 1, startQuality))
        app.updateQuality()
        val secondQuality = app.items[0].quality
        app.updateQuality()
        val finalQuality = app.items[0].quality

        val diff1 = startQuality - secondQuality
        val diff2 = secondQuality - finalQuality

        assertTrue(diff1 * 2 == diff2) // Quality loss should double
        // Using "assertTrue" rather than "assertEquals" because we don't know the "actual" value --
        // we're just testing to make sure the values are equal.
    }

    @Test
    fun sellin_lowers_daily() {
        val startSellin = 1
        val app = GetApp(Item("foo", startSellin, 10))
        app.updateQuality()
        val secondSellin = app.items[0].sellIn
        app.updateQuality()
        val finalSellin = app.items[0].sellIn

        assertEquals(startSellin - 1, secondSellin)
        assertEquals(secondSellin - 1, finalSellin)
    }

    @Test
    fun aged_brie_improves() {
        val startQuality = 10
        val app = GetApp(Item(aged_brie_name, 50, startQuality))

        app.updateQuality()
        assertTrue("Quality of $aged_brie_name should increase",
                app.items[0].quality > startQuality)
    }

    @Test
    fun quality_never_above_50() {
        val startQualities = arrayOf(49, 50)
        val startItems = startQualities.map {Item(aged_brie_name, 50, quality = it)}
        val app = GetApp(*startItems.toTypedArray())

        app.updateQuality()
        assertTrue("Quality should be <= 50", app.items.all {it.quality <= 50})
    }

    @Test
    fun sulfuras_never_changes() {
        val startQuality = 25
        val startSellin = 3

        val app = GetApp(Item(sulfuras_name, startSellin, startQuality))

        app.updateQuality()
        assertEquals(startQuality, app.items[0].quality);
        assertEquals(startSellin, app.items[0].sellIn);
    }

    @Test
    fun backstage_passes_improve() {
        val startQuality = 10
        val app = GetApp(Item(backstage_pass_name, 50, startQuality))

        app.updateQuality()
        assertTrue("Quality of ${app.items[0].name} should increase",
                app.items[0].quality > startQuality)
    }

    @Test
    fun backstage_passes_improve_more_as_deadline_approaches() {
        val startQuality = 10
        val app = GetApp(
                Item(backstage_pass_name, 10, startQuality),
                Item(backstage_pass_name, 5, startQuality),
                Item(backstage_pass_name, 0, startQuality))
        app.updateQuality()
        assertTrue("Quality of $backstage_pass_name should increase by 2 with <=10 days left",
                app.items[0].quality == startQuality + 2)
        assertTrue("Quality of $backstage_pass_name should increase by 3 with <=10 days left",
                app.items[1].quality == startQuality + 3)
        assertTrue("Quality of $backstage_pass_name should drop to 0 after concert",
                app.items[2].quality == 0)
    }

    @Test
    fun backstage_passes_quality_zero_past_sellby() {
        val startQuality = 10
        val app = GetApp(Item(backstage_pass_name, 0, startQuality))
        app.updateQuality()
        assertTrue("Quality of $backstage_pass_name should drop to 0 after concert",
                app.items[0].quality == 0)
    }

    @Test
    fun conjured_items_degrade_twice_as_fast() {
        val startQuality = 10
        // Only want degrading itemNames
        for (itemName in arrayOf("foo", sulfuras_name)) {
            val app = GetApp(
                    Item("Conjured $itemName", 10, startQuality),
                    Item(itemName, 10, startQuality)
            )

            app.updateQuality()
            val diffConjured = startQuality - app.items[0].quality
            val diffNormal = startQuality - app.items[1].quality
            assertTrue("Conjured items should degrade twice as fast as normal items",
                    diffConjured == 2 * diffNormal)
        }
    }
}


