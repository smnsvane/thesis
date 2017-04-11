package pddl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Internal (private) enum used to distinguish between whether a {@link Requirement}
 * is an abbreviation for a common set of (other) requirements or a planner functionality
 * adding requirement.
 *
 */
enum RequirementType
{
	functionality, abbreviation;
}

/**
 * TODO
 */
public enum Requirement
{
	// PDDL 3.0 build in modules

	/**
	 * Basic STRIPS-style adds and deletes.
	 */
	strips("strips", RequirementType.functionality),
	
	/**
	 * Allow type names in declarations of variables.
	 */
	typing("typing", RequirementType.functionality),
	
	/**
	 * Allow <code>not</code> in goal descriptions.
	 */
	negative_preconditions("negative-preconditions", RequirementType.functionality),
	
	/**
	 * Allow <code>or</code> in goal descriptions.
	 */
	disjunctive_preconditions("disjunctive-preconditions", RequirementType.functionality),

	/**
	 * Support <code>&equal;</code> as built-in predicate.
	 */
	equality("equality", RequirementType.functionality),
	
	/**
	 * Allow <code>exists</code> in goal descriptions.
	 */
	existential_preconditions("existential-preconditions", RequirementType.functionality),
	
	/**
	 * Allow <code>forall</code> in goal descriptions
	 */
	universal_preconditions("universal-preconditions", RequirementType.functionality),
			
	/**
	 * {@link #existential_preconditions} + {@link #universal_preconditions}
	 */
	quantified_preconditions("quantified-preconditions", RequirementType.abbreviation,
			existential_preconditions, universal_preconditions),
			
	/**
	 * Allow <code>when</code> in action effects.
	 */
	conditional_effects("conditional-effects", RequirementType.functionality),
	
	/**
	 * Allow function definitions and use of effects using assignment operations
	 * and arithmetic preconditions.
	 */
	fluents("fluents", RequirementType.functionality),
		
	/**
	 * {@link #strips} + {@link #typing} + {@link #negative_preconditions} + 
	 * {@link #disjunctive_preconditions} + {@link #equality} + 
	 * {@link #quantified_preconditions} + {@link #conditional_effects}
	 */
	adl("adl", RequirementType.abbreviation,
			strips, typing, negative_preconditions, disjunctive_preconditions, equality,
			quantified_preconditions, conditional_effects),

	/**
	 * Allows durative actions.  Note that this does not imply {@link #fluents}.
	 */
	durative_actions("durative-actions", RequirementType.functionality),
	
	/**
	 * Allows predicates whose truth value is defined by a formula.
	 */
	derived_predicates("derived-predicates", RequirementType.functionality),
	
	/**
	 * Allows the initial state to specify literals that will become true at a
	 * specified time point. Implies {@link durative_actions}.
	 */
	timed_initial_literals("timed-initial-literals", RequirementType.functionality,
			durative_actions),
	
	/**
	 * Allows use of preferences in action preconditions and goals.
	 */
	preferences("preferences", RequirementType.functionality),
	
	/**
	 * Allows use of constraints fields in domain and problem files. These may 
	 * contain modal operators supporting trajectory constraints.
	 */
	constraints("constraints", RequirementType.functionality),
	
	/**
	 * Allows use of shadow heuristic fields in domain files.
	 * Shadowed goal conditions can have a different heuristic weight.
	 */
	shadow("shadow", RequirementType.functionality);
	
	
	private String image;
	private RequirementType type;
	private Set<Requirement> sub;
	
	/**
	 * Create a {@link Requirement} enum given its image, a type and an optional list of other
	 * {@link Requirement}s. 
	 * 
	 * @param image the keyword used in PDDL to describe the requirement definition,
	 * 					e.g. "quantified-preconditions".
	 * @param type whether the {@link Requirement} is an abbreviation for a common set of (other)
	 * 				requirement definitions or a planner functionality adding module.
	 * @param sub an array of {@link Requirement}s that the new requirement is abbreviation of, if
	 * 				the type of the new requirement is an abbreviation of a common set of
	 * 				requirements. Otherwise the array reflects a list of requirements implied by
	 * 				the new requirement definition.
	 */
	private Requirement(String image, RequirementType type, Requirement...sub)
	{
		// A type should be applied the requirement definition
		if (type == null)
			throw new RuntimeException("The requirement definition " + this + " miss a type");
		
		// It make no sense to create a requirement definition of the type "shorthand" without
		// having any other requirements abbreviating.
		if (type == RequirementType.abbreviation && sub.length == 0)
			throw new RuntimeException("Requirement definition (" + this + ") of type \"" + 
					type + "\" should contain at least one (other, already existing) " +
					"requirement definition.");
		
		this.image = image;
		this.type  = type;

		this.sub = new HashSet<Requirement>(Arrays.asList(sub));
	}
	
