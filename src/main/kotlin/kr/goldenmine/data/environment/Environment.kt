package kr.goldenmine.data.environment

import java.io.File

interface Environment {
    val downloadPath: String
    val accountFilePath: String
    val chromeDriverPath: String
    val mapListPath: String
    val debug: Boolean

    val textName: String
    val textPassword: String
    val textLogin: String
    val textUnavilable: String
}