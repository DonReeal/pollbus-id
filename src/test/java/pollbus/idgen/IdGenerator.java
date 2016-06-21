package pollbus.idgen;

import io.baratine.service.Result;



/**
 * Generates unique Ids
 */
public interface IdGenerator {

	void next(Result<Long> result);

}