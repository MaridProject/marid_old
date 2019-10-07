module marid.util {

  requires transitive java.logging;

  requires static java.desktop;
  requires static org.jetbrains.annotations;

  exports org.marid.collections;
  exports org.marid.concurrent;
  exports org.marid.image;
  exports org.marid.io;
  exports org.marid.l10n;
  exports org.marid.logging;
  exports org.marid.misc;
  exports org.marid.xml;
}