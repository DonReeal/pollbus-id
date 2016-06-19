package pollbus.idgen;

import io.baratine.core.Result;



/**
 * Generates unique Ids
 */
public interface IdGenerator {

	void next(Result<Long> result);

}