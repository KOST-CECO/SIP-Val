package ch.bedag.a6z.sipvalidator.validation;

import ch.bedag.a6z.sipvalidator.service.MessageService;
import ch.bedag.a6z.sipvalidator.service.TextResourceService;

public abstract class ValidationModuleImpl {
    
    protected final String UNZIPDIRECTORY = "unzipped";
    protected final String METADATA = "metadata.xml";
    protected final String XSD_ARELDA = "arelda_v3.13.2.xsd";
    
    private TextResourceService textResourceService;
    private MessageService messageService;

    public TextResourceService getTextResourceService() {
        return textResourceService;
    }

    public void setTextResourceService(TextResourceService textResourceService) {
        this.textResourceService = textResourceService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public MessageService getMessageService() {
        return messageService;
    }


}
