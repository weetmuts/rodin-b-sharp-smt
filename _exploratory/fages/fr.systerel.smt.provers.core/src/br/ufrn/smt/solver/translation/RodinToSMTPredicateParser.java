/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Creation
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTIdentifier;
import fr.systerel.smt.provers.ast.SMTNode;
import fr.systerel.smt.provers.ast.commands.SMTDeclareFunCommand;

/**
 * This class is used to parse Event-B predicates, translates them into SMT-LIB
 * syntax with macros, and write them in an SMT-LIB file.
 */
public class RodinToSMTPredicateParser {
	private String translationPath;
	private String smtFilePath;
	private File smtFile;
	private final String lemmaName;
	private final Signature signature;
	private final Sequent sequent;
	private final List<String> macros;
	public static int counter = 0;
	
	
	/**
	 * Constructors
	 */
	public RodinToSMTPredicateParser(final String lemmaName,
			final List<Predicate> assumptions, final Predicate goal) {
		this.translationPath = System.getProperty("user.home");
		this.lemmaName = lemmaName;
		this.smtFilePath = this.translationPath + "/" + this.lemmaName + /*counter++ +*/ ".smt";
		this.signature = parseSignature(assumptions, goal);
		this.sequent = parseSequent(assumptions, goal);
		this.macros = new ArrayList<String>();			
	}
	
	public RodinToSMTPredicateParser(final List<Predicate> assumptions,
			final Predicate goal) {
		this("lemma", assumptions, goal);
	}

	/**
	 * This method parses hypothesis and goal predicates and translates them
	 * into SMT nodes
	 */
	private static Sequent parseSequent(final List<Predicate> assumptions,
			final Predicate goal) {
		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();
		
		HashSet<String> boundIdentifiers = new HashSet<String>();
		HashSet<String> freeIdentifiers = new HashSet<String>();
		
		ArrayList<String> a = new ArrayList<String>();
		ArrayList<String> b = new ArrayList<String>();
		
		for (Predicate assumption : assumptions) {						
			IdentifiersAndSMTStorage iSMT = TranslatorV1_2.translate1(assumption,a,b);
			
			boundIdentifiers.addAll(iSMT.getBoundIdentifiers());
			freeIdentifiers.addAll(iSMT.getFreeIdentifiers());			
			translatedAssumptions.add(iSMT.getSmtFormula());
			
			a = new ArrayList<String>(boundIdentifiers);
			b = new ArrayList<String>(freeIdentifiers);
		}
			
		SMTFormula smtFormula = TranslatorV1_2.translate(goal,a,b);		
		return new Sequent(translatedAssumptions, smtFormula);
	}