	/**
	 * Returns the set of {@link Requirement}s that adds functionality to the planner.
	 * All requirements that are abbreviations for common sets of (other) requirements
	 * is "unfolded" recursively, so that only a set of "functionality adding"
	 * requirements is returned.
	 *  
	 * @param requirement The requirement for which a {@link Set} of functionality
	 *			 			adding requirements is wanted. This can by both a functionality
	 *						adding requirement itself or a requirement abbreviations for
	 *						common sets of requirements.
	 * @return a set of requirements adding functionality to the planner, obtained recursively
	 * 			by "unfold" abbreviations for common sets of requirements, if the given
	 * 			requirement is a abbreviation, - a set only containing the requirement itself
	 * 			given a functionality adding requirement.
	 */
	public static Set<Requirement> functionality(Requirement requirement) {
		Set<Requirement> requirements = new HashSet<Requirement>();
		if (requirement.type == RequirementType.abbreviation)
			for(Requirement subRequirement : requirement.sub)
				// Call functionality(...) recursively to "unfold" abbreviations for common
				// sets of requirements, that contains abbreviations itself.
				requirements.addAll(functionality(subRequirement));
		else
			requirements.add(requirement);
		return requirements;
	}
	
	@Override
	public String toString() {
		return ':'+image;
	}
	
	/**
	 * Returns a a {@link Requirement} looked up by its image.
	 * 
	 * @param image the keyword used in PDDL to describe the requirement definition,
	 * 			e.g. "quantified-preconditions".
	 * @return the {@link Requirement} enum corresponding the given image.
	 */
	public static Requirement getRequirement(String image)
	{
		for (Requirement r : values())
			if (r.image.equals(image))
				return r;
		return null;
	}
	
	/**
	 * Returns a {@link Set} of {@link Requirement}s that is implied by the given requirement.
	 * 
	 * @param requirement the requirement for which a {@link Set} of implied {@link Requirement}s is wanted.
	 * @return a {@link Set} of implied {@link Requirement}s (of the given requirement.)
	 * 			if no requirements are implied an empty {@link Set} will be returned.
	 */
	public static Set<Requirement> implies(Requirement requirement) {
		// If the requirement is functionality adding for the planner, the implied
		// requirements is the list of the sub-requirement itself.
		if (requirement.type == RequirementType.functionality)
			return requirement.sub;
		
		Set<Requirement> implies = new HashSet<Requirement>();
		for(Requirement subRequirement : requirement.sub)
			implies.addAll(implies(subRequirement));
		return implies;
	}
	
	/**
	 * Computes and returns all names of requirements in a string array,
	 * requirement names will be sorted in enum comparator order.
	 * 
	 * @return an array containing names (with ':' as prefix) of all {@link Requirement}s.
	 */
	public static String[] getNames()
	{
		String[] reqs = new String[Requirement.values().length];
		for (int i = 0; i < reqs.length; i++)
			reqs[i] = ':'+Requirement.values()[i].name();
		return reqs;
	}
	
	/**
	 * Method for test purpose only ...
	 * @param args N/A
	 */
//	public static void main(String[] args) {
//		// Get all requirements adding functionality to the planner, for the nested requirement
//		// definition ADL (ADL abbreviate quantified_preconditions which abbreviate other functionality;
//		// existential-preconditions and universal-preconditions)
//		System.out.println(functionality(Requirement.adl));
//		
//		// Get all implied requirements of timed-initial-literals.
//		System.out.println(implies(Requirement.timed_initial_literals));
//	}
}
