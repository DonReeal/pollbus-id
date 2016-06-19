package pollbus.idgen.barflake;

import io.baratine.core.OnInit;
import io.baratine.core.Result;
import io.baratine.core.Service;

import pollbus.idgen.IdGenerator;

@Service("public:///barflakes")
public class BarflakeGeneratorService implements IdGenerator {
	
	private BarflakeGenerator generator;
	
	@OnInit
	public void init(Result<Boolean> result) {
		generator = new BarflakeGenerator(1, 1);
		result.complete(true);
	}
	
	@Override
	public void next(Result<Long> result) {
		result.complete(generator.next());
	}

}
