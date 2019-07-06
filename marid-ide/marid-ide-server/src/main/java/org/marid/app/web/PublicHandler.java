package org.marid.app.web;

import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.springframework.stereotype.Component;

@Component
public class PublicHandler extends PathHandler {

  public PublicHandler(AuthHandler authHandler) {
    super(new ResourceHandler(new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "public/"), authHandler));
  }
}
