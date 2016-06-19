package pollbus.idgen.barflake;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PollbusIdGeneratorTest {

	private BarflakeGenerator idGenerator;

	// ================================================================================
	// using defaults
	private static final int DATACENTER_ID_ZERO = 0;
	private static final int WORKER_ID_ZERO = 0;	
	private TimeFreezingMillisProvider timeConstZero = new TimeFreezingMillisProvider(0L);
	
	@Test
	public void initialSequenceShouldStartWithBinary1() {		
		idGenerator = new BarflakeGenerator(timeConstZero, DATACENTER_ID_ZERO, WORKER_ID_ZERO);
		long firstValue = idGenerator.next();
		assertThat(BarflakeDecoder.decodeSequence(firstValue), is(1));
	}
	
	
	@Test
	public void decodeWorker() {
	  
      idGenerator = new BarflakeGenerator(new TimeFreezingMillisProvider(0L), 1, 1);
      
      long _1 = idGenerator.next();
	  long _2 = idGenerator.next();
	  printOneTwo("snowflake", _1, _2);
	  
	  int seq_1 = BarflakeDecoder.decodeSequence(_1);
	  int seq_2 = BarflakeDecoder.decodeSequence(_2);
	  printOneTwo("sequence", seq_1, seq_2);
	  
	  int worker_1 = BarflakeDecoder.decodeWorker(_1);
	  int worker_2 = BarflakeDecoder.decodeWorker(_2);
	  printOneTwo("worker", worker_1, worker_2);
	  
	  
	  assertThat(_1, not(_2));
	  assertThat(worker_1, is(worker_2));

	  // as time goes
	  long time_1 = BarflakeDecoder.decodeTimestamp(_1, 0);
	  long time_2 = BarflakeDecoder.decodeTimestamp(_2, 0);	  
	  printOneTwo("timestamp", time_1, time_2);
	  
	  idGenerator = new BarflakeGenerator(new TimeFreezingMillisProvider(1L), 1, 1);
	  long _1_t2 = idGenerator.next();
	  long _2_t2 = idGenerator.next();
	  long time_1_t2 = BarflakeDecoder.decodeTimestamp(_1_t2, 0L);
	  long time_2_t2 = BarflakeDecoder.decodeTimestamp(_2_t2, 0L);
	  
	  printOneTwo("timestamp at t2", time_1_t2, time_2_t2);
	  
	}
	
	void printOneTwo(String valueName, Object _1, Object _2) {
	  String template = "%s #1: %s, #2: %s";
	  System.out.println();
	  System.out.println(String.format(template, valueName, _1, _2));
	}

	
	
	@Test
	public void encodedWorkerIdShouldBeEquallyRepresented() {
		
		idGenerator = new BarflakeGenerator(timeConstZero, DATACENTER_ID_ZERO, 1);
		// 00000 00001 000000000001
		long id_worker1 = idGenerator.next();
		assertThat(BarflakeDecoder.decodeWorker(id_worker1), is(1));

		
		idGenerator = new BarflakeGenerator(timeConstZero, DATACENTER_ID_ZERO, 2);
		// 00000 00010 000000000001
		long id_worker2 = idGenerator.next();		
		assertThat(BarflakeDecoder.decodeWorker(id_worker2), is(2));

	}

	
	@Test
	public void encodedDatacenterConfigurable() {
		
		idGenerator = new BarflakeGenerator(timeConstZero, 1, WORKER_ID_ZERO);
		long id_datacenter1 = idGenerator.next();
		assertThat(BarflakeDecoder.decodeDataCenter(id_datacenter1), is(1));

		idGenerator = new BarflakeGenerator(timeConstZero, 2, WORKER_ID_ZERO);
		long id_datacenter2 = idGenerator.next();
		assertThat(BarflakeDecoder.decodeDataCenter(id_datacenter2), is(2));
	}
	
	/** 
	 * The encoded timestamp is a simple 
	 * 1:1 mapping from the time provided by the MillisProvider
	 */
	@Test
	public void encodedTimestampIsMappedTimeProvided() {
		
		idGenerator = new BarflakeGenerator(new TimeFreezingMillisProvider(1L), DATACENTER_ID_ZERO, WORKER_ID_ZERO);
		long id1 = idGenerator.next();
		assertThat(BarflakeDecoder.decodeTimestamp(id1, 0), is(1L));

		
		idGenerator = new BarflakeGenerator(new TimeFreezingMillisProvider(2L), DATACENTER_ID_ZERO, WORKER_ID_ZERO);
		long id2 = idGenerator.next();
		assertThat(BarflakeDecoder.decodeTimestamp(id2, 0), is(2L));
		
	}
	
	private static final class TimeFreezingMillisProvider implements CurrentTimeMillisProvider {
		
	    public TimeFreezingMillisProvider(long initialValue) {
			timestamp_const = initialValue;
		}		
		
	    private final long timestamp_const;
		
	    @Override
		public long currentAppTime() {
			return timestamp_const;
		}
	};

}
