import kr.goldenmine.data.environment.DefaultKoreanEnvironment
import kr.goldenmine.util.*
import org.junit.Test
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class UnzipTest {
    @Test
    fun unzipOsuFiles() {
        val environment = DefaultKoreanEnvironment()

        val completedList = mutableSetOf<Int>()

        val completeFile = File("D:/osumap/completed.txt")
        if(!completeFile.exists()) completeFile.mkdirs()

        completedList.addAll(completeFile.readLines().filter { it.isNotEmpty() }.map { it.toInt() })

        val writer = BufferedWriter(FileWriter(completeFile))

        environment.downloadPath.toFile().listFiles()
            ?.filter { it.name.endsWith("osz") }
            ?.filter { !completedList.contains(it.mapId) }
            ?.forEach {
                println("unzipping: ${it.path}")
                val dest = File("D:/osumap/unzip/${it.nameExceptExtension}")
                dest.mkdirs()

                it.unzip(dest)

                writer.write(it.mapId.toString())
                writer.newLine()
                writer.flush()
            }

        writer.close()
    }
}