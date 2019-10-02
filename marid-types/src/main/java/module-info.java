import org.marid.types.VarianceProvider;

/**
 * Advanced generic type processing utilities: {@link org.marid.types}.
 */
module marid.types {

  requires static org.jetbrains.annotations;
  requires static jdk.dynalink;

  exports org.marid.types;

  uses VarianceProvider;
}