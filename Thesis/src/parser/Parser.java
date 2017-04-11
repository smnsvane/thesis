package parser;

import static pddl.Requirement.*;
import static fluents.FluentComparator.*;

import fluents.Constant;
import fluents.AssignmentOperator;
import fluents.FluentEffect;
import fluents.GroundFluentComparable;
import fluents.GroundFluentEffect;
import fluents.FluentComparable;
import fluents.FluentComparator;
import fluents.FluentPrecondition;
import fluents.GroundFluentPrecondition;
import fluents.GroundMathOperator;
import fluents.MathOperator;
import fluents.MathOperatorType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import meta.Utilities;

import object.ObjectList;
import object.PDDLObject;

import action.Action;
import action.Effect;
import action.GroundPrecondition;
import action.Precondition;

import pddl.Data;
import pddl.Requirement;

import state.InitState;
import state.StateImpl;
import unit.AbstractFunction;
import unit.AbstractPredicate;
import unit.AbstractUnit;
import unit.Function;
import unit.GroundFunction;
import unit.GroundPredicate;
import unit.GroundUnit;
import unit.Predicate;
import unit.Unit;

public class Parser
{
	// requirements understood by this parser 
	private HashSet<Requirement> domainSupportedRequirements =
		new HashSet<Requirement>(Arrays.asList(
			new Requirement[]
		{
			strips,
			typing,
			equality,
			negative_preconditions,
			fluents,
			shadow,
		}));
	
	public boolean parserSupport(Requirement...requirements)
	{
		for (Requirement r : requirements)
			if (!domainSupportedRequirements.containsAll(
					Requirement.functionality(r)))
				return false;
		return true;
	}
	
	/* ******************************************
	 * TREE BUILDER METHOD
	 ********************************************/
	/**
	 * @return root of tree
	 */
	private Split buildTree(String content)
	{
		// weed out tabulations
		content = content.replaceAll("(\\s)+", " ");
		content = content.replaceAll("\040?\\(\040?", "\\(");
		content = content.replaceAll("\040?\\)\040?", "\\)");
		
		if (content.charAt(0) != '(')
			Utilities.parserError("First charcter must be a '('");
		if (content.charAt(content.length() - 1) != ')')
			Utilities.parserError("Last charcter must be a ')'");
		
		content = content.substring(1, content.length() - 1);
		
		Split root = new Split(null, content);
		
		// return tree root
		return root;
	}
	
	/* ******************************************
	 * GENERAL PUBLIC PARSER METHODS
	 ********************************************/
	private Data data = new Data();
	
	public Data parse(String domainString, String problemString)
	{
		Split domainRoot = buildTree(domainString);
		//TODO: consider if a root child could be a leaf
		parseDomain(domainRoot.getSplits().iterator());
		
		Split problemRoot = buildTree(problemString);
		//TODO: consider if a root child could be a leaf
		parseProblem(problemRoot.getSplits().iterator());
		
		return data;
	}
	
	public String getDomainNameFromProblem(String problemString)
	{
		Split problemRoot = buildTree(problemString);
		
		if (!problemRoot.header().equals("define") && problemRoot.getChildren().size() > 1)
			Utilities.parserError("Malformed domain 'define' tag");
		
		Split domainOrProblemNode = problemRoot.getSplits().get(0);
		
		if (domainOrProblemNode.header().equals("problem"))
		{
			Split domainNode = (Split) problemRoot.getSplits().get(1);
			
			if (!domainNode.header().equals(":domain"))
				Utilities.parserError(domainNode.header(), ":domain");
			
			Leaf domainLeaf = domainNode.getLeaves().get(1);
			
			return domainLeaf.header();
		}
		else if (domainOrProblemNode.header().equals("domain"))
			Utilities.parserError("PDDL domain file", "PDDL problem file");
		else
			Utilities.parserError(domainOrProblemNode.header(),
					"domain", "problem");
		// to please eclipse, no thread will ever get here
		return null;
	}
	
