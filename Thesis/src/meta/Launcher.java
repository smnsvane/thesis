package meta;

import pddl.Engine;

import static planner.PlannerType.*;

public class Launcher
{
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.err.println("Expected parameter: <PDDL-problem-file-URL>");
			System.exit(1);
		}
		
//		String file = "/other/fridge/typed/fixB.pddl";
//		String file = "/IPC4/SATELLITE/STRIPS/STRIPS/P02_PFILE2.PDDL";
//		String file = "/IPC4/SATELLITE/STRIPS/STRIPS/satelite01.PDDL";
//		String file = "/IPC4/SATELLITE/NUMERIC/STRIPS_FLUENTS/P01_PFILE1.PDDL";
//		String file = "/IPC4/SATELLITE/NUMERIC/STRIPS_FLUENTS/P02_PFILE2.PDDL";
//		String file = "/post-apocalyptic/f1.pddl";//FIXME this exception should be an error
//		String file = "/pddl/post-apocalyptic/f1.pddl";
		String file = "/pddl/sudoku/sudoku-77-700000400020070080003008009000500300060020090001007006000300900030040060009001005.pddl";
//		String file = "/pddl/hanoi/strips-typing/hanoi-9discs.pddl";
//		String file = "/pddl/maze/strips/maze-01.pddl";
//		String file = "/pddl/blocksworld/blocksworld-10blocks-0012.pddl";
		
		if (!args[0].endsWith(".PDDL") || !args[0].endsWith(".pddl"))
			args[0] = args[0] + file;
		
		new Engine(true, true, false).run(args[0], EHC, 100000);
	}
}
