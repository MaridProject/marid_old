package org.marid.ide

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ComponentScan(
  excludeFilters = [
    ComponentScan.Filter(type = FilterType.REGEX, pattern = ["org[.]marid[.]ide[.]child[.].+"])
  ]
)
open class IdeContext {

}