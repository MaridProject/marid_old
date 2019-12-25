package org.marid.ide

import org.marid.spring.beans.InternalBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(
  excludeFilters = [
    ComponentScan.Filter(type = FilterType.REGEX, pattern = ["org[.]marid[.]ide[.]child[.].+"])
  ]
)
@Import(InternalBean::class)
open class IdeContext {

}