package ch.bedag.a6z.sipvalidator.service.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.bedag.a6z.sipvalidator.logging.Logger;
import ch.bedag.a6z.sipvalidator.service.MessageService;

public class MessageServiceImpl implements MessageService {
    
    private static final Logger LOGGER = new Logger(MessageServiceImpl.class);

    
    List<String[]> stack = new LinkedList<String[]>();
    
    @Override
    public void logDebug(String message) {
        this.stack.add(new String[]{MessageService.DEBUG, message});
    }

    @Override
    public void logError(String message) {
        this.stack.add(new String[]{MessageService.ERROR, message});
    }

    @Override
    public void logFatal(String message) {
        this.stack.add(new String[]{MessageService.FATAL, message});
    }

    @Override
    public void logInfo(String message) {
        this.stack.add(new String[]{MessageService.INFO, message});
    }

    @Override
    public void logWarning(String message) {
        this.stack.add(new String[]{MessageService.WARN, message});
    }

    @Override
    public void clear() {
        this.stack.removeAll(stack);
    }

    @Override
    public void print() {
        Iterator<String[]> it = this.stack.iterator();
        while (it.hasNext()) {
            String[] message = (String[]) it.next();
            if (message[0].equals(MessageService.DEBUG)) {
                LOGGER.logDebug(message[1]);
            }
            if (message[0].equals(MessageService.ERROR)) {
                LOGGER.logError(message[1]);
            }
            if (message[0].equals(MessageService.FATAL)) {
                LOGGER.logFatal(message[1]);
            }
            if (message[0].equals(MessageService.INFO)) {
                LOGGER.logInfo(message[1]);
            }
            if (message[0].equals(MessageService.WARN)) {
                LOGGER.logWarning(message[1]);
            }
        }
        
        this.clear();
    
    }
   
}
