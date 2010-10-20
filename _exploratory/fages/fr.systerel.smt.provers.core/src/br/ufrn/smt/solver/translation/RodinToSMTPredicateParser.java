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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.Predicate;

/**
 * This class is used to parse Event-B predicates, translates them into SMT-LIB
 * syntax with macros, and write them in an SMT-LIB file.
 */
public class RodinToSMTPredicateParser {
	private long minimalFiniteValue = 0;
	private long minimalEnumValue = 0;
	private long minimalElemvalue = 0;
	private String translatedPath;
	private ArrayList<String> macros = new ArrayList<String>();
	private ArrayList<String> assumptions = new ArrayList<String>();
	private TypeEnvironment typeEnvironment;
	private String smtGoal = "";
	private File translatedFile;
	private boolean isNecessaryAllMacros = false;

	/**
	 * Constructor
	 * 
	 * @param hypotheses
	 * @param goal
	 */
	public RodinToSMTPredicateParser(ArrayList<Predicate> hypotheses,
			Predicate goal) {
		this.typeEnvironment = new TypeEnvironment(hypotheses, goal);
		parsePredicates();
		printLemmaOnFile();
	}

	/**
	 * Getters
	 */
	public TypeEnvironment getTypeEnvironment() {
		return typeEnvironment;
	}

	public File getTranslatedFile() {
		return translatedFile;
	}

	public long getMinimalFiniteValue() {
		return minimalFiniteValue;
	}

	public long getMinimalEnumValue() {
		return minimalEnumValue;
	}

	public long getMinimalElemvalue() {
		return minimalElemvalue;
	}

	public ArrayList<String> getMacros() {
		return macros;
	}

	public ArrayList<String> getAssumptions() {
		return assumptions;
	}

	/**
	 * This method parses hypothesis and goal predicates and translates them
	 * into SMT nodes
	 */
	public void parsePredicates() {
		for (Predicate hyp : typeEnvironment.getHypotheses()) {
			final String translatedHyp = VisitorV1_2.translateToSMTNode(
					typeEnvironment, hyp).toString();
			if (!translatedHyp.isEmpty()) {
				assumptions.add(translatedHyp);
			}
		}

		if (typeEnvironment.getGoal() != null) {
			final String translatedGoal = VisitorV1_2.translateToSMTNode(
					typeEnvironment, typeEnvironment.getGoal()).toString();
			if (!translatedGoal.isEmpty()) {
				smtGoal = translatedGoal;
			}
		}
	}

