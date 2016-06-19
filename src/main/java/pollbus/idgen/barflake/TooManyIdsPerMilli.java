package pollbus.idgen.barflake;

public class TooManyIdsPerMilli extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  private static final String MSG = "Cannot create more than 4096 ids per millisecond. Resulting ids would not be unique!";
  
  public TooManyIdsPerMilli() {
    super(MSG);
  }

}
