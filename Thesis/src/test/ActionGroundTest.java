package test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import object.Assignment;
import object.PDDLObject;

import org.junit.Before;
import org.junit.Test;

import parser.ParserEngine;
import pddl.Data;

import action.Action;
import action.GroundAction;
import actionGrounder.GroundData;
import actionGrounder.SingleActionGrounder;

public class ActionGroundTest
{
	private SingleActionGrounder grounder;
	private Data data;
	@Before
	public void setUp() throws Exception
	{
		ParserEngine parser = new ParserEngine();
		data = parser.parse(
				"/home/s022808/Dropbox/Master" +
				"/pddl/hanoi/strips-typing/hanoi-2discs.pddl"
				);
		
		grounder = new SingleActionGrounder(
				data.getActions().get(0),
				data.getInitState()
				);
	}
	
	@SuppressWarnings("unused")
	private PDDLObject
		d1 = new PDDLObject("disc1", "disc"),
		d2 = new PDDLObject("disc2", "disc"),
		p1 = new PDDLObject("peg1", "peg"),
		p2 = new PDDLObject("peg2", "peg"),
		p3 = new PDDLObject("peg3", "peg");
	
	private GroundAction ga;
	
	@Test
	public void testGroundAction1()
	{
		grounder.groundAction();
		GroundData gData = grounder.getGroundData();
		LinkedList<GroundAction> gActions = gData.getGroundActions();
		
		Action a = data.getActions().get(0);
		
		Assignment assignment = new Assignment(a.getParameters());
		
		assignment.groundParameter(a.getParameters().get(0), d1);
		assignment.groundParameter(a.getParameters().get(1), d2);
		
		assignment.groundParameter(a.getParameters().get(2), p2);
		ga = a.ground(assignment);
		assertTrue(gActions.contains(ga));
		
		assignment.groundParameter(a.getParameters().get(2), p3);
		ga = a.ground(assignment);
		assertTrue(gActions.contains(ga));
		
		assertEquals(2, gActions.size());
		
	}
	
//	@Test
//	public void testGroundAction2()
//	{
//		grounder.groundAction();
//		GroundData gData = grounder.getGroundData();
//		LinkedList<GroundAction> gActions = gData.getGroundedActions();
//		
//		Action a = data.getActions().get(0);
//		
//		HashMap<Constant, Constant> map = new HashMap<Constant, Constant>();
//		
//		map.put(a.getParameters().get(0), d1);
//		map.put(a.getParameters().get(1), d2);
//		map.put(a.getParameters().get(2), p2);
//		
//		GroundAction ga = a.ground(map);
//		
//		assertTrue(gActions.contains(ga));
//	}
}
