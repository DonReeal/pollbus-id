package pollbus.idgen.barflake;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PollbusIdGeneratorTest {

	private BarflakeGenerator idGenerator;

	private static final int DATACENTER_ID_ZERO = 0;
	private static final int WORKER_ID_ZERO = 0;	
	private static final TimeFreezingMillisProvider CURRENT_TIME_FROZEN_AT_0 = new TimeFreezingMillisProvider(0L);
	
	   
    @Test
    public void encodedTimestampIsMappingOfTimeProvided() {
        
        idGenerator = new BarflakeGenerator(new TimeFreezingMillisProvider(1L), DATACENTER_ID_ZERO, WORKER_ID_ZERO);
        long id1 = idGenerator.next();
        assertThat(BarflakeDecoder.decodeTimestamp(id1, 0), is(1L));

        
        idGenerator = new BarflakeGenerator(new TimeFreezingMillisProvider(2L), DATACENTER_ID_ZERO, WORKER_ID_ZERO);
        long id2 = idGenerator.next();
        assertThat(BarflakeDecoder.decodeTimestamp(id2, 0), is(2L));
        
    }
    
    
    @Test
    public void firstSequenceGeneratedIsOne() {
      
        idGenerator = new BarflakeGenerator(
            CURRENT_TIME_FROZEN_AT_0, 
            DATACENTER_ID_ZERO, 
            WORKER_ID_ZERO);
        
        long firstValue = idGenerator.next();
        
        assertThat(BarflakeDecoder.decodeSequence(firstValue), is(1));
        
    }
    
    
    @Test
    public void sequenceIteratesOneByOne() {
      
        idGenerator = new BarflakeGenerator(
            CURRENT_TIME_FROZEN_AT_0, 
            DATACENTER_ID_ZERO, 
            WORKER_ID_ZERO);       
        
        long first = idGenerator.next();
        assertThat(idGenerator.next(), is(first + 1));
        assertThat(idGenerator.next(), is(first + 2));
        assertThat(idGenerator.next(), is(first + 3));
    }
	
	
	@Test
	public void encodedWorkerIsMappingOfConfiguredValue() {
		
		idGenerator = new BarflakeGenerator(CURRENT_TIME_FROZEN_AT_0, DATACENTER_ID_ZERO, 1);
		// 00000 00001 000000000001
		long id_worker1 = idGenerator.next();
		assertThat(BarflakeDecoder.decodeWorker(id_worker1), is(1));

		
		idGenerator = new BarflakeGenerator(CURRENT_TIME_FROZEN_AT_0, DATACENTER_ID_ZERO, 2);
		// 00000 00010 000000000001
		long id_worker2 = idGenerator.next();		
		assertThat(BarflakeDecoder.decodeWorker(id_worker2), is(2));

	}

	
	@Test 
	public void encodedDatacenterIsMappingOfConfiguredValue() {
		
		idGenerator = new BarflakeGenerator(CURRENT_TIME_FROZEN_AT_0, 1, WORKER_ID_ZERO);
		long id_datacenter1 = idGenerator.next();
		assertThat(BarflakeDecoder.decodeDataCenter(id_datacenter1), is(1));

		idGenerator = new BarflakeGenerator(CURRENT_TIME_FROZEN_AT_0, 2, WORKER_ID_ZERO);
		long id_datacenter2 = idGenerator.next();
		assertThat(BarflakeDecoder.decodeDataCenter(id_datacenter2), is(2));
	}
	
	
	
	private static final class TimeFreezingMillisProvider implements CurrentTimeMillisProvider {
		
	    public TimeFreezingMillisProvider(long initialValue) {
			CURRENT_TIME__MILLIS_FROZEN = initialValue;
		}		
		
	    private final long CURRENT_TIME__MILLIS_FROZEN;
		
	    @Override
		public long currentAppTime() {
			return CURRENT_TIME__MILLIS_FROZEN;
		}
	};

}