	/* ******************************************
	 * GENEREL PARSE METHODS
	 ********************************************/
	private void parseDomain(Iterator<Split> rootIterator)
	{
		Utilities.println("Parsing PDDL domain..");
		
		// get domain name
		Utilities.print("Parsing name..\n\t");
		Split current = (Split) rootIterator.next();
		String name = current.getLeaves().get(1).header();
		Utilities.println("Domain name: "+name);
		data.setDomainName(name);
		
		// get domain requirements
		current = rootIterator.next();
		if (current.header().equals(":requirements"))
		{
			Utilities.println("Parsing requirements..");
			parseRequirements(current);
			current = rootIterator.next();
		}
		else
		{
			Utilities.printWarning(
					"No 'requirements' tag, domain assumed to require strips "+
					"(strips is included in domain by parser)");
			data.addRequirement(strips);
		}
		
		// get domain type-def
		if (current.header().equals(":types"))
		{
			Utilities.println("Parsing types-def..");
			if (!data.instanceSupport(typing))
				Utilities.supportError(typing, "a types-def have been found");
			else
				for (int i = 1; i < current.getLeaves().size(); i++)
					data.addType(current.getLeaves().get(i).header());
			current = rootIterator.next();
		}
		
		// get domain constant-def
		if (current.header().equals(":constants"))
		{
			Utilities.println("Parsing constants-def..");
			// method will automatically put new atoms into domain
			parseTypedList(current.getLeaves(), 1, false);
			current = rootIterator.next();
		}
		
		// get domain predicates-def
		if (current.header().equals(":predicates"))
		{
			Utilities.println("Parsing predicates-def..");
			for (Split predicateNode : current.getSplits())
			{
				AbstractPredicate p =
					(AbstractPredicate) parseAbstractUnit(predicateNode, true);
				data.addAbstractPredicate(p);
			}
			current = rootIterator.next();
		}
		
		// get domain functions-def (req fluents)
		if (current.header().equals(":functions"))
		{
			Utilities.println("Parsing functions-def..");
			if (!data.instanceSupport(fluents))
				Utilities.supportError(fluents, "a functions-def have been found");
			for (Split functionNode : current.getSplits())
			{
				AbstractFunction f =
					(AbstractFunction) parseAbstractUnit(functionNode, false);
				data.addFunction(f);
			}
			current = rootIterator.next();
		}
		
		// get domain constraints (req constraints)
		if (current.header().equals(":constraints"))
		{
			Utilities.println("Parsing constraints-def..");
			if (!data.instanceSupport(constraints))
				Utilities.supportError(constraints, "a constraints-def have been found");
//			current = rootIterator.next();
		}
		
		// get domain constraints (req constraints)
		if (current.header().equals(":shadow"))
		{
			Utilities.println("Parsing shadow-def..");
			if (!data.instanceSupport(shadow))
				Utilities.supportError(shadow, "a shadow-def have been found");
			for (Split shadowBlock : current.getSplits())
			{
				Split lightTakerSplit = shadowBlock.getSplits().get(0);
				Predicate lightTaker =
					(Predicate) parseUnit(lightTakerSplit, false, true, new HashMap<String, String>());
				
				Split shadowedSplit = shadowBlock.getSplits().get(1);
				Predicate shadowed =
					(Predicate) parseUnit(shadowedSplit, false, true, new HashMap<String, String>());
				
				data.addShadow(lightTaker, shadowed);
			}
			current = rootIterator.next();
		}
		
		Utilities.println("Parsing actions-def..");
		// get domain structure-def
		while (true)
		{
			if (current.header().equals(":action"))
				parseAction(current);
			else if (current.header().equals(":durative-action"))
			{
				if (!data.instanceSupport(durative_actions))
					Utilities.supportError(durative_actions, "a durative-action-def have been found");
				Utilities.supportError(durative_actions);
			}
			else if (current.header().equals(":derived"))
			{
				if (!data.instanceSupport(derived_predicates))
					Utilities.supportError(derived_predicates, "a derived-def have been found");
				Utilities.supportError(derived_predicates);
			}
			else Utilities.parserError("Illegal tag ("+current.header()+")");
			
			if (!rootIterator.hasNext())
				 break;
			current = rootIterator.next();
		}
	}
	private void parseProblem(Iterator<Split> rootIterator)
	{
		Utilities.println("\nParsing PDDL problem..");
		
		Split current = rootIterator.next();
		
		// get problem name
		Utilities.print("Parsing name..\n\t");
		String name = current.getLeaves().get(1).header();
		Utilities.println("Problem name: "+name);
		data.setProblemName(name);
		current = rootIterator.next();
		
		// get problem domain reference
		if (!current.header().equals(":domain"))
			Utilities.parserError(current.header(), ":domain");
		else if (!data.getDomainName().equalsIgnoreCase(current.getLeaves().get(1).header()))
			Utilities.parserError(
					"Problem domain reference and domain does not match");
		current = rootIterator.next();
		
		// get problem requirements
		if (current.header().equals(":requirements"))
		{
			Utilities.println("Requirements tag found..\n\tParsing problem requirements..");
			parseRequirements(current);
			current = rootIterator.next();
		}
		
		// get problem objects (similar to domain constants)
		if (current.header().equals(":objects"))
		{
			Utilities.println("Objects tag found..\n\tParsing objects (constants)..");
			// method will put new constants in domain
			parseTypedList(current.getLeaves(), 1, false);
			current = rootIterator.next();
		}
		
		if (current.header().equals(":init"))
		{
			Utilities.println("Parsing init state..");
			
			InitState initState = parseInitState(current);
			
			data.setInitState(initState);
			current = rootIterator.next();
			
			// creating equality predicate if :equality extension is used
			if (data.instanceSupport(equality))
			{
				// abstract predicate
				List<String> typeList = Arrays.asList((new String[] {null, null}));
				data.addAbstractPredicate(new AbstractPredicate("=", typeList));
								
				// ground predicate
				for (PDDLObject arg : data.getConstants())
				{
					ObjectList args = new ObjectList();
					args.getObjectList().add(arg);
					args.getObjectList().add(arg);
					args.makeImmutable();
					GroundPredicate gp = new GroundPredicate("=", false, args);
					initState.initialize(gp);
				}
			}
		}
		else
			Utilities.parserError(current.header(), ":init");
		
		if (current.header().equals(":goal"))
		{
			Utilities.println("Parsing goal state..");
			Split logicRoot = current.getSplits().get(0);
			
			List<GroundPrecondition> goal = parseGoal(logicRoot);
			data.setGoal(goal);
		}
		else
			Utilities.parserError(current.header(), ":goal");
		
	}
	
