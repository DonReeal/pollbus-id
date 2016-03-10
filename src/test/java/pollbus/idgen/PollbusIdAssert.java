package pollbus.idgen;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PollbusIdAssert {

    // TODO: Add real mask values.
	private static final int maskSequenceBits = 1;      // 00...001
    private static final int maskWorkerBits = 2;        // 00...010
    private static final int maskDatacenterbits = 4;    // 00...100


	static void assertSequenceBitsAre(long pollid, String expected) {
		assertThat(binarySubstringOf(pollid, 0, 12), is(expected));
	}

	static void assertWorkerBitsAre(long pollid, String expected) {
		assertThat(binarySubstringOf(pollid, 12, 17), is(expected));
	}

	static void assertDatacenterBitsAre(long pollid, String expected) {
		assertThat(binarySubstringOf(pollid, 17, 22), is(expected));
	}

	static String binarySubstringOf(long pollid, int toBit, int fromBit) {
		String binaryStringValue = Long.toBinaryString(pollid);
		return (binaryStringValue.substring(binaryStringValue.length() - fromBit, binaryStringValue.length() - toBit));
	}

	static long sumOfExponentsBase2(Integer... eee) {
		long result = 0L;
		for (int i = 0; i < eee.length; i++) {
			result += Math.pow(2, eee[i]);
		}
		return result;
	}
}