	private static Signature parseSignature(final List<Predicate> assumptions,
			final Predicate goal) {
		final List<String> sorts = new ArrayList<String>();
		final HashMap<String, String> funs = new HashMap<String, String>();
		final List<SMTDeclareFunCommand> declarefuns = new ArrayList<SMTDeclareFunCommand>();
		final HashMap<String, String> singleQuotVars = new HashMap<String, String>();
		final HashMap<String, String> preds = new HashMap<String, String>();

		boolean insertPairDecl = false;
		
		final HashMap<String, Type> typeEnvironment = extractTypeEnvironment(
				assumptions, goal);
		for (final Entry<String, Type> var : typeEnvironment.entrySet()) {
			final String varName = var.getKey();
			if (varName.contains("\'")) {
				final String alternativeName = renamePrimeVar(varName,
						typeEnvironment);
				singleQuotVars.put(varName, alternativeName);
			}
			final Type varType = var.getValue();
			// Regra 4
			if (varName.equals(varType.toString())) {
				sorts.add(varType.toString());
			}
			// Regra 6
			else if (varType.getSource() != null) {
				
				if(varType.getSource().getSource() != null || varType.getSource().getBaseType() != null)
				{
					//TODO: Insert an Error message and abort, cartesian product of cartesian product || cartesian product of power type is not implemeted yet
					System.err.println("Cartesian product of cartesian product || Cartesian product of power type is not implemented yet");
				}			
				
				String type1 = getSMTAtomicExpressionFormat(varType.getSource()
									.toString());
				String type2 = getSMTAtomicExpressionFormat(varType.getTarget()
									.toString());				
				
				String pair = "(Pair "
						+ type1
						+ " "
						+ type2 
						+ ")";
				preds.put(varName, pair);
				
				if(!insertPairDecl)
				{
					sorts.add("(Pair 's 't)");
				}				
				if(!sorts.contains(type1))
				{
					sorts.add(type1);
				}
				if(!sorts.contains(type2))
				{
					sorts.add(type2);
				}				
				insertPairDecl = true;
				
			} else if (varType.getBaseType() != null) {
				if (varName.equals(varType.getBaseType().toString())) {
					sorts.add(varName);
				} else {
					preds.put(varName, getSMTAtomicExpressionFormat(varType
							.getBaseType().toString()));
				}
			} else {
				funs.put(varName,
						getSMTAtomicExpressionFormat(varType.toString()));
				declarefuns.add(new SMTDeclareFunCommand(new SMTIdentifier(
						varName), new Type[] {}, varType));
			}
		}		
		if(insertPairDecl)
		{
			funs.put("pair 's 't", " (Pair 's 't)");
		}
		return new Signature(sorts, funs, declarefuns, singleQuotVars, preds);
	}

	private static HashMap<String, Type> extractTypeEnvironment(
			final List<Predicate> assumptions, final Predicate goal) {
		HashMap<String, Type> typeEnvironment = new HashMap<String, Type>();

		extractFreeVarsTypes(typeEnvironment, goal);
		extractBoundVarsTypes(typeEnvironment, goal);

		for (final Predicate assumption : assumptions) {
			extractFreeVarsTypes(typeEnvironment, assumption);
			extractBoundVarsTypes(typeEnvironment, assumption);
		}

		return typeEnvironment;
	}

	private static void extractFreeVarsTypes(
			final HashMap<String, Type> typeEnvironment,
			final Predicate predicate) {
		FreeIdentifier[] freeVars = predicate.getFreeIdentifiers();
		if (freeVars != null) {
			for (int i = 0; i < freeVars.length; i++) {
				String name = freeVars[i].getName();
				Type type = freeVars[i].getType();
				typeEnvironment.put(name, type);
			}
		}
	}

	private static void extractBoundVarsTypes(
			final HashMap<String, Type> typeEnvironment,
			final Predicate predicate) {
		BoundIdentifier[] boundVars = predicate.getBoundIdentifiers();
		if (boundVars != null) {
			for (int i = 0; i < boundVars.length; i++) {
				String name = boundVars[i].toString();
				Type type = boundVars[i].getType();
				typeEnvironment.put(name, type);
			}
		}
	}

	/**
	 * ??? replaces all occurrences of character "\'" in the given string and
	 * add it into the given type environment
	 */
	private static String renamePrimeVar(final String name,
			final HashMap<String, Type> typeEnv) {
		int countofQuots = name.length() - name.lastIndexOf('\'');

		String alternativeName = name.replaceAll("'", "_" + countofQuots + "_");
		Type t = typeEnv.get(alternativeName);
		while (t != null) {
			countofQuots = countofQuots + 1;
			alternativeName = name + "_" + countofQuots;
			t = typeEnv.get(alternativeName);
		}
		return alternativeName;
	}

	public static String getSMTAtomicExpressionFormat(String atomicExpression) {
		if (atomicExpression.equals("\u2124")) { // INTEGER
			return "Int";
		} else if (atomicExpression.equals("\u2115")) { // NATURAL
			return "Nat";
		} else if (atomicExpression.equals("\u2124" + 1)) {
			return "Int1";
		} else if (atomicExpression.equals("\u2115" + 1)) {
			return "Nat1";
		} else if (atomicExpression.equals("BOOL")) {
			return "Bool";
		} else if (atomicExpression.equals("TRUE")) {
			return "true";
		} else if (atomicExpression.equals("FALSE")) {
			return "false";
		} else if (atomicExpression.equals("\u2205")) {
			return "emptyset";
		}
		return atomicExpression;
	}

