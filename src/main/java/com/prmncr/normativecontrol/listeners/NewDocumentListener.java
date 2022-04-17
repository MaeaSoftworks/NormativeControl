package com.prmncr.normativecontrol.listeners;

import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.services.DocumentHandler;
import com.prmncr.normativecontrol.services.DocumentQueue;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NewDocumentListener {
    private final DocumentQueue storage;
    private final DocumentHandler handler;

    @Async
    @EventListener
    public void handleDocument(NewDocumentEvent event) {
        val document = storage.getById(event.getDocumentId());
        assert document != null;
        document.setState(State.PROCESSING);
        handler.handle(document);
        document.setState(State.READY);
    }
}
