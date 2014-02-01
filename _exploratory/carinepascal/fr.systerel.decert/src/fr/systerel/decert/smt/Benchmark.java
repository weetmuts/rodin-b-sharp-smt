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
package fr.systerel.decert.smt;

import java.util.ArrayList;
import java.util.List;

import fr.systerel.decert.Lemma;
import fr.systerel.decert.LemmaPredicate;

/**
 * This class is the entry-point for the construction of SMT-LIB benchmarks.
 */
public final class Benchmark {

	// =========================================================================
	// Variables
	// =========================================================================
	/** The formula visitor. */
	private final Visitor visitor = new Visitor();

	/** The benchmark name. */
	private final String name;

	/** The benchmark logic. */
	private final String logic;

	/** The benchmark assumptions. */
	private final List<BenchmarkFormula> assumptions;

	/** The benchmark formula. */
	private final BenchmarkFormula formula;

	/** The benchmark status. */
	private final Status status;

	/** The sorts. */
	private final List<Sort> sorts;

	/** The functions. */
	private final List<BenchmarkFunction> functions;

	/** The predicates. */
	private final List<BenchmarkPredicate> predicates;
	
	/** Some useful notes. */
	private final String notes;
	
	/** Some useful annotations. */
	private final List<Annotation> annotations;

	// =========================================================================
	// Constructors
	// =========================================================================

	/**
	 * Builds a benchmark from a lemma.
	 * 
	 * @param lemma
	 *            the lemma to be transformed
	 */
	public Benchmark(final Lemma lemma) {
		name = lemma.getTitle();
		// TODO: Parse the theories of the lemma to determine the logic to be
		// associated to this benchmark
		logic = "QF_LIA";
		assumptions = new ArrayList<BenchmarkFormula>();
		for (LemmaPredicate hypothesis : lemma.getHypotheses())
			assumptions.add(new BenchmarkFormula(visitor, hypothesis));
		formula = new BenchmarkFormula(visitor, lemma.getGoal());
		status = Status.SAT;
		sorts = new ArrayList<Sort>();
		functions = new ArrayList<BenchmarkFunction>();
		for (String s : lemma.getTypeEnvironment().getNames())
			functions.add(new BenchmarkFunction(s, lemma.getTypeEnvironment().getType(s)));
		predicates = new ArrayList<BenchmarkPredicate>();
		notes = lemma.getComment();
		annotations = new ArrayList<Annotation>();
		annotations.add(new Annotation("origin", lemma.getOrigin()));
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Gets the benchmark name.
	 * 
	 * @return the benchmark name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the benchmark logic.
	 * 
	 * @return the benchmark logic
	 */
	public final String getLogic() {
		return logic;
	}

	/**
	 * Gets the benchmark assumptions.
	 * 
	 * @return a list of assumptions
	 */
	public final List<BenchmarkFormula> getAssumptions() {
		return assumptions;
	}

	/**
	 * Gets the benchmark formula.
	 * 
	 * @return the benchmark formula
	 */
	public final BenchmarkFormula getFormula() {
		return formula;
	}

	/**
	 * Gets the benchmark status.
	 * 
	 * @return the benchmark status
	 */
	public final Status getStatus() {
		return status;
	}

	/**
	 * Gets the benchmark sorts.
	 * 
	 * @return a list of sorts
	 */
	public final List<Sort> getSorts() {
		return sorts;
	}
	
	/**
	 * Gets the benchmark functions.
	 * 
	 * @return a list of functions
	 */
	public final List<BenchmarkFunction> getFunctions() {
		return functions;
	}
	
	/**
	 * Gets the benchmark predicates.
	 * 
	 * @return a list of predicates
	 */
	public final List<BenchmarkPredicate> getPredicates() {
		return predicates;
	}
	
	/**
	 * Gets the benchmark notes.
	 * 
	 * @return the benchmark notes
	 */
	public final String getNotes() {
		return notes;
	}
	
	/**
	 * Gets the benchmark annotations.
	 * 
	 * @return the benchmark annotations
	 */
	public final List<Annotation> getAnnotations() {
		return annotations;
	}
}
