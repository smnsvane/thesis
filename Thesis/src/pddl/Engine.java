package pddl;

import meta.Timer;
import meta.Utilities;

import parser.ParserEngine;

import planner.Planner;
import planner.PlannerFactory;
import planner.PlannerType;

public class Engine
{
	private boolean
		warnings								= true,
		status									= true,
		debug									= false;
	
	public Engine(boolean warnings, boolean status, boolean debug)
	{
		this.warnings = warnings;
		this.status = status;
		this.debug = debug;
	}
	public Engine() { }
	
	private Timer engineTimer = new Timer();
	
	public boolean run(String PDDLProblemFileURL,
			PlannerType plannerType, int limit)
	{
		engineTimer.startMaster();
		engineTimer.start("Engine");
		
		Utilities.setPrintWarnings(warnings);
		Utilities.setPrintStatus(status);
		Utilities.setPrintDebug(debug);
		
		Utilities.println("Reading PDDL files.. ");
		
		engineTimer.stop("Engine");
		engineTimer.start("Parse");
		
		ParserEngine parser = new ParserEngine();
		Data data = parser.parse(PDDLProblemFileURL);
		
		engineTimer.stop("Parse");
//		System.out.println(Utilities.implode(data.getActions(), "\n"));
		engineTimer.start("Engine");
		
		Utilities.println("\tDone ("+engineTimer.getTime("Parse")+")");
		Utilities.println("\n * * *\n");
		Utilities.println("Planning..");
		
		engineTimer.stop("Engine");
		engineTimer.start("Planning");
		
		Planner planner = PlannerFactory.getInstance(plannerType);
		// check PDDL requirement support in planner
		boolean plannerSupportAllExtentions = true;
		for (Requirement req : data.getInstanceRequirements())
			if (!planner.support(req))
			{
				System.out.println(
						"The specified planner do not support the PDDL requrement '"+
						req+"'");
				plannerSupportAllExtentions = false;
			}
		if (!plannerSupportAllExtentions)
			System.exit(0);
		
		planner.setInitState(data.getInitState());
		planner.setStateExpansionLimit(limit);
		
		boolean success = planner.solve();
		
		engineTimer.stop("Planning");
		engineTimer.start("Engine");
		
		if (success)
			Utilities.println("\n"+planner.getSolution());
		
		Utilities.println("\nPlanner time decomposistion:");
		Utilities.println(planner.getTimer());
		
		engineTimer.stop("Engine");
		engineTimer.stopMaster();
		
		Utilities.println("\nEngine time decomposition:");
		Utilities.println(engineTimer);
		
		return success;
	}
}
