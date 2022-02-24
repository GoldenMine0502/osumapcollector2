package kr.goldenmine.downloader.downloaders

import kr.goldenmine.downloader.MapDownloader
import javax.inject.Inject

class MapDownloaderNerina @Inject constructor(
//    val environment: Environment,
//    val account: Account
) : MapDownloader {

//    private val chromeDriver: WebDriver

    init {
//        chromeDriver = ChromeDriver()
    }

    override fun getRootLink(): String = "https://nerina.pw/"
    override fun getDownloadableLinks(argument: String, network: Boolean): List<String> {
        TODO("Not yet implemented")
    }

    override fun downloadAll(links: List<String>) {
        TODO("Not yet implemented")
    }
}