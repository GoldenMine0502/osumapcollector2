package kr.goldenmine.util

import org.openqa.selenium.WebElement

@Throws(InterruptedException::class)
fun sendKeys(element: WebElement, str: String, sleep: Int, updown: Int) {
    for (ch in str) {
        element.sendKeys(ch.toString())
        Thread.sleep((sleep + if (updown > 0) getRandom(-updown, updown) else 0).toLong())
    }
}