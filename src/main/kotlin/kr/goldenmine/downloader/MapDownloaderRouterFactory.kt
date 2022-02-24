package kr.goldenmine.downloader

import dagger.Component
import kr.goldenmine.data.account.AccountModule
import kr.goldenmine.data.environment.EnvironmentModule
import kr.goldenmine.logger.LoggerModule
import javax.inject.Singleton

@Singleton
@Component(modules = [MapDownloaderModule::class, EnvironmentModule::class, AccountModule::class, LoggerModule::class])
interface MapDownloaderRouterFactory {
    fun router(): MapDownloaderRouter
}
