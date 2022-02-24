import kr.goldenmine.data.environment.DefaultKoreanEnvironment
import kr.goldenmine.downloader.DaggerMapDownloaderRouterFactory
import kr.goldenmine.util.nameExceptExtension
import kr.goldenmine.util.toFile
import kr.goldenmine.util.unzip
import org.junit.Test
import java.io.File
import java.lang.Thread.sleep
import kotlin.system.exitProcess

class DownloaderTest {
    @Test
    fun testBancho() {
        val argument = "m=0"

        val banchoDownloader = DaggerMapDownloaderRouterFactory.create().router()["bancho"]!!

        val links = banchoDownloader.getDownloadableLinks(argument, false)
        banchoDownloader.downloadAll(links)

        sleep(60000L)

        exitProcess(0)
    }
}