	/* ******************************************
	 * PARSE INIT STATE AND GOAL METHODS
	 ********************************************/
	
	private InitState parseInitState(Split initRoot)
	{
		if (initRoot.getLeaves().size() != 1)
			Utilities.parserError("Malformed initial-state-def");
		
		InitState state = new StateImpl(data);
		
		for (Split split : initRoot.getSplits())
			if (split.header().equals("="))
			{
				GroundFluentEffect gfe = parseGroundFunctionEffect(split);
				state.initialize(gfe);
			}
			else if (split.header().equals("not"))
			{
				Node predNode = split.getChildren().get(0);
				GroundPredicate pred =
					(GroundPredicate) parseGroundUnit(predNode, true, true);
				state.initialize(pred);
			}
			else
			{
				GroundPredicate pred =
					(GroundPredicate) parseGroundUnit(split, false, true);
				state.initialize(pred);
			}
		
		return state;
	}
	
	private List<GroundPrecondition> parseGoal(Split goalRoot)
	{
		return parseGroundPreconditions(goalRoot);
	}
	
	/* ******************************************
	 * PARSE PDDL TAG METHODS
	 ********************************************/
	
	private void parseRequirements(Split requirementNode)
	{
		for (int i = 1; i < requirementNode.getLeaves().size(); i++)
		{
			String requirementImage = requirementNode.getLeaves().get(i).header();
			if (requirementImage.charAt(0) == ':')
				requirementImage = requirementImage.substring(1);
			else
				Utilities.parserError(requirementImage, Requirement.getNames());
			Requirement req = Requirement.getRequirement(requirementImage);
			
			if (req == null)
				Utilities.parserError(requirementImage, Requirement.getNames());
			
			// check parser support
			if (!parserSupport(req))
				Utilities.parserError(
						"The parser do not support the requirement '"+req+"'");
			
			data.addRequirement(req);
		}
		if (!data.instanceSupport(strips))
		{
			Utilities.printWarning(
					"No requirement for 'strips' found. 'strips' support was added by parser.");
			data.addRequirement(strips);
		}
		// check for requirement preconditions (implies)
		for (Requirement r0 : Requirement.values())
			if (data.instanceSupport(r0))
			{
				Set<Requirement> implies = Requirement.implies(r0);
				if (!implies.isEmpty())
					if (!data.instanceSupports(implies))
						Utilities.parserError("The requirement '"+r0+
								"' implies another requirement that is not supported by this domain.");
			}
	}
	
