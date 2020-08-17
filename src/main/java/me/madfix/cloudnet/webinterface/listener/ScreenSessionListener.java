package me.madfix.cloudnet.webinterface.listener;

import me.madfix.cloudnet.webinterface.WebInterface;
import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnetcore.api.event.server.ScreenInfoEvent;
import java.util.ArrayList;

public final class ScreenSessionListener implements IEventListener<ScreenInfoEvent> {

  private final WebInterface webInterface;

  public ScreenSessionListener(WebInterface webInterface) {
    this.webInterface = webInterface;
  }

  @Override
  public void onCall(ScreenInfoEvent screenInfoEvent) {
    screenInfoEvent.getScreenInfos().forEach(t -> {
      if (this.webInterface.getScreenInfos().containsKey(t.getServiceId().getServerId())) {
        this.webInterface.getScreenInfos().get(t.getServiceId().getServerId()).add(t.getLine());
      } else {
        this.webInterface.getScreenInfos().put(t.getServiceId().getServerId(), new ArrayList<>());
        this.webInterface.getScreenInfos().get(t.getServiceId().getServerId()).add(t.getLine());
      }
    });
  }
}