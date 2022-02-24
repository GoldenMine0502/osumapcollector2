package kr.goldenmine.data.environment

import dagger.Binds
import dagger.Module

@Module
interface EnvironmentModule {
    @Binds
    fun defaultKoreanEnvironment(defaultEnvironment: DefaultKoreanEnvironment): Environment
}