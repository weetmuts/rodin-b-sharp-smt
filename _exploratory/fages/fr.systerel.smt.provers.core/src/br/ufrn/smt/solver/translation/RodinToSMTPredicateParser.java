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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

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
	private List<String> macros = new ArrayList<String>();

	/**
	 * Constructors
	 */
	public RodinToSMTPredicateParser(final String lemmaName,
			final List<Predicate> assumptions, final Predicate goal) {
		this.translationPath = System.getProperty("user.home");
		this.lemmaName = lemmaName;
		this.smtFilePath = this.translationPath + "/" + this.lemmaName + ".smt";
		this.signature = parseSignature(assumptions, goal);
		this.sequent = parseSequent(assumptions, goal);
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
		final List<SMTNode<?>> translatedAssumptions = new ArrayList<SMTNode<?>>();

		for (Predicate assumption : assumptions) {
			translatedAssumptions.add(TranslatorV1_2.translate(assumption));
		}
		final SMTNode<?> translatedGoal = TranslatorV1_2.translate(goal);

		return new Sequent(translatedAssumptions, translatedGoal);
	}

	private static Signature parseSignature(final List<Predicate> assumptions,
			final Predicate goal) {
		final List<String> sorts = new ArrayList<String>();
		final Hashtable<String, String> funs = new Hashtable<String, String>();
		final List<SMTDeclareFunCommand> declarefuns = new ArrayList<SMTDeclareFunCommand>();
		final Hashtable<String, String> singleQuotVars = new Hashtable<String, String>();
		final Hashtable<String, String> preds = new Hashtable<String, String>();

		final Hashtable<String, Type> typeEnvironment = extractTypeEnvironment(
				assumptions, goal);
		final Iterator<Entry<String, Type>> typeEnvIterator = typeEnvironment
				.entrySet().iterator();

		while (typeEnvIterator.hasNext()) {
			final Entry<String, Type> var = typeEnvIterator.next();
			final String varName = var.getKey();
			if (varName.contains("\'")) {
				final String alternativeName = renameQuotVar(varName,
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
				String pair = "(Pair "
						+ getSMTAtomicExpressionFormat(varType.getSource()
								.toString())
						+ " "
						+ getSMTAtomicExpressionFormat(varType.getTarget()
								.toString() + ")");
				preds.put(varName, pair);
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

		return new Signature(sorts, funs, declarefuns, singleQuotVars, preds);
	}

	private static Hashtable<String, Type> extractTypeEnvironment(
			final List<Predicate> assumptions, final Predicate goal) {
		Hashtable<String, Type> typeEnvironment = new Hashtable<String, Type>();

		extractFreeVarsTypes(typeEnvironment, goal);
		extractBoundVarsTypes(typeEnvironment, goal);

		for (final Predicate assumption : assumptions) {
			extractFreeVarsTypes(typeEnvironment, assumption);
			extractBoundVarsTypes(typeEnvironment, assumption);
		}

		return typeEnvironment;
	}

	private static void extractFreeVarsTypes(
			final Hashtable<String, Type> typeEnvironment,
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
			final Hashtable<String, Type> typeEnvironment,
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
	private static String renameQuotVar(final String name,
			final Hashtable<String, Type> typeEnv) {
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

	private static String extraHashtableSection(
			final Hashtable<String, String> extraTable, final String sectionName) {
		String extraSection = ":" + sectionName + "(";
		for (final Entry<String, String> extraElt : extraTable.entrySet()) {
			extraSection = extraSection + "(" + extraElt.getKey() + " "
					+ extraElt.getValue() + ")";
		}
		extraSection = extraSection + ")";
		return extraSection;
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
		return extraHashtableSection(this.signature.getPreds(), "extrapreds");
	}

	private String extrafunsSection() {
		return extraHashtableSection(this.signature.getFuns(), "extrafuns");
	}

	private String extramacrosSection() {
		String extramacros = ":extramacros (";
		for (final String macro : this.macros) {
			extramacros = extramacros + "\n" + macro;
		}
		extramacros = extramacros + ")";
		return extramacros;
	}

	private String assumptionsSection() {
		String assumptionsSection = "";
		for (final SMTNode<?> assumption : this.sequent.getAssumptions()) {
			assumptionsSection = assumptionsSection + ":assumption"
					+ assumption.toString() + "\n";
		}
		return assumptionsSection;
	}

	private String formulaSection() {
		return ":formula (not" + this.sequent.getGoal().toString() + ")";
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
