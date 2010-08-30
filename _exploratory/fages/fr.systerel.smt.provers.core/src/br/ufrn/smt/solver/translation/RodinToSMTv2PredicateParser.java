/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel (YFT) - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

import fr.systerel.smt.provers.astV1_2.SMTLogic;
import fr.systerel.smt.provers.ast.commands.SMTAssertCommand;
import fr.systerel.smt.provers.ast.commands.SMTDeclareFunCommand;
import fr.systerel.smt.provers.ast.commands.SMTSetLogicCommand;
import fr.systerel.smt.provers.astV1_2.SMTIdentifier;

public class RodinToSMTv2PredicateParser {

	private ArrayList<Predicate> hypotheses;
	private Predicate goal;	
	private TypeEnvironment typeEnvironment = null;	
	private String logic = new String();
	private ArrayList<String> declareFuns = new ArrayList<String>();
	private ArrayList<String> asserts = new ArrayList<String>();
	private File translatedFile;
	private String translatedPath;
	
	public String getTranslatedPath() {
		return translatedPath;
	}

	public void setTranslatedPath(String translatedPath) {
		this.translatedPath = translatedPath;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	
	public ArrayList<String> getAsserts() {
		return asserts;
	}

	public void setAsserts(ArrayList<String> asserts) {
		this.asserts = asserts;
	}

	public ArrayList<String> getDeclareFuns() {
		return declareFuns;
	}

	public void setDeclareFuns(ArrayList<String> declareFuns) {
		this.declareFuns = declareFuns;
	}

	public TypeEnvironment getTypeEnvironment() {
		return typeEnvironment;
	}

	public void setTypeEnvironment(TypeEnvironment typeEnvironment) {
		this.typeEnvironment = typeEnvironment;
	}

	public ArrayList<Predicate> getHypothesis() {
		return hypotheses;
	}

	public void setHypothesis(ArrayList<Predicate> hypothesis) {
		this.hypotheses = hypothesis;
	}

	public Predicate getGoal() {
		return goal;
	}

	public void setGoal(Predicate goal) {
		this.goal = goal;
	}
	
	public File getTranslatedFile() {
		return translatedFile;
	}

	public void setTranslatedFile(File translatedFile) {
		this.translatedFile = translatedFile;
	}

	public RodinToSMTv2PredicateParser(ArrayList<Predicate> hypotheses,
			Predicate goal) throws TranslationException {
		this.hypotheses = hypotheses;
		this.goal = goal;
		this.typeEnvironment = new TypeEnvironment(hypotheses, goal);
		typeEnvironment.getTypeEnvironment();
		parsePredicates();

	}

	void parsePredicates() throws TranslationException {
		// Set logic
		SMTSetLogicCommand log = new SMTSetLogicCommand(SMTLogic.QF_LIA);
		logic = log.toString();
		
		// set declare funs
		for (SMTDeclareFunCommand dfc : typeEnvironment.getDeclarefuns()) {
			declareFuns.add(dfc.toString());
		}
				
		// Parse hypotheses
		VisitorV2_0 visHyp = null;
		if (!this.hypotheses.isEmpty()) {
			for (Predicate hyp : hypotheses) {
				visHyp = new VisitorV2_0();
				hyp.accept(visHyp);
				String translatedHyp = visHyp.getSMTNode();
				if (!translatedHyp.equals("")) {
					asserts.add(translatedHyp);
				}
			}
		}

		// Parse Goal
		VisitorV2_0 visGoal = null;
		// inverse the goal
		FormulaFactory ff= FormulaFactory.getDefault();
		Predicate inversedGoal = ff.makeUnaryPredicate(Formula.NOT, this.goal, null);
		
		if (this.goal != null) {
			visGoal = new VisitorV2_0();
			inversedGoal.accept(visGoal);
			String translatedGoal = visGoal.getSMTNode();
			if (!translatedGoal.equals("")) {
				asserts.add(translatedGoal);
			}
		}
		
		// Print SMT hyps & goal in a file
		printLemmaOnFile();
	}
	
	private void printLemmaOnFile() {
		try {
			String s = "";
			if (translatedPath != null) {
				s = translatedPath;
			} else {
				s = System.getProperty("user.home");
			}
			
			translatedFile = new File(s + "/smtComArvV2.smt");
			if (!translatedFile.exists()) {
				translatedFile.createNewFile();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(translatedFile)));
			
			// print logic
			out.println(logic);
			
			// print funs declaration 
			for (String fun : declareFuns) {
				out.println(fun);
			}
			
			// print assertions		
			for (String assertion : asserts) {
				out.println(assertion);
			}
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
