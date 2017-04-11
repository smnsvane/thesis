package state;

import fluents.GroundFluentEffect;
import unit.GroundPredicate;

public interface InitState
{
	public void initialize(GroundPredicate predicate);
	public void initialize(GroundFluentEffect function);
}
