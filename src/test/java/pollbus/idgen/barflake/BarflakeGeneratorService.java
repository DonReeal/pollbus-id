package pollbus.idgen.barflake;

import io.baratine.service.OnInit;
import io.baratine.service.Result;
import io.baratine.service.Service;
import pollbus.idgen.IdGeneratorAsync;

@Service("/barflakes")
public class BarflakeGeneratorService implements IdGeneratorAsync {
	
	private BarflakeGenerator generator;
	
	@OnInit
	public void init(Result<Boolean> result) {
		generator = new BarflakeGenerator(1, 1);
		result.ok(true);
	}
	
	@Override
	public void next(Result<Long> result) {
		result.ok(generator.next());
	}

}
