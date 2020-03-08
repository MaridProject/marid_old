package org.marid.fx.extensions

import java.nio.file.Path

val Path.url get() = toUri().toURL()