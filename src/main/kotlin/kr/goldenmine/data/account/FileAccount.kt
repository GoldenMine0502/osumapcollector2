package kr.goldenmine.data.account

import kr.goldenmine.data.environment.Environment
import java.io.File
import javax.inject.Inject

class FileAccount @Inject constructor(environment: Environment): Account {
    private val fileLines: List<String> = File(environment.accountFilePath).readLines()

    override val id: String
        get() = fileLines[0]
    override val password: String
        get() = fileLines[1]
}