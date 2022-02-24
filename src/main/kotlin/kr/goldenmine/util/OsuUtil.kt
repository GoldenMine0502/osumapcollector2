package kr.goldenmine.util

import java.io.File

val File.mapId: Int
    get() = name.substring(0, name.indexOf(" ")).toInt()