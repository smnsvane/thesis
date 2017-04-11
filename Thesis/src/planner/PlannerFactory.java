package planner;

public abstract class PlannerFactory
{
	public static Planner getInstance(PlannerType type)
	{
		switch (type)
		{
		case BestFirst:
			return new BestFirst();
		case BreathFirst:
			return new BreathFirst();
		case Dummy:
			return new Dummy();
		case HillClimb:
			return new HillClimb();
		case EHC:
			return new EnforcedHillClimb();
		default:
			throw new RuntimeException("Unknown planner type");
		}
	}
}