	/* ******************************************
	 * PARSE PREDICATE AND FUNCTION METHODS
	 ********************************************/
	private AbstractUnit parseAbstractUnit(Split unitNode, boolean isPredicate)
	{
		String image = unitNode.header();
		
		ObjectList parameters =
			parseTypedList(unitNode.getLeaves(), 1, true);
		
		ArrayList<String> paramTypes = new ArrayList<String>();
		for (PDDLObject param : parameters)
			paramTypes.add(param.getType());
		
		if (isPredicate)
			return new AbstractPredicate(image, paramTypes);
		return new AbstractFunction(image, paramTypes);
	}
	
	private Unit parseUnit(Node node, boolean negated, boolean isPredicate,
			HashMap<String, String> paramToType)
	{
		if (negated && !isPredicate)
			throw new RuntimeException("Internal parser error");
		
		String image = node.header();
		
		// if node is a leaf return a zero args unit
		if (node instanceof Leaf)
		{
			ObjectList parameters = new ObjectList();
			parameters.makeImmutable();
			
			HashMap<Integer, PDDLObject> fixedArguments =
				new HashMap<Integer, PDDLObject>();
			
			if (isPredicate)
				return new Predicate(image, negated, parameters, fixedArguments);
			else
				return new Function(image, parameters, fixedArguments);
		}
		
		Split splitNode = (Split) node;	
		
		// parse param/arg list
		ObjectList paramsOrArgs = parseTypedList(splitNode.getLeaves(), 1, false);
		
		Utilities.printDebug("creating "+(isPredicate?"predicate":"function")+
				": image="+image+(isPredicate?" isNegated="+negated:"")+
				" raw-params="+paramsOrArgs);
		
		HashMap<Integer, PDDLObject> fixedArguments = new HashMap<Integer, PDDLObject>();
		AbstractUnit abstractUnit;
		if (isPredicate)
			abstractUnit = data.getPredicate(image);
		// if abstract unit is null it indicates that there is no predicate with that image
		else
			abstractUnit = data.getFunction(image);
		for (int i = 0; i < paramsOrArgs.length(); i++)
		{
			// is this an argument?
			if (paramsOrArgs.get(i).isArgument())
			{
				PDDLObject arg = paramsOrArgs.get(i);
				// add object to domain if it is missing
				if (!data.containConstant(arg))
				{
					String objectImage = arg.getImage();
					// fetch correct object type
					if (abstractUnit != null)
					{
						String objectType = abstractUnit.getTypeList().get(i);
						arg = new PDDLObject(objectImage, objectType);
					}
					else
						arg = new PDDLObject(objectImage, null);
					data.addObject(arg);
					Utilities.printWarning("New object '"+arg+"' found outside constant-def");
				}
				else
				{
					// replace argument with a domain held copy to get correct type
					arg = data.getObject(arg.getImage());
				}
				// put fixed argument in map, and remember its position
				fixedArguments.put(i, arg);
				// replace argument with a parameter
				paramsOrArgs.getObjectList().set(i, PDDLObject.createDummyParameter(arg.getType()));
			}
			else
			{
				String type = paramToType.get(paramsOrArgs.get(i).getImage());
				PDDLObject newObject = new PDDLObject(paramsOrArgs.get(i).getImage(), type);
				paramsOrArgs.getObjectList().set(i, newObject);
			}
		}
		paramsOrArgs.makeImmutable();
		Unit unit;
		if (isPredicate)
			unit = new Predicate(image, negated, paramsOrArgs, fixedArguments);
		else
			unit = new Function(image, paramsOrArgs, fixedArguments);
		Utilities.printDebug("unit created: "+unit);
		return unit;
	}
	private FluentPrecondition parseFunctionPrecondition(Split functionNode,
			HashMap<String, String> paramToType)
	{
		if (functionNode.getChildren().size() != 3)
			Utilities.parserError("Malformed function precondition");
		
		FluentComparator comp = FluentComparator.lookup(functionNode.header());
		
		Node leftChild = functionNode.getChildren().get(1);
		FluentComparable left = parseFunctionComparable(leftChild, paramToType);
		
		Node rightChild = functionNode.getChildren().get(2);
		FluentComparable right = parseFunctionComparable(rightChild, paramToType);
		
		return new FluentPrecondition(comp, left, right);
	}
	private FluentComparable parseFunctionComparable(Node node,
			HashMap<String, String> paramToType)
	{
		MathOperatorType op = MathOperatorType.lookup(node.header());
		if (op != null)
		{
			List<Node> children = ((Split) node).getChildren(); 
			FluentComparable left = parseFluentComparable(children.get(1), paramToType);
			if (children.size() == 2)
				return new MathOperator(op, left, null);
			if (children.size() == 3)
			{
				FluentComparable right = parseFluentComparable(children.get(2), paramToType);
				return new MathOperator(op, left, right);
			}
			Utilities.parserError("Binary math operators must have 2 operands,"+
					" unary minus must have 1 operand. "+
					"Operator in question '"+op.getImage()+
					"'. Number of operands found '"+children.size()+"'");
		}
		try
		{
			double constant = Double.parseDouble(node.header());
			return new Constant(constant);
		}
		catch (NumberFormatException e)
		{
			return (Function) parseUnit(node, false, false, paramToType);
		}
	}
	
