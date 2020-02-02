@file:Suppress("UnstableApiUsage")

package org.marid.fx.extensions

import com.google.common.io.MoreFiles
import com.google.common.io.RecursiveDeleteOption
import java.nio.file.Path

fun Path.deleteDirectoryContents(vararg options: RecursiveDeleteOption) = MoreFiles.deleteDirectoryContents(this, *options)
fun Path.deleteDirectory(vararg options: RecursiveDeleteOption) = MoreFiles.deleteRecursively(this, *options)