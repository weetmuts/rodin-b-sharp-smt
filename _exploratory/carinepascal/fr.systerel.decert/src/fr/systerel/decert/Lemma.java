/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * This class is the entry-point for the construction of lemmas.
 */
public final class Lemma {

	// =========================================================================
	// Constants
	// =========================================================================
	/** The formula factory. */
	final static FormulaFactory ff = FormulaFactory.getDefault();

	// =========================================================================
	// Variables
	// =========================================================================

	/** The lemma title. */
	private final String title;

	/** The lemma origin. */
	private final String origin;

	/** The lemma description. */
	private final String comment;

	/** The theories on which this lemma is based. */
	private final List<Theory> theories;

	/** The type environment for this lemma. */
	private final ITypeEnvironment environment;

	/** The hypotheses introduced in this lemma. */
	private final List<LemmaPredicate> hypotheses;

	/**
	 * The goal of this lemma, according to the the hypotheses and the type
	 * environment.
	 */
	private final LemmaPredicate goal;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds a lemma.
	 * 
	 * @param title
	 *            the title to be assigned to this lemma
	 * @param origin
	 *            the origin to be specified for this lemma
	 * @param comment
	 *            the description to be added to this lemma        
	 * @param goal
	 *            the goal of this lemma     
	 */
	public Lemma(final String title, final String origin, final String comment, final LemmaPredicate goal) {
		this.origin = origin;
		this.title = title;
		this.comment = comment;
		this.theories = new ArrayList<Theory>();
		this.environment = ff.makeTypeEnvironment();
		this.hypotheses = new ArrayList<LemmaPredicate>();
		this.goal = goal;
	}
	
	/**
	 * Builds a lemma.
	 * 
	 * @param title
	 *            the title to be assigned to this lemma
	 * @param origin
	 *            the origin to be specified for this lemma        
	 * @param goal
	 *            the goal of this lemma          
	 */
	public Lemma(final String title, final String origin, final LemmaPredicate goal) {
	  this(title,origin,"",goal);
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the lemma title.
	 * 
	 * @return the lemma title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * Gets the lemma origin.
	 * 
	 * @return the lemma origin
	 */
	public final String getOrigin() {
		return origin;
	}

	/**
	 * Gets the lemma description.
	 * 
	 * @return the lemma description
	 */
	public final String getComment() {
		return comment;
	}

	/**
	 * Gets the mathematical theories related to this lemma.
	 * 
	 * @return a list of theories
	 */
	public final List<Theory> getTheories() {
		return theories;
	}

	/**
	 * Gets the type environment for this lemma.
	 * 
	 * @return a type environment
	 */
	public final ITypeEnvironment getTypeEnvironment() {
		return environment;
	}

	/**
	 * Gets the hypotheses introduced in this lemma.
	 * 
	 * @return a list of hypotheses
	 */
	public final List<LemmaPredicate> getHypotheses() {
		return hypotheses;
	}

	/**
	 * Gets the goal of this lemma.
	 * 
	 * @return a goal
	 */
	public final LemmaPredicate getGoal() {
		return goal;
	}

	// =========================================================================
	// Setters
	// =========================================================================

	/**
	 * Associates a new mathematical theory to this lemma.
	 * 
	 * @param theory
	 *            the theory to be associated to this lemma
	 */
	public final void addTheory(final Theory theory) {
		theories.add(theory);
	}

	/**
	 * Adds a new variable in the type environment.
	 * 
	 * @param variable
	 *            the variable to be added to the type environment
	 */
	public final void addToTypeEnvironment(final Variable variable) {
		environment.addName(variable.getName(), variable.getType());
	}

	/**
	 * Adds a new hypothesis to this lemma.
	 * 
	 * @param hypothesis
	 *            the hypothesis to be added to this lemma
	 */
	public final void addHypothesis(final LemmaPredicate hypothesis) {
		hypotheses.add(hypothesis);
	}

	// =========================================================================
	// Type-checking
	// =========================================================================

	/**
	 * Determines if the hypotheses and the goal type-check in the type
	 * environment.
	 * 
	 * @return the list of problems encountered during the type
	 *         checking
	 */
	public final List<Result> typeCheck() {
		List<Result> problems = new ArrayList<Result>();
		ITypeCheckResult result = null;
		for (LemmaPredicate hypothesis : hypotheses) {
			result = hypothesis.getContent().typeCheck(environment);
			if (!result.hasProblem())
				environment.addAll(result.getInferredEnvironment());
			else
				problems.add(new Result(hypothesis,result));
		}
		result = goal.getContent().typeCheck(environment);
		if (!result.hasProblem())
			environment.addAll(result.getInferredEnvironment());
		else
			problems.add(new Result(goal,result));
		return problems;
	}

}