	private GroundFluentPrecondition parseGroundFunctionPrecondition(Split functionNode)
	{
		if (functionNode.getChildren().size() != 3)
			Utilities.parserError("Malformed ground-function precondition");
		
		FluentComparator comp = FluentComparator.lookup(functionNode.header());
		
		Node leftChild = functionNode.getChildren().get(1);
		GroundFluentComparable left = parseGroundFunctionComparable(leftChild);
		
		Node rightChild = functionNode.getChildren().get(2);
		GroundFluentComparable right = parseGroundFunctionComparable(rightChild);
		
		return new GroundFluentPrecondition(comp, left, right);
	}
	private GroundFluentComparable parseGroundFunctionComparable(Node node)
	{
		MathOperatorType op = MathOperatorType.lookup(node.header());
		if (op != null)
		{
			List<Node> children = ((Split) node).getChildren(); 
			GroundFluentComparable left = parseGroundFluentComparable(children.get(1));
			if (children.size() == 1)
				return new GroundMathOperator(op, left, null);
			if (children.size() == 2)
			{
				GroundFluentComparable right = parseGroundFluentComparable(children.get(2));
				return new GroundMathOperator(op, left, right);
			}
			Utilities.parserError("Binary math operators must have 2 operands, unary minus must have 1 operand");
		}
		try
		{
			double constant = Double.parseDouble(node.header());
			return new Constant(constant);
		}
		catch (NumberFormatException e)
		{
			return (GroundFunction) parseGroundUnit(node, false, false);
		}
	}
	
	
	private GroundUnit parseGroundUnit(Node unitNode, boolean negated, boolean isPredicate)
	{
		String image = unitNode.header();
		
		if (unitNode instanceof Leaf)
		{
			ObjectList arguments = new ObjectList();
			arguments.makeImmutable();
			
			if (isPredicate)
				return new GroundPredicate(image, negated, arguments);
			return new GroundFunction(image, arguments);
		}
		
		Split unitSplit = (Split) unitNode;
		ObjectList arguments = parseTypedList(unitSplit.getLeaves(), 1, true);
		
		if (data.instanceSupport(typing))
		{
			AbstractUnit abstractUnit;
			if (isPredicate)
				abstractUnit = data.getPredicate(image);
			else
				abstractUnit = data.getFunction(image);
			
			for (int i = 0; i < arguments.length(); i++)
			{
				PDDLObject argument = arguments.get(i);
				String parameterType = abstractUnit.getTypeList().get(i);
				if (!argument.subTypeOf(parameterType))
					Utilities.parserError("The argument '"+argument+
							"' in the predicate '"+image+
							"' is not a sub-type of parameter-type '"+parameterType+"'");
			}
		}
		
		if (isPredicate)
			return new GroundPredicate(image, negated, arguments);
		return new GroundFunction(image, arguments);
	}
	