	private PrintWriter openSMTFileWriter() {
		try {
			this.smtFile = new File(this.smtFilePath);
			if (!this.smtFile.exists()) {
				this.smtFile.createNewFile();
			}

			final PrintWriter smtFileWriter = new PrintWriter(
					new BufferedWriter(new FileWriter(this.smtFile)));

			return smtFileWriter;

		} catch (IOException ioe) {
			ioe.printStackTrace();
			ioe.getMessage();
			return null;
		}
	}

	private static void closeSMTFileWriter(PrintWriter smtFileWriter) {
		smtFileWriter.close();
	}

	private static String extraHashMapSection(
			final HashMap<String, String> extraMap, final String sectionName) {
		String extraSection = ":" + sectionName + "(";
		for (final Entry<String, String> extraElt : extraMap.entrySet()) {
			extraSection = extraSection + "(" + extraElt.getKey() + " "
					+ extraElt.getValue() + ")";
		}
		extraSection = extraSection + ")";
		return extraSection;
	}

	private static String formatAssumption(String assumptions,
			final String assumption) {
		return assumptions + ":assumption " + assumption + "\n";
	}

	private static String formatExtraMacros(String macros, final String macro) {
		return macros + "\n" + macro;
	}

	private static String benchmarkCmdClosing() {
		return ")";
	}

	private String benchmarkCmdOpening() {
		return "(benchmark " + this.lemmaName + " ";
	}

	private String logicSection() {
		return ":logic " + this.signature.getLogic();
	}

	private String extrasortsSection() {
		String extrasorts = ":extrasorts (";
		for (final String sort : this.signature.getSorts()) {
			extrasorts = extrasorts + " " + sort;
		}
		extrasorts = extrasorts + ")";
		return extrasorts;
	}

	private String extrapredsSection() {
		return extraHashMapSection(this.signature.getPreds(), "extrapreds");
	}

	private String extrafunsSection() {
		return extraHashMapSection(this.signature.getFuns(), "extrafuns");
	}

	private String extramacrosSection() {
		String extramacros = ":extramacros (";
		for (final String macro : this.macros) {
			extramacros = formatExtraMacros(extramacros, macro);
		}

		extramacros = extramacros + ")";
		return extramacros;
	}

	private String assumptionsSection() {
		String assumptionsSection = "";
		for (final SMTNode<?> assumption : this.sequent.getAssumptions()) {
			assumptionsSection = formatAssumption(assumptionsSection,
					assumption.toString());
		}
		return assumptionsSection;
	}

	private String formulaSection() {
		return ":formula (not " + this.sequent.getGoal().toString() + ")";
	}

	public void printLemma(final PrintWriter pw) {
		pw.println(this.benchmarkCmdOpening());
		pw.println(this.logicSection());
		if (!this.signature.getSorts().isEmpty()) {
			pw.println(this.extrasortsSection());
		}
		if (!this.signature.getPreds().isEmpty()) {
			pw.println(this.extrapredsSection());
		}
		if (!this.signature.getFuns().isEmpty()) {
			pw.println(this.extrafunsSection());
		}
		if (!this.macros.isEmpty()) {
			pw.println(this.extramacrosSection());
		}
		pw.println(this.assumptionsSection());
		pw.println(this.formulaSection());
		pw.println(benchmarkCmdClosing());
	}

	public File getSMTFile() {
		return smtFile;
	}

	public void writeSMTFile() {
		final PrintWriter smtFileWriter = this.openSMTFileWriter();
		this.printLemma(smtFileWriter);
		closeSMTFileWriter(smtFileWriter);
	}
}
