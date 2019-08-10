package org.marid.runtime.model;

import java.net.URL;
import java.net.URLClassLoader;

final class RackClassLoader extends URLClassLoader {

  final Deployment deployment;

  RackClassLoader(URL[] urls, Deployment deployment) {
    super(urls, Thread.currentThread().getContextClassLoader());
    this.deployment = deployment;
  }
}