	private FluentComparable parseFluentComparable(Node funcNode,
			HashMap<String, String> paramToType)
	{
		MathOperatorType mathOp = MathOperatorType.lookup(funcNode.header()); 
		if (mathOp != null)
		{
			Split mathOpSplit = (Split) funcNode;
			// take into account that operator is the first child
			if (mathOpSplit.getChildren().size() != 3)
				if (mathOp.equals(MathOperatorType.minus) &&
						mathOpSplit.getChildren().size() == 2)
				{
					FluentComparable operand =
						parseFluentComparable(mathOpSplit.getChildren().get(1),
								paramToType);
					return new MathOperator(mathOp, operand, null);
				}
				else
					Utilities.parserError("Ilegal operand number '"+
							(mathOpSplit.getChildren().size() - 1)+
							"' for this operator '"+mathOp+"'");
				
			FluentComparable left =
				parseFluentComparable(mathOpSplit.getChildren().get(1), paramToType);
			FluentComparable right =
				parseFluentComparable(mathOpSplit.getChildren().get(2), paramToType);
			
			return new MathOperator(mathOp, left, right);
		}
		try
		{
			double constantValue = Double.parseDouble(funcNode.header());
			return new Constant(constantValue);
		}
		catch (NumberFormatException e)
		{
			return (Function) parseUnit(funcNode, false, false, paramToType);
		}
	}
	private GroundFluentComparable parseGroundFluentComparable(Node funcNode)
	{
		MathOperatorType mathOp = MathOperatorType.lookup(funcNode.header()); 
		if (mathOp != null)
		{
			Split mathOpSplit = (Split) funcNode;
			// take into account that operator is the first child
			if (mathOpSplit.getChildren().size() != 3)
				if (mathOp.equals(MathOperatorType.minus) &&
						mathOpSplit.getChildren().size() == 2)
				{
					GroundFluentComparable operand =
						parseGroundFluentComparable(mathOpSplit.getChildren().get(1));
					return new GroundMathOperator(mathOp, operand, null);
				}
				else
					Utilities.parserError("Illegal operand number '"+
							(mathOpSplit.getChildren().size() - 1)+
							"' for this operator '"+mathOp+"'");
				
			GroundFluentComparable left =
				parseGroundFluentComparable(mathOpSplit.getChildren().get(1));
			GroundFluentComparable right =
				parseGroundFluentComparable(mathOpSplit.getChildren().get(2));
			
			return new GroundMathOperator(mathOp, left, right);
		}
		try
		{
			double constantValue = Double.parseDouble(funcNode.header());
			return new Constant(constantValue);
		}
		catch (NumberFormatException e)
		{
			return (GroundFunction) parseGroundUnit(funcNode, false, false);
		}
	}
	
