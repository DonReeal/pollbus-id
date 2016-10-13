package pollbus.idgen.barflake;

import static pollbus.idgen.barflake.BarflakeGenerator.*;

public class BarflakeDecoder {

	public static int decodeSequence(long id) {
		return (int) (id & 0x0000000000000_FFF);
	}

	public static int decodeWorker(long id) {
		return (int) ((id & 0b000000000000000000000000000000000000000000_00000_11111_000000000000) >> LSHIFT_WORKER);
	}

	public static int decodeDataCenter(long id) {
		return (int) ((id & 0b000000000000000000000000000000000000000000_11111_00000_000000000000) >> LSHIFT_DATACENTER);
	}

	public static long decodeTimestamp(long id, long epoch) {
		long unixTime = (id >> LSHIFT_TIMESTAMP) + epoch;
		return unixTime;
	}

	public static int decodeMachineId(long id) {
		return (int) ((id & 0x00000000003FF_000) >> LSHIFT_WORKER);
	}

}
