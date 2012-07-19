package ch.bedag.a6z.sipvalidator.service.vo;
/**
 * Ein Value Object, das die "validatedformat" Elemente aus der Konfigurationsdatei einkapselt.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public class ValidatedFormat {
    
    final static String JHOVE = "JHOVE";
    final static String PDFTRON = "PDFTRON";
    
    private String pronomUniqueId;
    private String validator;
    private String extension;
    private String description;
    
    public ValidatedFormat(String pronomUniqueId, String validator, String extension, String description) {
        super();
        this.pronomUniqueId = pronomUniqueId;
        this.validator = validator;
        this.extension = extension;
        this.description = description;
    }
    
    public String getPronomUniqueId() {
        return pronomUniqueId;
    }
    public void setPronomUniqueId(String pronomUniqueId) {
        this.pronomUniqueId = pronomUniqueId;
    }
    public String getValidator() {
        return validator;
    }
    public void setValidator(String validator) {
        this.validator = validator;
    }
    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
}