	//TODO: the two methods below should have some sub-methods in common
	private FluentEffect parseFunctionEffect(Split funcSplit, HashMap<String, String> paramToType)
	{
		if (funcSplit.getChildren().size() != 3)
			Utilities.parserError("Malformed ground function assignment");
		
		AssignmentOperator op =
			AssignmentOperator.lookup(funcSplit.getChildren().get(0).header());
		
		Node functionNode = funcSplit.getChildren().get(1);
		Function function =
			(Function) parseUnit(functionNode, false, false, paramToType);
		
		Node valueExpressionNode = funcSplit.getChildren().get(2);
		FluentComparable value = parseFluentComparable(valueExpressionNode, paramToType);
		
		return new FluentEffect(op, function, value);
	}
	private GroundFluentEffect parseGroundFunctionEffect(Split funcSplit)
	{
		if (funcSplit.getChildren().size() != 3)
			Utilities.parserError("Malformed ground function assignment");
		
		AssignmentOperator op =
			AssignmentOperator.lookup(funcSplit.getChildren().get(0).header());
		
		if (op == null && funcSplit.header().equals("="))
			op = AssignmentOperator.assign;
		
		Node functionNode = funcSplit.getChildren().get(1);
		GroundFunction function =
			(GroundFunction) parseGroundUnit(functionNode, false, false);
		
		Node valueExpressionNode = funcSplit.getChildren().get(2);
		GroundFluentComparable value = parseGroundFluentComparable(valueExpressionNode);
		
		return new GroundFluentEffect(op, function, value);
	}
	
	/* ******************************************
	 * PARSE PRECONDITION AND EFFECT METHODS
	 ********************************************/
	
	private List<Precondition> parsePreconditions(Split logicRoot, HashMap<String, String> paramToType)
	{
		List<Precondition> predicates = new ArrayList<Precondition>();
		if (logicRoot.header().equals("and"))
			for (Split s : logicRoot.getSplits())
				predicates.addAll(parsePreconditions(s, paramToType));
		else if (logicRoot.header().equals("not"))
		{
			if (!data.instanceSupport(negative_preconditions))
				Utilities.supportError(negative_preconditions,
						"a logical 'not' have been found in an action precondition definition");
			else
			{
				// parse negated predicate
				Split predicateNode = logicRoot.getSplits().get(0);
				predicates.add((Predicate) parseUnit(predicateNode, true, true, paramToType));
			}
		}
		else
			predicates.add(parsePrecondition(logicRoot, paramToType));
		return predicates;
	}
	private Precondition parsePrecondition(Split precondNode,
			HashMap<String, String> paramToType)
	{
		FluentComparator comp = FluentComparator.lookup(precondNode.header());
		if (comp == null)
			return (Predicate) parseUnit(precondNode, false, true, paramToType);
		if (comp == equal && data.instanceSupport(equality))
		{
			// determine if this is a equality predicate
			//FIXME
			throw new RuntimeException(
					"Parser do not support both equality and fluents");
		}
		return parseFunctionPrecondition(precondNode, paramToType);
	}
	
	private List<GroundPrecondition> parseGroundPreconditions(Split logicRoot)
	{
		List<GroundPrecondition> preconditions = new ArrayList<GroundPrecondition>();
		if (logicRoot.header().equals("and"))
			for (Split s : logicRoot.getSplits())
				preconditions.addAll(parseGroundPreconditions(s));
		else if (logicRoot.header().equals("not"))
		{
			if (!data.instanceSupport(negative_preconditions))
				Utilities.supportError(negative_preconditions,
						"a logical 'not' have been found in the goal definition");
			else
			{
				// parse negated predicate
				Split predicateNode = logicRoot.getSplits().get(0);
				preconditions.add((GroundPredicate) parseGroundUnit(predicateNode, true, true));
			}
		}
		else
			preconditions.add(parseGroundPrecondition(logicRoot));
		return preconditions;
	}
	private GroundPrecondition parseGroundPrecondition(Split precondNode)
	{
		FluentComparator comp = FluentComparator.lookup(precondNode.header());
		
		if (comp == null)
			return (GroundPredicate) parseGroundUnit(precondNode, false, true);
		
		return parseGroundFunctionPrecondition(precondNode);
	}
	
	private List<Effect> parseEffects(Split logicRoot, HashMap<String, String> paramToType)
	{
		List<Effect> effects = new ArrayList<Effect>();
		if (logicRoot.header().equals("and"))
			for (Split s : logicRoot.getSplits())
				effects.addAll(parseEffects(s, paramToType));
		else if (logicRoot.header().equals("not"))
		{
			// parse negated predicate
			Split predicateNode = logicRoot.getSplits().get(0);
			effects.add((Predicate) parseUnit(predicateNode, true, true, paramToType));
		}
		else
			effects.add(parseEffect(logicRoot, paramToType));
		return effects;
	}
	private Effect parseEffect(Split effectNode,
			HashMap<String, String> paramToType)
	{
		AssignmentOperator op =
			AssignmentOperator.lookup(effectNode.header());
		
		if (op == null)
			return (Predicate) parseUnit(effectNode, false, true, paramToType);
		
		return parseFunctionEffect(effectNode, paramToType);
	}
	
