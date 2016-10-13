package pollbus.idgen;

import io.baratine.service.Result;

/**
 * Generates unique Ids
 */
public interface IdGeneratorAsync {

	void next(Result<Long> result);

}