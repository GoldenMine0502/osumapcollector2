package kr.goldenmine.downloader

interface MapDownloader {
    fun getRootLink(): String
    fun getDownloadableLinks(argument: String, network: Boolean): List<String>
    fun downloadAll(links: List<String>)
}