package pollbus.idgen.barflake;

import pollbus.idgen.IdGeneratorSync;


public class BarflakeGenerator implements IdGeneratorSync {
    
  
	private static final int SEQUENCE_BITS = 12;
	private static final int SEQ_MAX = 4095;
	
	private static final int WORKER_BITS = 5;
	private static final int DATACENTER_BITS = 5;
	
	static final long EPOCH_DEFAULT = 1456530296738L;
    // private final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
    static final int LSHIFT_WORKER = SEQUENCE_BITS;
    static final int LSHIFT_DATACENTER = SEQUENCE_BITS + WORKER_BITS;
    static final int LSHIFT_TIMESTAMP = SEQUENCE_BITS + WORKER_BITS + DATACENTER_BITS;

	
	// runtime config
	private final CurrentTimeMillisProvider timer;
    private final int datacenterId;
    private final int workerId;
	
    // state ===================================================================
	private long timestamp = -1L;
	private long sequence = 0L;
	// =========================================================================
	
	/** */
	public BarflakeGenerator(
	    CurrentTimeMillisProvider timer, 
	    int datacenterId, 
	    int workerId) {		
	  
		this.timer = timer;
		this.datacenterId = datacenterId;
		this.workerId = workerId;
	}
	
	public BarflakeGenerator(int datacenterId, int workerId) {
		this(new DefaultEpochTimeProvider(), datacenterId, workerId);
	}
	
	
	@Override
	public long next() throws InvalidSystemClock {	
		updateState();
		return encodeState();
	}

	private void updateState() throws InvalidSystemClock {      
		
	    long currentTime = this.timer.currentAppTime();	
	    
		if (currentTime == this.timestamp) {		  
		    sequence++;
		    if(sequence > SEQ_MAX) {
		      throw new IllegalStateException("Too many ids created for this millisecond");
		    }
		}	
		
		else if (currentTime > this.timestamp) {
			this.timestamp = currentTime;
			this.sequence = 1L;
		}
		
        else if(currentTime < this.timestamp) {
          throw new InvalidSystemClock("current time is before last used timestamp!");    
        }
		
	}
	
    private long encodeState() {
        
        long value = ((this.timestamp) << LSHIFT_TIMESTAMP) | 
           (this.datacenterId << LSHIFT_DATACENTER) | 
           (this.workerId << LSHIFT_WORKER) |
           (sequence /*& SEQUENCE_MASK */);
       
        return value;       
    }
	
    
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [datacenterId=" + datacenterId + ", workerId=" + workerId + "]";
	}
	
	private static final class DefaultEpochTimeProvider implements CurrentTimeMillisProvider {		
		@Override
		public long currentAppTime() {
			return System.currentTimeMillis() - EPOCH_DEFAULT;
		}
	}

}
