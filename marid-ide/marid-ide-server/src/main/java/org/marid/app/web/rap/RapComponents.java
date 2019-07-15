package org.marid.app.web.rap;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RapComponents {

  @Bean
  public Runnable exitCommand() {
    return () -> RWT.getClient().getService(JavaScriptExecutor.class).execute("document.location.assign('/logout')");
  }
}
