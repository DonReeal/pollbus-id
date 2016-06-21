package pollbus.idgen.barflake;

import io.baratine.service.OnInit;
import io.baratine.service.Result;
import io.baratine.service.Service;

import pollbus.idgen.IdGenerator;

@Service("/barflakes")
public class BarflakeGeneratorService implements IdGenerator {
	
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
