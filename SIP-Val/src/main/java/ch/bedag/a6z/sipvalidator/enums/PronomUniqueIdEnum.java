package ch.bedag.a6z.sipvalidator.enums;

public enum PronomUniqueIdEnum implements BaseEnumItemEnum {
    
    JHOVE(101),
    PDFTRON(102);

    private final long id;

    PronomUniqueIdEnum(long id){
        this.id = id;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    
}
