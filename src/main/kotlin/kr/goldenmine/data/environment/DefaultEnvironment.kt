package kr.goldenmine.data.environment

import java.io.File

abstract class DefaultEnvironment: Environment {
    override val accountFilePath = "D:/osumap/Account.txt"
    override val downloadPath = "D:/osumap/maps/"
    override val chromeDriverPath = "D:/osumap/api/chromedriver.exe"
    override val mapListPath = "D:/osumap/"
    override val debug = true
}