	/* ******************************************
	 * PARSE PDDL-Object STRUCTURE METHODS
	 ********************************************/
	private ObjectList parseTypedList(List<Leaf> leaves, int fromIndex, boolean immutableList)
	{
		ArrayList<PDDLObject> typedVars = new ArrayList<PDDLObject>();
		LinkedList<String> untypedVars = new LinkedList<String>();
		
		for (int i = fromIndex; i < leaves.size(); i++)
		{
			if (leaves.get(i).header().equals("-"))
			{
				if (!data.instanceSupport(typing))
					Utilities.parserError("The current domain does not support typing, "+
							"yet a type marker '-' have been found in an atom list");
				else if (untypedVars.isEmpty())
					Utilities.parserError("A type marker '-' have been found, "+
							"but there are no candidate atoms (atom before "+leaves.get(i-1)+", untyped content:"+untypedVars+")");
				else
					try
					{
						for (String type = leaves.get(i+1).header(); !untypedVars.isEmpty();)
						{
							String varImage = untypedVars.remove();
							PDDLObject object = new PDDLObject(varImage, type);
							if (object.isArgument())
							{
								PDDLObject domainAtom = data.getObject(varImage);
								if (domainAtom == null)
									data.addObject(object);
								else if (!type.equals(domainAtom.getType()))
									Utilities.parserError("Domain object type '"+
											domainAtom.getType()+
											"' not equal with type of new object '"+object+"'");
							}
							typedVars.add(object);
						}
						i++;
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						Utilities.parserError("Type marker '-' found at end of typed list");
					}
			}
			else
				untypedVars.add(leaves.get(i).header());
		}
		for (String atomImage: untypedVars)
		{
			PDDLObject atom = new PDDLObject(atomImage, null);
			if (atom.isArgument())
			{
				PDDLObject domainAtom = data.getObject(atomImage);
				if (domainAtom == null)
				{
					data.addObject(atom);
					typedVars.add(atom);
				}
				else
					typedVars.add(domainAtom);
			}
			else
				typedVars.add(atom);
		}
		return new ObjectList(typedVars, immutableList);
	}
	
	/* ******************************************
	 * PARSE ACTION METHODS
	 ********************************************/
	private void parseAction(Split actionNode)
	{
		List<Node> children = actionNode.getChildren();
		
		String image = children.get(1).header();
		
		Utilities.printDebug("creating action '"+image+"'");
		
		if (!children.get(2).header().equals(":parameters"))
			Utilities.parserError(children.get(2).header(), ":parameters");
		
		Split parameterNode = (Split) children.get(3);
		
		if (!children.get(4).header().equals(":precondition"))
			Utilities.parserError("this parser have no support for precondition-less actions");
		
		Split precondNode = (Split) children.get(5);
		
		if (!children.get(6).header().equals(":effect"))
			Utilities.parserError("this parser have no support for effect-less actions");
		
		Split effectNode = (Split) children.get(7);
		
		ObjectList parameters =
			parseTypedList(parameterNode.getLeaves(), 0, true);
		
		Utilities.printDebug("action parameters: "+parameters);
		
		HashMap<String, String> paramToType = new HashMap<String, String>();
		for (PDDLObject param : parameters)
			paramToType.put(param.getImage(), param.getType());
		
		List<Precondition> preconditions =
			parsePreconditions(precondNode, paramToType);
		
		List<Effect> effects =
			parseEffects(effectNode, paramToType);
		
		for (Effect effect : effects)
			if (effect instanceof Predicate)
				data.setDynamic(((Predicate) effect).getImage());
		
		Action action = new Action(image, parameters, preconditions, effects);
		data.addAction(action);
	}
}
