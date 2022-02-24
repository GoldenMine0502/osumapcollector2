package kr.goldenmine.logger

import javax.inject.Inject

class LoggerSystemOut @Inject constructor(): Logger {
    override fun log(data: Any?) {
        println(data)
    }
}