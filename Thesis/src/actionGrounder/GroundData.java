package actionGrounder;

import java.util.LinkedList;
import java.util.List;

import object.Assignment;
import object.ObjectList;
import object.PDDLObject;
import unit.GroundPredicate;
import unit.Predicate;

import action.Action;
import action.GroundAction;

public class GroundData
{
	private Action action;
	private Assignment assignment;
	public GroundData(Action action)
	{
		this.action = action;
		assignment = action.getParameters().createAssignment();
	}
	
	public void ground(PDDLObject parameter, PDDLObject argument)
	{
		assignment.groundParameter(parameter, argument);
	}
	public PDDLObject unground(PDDLObject parameter)
	{
		return assignment.ungroundParameter(parameter);
	}
	
	private LinkedList<GroundPredicate> preconditionGrounds = new LinkedList<GroundPredicate>();
	private LinkedList<Predicate> preconditions = new LinkedList<Predicate>();
	public LinkedList<GroundPredicate> getPredGroundList() { return preconditionGrounds; }
	
	public void clearGroundings()
	{
		assignment.clearArguments();
		preconditions.clear();
		preconditionGrounds.clear();
	}
	
	public void groundAction()
	{
		GroundAction ga = action.ground(assignment.clone());
		groundInstances.add(ga);
	}
	public GroundPredicate ungroundLastGroundedPrecondition()
	{
		GroundPredicate keeper = preconditionGrounds.removeLast();
		Predicate precondition = preconditions.removeLast();
		
		outer:
		for (PDDLObject param : precondition.getParameters())
		{
			for (Predicate precond : preconditions)
				if (precond.getParameters().getObjectList().contains(param))
					continue outer;
			assignment.ungroundParameter(param);
		}
		
		return keeper;
	}
	public boolean groundPrecondition(Predicate precondition, GroundPredicate ground)
	{
		ObjectList parameters = precondition.getParameters();
		ObjectList arguments = ground.getArguments();
		
		for (int i = 0; i < parameters.length(); i++)
		{
			PDDLObject parameter = parameters.get(i);
			PDDLObject argument = arguments.get(i);
			PDDLObject fixedArg = precondition.getFixedArgument(i);
			
			// if fixed argument exists..
			if (fixedArg != null)
				// fixed argument must be equal to ground-predicate argument
				if (fixedArg.equals(argument))
					// if so ground-predicate argument
					// will have no effect on the parameter assignment
					continue;
				else
					// otherwise it is an illegal ground
					return false;
			
			// if parameter have been ground by another predicate..
			if (assignment.isGround(parameter))
			{
				// new argument must be equal to the old argument
				PDDLObject oldArg = assignment.getArg(parameter);
				if (!argument.equals(oldArg))
					return false;
			}
		}
		for (int i = 0; i < parameters.length(); i++)
		{
			PDDLObject fixedArg = precondition.getFixedArgument(i);
			if (fixedArg != null)
				continue;
			
			PDDLObject param = parameters.get(i);
			
			if (assignment.isGround(param))
				continue;
			
			PDDLObject arg = arguments.get(i);
			assignment.groundParameter(param, arg);
		}
		
		preconditions.add(precondition);
		preconditionGrounds.add(ground);
		
		return true;
	}
	
	/**
	 * can return true even if some parameters have not yet been ground
	 * @return true if all action preconditions have been ground
	 */
	public boolean isPreconditionsGround()
	{
		return action.getPPrecond().size() == preconditionGrounds.size();
	}
	
	private LinkedList<GroundAction> groundInstances = new LinkedList<GroundAction>();
	/**
	 * @return a list containing all grounded instances (so far) of the action in question
	 */
	public LinkedList<GroundAction> getGroundActions() { return groundInstances; }
	
	//////////////////////////////
	// PARAMETERS
	//////////////////////////////
	
	/**
	 * if this method returns true, then the action in question is ready to be ground
	 * @return true if all parameters is ground 
	 */
	public boolean isParametersGround()
	{
		return assignment.getUngroundParameters().size() == 0;
	}
	
	/**
	 * computes and returns all parameters than have not yet been ground
	 * @return all un-ground parameters
	 */
	public List<PDDLObject> getUngroundParameters()
	{
		List<PDDLObject> ungroundParameters =
			assignment.getUngroundParameters();
		return ungroundParameters;
	}
}
