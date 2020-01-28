package org.marid.ide.child.project.exceptions

import java.lang.RuntimeException

class ResolveError(message: String) : Error(message, null, false, false)
class ResolveException : RuntimeException("Resolve error")