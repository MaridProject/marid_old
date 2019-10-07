import org.marid.processors.CheckedFunctionalInterfaceProcessor;

import javax.annotation.processing.Processor;

module marid.processors {

  requires transitive java.compiler;
  requires static org.jetbrains.annotations;

  exports org.marid.processors;

  provides Processor with CheckedFunctionalInterfaceProcessor;
}