package pollbus.idgen.barflake;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class BarflakeDecoderTest {

	@Test
	public void exampleDecode() {

		long id = 0b000000000000000000000000000000000000000100_00011_00010_000000000001;

		int sequence = BarflakeDecoder.decodeSequence(id);
		assertThat(sequence, is(1));

		int worker = BarflakeDecoder.decodeWorker(id);
		assertThat(worker, is(2));

		int dataCenter = BarflakeDecoder.decodeDataCenter(id);
		assertThat(dataCenter, is(3));

		long timestamp = BarflakeDecoder.decodeTimestamp(id, 0);
		assertThat(timestamp, is(4L));

	}

}
