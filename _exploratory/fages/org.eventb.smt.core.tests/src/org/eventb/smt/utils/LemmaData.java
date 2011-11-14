/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.utils;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;

public class LemmaData {
	private String lemmaName;
	private List<String> hypotheses;
	private String goal;
	private ITypeEnvironment te;
	private String origin;
	@SuppressWarnings("unused")
	private String comments;
	private Set<String> theories;
	private List<String> neededHypotheses;
	private boolean goalNeeded;

	public LemmaData(final String lemmaName, final List<String> hypotheses,
			final String goal, final ITypeEnvironment te, final String origin,
			final String comments, final Set<String> theories,
			final List<String> neededHypotheses, final boolean goalNeeded) {
		super();
		this.lemmaName = lemmaName;
		this.hypotheses = hypotheses;
		this.goal = goal;
		this.te = te;
		this.origin = origin;
		this.comments = comments;
		this.theories = theories;
		this.neededHypotheses = neededHypotheses;
		this.goalNeeded = goalNeeded;
	}

	public String getLemmaName() {
		return lemmaName;
	}

	public List<String> getHypotheses() {
		return hypotheses;
	}

	public String getGoal() {
		return goal;
	}

	public ITypeEnvironment getTe() {
		return te;
	}

	public String getOrigin() {
		return origin;
	}

	public Set<String> getTheories() {
		return theories;
	}

	public List<String> getNeededHypotheses() {
		return neededHypotheses;
	}

	public boolean isGoalNeeded() {
		return goalNeeded;
	}
}
