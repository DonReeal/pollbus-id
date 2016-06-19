package pollbus.idgen.barflake;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.baratine.core.Lookup;
import io.baratine.core.Result;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.caucho.junit.ConfigurationBaratine;
import com.caucho.junit.RunnerBaratine;

import pollbus.idgen.IdGenerator;
import pollbus.idgen.IdGeneratorSync;

@RunWith(RunnerBaratine.class)
@ConfigurationBaratine(services={BarflakeGeneratorService.class})
public class PollIdDefaultServiceTest {


	@Inject @Lookup("/barflakes")
	private IdGeneratorSync idGen;

	@Test
	public void syncEndpointAvailable() {
	  
	    long value = idGen.next();		
	    assertThat(value, notNullValue());
	    
        System.out.println("received a barflake: " + stringifyId(value));        
	}	
	

	@Inject @Lookup("/barflakes")
	private IdGenerator idGenAsync;	

	@Test 
	public void asyncYieldsFifeThousandIdsPerSecond() throws InterruptedException {
	  
        int secondsToRun = 15;
        int ypsMinExpected = 100_000;    
        
        int maxAPICalls = 15_000_000; // that meant 100_000 ps
	    CountDownLatch resultsCountDown = new CountDownLatch(maxAPICalls);
	    ExecutorService taskRunner = Executors.newSingleThreadExecutor();
	    Runnable queryAPI = new Runnable() {
          @Override
          public void run() {
            for(int i = 0; i < maxAPICalls; i++) {          
              idGenAsync.next(new Result<Long>() {
                @Override
                public void complete(Long result) {                  
                  resultsCountDown.countDown();
                }
              });
            }            
          }
        };
        
        // ======================================================================================
        taskRunner.submit(queryAPI); 
        resultsCountDown.await(secondsToRun, TimeUnit.SECONDS);
        long countDown = resultsCountDown.getCount();
        long resultsYielded = maxAPICalls - countDown;
        // ======================================================================================
        taskRunner.shutdownNow();
        
        double ypsActual = resultsYielded / secondsToRun;
        System.out.println("Yielded Results per second: " + ypsActual);
        
        assertTrue(
            "Not enough results yielded! Expected " + ypsMinExpected + " results per second. Was acutally: " + ypsActual,
            (ypsActual > ypsMinExpected));
         
    }
	
	
	private String stringifyId(Long value) {
      
      long unixMillis = BarflakeDecoder.decodeTimestamp(value, 1456530296738L);
      long unixSeconds = unixMillis / 1000;
      int nanos = (int) (unixMillis % 1000) * 1000000;
      
      return " { "
          + "value: " + value
        + ", timestamp: " + LocalDateTime.ofEpochSecond(unixSeconds, nanos, ZoneOffset.UTC)
        + ", sequence: " + BarflakeDecoder.decodeSequence(value)
        + ", datacenter: " + BarflakeDecoder.decodeDataCenter(value)
        + ", worker: " + BarflakeDecoder.decodeWorker(value)
        + " }";
  }
	
}
