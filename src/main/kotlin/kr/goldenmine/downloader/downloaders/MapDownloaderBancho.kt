package kr.goldenmine.downloader.downloaders

import kr.goldenmine.data.account.Account
import kr.goldenmine.data.environment.Environment
import kr.goldenmine.downloader.MapDownloader
import kr.goldenmine.logger.Logger
import kr.goldenmine.util.getRandom
import kr.goldenmine.util.sendKeys
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import java.lang.StringBuilder
import java.lang.Thread.sleep
import javax.inject.Inject
import org.openqa.selenium.By
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import org.openqa.selenium.WebDriver

import org.openqa.selenium.chrome.ChromeOptions

import java.util.HashMap


const val STOP_SEARCHING_ON_SAME = 100
const val SLEEP_AFTER_DOWNLOAD_A_MAP = 4000L

class MapDownloaderBancho @Inject constructor(
    private val environment: Environment,
    private val account: Account,
    private val logger: Logger
) : MapDownloader {

    private val driver: WebDriver

    init {
        System.setProperty("webdriver.chrome.driver", File(environment.chromeDriverPath).absolutePath)

        val prefs: MutableMap<String, Any> = HashMap()
        prefs["download.default_directory"] = File(environment.downloadPath).absolutePath

        val options = ChromeOptions()
        options.setExperimentalOption("prefs", prefs)

        // 디버그 꺼져있을 때 화면을 표시하지 않음
        if (!environment.debug)
            options.addArguments("--headless")

        driver = ChromeDriver(options)

        Runtime.getRuntime().addShutdownHook(Thread { driver.quit() })
    }

    override fun getRootLink(): String = "https://osu.ppy.sh"

    override fun getDownloadableLinks(argument: String, network: Boolean): List<String> {
        deleteChromeDownloadFile()

        val alreadyDownloadedMaps = getMapIdSet(argument).toMutableSet()
        val maps = mutableSetOf<Int>()

        driver[getRootLink()]
        login()

        if (network) {
            driver["${getRootLink()}/beatmapsets?$argument"]
            driver.manage().window().size = Dimension(1920, 1080)

            logger.log(alreadyDownloadedMaps.joinToString { it.toString() })
            logger.log("이미 다운로드 된 맵 갯수: ${maps.size}")

            val elementOsuFileList = driver.findElement(By.className("osu-layout__row"))
                .findElement(By.className("beatmapsets__content"))

            val elementBoxInfo = elementOsuFileList.findElement(By.tagName("div"))

            logger.log("맵 리스트 찾기 시작")

            var lastPaddingTop = 0
            var equalTime = 0

            while (equalTime <= STOP_SEARCHING_ON_SAME) {
                val currentPaddingTop = getPaddingTopLen(elementBoxInfo.getAttribute("style"))
                if (lastPaddingTop == currentPaddingTop) equalTime++ else equalTime = 0

                scroll(100, 350)
                logger.log("scroll")
                sleep(50L)

                try {
                    driver.findElements(By.className("beatmapsets__item"))
                        .map { it.findElement(By.tagName("a")) }
                        .filterNotNull()
                        .map {
                            val link = it.getAttribute("href")

                            link.substring(link.lastIndexOf("/") + 1, link.length).toInt()
                        }.forEach {
                            if (!maps.contains(it)) {
                                maps.add(it)
                                equalTime = 0
                                logger.log("added $it, alreadyDownloaded: ${alreadyDownloadedMaps.contains(it)}")
                            }
                        }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    logger.log("error, sleeping 1s")
                    sleep(1000L)
                }

                lastPaddingTop = currentPaddingTop
            }

        }

        maps.addAll(alreadyDownloadedMaps)
        saveMapIdSet(maps, argument)

        val excludedMaps = getAlreadyDownloadedMaps()

        logger.log("제외된 맵: ${excludedMaps.joinToString()}")
        logger.log("제외된 맵 갯수: ${excludedMaps.size}(${maps.size})")

        maps.removeAll(excludedMaps)

        logger.log("다운로드할 맵: ${maps.joinToString()}")
        logger.log("현재 맵 갯수: ${maps.size}")

        return maps.sortedDescending().map { "https://osu.ppy.sh/beatmapsets/$it" }.toList()
    }

    private fun deleteChromeDownloadFile() {
        val folder = File(environment.downloadPath)

        val set = if (folder.exists())
            folder.listFiles()
                ?.filter { it.name.endsWith(".crdownload") || it.name.endsWith(" (1).osz") }
                ?.toSet()
            else null

        set?.forEach {
            logger.log("${it.name} 제거됨.")
            it.delete()
        }
    }

    private fun getAlreadyDownloadedMaps(): Set<Int> {
        val folder = File(environment.downloadPath)

        logger.log("route: ${folder.path}")

        val set = if (folder.exists())
            folder.listFiles()
                ?.filter { it.name.endsWith(".osz") }
                ?.map { it.name.split(" ")[0].toInt() }
                ?.toSet()
        else null

        return set ?: setOf()
    }

    override fun downloadAll(links: List<String>) {
        val iterator = links.listIterator()

        while (iterator.hasNext()) {
            val link = iterator.next()
            driver[link]
            logger.log("다운로드: $link")

            sleep(1000L)

            val downloadButton =
                driver.findElements(By.className("btn-osu-big__text-top")).firstOrNull { it.text == "다운로드" }

            sleep(1000L)

            if (downloadButton != null) {
                downloadButton.click()
            } else {
                logger.log("다운로드 버튼을 찾지 못하여 재시도.")
                iterator.previous()
            }

            if (checkUnavailable()) {
                logger.log("요청 거부로 인한 2분 대기")
                iterator.previous()
                sleep(2 * 60 * 1000L)
            }

            sleep(SLEEP_AFTER_DOWNLOAD_A_MAP)
        }

        logger.log("다운로드 완료.")
    }

    private fun scroll(minimumPx: Int, maximumPx: Int) {
        val jsExecutor = driver as JavascriptExecutor
        jsExecutor.executeScript("window.scrollBy(0, ${getRandom(minimumPx, maximumPx)})")
    }

    private fun login() {
        sleep(5000L)

        val formOpenButton = driver.findElements(By.className("js-user-login--menu")).firstOrNull()

        val formLoginId = driver.findElements(By.className("login-box__form-input"))
            .firstOrNull { it.getAttribute("placeholder") == environment.textName }
        val formLoginPassword = driver.findElements(By.className("login-box__form-input"))
            .firstOrNull { it.getAttribute("placeholder") == environment.textPassword }
        val formLoginButton = driver.findElements(By.className("login-box__action"))
            .firstOrNull()
//            .firstOrNull { it.text.contains(environment.textLogin) }
        sleep(1000L)

        if (formLoginId == null || formLoginPassword == null || formLoginButton == null)
            throw NotFoundException("loginId: ${formLoginId != null}, loginPassword: ${formLoginPassword != null}, loginButton: ${formLoginButton != null}")

        // 이게 null이면 formLoginId에 텍스트 입력을 못해서 에러가 터짐
        if (formOpenButton != null) {
            formOpenButton.click()
            sleep(1000L)
        }

        sendKeys(formLoginId, account.id, 500, 100)
        sleep(1000L)

        sendKeys(formLoginPassword, account.password, 500, 100)
        sleep(1000L)

        formLoginButton.click()
        sleep(3000L)
    }

    private fun checkUnavailable() =
        driver.findElements(By.tagName("h1")).any { it.text.contains(environment.textUnavilable) }

    private fun getMapIdSet(argument: String): Set<Int> {
        val file = File("${environment.mapListPath}maps$argument.txt")

        logger.log("map route: ${file.path}")

        if (!file.exists())
            file.createNewFile()

        return file.readLines().filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
    }

    private fun saveMapIdSet(set: Set<Int>, argument: String) {
        val file = File("${environment.mapListPath}maps$argument.txt")
        val writer = BufferedWriter(FileWriter(file))

        set.toList().sorted().forEach {
            println("saving: $it")
            writer.write(it.toString())
            writer.newLine()
        }
        writer.close()
    }

    private fun getPaddingTopLen(style: String): Int {
        //System.out.println(style);
        val list = style.split(";[ |]").toTypedArray()
        list[list.size - 1] = list[list.size - 1].substring(0, list[list.size - 1].length - 1)
        for (s in list) {
            //System.out.println(s + ", " + list[list.length-1]);
            val keyValue = s.split(":[ |]").toTypedArray()
            if (keyValue[0] == "padding-top") {
                return parseIntNoChar(keyValue[1])
            }
        }
        return -1
    }

    private fun parseIntNoChar(data: String): Int {
        data.filter { it in '0'..'9' }.toInt()
        val sb = StringBuilder()
        for (element in data) {
            if (element in '0'..'9') {
                sb.append(element)
            }
        }
        return sb.toString().toInt()
    }
}