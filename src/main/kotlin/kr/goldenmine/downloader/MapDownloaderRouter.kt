package kr.goldenmine.downloader

import javax.inject.Inject

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
class MapDownloaderRouter @Inject constructor(
    private val downloaders: Map<String, MapDownloader>
    ){

    operator fun get(key: String): MapDownloader? = downloaders[key]
}