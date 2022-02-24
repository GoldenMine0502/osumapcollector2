package kr.goldenmine.util

import java.io.File
import javax.print.attribute.standard.Destination
import java.io.IOException

import java.nio.file.StandardCopyOption

import java.util.zip.ZipEntry

import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path

import java.util.zip.ZipInputStream
import java.io.FileOutputStream





fun String.toFile() = File(this)

val File.nameExceptExtension: String
    get() {
        val index = this.name.lastIndexOf(".")
        return if (index >= 0) {
            this.name.substring(0, index)
        } else {
            this.name
        }
    }

fun File.unzip(destDir: File) {
//    val destDir = File("src/main/resources/unzipTest")
    val buffer = ByteArray(1024)
    val zis = ZipInputStream(FileInputStream(this))
    var zipEntry = zis.nextEntry
    while (zipEntry != null) {
        val newFile: File = newFile(destDir, zipEntry)
        if (zipEntry.isDirectory) {
            if (!newFile.isDirectory && !newFile.mkdirs()) {
                throw IOException("Failed to create directory $newFile")
            }
        } else {
            // fix for Windows-created archives
            val parent = newFile.parentFile
            if (!parent.isDirectory && !parent.mkdirs()) {
                throw IOException("Failed to create directory $parent")
            }

            // write file content
            val fos = FileOutputStream(newFile)
            var len: Int
            while (zis.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
            }
            fos.close()
        }
        zipEntry = zis.nextEntry
    }
    zis.closeEntry()
    zis.close()
}

fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
    val destFile = File(destinationDir, zipEntry.name)
    val destDirPath = destinationDir.canonicalPath
    val destFilePath = destFile.canonicalPath
    if (!destFilePath.startsWith(destDirPath + File.separator)) {
        throw IOException("Entry is outside of the target dir: " + zipEntry.name)
    }
    return destFile
}

//fun File.unzip(destination: File) {
//    try {
//        val destinationPath = destination.toPath()
//        ZipInputStream(FileInputStream(this)).use { zis ->
//            // list files in zip
//            var zipEntry = zis.nextEntry
//            while (zipEntry != null) {
//                val isDirectory = zipEntry.name.endsWith(File.separator)
//                val newPath = zipSlipProtect(zipEntry, destinationPath)
//
//                if (isDirectory) {
//                    Files.createDirectories(newPath)
//                } else {
//                    if (newPath.parent != null) {
//                        if (Files.notExists(newPath.parent)) {
//                            Files.createDirectories(newPath.parent)
//                        }
//                    }
//                    // copy files
//                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING)
//                }
//                zipEntry = zis.nextEntry
//            }
//            zis.closeEntry()
//        }
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//}
//
//fun zipSlipProtect(zipEntry: ZipEntry, targetDir: Path): Path {
//    // test zip slip vulnerability
//    val targetDirResolved = targetDir.resolve(zipEntry.name)
//
//    // make sure normalized file still has targetDir as its prefix
//    // else throws exception
//    val normalizePath = targetDirResolved.normalize()
//    if (!normalizePath.startsWith(targetDir)) {
//        throw IOException("Bad zip entry: " + zipEntry.name)
//    }
//    return normalizePath
//}