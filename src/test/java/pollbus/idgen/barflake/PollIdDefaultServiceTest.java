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
@ConfigurationBaratine(services = { BarflakeGeneratorService.class }, pod = "a")
public class PollIdDefaultServiceTest {


	@Inject @Lookup("pod://a/barflakes")
	private IdGeneratorSync idGen;

	@Test
	public void syncEndpointAvailable() {
		assertThat(idGen.next(), notNullValue());
	}	
	
	/* 
	 * Some prototype how to test async endpoints.
	 * TODO: Should assert more meaningful result like [yiels/s]
	 */
	
	@Inject @Lookup("pod://a/barflakes")
	private IdGenerator idGenAsync;
	private ExecutorService taskRunner = Executors.newSingleThreadExecutor();	

	@Test 
	public void asyncYieldsTenThousandIdsIn500ms() throws InterruptedException {
	  
	    int rounds = 10_000;
     
	    CountDownLatch resultsCountDown = new CountDownLatch(rounds);
	    
	    Runnable queryAllRounds = new Runnable() {
          @Override
          public void run() {
            for(int i = 0; i < rounds; i++) {          
              idGenAsync.next(new Result<Long>() {
                @Override
                public void complete(Long result) {              
                    resultsCountDown.countDown();               
                    if(resultsCountDown.getCount() == 0) {                  
                      long unixMillis = BarflakeDecoder.decodeTimestamp(result, 1456530296738L);
                      long unixSeconds = unixMillis / 1000;
                      int nanos = (int) (unixMillis % 1000) * 1000000;                  
                      System.out.println(
                          "final values was:  " + result
                          + " { value: " + result
                          + ", timestamp: " + LocalDateTime.ofEpochSecond(unixSeconds, nanos, ZoneOffset.UTC)
                          + ", sequence: " + BarflakeDecoder.decodeSequence(result)
                          + ", datacenter: " + BarflakeDecoder.decodeDataCenter(result)
                          + ", worker: " + BarflakeDecoder.decodeWorker(result)
                          + " }");               
                    }
                }
              });
            }            
          }
        };	    
        
        // ======================================================================================
        long start = System.currentTimeMillis();    
        taskRunner.submit(queryAllRounds); 
        resultsCountDown.await(500, TimeUnit.MILLISECONDS);
        // ======================================================================================
        
        if(resultsCountDown.getCount() > 0) {
          fail("Not done in time -- " + resultsCountDown.getCount()+ " missing of " + rounds);
        } 
        
        else {
          System.out.println(String.format("Receiving %s results took %s", 
              rounds, (System.currentTimeMillis() - start)));          
        }   
    }
	
}
