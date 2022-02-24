package kr.goldenmine.logger

import dagger.Binds
import dagger.Module

@Module
interface LoggerModule {
    @Binds
    fun sysout(loggerSystemOut: LoggerSystemOut): Logger
}