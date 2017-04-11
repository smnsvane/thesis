package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pddl.Engine;

import static planner.PlannerType.*;

public class EngineTest
{
	private String baseURL = "/home/s022808/Dropbox/Master";
	private Engine engine;
	
	@Before
	public void setUp() { engine = new Engine(true, false, false); }
	
	// simple tower of hanoi test strips and typing
	@Test
	public void testHanoi2discs()
	{
		boolean success = 
			engine.run(baseURL+"/pddl/hanoi/strips-typing/hanoi-2discs.pddl",
					BreathFirst, 3000);
		assertTrue(success);
	}
	
	// simple maze test using strips only
	@Test
	public void testMaze01()
	{
		boolean success = 
			engine.run(baseURL+"/pddl/maze/strips/maze-01.pddl",
					BreathFirst, 3000);
		assertTrue(success);
	}
	
	@Test
	public void testSatelliteP01()
	{
		boolean success = 
			engine.run(baseURL+"/IPC4/SATELLITE/STRIPS/STRIPS/P01_PFILE1.PDDL",
					BreathFirst, 3000);
		assertTrue(success);
	}
}
