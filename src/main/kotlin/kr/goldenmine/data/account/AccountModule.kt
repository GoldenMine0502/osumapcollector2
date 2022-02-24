package kr.goldenmine.data.account

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
interface AccountModule {
    @Binds
//    @IntoMap
//    @StringKey("file")
    fun file(file: FileAccount): Account
}