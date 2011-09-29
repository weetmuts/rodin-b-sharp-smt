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

import org.eventb.core.ast.ITypeEnvironment;

public class LemmaData {

	public LemmaData(final String lemmaName, final List<String> hypotheses,
			final String goal, final ITypeEnvironment te, final String origin,
			final String comments, final List<String> theories) {
		super();
		this.lemmaName = lemmaName;
		this.hypotheses = hypotheses;
		this.goal = goal;
		this.te = te;
		this.origin = origin;
		this.comments = comments;
		this.theories = theories;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public List<String> getTheories() {
		return theories;
	}

	public void setTheories(final List<String> theories) {
		this.theories = theories;
	}

	private String lemmaName;
	private List<String> hypotheses;
	private String goal;
	private ITypeEnvironment te;
	private String origin;
	private String comments;
	private List<String> theories;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(final String origin) {
		this.origin = origin;
	}

	public ITypeEnvironment getTe() {
		return te;
	}

	public void setTe(final ITypeEnvironment te) {
		this.te = te;
	}

	public String getLemmaName() {
		return lemmaName;
	}

	public void setLemmaName(final String lemmaName) {
		this.lemmaName = lemmaName;
	}

	public List<String> getHypotheses() {
		return hypotheses;
	}

	public void setHypotheses(final List<String> hypotheses) {
		this.hypotheses = hypotheses;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(final String goal) {
		this.goal = goal;
	}

}