	private void printLemmaOnFile() {
		String benchmark = "(benchmark smtTesteComArvoreSintatica ";// nameOfThisLemma;

		try {
			String s = "";
			if (translatedPath != null) {
				s = translatedPath;
			} else {
				s = System.getProperty("user.home");
			}
			translatedFile = new File(s + "/smtComArv.smt");
			if (!translatedFile.exists()) {
				translatedFile.createNewFile();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(translatedFile)));
			out.println(benchmark);
			out.println(":logic UNKNOWN");
			if (!typeEnvironment.getSorts().isEmpty()) {
				String extrasorts = ":extrasorts (";
				for (int i = 0; i < typeEnvironment.getSorts().size(); i++) {
					extrasorts = extrasorts + " "
							+ typeEnvironment.getSorts().get(i);
				}
				extrasorts = extrasorts + ")";
				out.println(extrasorts);
			}

			if (!typeEnvironment.getPreds().isEmpty()) {
				Set<Entry<String, String>> set = typeEnvironment.getPreds()
						.entrySet();
				Iterator<Entry<String, String>> iterator = set.iterator();
				String extrapreds = ":extrapreds (";
				while (iterator.hasNext()) {
					Entry<String, String> el = iterator.next();
					extrapreds = extrapreds + "(" + el.getKey() // preds.eget(i).getFirstElement()
							+ " " + el.getValue() + ")"; // preds.get(i).getSecondElement()
															// + ")";
				}
				extrapreds = extrapreds + ")";
				out.println(extrapreds);
			}

			if (!typeEnvironment.getFuns().isEmpty()) {
				Set<Entry<String, String>> set = typeEnvironment.getFuns()
						.entrySet();
				Iterator<Entry<String, String>> iterator = set.iterator();
				String extrafuns = ":extrafuns (";
				while (iterator.hasNext()) {
					Entry<String, String> el = iterator.next();
					extrafuns = extrafuns + "(" + el.getKey() // preds.eget(i).getFirstElement()
							+ " " + el.getValue() + ")"; // preds.get(i).getSecondElement()
															// + ")";
				}
				extrafuns = extrafuns + ")";
				out.println(extrafuns);
			}

			out.println(":extramacros(");

			out.println("(union (lambda (?p1 ('t boolean)) (?q1 ('t boolean)) . (lambda (?x6 't) . (or (?p1 ?x6) (?q1 ?x6)))))");
			out.println("(emptyset (lambda (?x5 't). false))");
			out.println("(inter (lambda (?pd ('sd boolean))(?qd ('sd boolean)) . (lambda (?x7d 'sd) . (and (?pd ?x7d) (?qd ?x7d)))))");
			out.println("(setminus (lambda (?p2 ('t boolean)) (?q2 ('t boolean)) . (lambda (?x8 't) . (and (?p2 ?x8) (not (?q2 ?x8))))))");
			out.println("(in (lambda (?x9 't) (?p3 ('t boolean)) . (?p3 ?x9)))");
			out.println("(subseteq (lambda (?s ('t boolean)) (?h ('t boolean)) . (forall (?t 't). (implies (?s ?t) (?h ?t)))))");
			out.println("(subset (lambda (?p4 ('t boolean)) (?q3 ('t boolean)) . (and (subseteq ?p4 ?q3) 	(not (= ?p4 ?q3	)))))");
			out.println("(Nat (lambda (?i Int) . (<= 0 ?i)))");
			out.println("(ismax (lambda (?m Int) (?pi (Int boolean)) . (and (?pi ?m)(forall (?i1 Int) . (implies (?pi ?i1) (<= ?i1 ?m))))))");
			out.println("(ismin (lambda (?m2 Int) (?ta (Int boolean)) . (and(in ?m2 ?ta)(forall (?xb Int) . (implies (in ?xb ?ta)(<=?m2 ?xb))))))");
			out.println("(Nat1 (lambda (?i Int) . (<= 1 ?i)))");
			out.println("(cartesianproduct (lambda (?p12 ('t1 boolean)) (?q12 ('t2 boolean)) . (lambda (?x1 't1) (?x2 't2) . (and (?p12 ?x1) (?q12 ?x2)))))");
			out.println("(range (lambda (?i1 Int) (?i2 Int) . (lambda (?i Int) . (and (<= ?i1 ?i) (<= ?i ?i2)))))");
			out.println("(subseteq2 (lambda (?p11 ('t1 't2 boolean)) (?q ('t1 't2 boolean)) . (forall (?x1 't1) (?x2 't2) . (implies (?p11 ?x1 ?x2) (?q ?x1 ?x2)))))");
			out.println("(union2 (lambda (?p2c ('t1c 't2c boolean)) (?q2c ('t1c 't2c boolean)) . (lambda (?x1c 't1c) (?x2c 't2c) . (or (?p2c ?x1c ?x2c) (?q2c ?x1c ?x2c)))))");
			out.println("(emptyset2 (lambda (?x 't1) (?y 't2). false))");
			out.println("(inter2 (lambda (?p ('t1 't2 boolean)) (?q ('t1 't2 boolean)) . (lambda (?x 't1) (?y 't2) . (and (?p ?x ?y) (?q ?x ?y)))))");
			out.println("(Pair (lambda (?e1 't) (?e2 't) . (lambda (?f1 't) (?f2 't) . (and (= ?f1 ?e1) (= ?f2 ?e2)))))");
			out.println("(finite (lambda (?tb ('s boolean)) (?pe boolean) (?f ('s Int)) (?k Int).(iff ?pe (and (forall (?xa 's).(implies (in ?xa ?tb)(in (?f ?xa)(range 1 ?k))))(forall (?xa 's)(?ya 's).(implies (and (in ?xa ?tb)(in ?ya ?tb)(not (= ?xa ?ya)))(not (= (?f ?xa)(?f ?ya)))))))))");
			out.println("(domain (lambda (?r ((Pair 't1 't2) boolean)) . (lambda (?x1 't1) . (exists (?x2 't2) . (?r (Pair ?x1 ?x2))))))");
			// Domain
			if (isNecessaryAllMacros) {
				// range and relational image

				out.println("(ran (lambda (?r ((Pair 's 't) boolean) (lambda (?y 't)(exists (?x 's)(r (pair ?x ?y)))))))");
				out.println("(img (lambda (?r ((Pair 's 't) boolean)(?p ('s boolean) (lambda (?y 't) (exists (?x 's) (and (?p ?x)(r (pair ?x ?y)))))))))");

				// Todas essas macros estão feitas sem os pontos, inclusive a
				// macro img acima
				// Domain restriction and subtraction, range restriction and
				// subtraction
				out.println("(domr (lambda (?r ((Pair 's 't) boolean)(?s ('s boolean)) (lambda (?p (Pair 's 't)(and (?r ?p)(?s (fst ?p)))))))) ");
				out.println("(doms (lambda (?r ((Pair 's 't) boolean)(?s ('s boolean)) (lambda (?p (Pair 's 't)(and (?r ?p)(not (?s (fst ?p)))))))))");
				out.println("(ranr (lambda (?r ((Pair 's 't) boolean)(?s ('t boolean)) (lambda (?p (Pair 's 't)(and (?r ?p)(?s (snd ?p))))))))");
				out.println("(rans (lambda (?r ((Pair 's 't) boolean)(?s ('t boolean)) (lambda (?p (Pair 's 't)(and (?r ?p)(not (?s (snd ?p)))))))))");

				// Inverse, composition, overwrite and identidy
				out.println("(inv (lambda (?r ((Pair 's 't) boolean) (lambda (?p (Pair 's 't)(?r(pair (snd ?p)(fst ?p))))))))");
				out.println("(comp (lambda (?r1 ((Pair 's 't) boolean)(?r2 ((Pair 't 'u) boolean) (lambda (?p (Pair 's 'u)) (exists (?x 't)(and (?r1 (pair (fst ?p) ?x)) (?r2 (pair ?x (snd ?p))))))))))");
				out.println("(ovr (lambda (?r1 ((Pair 's 't) boolean)(?r2 ((Pair 's 't) boolean) (lambda (?p (Pair 's 'u))(or (?r2 ?p)(and (?r1 ?p)(not(exists(?q (Pair 's 't)) (and (?r2 ?q) (= (fst ?q)(fst ?p)))))))))))) ");
				out.println("(id (lambda (?s ('t boolean)) (lambda ?p (Pair 't 't)) (and (?s (fst ?p)) (= (fst ?p)(snd ?p)))))");

				// Auxiliary properties on relations
				out.println("(funp (lambda (?R ((Pair 's 't) boolean)) (forall (?p (Pair 's 't))(?p1 (Pair 's 't)) (implies (and (?R ?p)(?R ?p1))(implies (= (fst ?p)(fst ?p1))(= (snd ?p)(snd ?p1)))))))");
				out.println("(injp (lambda (?R ((Pair 's 't) boolean))(funp (inv ?R))))");
				out.println("(totp (lambda (?X ('s boolean))(?R((Pair 's 't) boolean)) (forall (?p (Pair 's 't)) (= (?R ?p)(?X (fst ?p))))))");
				out.println("(surp (lambda (?Y ('t boolean))(?R ((Pair 's 't) boolean)) (forall (?p (Pair 's 't)) (= (?R ?p)(?Y (snd ?p))))))");

				// Sets of relations, functions (partial/total,
				// injective/surjective/bijective);
				out.println("(rel  (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean)) (forall (?p (Pair 's 't)) (implies (?R ?p)(and (?X (fst ?p))(?Y (snd ?p))))))))");
				out.println("(pfun (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((rel ?X ?Y)  ?R) (funp ?R)))))");
				out.println("(tfun (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((pfun ?X ?Y) ?R)(totp ?X ?R)))))");
				out.println("(pinj (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((pfun ?X ?Y) ?R)(injp ?R)))))");
				out.println("(tinj (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((pinj ?X ?Y) ?R)(totp ?X ?R)))))");
				out.println("(psur (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((pfun ?X ?Y) ?R)(surp ?Y ?R)))))");
				out.println("(tsur (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((psur ?X ?Y) ?R)(totp ?X ?R)))))");
				out.println("(bij  (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((tsur ?X ?Y) ?R)((tinj ?X ?Y) ?R)))))");
			}

			for (int i = 0; i < macros.size(); i++) {
				out.println(macros.get(i));
			}
			out.println(")");
			for (int i = 0; i < assumptions.size(); i++) {
				out.println(":assumption " + assumptions.get(i));
			}
			out.println(":formula (not" + smtGoal + ")");
			out.println(")");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
