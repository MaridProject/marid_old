import org.marid.processors.CheckedFunctionalInterfaceProcessor;
import org.marid.processors.GenerateHelperProcessor;

import javax.annotation.processing.Processor;

module marid.processors {

  requires java.compiler;
  requires jdk.compiler;
  requires jdk.unsupported;

  exports org.marid.processors;

  provides Processor with
      CheckedFunctionalInterfaceProcessor,
      GenerateHelperProcessor;
}