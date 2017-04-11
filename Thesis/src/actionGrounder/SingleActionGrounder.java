package actionGrounder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import object.PDDLObject;

import action.Action;

import state.State;
import unit.GroundPredicate;
import unit.Predicate;

public class SingleActionGrounder
{
	private GroundData gData;
	public GroundData getGroundData() { return gData; }
	
	private Matrix<Predicate, GroundPredicate> matrix;
	
	private State state;
	
	private boolean groundDone = false;
	
	public SingleActionGrounder(Action action, State state)
	{
		this.state = state;
		gData = new GroundData(action);
		
		Matrix<Predicate, GroundPredicate> matrix =
			new Matrix<Predicate, GroundPredicate>();
		
		for (Predicate precondition : action.getPPrecond())
		{
			ArrayList<GroundPredicate> row = createRow(precondition, state);
			if (row.isEmpty())
			{
				groundDone = true;
				return;
			}
			matrix.appendRow(precondition, row);
		}
		
		matrix.sortRows();
		
		this.matrix = matrix;
	}
	private ArrayList<GroundPredicate> createRow(Predicate precondition, State state)
	{
		PDDLObject[] arguments = null;
		if (precondition.haveFixedArguments())
		{
			arguments = new PDDLObject[precondition.getParameters().length()];
			
			// fill in fixed
			for (int i = 0; i < arguments.length; i++)
				arguments[i] = precondition.getFixedArgument(i);
		}
		
		// fetch possibilities
		// TODO make sure that no-one is altering this list
		return state.getPredicates(precondition, arguments);
	}
	
	public void groundAction()
	{
		if (!groundDone)
			findLegalGrounds();
	}
	private void findLegalGrounds()
	{
		boolean ok = groundPrecondition();
		if (ok)
		{
			if (gData.isPreconditionsGround())
			{
				if (!gData.isParametersGround())
					groundParameters();
				else
					gData.groundAction();
				
				matrix.incrementIndex();
				
				gData.ungroundLastGroundedPrecondition();
				
				findLegalGrounds();
			}
			else
			{
				matrix.incrementRowIndex();
				findLegalGrounds();
			}
		}
		else
		{
			ok = backtrack();
			if (ok)
			{
				matrix.incrementIndex();
				findLegalGrounds();
			}
		}
	}
	private boolean groundPrecondition()
	{
		if (!matrix.haveCurrentElement())
			return false;
		boolean ok = gData.groundPrecondition(matrix.getHeader(), matrix.getElement());
		while (!ok)
		{
			if (!matrix.haveNextElement())
				return false;
			matrix.incrementIndex();
			ok = gData.groundPrecondition(matrix.getHeader(), matrix.getElement());
		}
		return true;
	}
	private boolean backtrack()
	{
		gData.clearGroundings();
		do
		{
			matrix.resetIndex();
			if (matrix.havePrevRow())
				matrix.decrementRowIndex();
			else
				return false;
		}
		while (!matrix.haveNextElement());
		
		for (int i = 0; i < matrix.getRowIndex(); i++)
		{
			Predicate param = matrix.getHeaderAtRow(i);
			GroundPredicate arg = matrix.getElementAtRow(i);
			gData.groundPrecondition(param, arg);
		}
		
		return true;
	}
	private void groundParameters()
	{
		List<PDDLObject> unground = gData.getUngroundParameters();
		ParameterGrounder pg = new ParameterGrounder(state.getData(), unground);
		for (HashMap<PDDLObject, PDDLObject> paramToArg : pg)
		{
			for (PDDLObject parameter : paramToArg.keySet())
				gData.ground(parameter, paramToArg.get(parameter));
			gData.groundAction();
			for (PDDLObject unboundParameter : unground)
				gData.unground(unboundParameter);
		}
	}
}
