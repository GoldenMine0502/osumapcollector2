package kr.goldenmine.downloader;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import kr.goldenmine.downloader.downloaders.MapDownloaderBancho;
import kr.goldenmine.downloader.downloaders.MapDownloaderNerina;

@Module
interface MapDownloaderModule {
    @Binds
    @IntoMap
    @StringKey("bancho")
    MapDownloader bancho(MapDownloaderBancho bancho);


    @Binds
    @IntoMap
    @StringKey("nerina")
    MapDownloader nerina(MapDownloaderNerina bancho);
}
