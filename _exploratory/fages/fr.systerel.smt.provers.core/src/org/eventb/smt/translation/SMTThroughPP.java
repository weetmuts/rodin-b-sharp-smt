/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.translation;

import static org.eventb.smt.ast.SMTFactory.makeBool;
import static org.eventb.smt.ast.SMTFactory.makeInteger;
import static org.eventb.smt.ast.symbols.SMTFunctionSymbol.ASSOCIATIVE;
import static org.eventb.smt.translation.SMTLIBVersion.V1_2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;
import org.eventb.smt.ast.SMTBenchmark;
import org.eventb.smt.ast.SMTBenchmarkPP;
import org.eventb.smt.ast.SMTFactory;
import org.eventb.smt.ast.SMTFactoryPP;
import org.eventb.smt.ast.SMTFormula;
import org.eventb.smt.ast.SMTSignature;
import org.eventb.smt.ast.SMTSignatureV1_2PP;
import org.eventb.smt.ast.SMTSignatureV2_0PP;
import org.eventb.smt.ast.SMTTerm;
import org.eventb.smt.ast.SMTVar;
import org.eventb.smt.ast.symbols.SMTFunctionSymbol;
import org.eventb.smt.ast.symbols.SMTPredicateSymbol;
import org.eventb.smt.ast.symbols.SMTSortSymbol;
import org.eventb.smt.ast.symbols.SMTSymbol;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.ast.theories.SMTLogic.SMTLIBUnderlyingLogicV1_2;
import org.eventb.smt.ast.theories.SMTLogic.SMTLogicPP;
import org.eventb.smt.ast.theories.SMTLogic.SMTOperator;
import org.eventb.smt.ast.theories.SMTLogic.UFNIAv2_0;
import org.eventb.smt.ast.theories.SMTTheory;
import org.eventb.smt.ast.theories.SMTTheoryV1_2;
import org.eventb.smt.ast.theories.SMTTheoryV1_2.Booleans;
import org.eventb.smt.provers.internal.core.IllegalTagException;

/**
 * This class does the SMT translation through ppTrans. ppTrans is called first,
 * to reduce an Event-B sequent to Predicate Calculus. Then the SMT translation
 * is done.
 */
public class SMTThroughPP extends TranslatorV1_2 {
	/**
	 * The SMT factory used by the translator to make SMT symbols
	 */
	private final SMTFactory sf;

	/**
	 * The gatherer is a class used to determine the logic and sets defined with
	 * specialized membership predicates
	 */
	private Gatherer gatherer;

	/**
	 * An instance of <code>SMTThroughPP</code> is associated to a signature
	 * that is completed during the translation process.
	 */
	private SMTSignature signature;

	private final Map<SMTOperator, SMTSymbol> operatorMap = new HashMap<SMTLogic.SMTOperator, SMTSymbol>();

	/**
	 * In order to translate memberships, the approach implemented in this class
	 * defines some new predicates. <code>msTypeMap</code> is a map between each
	 * Event-B type used in a membership and the corresponding SMT-LIB
	 * actualPredicate symbols.
	 */
	private final Map<Type, SMTPredicateSymbol> msTypeMap = new HashMap<Type, SMTPredicateSymbol>();

	/**
	 * This field maps Event-B free identifiers to specialized membership
	 * predicates that represents simpler sets.
	 */
	private final Map<FreeIdentifier, SMTPredicateSymbol> specialMSPredsMap = new HashMap<FreeIdentifier, SMTPredicateSymbol>();

	/**
	 * This list contains the terms of the current membership being translated
	 * in the translation process.
	 */
	private final List<SMTTerm> membershipPredicateTerms = new ArrayList<SMTTerm>();

	/**
	 * Constructor of a PP approach translator of Event-B to SMT-LIB
	 * 
	 * @param solver
	 *            Solver which will be used to discharge the sequent
	 */
	public SMTThroughPP(final String solver, final SMTLIBVersion smtlibVersion) {
		super(solver, smtlibVersion);
		sf = SMTFactoryPP.getInstance();
	}

	/**
	 * This method recursively traverses the type tree to list all base types
	 * used to define it. That is, those which can be extracted from powerset
	 * types and product types.
	 * 
	 * @param baseTypes
	 *            the actual list of extracted base types
	 * @param type
	 *            the type from which base types must be extracted
	 * @return the list of base types extracted from this type
	 */
	private static Set<Type> getBaseTypes(final Set<Type> baseTypes,
			final Type type) {
		final boolean isAProductType = type instanceof ProductType;
		/**
		 * Base case: the type is a base type. Adds it to the list and returns
		 * the list.
		 */
		if (type.getSource() == null && type.getTarget() == null
				&& type.getBaseType() == null && !isAProductType) {
			baseTypes.add(type);
			return baseTypes;
		}

		/**
		 * The type looks like <code>alpha × beta</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (isAProductType) {
			final ProductType product = (ProductType) type;
			return getBaseTypes(getBaseTypes(baseTypes, product.getLeft()),
					product.getRight());
		}

		/**
		 * The type looks like <code>ℙ(alpha × beta)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (type.getSource() != null) {
			return getBaseTypes(getBaseTypes(baseTypes, type.getSource()),
					type.getTarget());
		}

		/**
		 * The type looks like <code>ℙ(alpha)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha.
		 */
		else if (type.getBaseType() != null) {
			return getBaseTypes(baseTypes, type.getBaseType());
		}

		/**
		 * This case should not be reached because Event-B types given as
		 * arguments are well-formed.
		 */
		else {
			throw new IllegalArgumentException(Messages.Misformed_EventB_Types);
		}
	}

	/**
	 * Builds a basename for the given type.
	 */
	private static StringBuilder buildBasenameString(
			final StringBuilder builder, final Type type) {
		final Type source = type.getSource();
		final Type target = type.getTarget();
		final Type baseType = type.getBaseType();
		final boolean isAProductType = type instanceof ProductType;

		/**
		 * Base case: the type is a base type. Appends its first character
		 * changed to upper case.
		 */
		if (source == null && target == null && baseType == null
				&& !isAProductType) {
			if (type instanceof IntegerType) {
				return builder.append("Z");
			} else {
				return builder.append(type.toString().toUpperCase().charAt(0));
			}
		}

		/**
		 * The type looks like <code>alpha × beta</code>. Calls recursively
		 * <code>buildBasenameString</code> on alpha and beta.
		 */
		else if (isAProductType) {
			final ProductType product = (ProductType) type;
			return buildBasenameString(
					buildBasenameString(builder, product.getLeft()),
					product.getRight());
		}

		/**
		 * The type looks like <code>ℙ(alpha × beta)</code>. Calls recursively
		 * <code>buildBasenameString</code> on alpha and beta.
		 */
		else if (source != null) {
			return buildBasenameString(buildBasenameString(builder, source),
					target);
		}

		/**
		 * The type looks like <code>ℙ(alpha)</code>. Calls recursively
		 * <code>buildBasenameString</code> on alpha.
		 */
		else if (baseType != null) {
			return buildBasenameString(builder, baseType);
		}

		/**
		 * This case should not be reached because Event-B types given as
		 * arguments are well-formed.
		 */
		else {
			throw new IllegalArgumentException(Messages.Misformed_EventB_Types);
		}
	}

	/**
	 * This is the pp translation method.
	 */
	private static PPProof ppTranslation(final List<Predicate> hypotheses,
			final Predicate goal) {
		final PPProof ppProof = new PPProof(hypotheses, goal, new IPPMonitor() {

			@Override
			public boolean isCanceled() {
				return false;
			}
		});

		/**
		 * Translates the original hypotheses and goal to actualPredicate
		 * calculus
		 */
		ppProof.translate();

		return ppProof;
	}

	private SMTSignature getSignature() {
		return signature;
	}

	/**
	 * determines the logic
	 * 
	 * @param hypotheses
	 *            the hypotheses
	 * @param goal
	 *            the goal
	 * @return the logic that will be used in the benchmark
	 */
	@Override
	protected SMTLogic determineLogic(final List<Predicate> hypotheses,
			final Predicate goal) {
		gatherer = Gatherer.gatherFrom(hypotheses, goal);

		switch (smtlibVersion) {
		case V1_2:
			if (gatherer.usesBoolTheory()) {
				return new SMTLogic.SMTLogicPP(SMTLogic.UNKNOWN,
						SMTTheoryV1_2.Ints.getInstance(),
						SMTTheoryV1_2.Booleans.getInstance());
			}
			return SMTLIBUnderlyingLogicV1_2.getInstance();

		default:
			// TODO : if there is no element of Ints theory in the sequent, then
			// the underlying logic of SMT-LIB 2.0 should be used (which only
			// contains the Core theory). A method usesIntsTheory will be needed
			// in the gatherer to do this.
			return UFNIAv2_0.getInstance();
		}
	}

	private void translateTypeEnvironment(final ITypeEnvironment typeEnvironment) {

		/**
		 * For each membership of the type environment,
		 */
		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			Type varType = iter.getType();
			boolean parseConstant = true;

			/**
			 * Checks if the the set expects a specialized membership predicate.
			 * If so, translates the base type of it.
			 */
			for (final FreeIdentifier setForSpecialMSPred : gatherer
					.getSetsForSpecialMSPreds()) {
				if (setForSpecialMSPred.getName().equals(varName)
						&& setForSpecialMSPred.getType().equals(varType)) {
					varType = iter.getType().getBaseType();
					parseConstant = false;
					break;
				}
			}

			/**
			 * translates the type into an SMT-LIB sort, adds it to the types
			 * mapping (and adds it to the signature sort symbols set)
			 */
			final SMTSortSymbol smtSortSymbol;
			if (!typeMap.containsKey(varType)) {
				smtSortSymbol = translateTypeName(varType);
				typeMap.put(varType, smtSortSymbol);
			} else {
				smtSortSymbol = typeMap.get(varType);
			}

			/**
			 * gets a fresh name for the type of the variable to be typed, adds
			 * it to the type names mapping (and adds it to the signature
			 * symbols set)
			 * 
			 */
			final Set<Type> baseTypes = getBaseTypes(new HashSet<Type>(),
					varType);
			for (final Type baseType : baseTypes) {
				if (!typeMap.containsKey(baseType)) {
					final SMTSortSymbol baseSort = translateTypeName(baseType);
					typeMap.put(baseType, baseSort);
				}
			}

			if (parseConstant) {
				/**
				 * gets a fresh name for the variable to be typed, adds it to
				 * the variable names mapping (and adds it to the signature
				 * symbols set)
				 */
				final SMTFunctionSymbol smtConstant;
				if (!varMap.containsKey(varName)) {
					if (isBoolTheoryAndDoesNotUseTruePred(varType)) {
						final SMTPredicateSymbol predSymbol = signature
								.freshPredicateSymbol(varName);
						varMap.put(varName, predSymbol);
						continue;
					} else {
						/**
						 * adds the typing item (<code>x ⦂ S</code>) to the
						 * signature as a constant (<code>extrafuns</code>
						 * SMT-LIB section, with a sort but no argument:
						 * <code>(x S)</code>).
						 */
						smtConstant = signature.freshConstant(varName,
								smtSortSymbol);
						varMap.put(varName, smtConstant);
					}
				}
			}
		}
	}

	/**
	 * Translate the type of the bound identifiers
	 * 
	 * @param hypotheses
	 *            the hypotheses
	 * @param goal
	 *            the goal
	 */
	private void translateBoundIdentTypes(final List<Predicate> hypotheses,
			final Predicate goal) {
		final List<Type> biTypes = BidTypeInspector.getBoundIDentDeclTypes(
				hypotheses, goal);

		final Iterator<Type> bIterator = biTypes.iterator();

		while (bIterator.hasNext()) {

			final Type varType = bIterator.next();

			/**
			 * translates the type into an SMT-LIB sort, adds it to the types
			 * mapping (and adds it to the signature sort symbols set)
			 */
			final SMTSortSymbol smtSortSymbol;
			if (!typeMap.containsKey(varType)) {
				smtSortSymbol = translateTypeName(varType);
				typeMap.put(varType, smtSortSymbol);
			} else {
				smtSortSymbol = typeMap.get(varType);
			}

			/**
			 * do the same for each base type used to define this type into an
			 * SMT-LIB sort
			 */
			final Set<Type> baseTypes = getBaseTypes(new HashSet<Type>(),
					varType);
			for (final Type baseType : baseTypes) {
				if (!typeMap.containsKey(baseType)) {
					final SMTSortSymbol baseSort = translateTypeName(baseType);
					typeMap.put(baseType, baseSort);
				}
			}
		}
	}

	/**
	 * Given a actualPredicate <code>a ∈ s</code>, with <code>a ⦂ S</code> and
	 * <code>s ⦂ ℙ(S)</code>.
	 * <p>
	 * If this actualPredicate is in accordance with the rules of optimization
	 * of translation setsForSpecialMSPreds, the membership is translated to:
	 * 
	 * (s a)
	 * 
	 * <p>
	 * else the type of <code>a</code> is mapped to the SMT-LIB actualPredicate:
	 * <code>(p S PS)</code>.
	 * 
	 * @param membershipPredicate
	 *            The membership actualPredicate that will be translated.
	 */
	private SMTFormula translateMemberShipPredicate(
			final RelationalPredicate membershipPredicate) {

		final Expression left = membershipPredicate.getLeft();
		final Expression right = membershipPredicate.getRight();

		final SMTTerm leftTerm = smtTerm(left);

		final boolean leftTagIsMapsTo = left.getTag() == Formula.MAPSTO;
		final Type leftType = left.getType();

		// Translate setsForSpecialMSPreds (special case)
		if (right instanceof FreeIdentifier) {
			final FreeIdentifier rightSet = (FreeIdentifier) right;
			if (gatherer.getSetsForSpecialMSPreds().contains(rightSet)) {
				return translateInSpecializedMembershipPredicate(leftTerm,
						leftType, leftTagIsMapsTo, rightSet);
			}
		}
		return translateInClassicMembershipPredicate(leftTerm, leftType,
				leftTagIsMapsTo, right);
	}

	/**
	 * This method translates membership predicate in a normal way (the other
	 * way is the simpler one with specialized predicates).
	 * 
	 * @param leftTerm
	 * @param leftType
	 * @param leftTagIsMapsTo
	 * @param right
	 * @return the membership translated with ad-hoc uninterpreted predicate
	 *         symbol
	 */
	private SMTFormula translateInClassicMembershipPredicate(
			final SMTTerm leftTerm, final Type leftType,
			final boolean leftTagIsMapsTo, final Expression right) {
		final SMTTerm rightTerm = smtTerm(right);

		if (!leftTagIsMapsTo) {
			membershipPredicateTerms.add(0, leftTerm);
		}
		membershipPredicateTerms.add(rightTerm);

		final int numberOfArguments = membershipPredicateTerms.size();
		final SMTSortSymbol[] argSorts = new SMTSortSymbol[numberOfArguments];
		for (int i = 0; i < numberOfArguments; i++) {
			argSorts[i] = membershipPredicateTerms.get(i).getSort();
		}

		final SMTPredicateSymbol predSymbol = getMembershipPredicateSymbol(
				leftType, argSorts);

		final SMTTerm[] args = membershipPredicateTerms
				.toArray(new SMTTerm[numberOfArguments]);

		final SMTFormula membership = SMTFactory.makeAtom(predSymbol, args,
				signature);
		membershipPredicateTerms.clear();
		return membership;
	}

	/**
	 * This method translates membership to specialized membership predicates
	 * (optimization)
	 * 
	 * @param leftTerm
	 *            the translated left child of the membership
	 * @param right
	 *            the right child of the membership
	 */
	private SMTFormula translateInSpecializedMembershipPredicate(
			final SMTTerm leftTerm, final Type leftType,
			final boolean leftTagIsMapsTo, final FreeIdentifier right) {

		if (!leftTagIsMapsTo) {
			membershipPredicateTerms.add(0, leftTerm);
		}

		final int numberOfArguments = membershipPredicateTerms.size();
		final SMTTerm[] args = new SMTTerm[numberOfArguments];
		final SMTSortSymbol[] argSorts = new SMTSortSymbol[numberOfArguments];
		for (int i = 0; i < numberOfArguments; i++) {
			args[i] = membershipPredicateTerms.get(i);
			argSorts[i] = args[i].getSort();
		}

		SMTPredicateSymbol specializedMembershipPredicate = specialMSPredsMap
				.get(right);

		if (specializedMembershipPredicate == null) {
			specializedMembershipPredicate = signature.freshPredicateSymbol(
					right.getName(), argSorts);
			specialMSPredsMap.put(right, specializedMembershipPredicate);
		}

		final SMTFormula membership = SMTFactory.makeAtom(
				specializedMembershipPredicate, args, signature);
		membershipPredicateTerms.clear();
		return membership;
	}

	/**
	 * Creates a fresh membership predicate for the given type and argument
	 * sorts if it doesn't already exists in msTypeMap. As we use unique names
	 * for each rank, we don't need to specify the name of the needed predicate.
	 * 
	 * @param type
	 *            Event-B type to be mapped.
	 * @param argSorts
	 *            SMT sorts of the arguments of the membership predicate.
	 * @return the needed membership predicate.
	 */
	private SMTPredicateSymbol getMembershipPredicateSymbol(final Type type,
			final SMTSortSymbol... argSorts) {
		SMTPredicateSymbol membershipPredicateSymbol = msTypeMap.get(type);
		if (membershipPredicateSymbol == null) {
			membershipPredicateSymbol = signature.freshPredicateSymbol(
					SMTPredicateSymbol.MS_PREDICATE_NAME, argSorts);
			msTypeMap.put(type, membershipPredicateSymbol);
		}
		assert membershipPredicateSymbol != null;
		return membershipPredicateSymbol;
	}

	/**
	 * This method is used to check if the symbol must be added as predicate or
	 * a function symbol.
	 * 
	 * @param type
	 * @return true if the type is of sort Bool and the actual translation is
	 *         not using the TRUE pred symbol. Otherwise returns false
	 */
	private boolean isBoolTheoryAndDoesNotUseTruePred(final Type type) {
		if (!gatherer.usesTruePredicate()) {
			if (type instanceof BooleanType) {
				switch (smtlibVersion) {
				case V1_2:
					for (final SMTTheory theories : signature.getLogic()
							.getTheories()) {
						if (theories instanceof Booleans) {
							return true;
						}
					}
					break;

				default:
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This method is used to translate boolean equalities, where the predicate
	 * TRUE is not rid and the left and the right side are identifiers.
	 * <p>
	 * Example:
	 * <p>
	 * b = c,
	 * <p>
	 * where b and c are of type Bool.
	 * <p>
	 * The translation is then:
	 * <p>
	 * (iff b c)
	 * 
	 * @param left
	 *            the left child of the equality
	 * @param right
	 *            the right child of the equality
	 * @return the translation of the equality
	 */
	private SMTFormula translateBoolIds(final Expression left,
			final Expression right) {
		final SMTFormula leftFormula = translateTruePred(left);
		final SMTFormula rightFormula = translateTruePred(right);

		return SMTFactory.makeIff(
				new SMTFormula[] { leftFormula, rightFormula }, smtlibVersion);
	}

	/**
	 * This method is used to translated boolean equalities, where the predicate
	 * TRUE is not rid and the left or the right side is the TRUE predicate and
	 * the other an identifier.
	 * <p>
	 * Example:
	 * <p>
	 * b = TRUE,
	 * <p>
	 * where b is of type Bool.
	 * <p>
	 * The translation is then:
	 * <p>
	 * b
	 * 
	 * @param expr
	 *            the other element of equality. It is supposed that one of the
	 *            sides are already the TRUE predicate.
	 * @return the translation of the equality
	 */
	private SMTFormula translateTruePred(final Expression expr) {
		if (expr.getTag() == Formula.TRUE) {
			throw new IllegalArgumentException(
					Messages.TRUE_On_Both_Sides_Of_Boolean_Equality_error);
		} else {
			if (gatherer.usesTruePredicate()) {
				final SMTTerm term = smtTerm(expr);
				return SMTFactory.makeAtom(signature.getLogic().getTrue(),
						new SMTTerm[] { term }, signature);
			}
		}
		return smtFormula(expr);
	}

	/**
	 * In order to translate sets equality, we use the extensionality axiom
	 * instead.
	 * <code>∀ A ⦂ ℙ(S), B ⦂ ℙ(S) · ((∀c ⦂ S · (c ∈ A ⇔ c ∈ B)) ⇒ (A = B))</code>
	 **/
	private SMTFormula translateSetsEquality(final Identifier leftSet,
			final Identifier rightSet) {
		// boolean constant for indicating mapplet on the left hand side of a
		// membership
		final boolean leftTagIsMapsTo = true;

		// gets the type of elements contained in the given sets
		final Type baseType = leftSet.getType().getBaseType();

		// creates the quantified variable with a fresh name
		final String varName = signature.freshSymbolName("x");
		final SMTSortSymbol varSort;
		if (!typeMap.containsKey(baseType)) {
			varSort = translateTypeName(baseType);
			typeMap.put(baseType, varSort);
		} else {
			varSort = typeMap.get(baseType);
		}
		final SMTTerm smtVar = SMTFactory.makeVar(varName, varSort,
				smtlibVersion);

		// creates the membership of the created bounded variable into the left
		// set
		final SMTFormula leftMembership;
		if (gatherer.getSetsForSpecialMSPreds().contains(leftSet)) {
			leftMembership = translateInSpecializedMembershipPredicate(smtVar,
					baseType, false, (FreeIdentifier) leftSet);
		} else {
			leftMembership = translateInClassicMembershipPredicate(smtVar,
					baseType, false, leftSet);
		}

		// creates the membership of the created bounded variable into the right
		// set
		final SMTFormula rightMembership;
		if (gatherer.getSetsForSpecialMSPreds().contains(rightSet)) {
			rightMembership = translateInSpecializedMembershipPredicate(smtVar,
					baseType, false, (FreeIdentifier) rightSet);
		} else {
			rightMembership = translateInClassicMembershipPredicate(smtVar,
					baseType, !leftTagIsMapsTo, rightSet);
		}

		// creates the equivalence between the two memberships
		final SMTFormula equivalence = SMTFactory.makeIff(new SMTFormula[] {
				leftMembership, rightMembership }, smtlibVersion);

		// returns the quantified formula
		return SMTFactory.makeForAll(new SMTTerm[] { smtVar }, equivalence,
				smtlibVersion);
	}

	/**
	 * Generates the translated SMT-LIB formula for this Event-B predicate:
	 * 
	 * <code>∀x·x ∈ ℤ</code>
	 * 
	 * @return The SMTFormula corresponding to the translation of the Event-B
	 *         predicate shown above
	 */
	private SMTFormula generateIntegerAxiom() {
		// gets the event-B integer type
		final Type integerType = FormulaFactory.getDefault().makeIntegerType();

		// creates the quantified variable with a fresh name
		final String varName = signature.freshSymbolName("x");
		final SMTSortSymbol intSort = signature.getLogic().getIntegerSort();
		final SMTTerm smtVar = SMTFactory.makeVar(varName, intSort,
				smtlibVersion);

		// creates the integer constant
		final SMTTerm intConstant = SMTFactory.makeConstant(signature
				.getLogic().getIntsSet(), signature);

		// creates the sort POW(INT)
		final Type powerSetIntegerType = FormulaFactory.getDefault()
				.makePowerSetType(integerType);
		final SMTSortSymbol powerSetIntSort = typeMap.get(powerSetIntegerType);

		// gets the membership symbol
		final SMTPredicateSymbol membershipPredSymbol = getMembershipPredicateSymbol(
				integerType, intSort, powerSetIntSort);

		// creates the membership formula
		final SMTFormula membershipFormula = SMTFactory.makeAtom(
				membershipPredSymbol, new SMTTerm[] { smtVar, intConstant },
				signature);

		// creates the axiom
		final SMTFormula axiom = SMTFactory.makeForAll(
				new SMTTerm[] { smtVar }, membershipFormula, smtlibVersion);

		axiom.setComment("Integer axiom");
		return axiom;
	}

	/**
	 * Generates the translated SMT-LIB formula for this Event-B predicate:
	 * 
	 * <code>∀x·x ∈ BOOL</code>
	 * 
	 * @return The SMTFormula corresponding to the translation of the Event-B
	 *         predicate shown above
	 */
	private SMTFormula generateBoolAxiom() {
		// gets the event-B boolean type
		final Type booleanType = FormulaFactory.getDefault().makeBooleanType();

		// creates the quantified variable with a fresh name
		final String varName = signature.freshSymbolName("x");
		final SMTSortSymbol boolSort = signature.getLogic().getBooleanSort();
		final SMTTerm smtVar = SMTFactory.makeVar(varName, boolSort,
				smtlibVersion);

		// creates the boolean constant
		final SMTTerm boolConstant = SMTFactory.makeConstant(signature
				.getLogic().getBoolsSet(), signature);

		// creates the sort POW(BOOL)
		final Type powerSetBooleanType = FormulaFactory.getDefault()
				.makePowerSetType(booleanType);
		final SMTSortSymbol powerSetBoolSort = typeMap.get(powerSetBooleanType);

		// gets the membership symbol
		final SMTPredicateSymbol membershipPredSymbol = getMembershipPredicateSymbol(
				booleanType, boolSort, powerSetBoolSort);

		// creates the membership formula
		final SMTFormula membershipFormula = SMTFactory.makeAtom(
				membershipPredSymbol, new SMTTerm[] { smtVar, boolConstant },
				signature);

		// creates the axiom
		final SMTFormula axiom = SMTFactory.makeForAll(
				new SMTTerm[] { smtVar }, membershipFormula, smtlibVersion);

		axiom.setComment("Bool axiom");
		return axiom;
	}

	/**
	 * Generates the SMT-LIB formula for this event-B formula:
	 * <code>∀x, y·(x = TRUE ⇔ y = TRUE) ⇔ x = y</code>
	 * 
	 * @return the SMTFormula representing the translated axiom
	 */
	private SMTFormula generateTrueEqualityAxiom() {
		// creates the quantified boolean variables with fresh names
		final SMTSortSymbol boolSort = signature.getLogic().getBooleanSort();
		final String xName = signature.freshSymbolName("x");
		final String yName = signature.freshSymbolName("y");
		final SMTTerm xTerm = SMTFactory
				.makeVar(xName, boolSort, smtlibVersion);
		final SMTTerm yTerm = SMTFactory
				.makeVar(yName, boolSort, smtlibVersion);

		// creates the formula <code>x = TRUE ⇔ y = TRUE</code>
		final SMTPredicateSymbol truePredSymbol = signature.getLogic()
				.getTrue();
		final SMTFormula trueX = SMTFactory.makeAtom(truePredSymbol,
				new SMTTerm[] { xTerm }, signature);
		final SMTFormula trueY = SMTFactory.makeAtom(truePredSymbol,
				new SMTTerm[] { yTerm }, signature);
		final SMTFormula trueXEqvTrueY = SMTFactory.makeIff(new SMTFormula[] {
				trueX, trueY }, smtlibVersion);

		// creates the formula <code>x = y</code>
		final SMTFormula xEqualY = SMTFactory.makeEqual(new SMTTerm[] { xTerm,
				yTerm }, V1_2);

		// creates the formula <code>(x = TRUE ⇔ y = TRUE) ⇔ x = y</code>
		final SMTFormula equivalence = SMTFactory.makeIff(new SMTFormula[] {
				trueXEqvTrueY, xEqualY }, smtlibVersion);

		// creates the axiom
		final SMTFormula axiom = SMTFactory.makeForAll(new SMTTerm[] { xTerm,
				yTerm }, equivalence, smtlibVersion);

		axiom.setComment("True equality axiom");
		return axiom;
	}

	/**
	 * Generates the SMT-LIB formula for this event-B formula:
	 * <code>∃ x ⦂ BOOL, y ⦂ BOOL · x = TRUE ∧ y ≠ TRUE</code>
	 * 
	 * @return the SMTFormula representing the translated axiom
	 */
	private SMTFormula generateTrueExistenceAxiom() {
		// creates the quantified boolean variables with fresh names
		final SMTSortSymbol boolSort = signature.getLogic().getBooleanSort();
		final String xName = signature.freshSymbolName("x");
		final String yName = signature.freshSymbolName("y");
		final SMTTerm xTerm = SMTFactory
				.makeVar(xName, boolSort, smtlibVersion);
		final SMTTerm yTerm = SMTFactory
				.makeVar(yName, boolSort, smtlibVersion);

		// creates the formula <code>x = TRUE ⇔ y = TRUE</code>
		final SMTPredicateSymbol truePredSymbol = signature.getLogic()
				.getTrue();
		final SMTFormula trueX = SMTFactory.makeAtom(truePredSymbol,
				new SMTTerm[] { xTerm }, signature);
		final SMTFormula notTrueY = SMTFactory.makeNot(
				new SMTFormula[] { SMTFactory.makeAtom(truePredSymbol,
						new SMTTerm[] { yTerm }, signature) }, smtlibVersion);

		// creates the conjunction <code>x = TRUE ∧ y ≠ TRUE</code>
		final SMTFormula trueXAndNotTrueY = SMTFactory.makeAnd(
				new SMTFormula[] { trueX, notTrueY }, smtlibVersion);

		// creates the axiom
		final SMTFormula axiom = SMTFactory.makeExists(new SMTTerm[] { xTerm,
				yTerm }, trueXAndNotTrueY, smtlibVersion);

		axiom.setComment("True existence axiom");
		return axiom;
	}

	/**
	 * Generates the SMT-LIB formula for the extensionality axiom event-B
	 * formula:
	 * <code>∀ A ⦂ ℙ(S), B ⦂ ℙ(S) · ((∀c ⦂ S · (c ∈ A ⇔ c ∈ B)) ⇒ (A = B))</code>
	 * 
	 * @return the SMTFormula representing the translated axiom
	 */
	private SMTFormula generateExtensionalityAxiom(
			final SMTPredicateSymbol membershipPredSymbol) {
		final SMTSortSymbol[] membershipArgSorts = membershipPredSymbol
				.getArgSorts();
		final int leftMembersNumber = membershipArgSorts.length - 1;
		final SMTSortSymbol setSort = membershipArgSorts[leftMembersNumber];
		// creates the quantified set variables with fresh names
		final String setA = signature.freshSymbolName("A");
		final String setB = signature.freshSymbolName("B");
		final SMTTerm termA = SMTFactory.makeVar(setA, setSort, smtlibVersion);
		final SMTTerm termB = SMTFactory.makeVar(setB, setSort, smtlibVersion);

		// creates the quantified element variables with fresh names
		final SMTTerm[] eltTerms = new SMTTerm[leftMembersNumber];
		final SMTTerm[] setAmembershipArgs = new SMTTerm[membershipArgSorts.length];
		final SMTTerm[] setBmembershipArgs = new SMTTerm[membershipArgSorts.length];
		for (int i = 0; i < leftMembersNumber; i++) {
			final String xVar = signature.freshSymbolName("x");
			final SMTTerm xTerm = SMTFactory.makeVar(xVar,
					membershipArgSorts[i], smtlibVersion);
			eltTerms[i] = xTerm;
			setAmembershipArgs[i] = xTerm;
			setBmembershipArgs[i] = xTerm;
		}
		setAmembershipArgs[leftMembersNumber] = termA;
		setBmembershipArgs[leftMembersNumber] = termB;

		// creates the membership formulas
		final SMTFormula setAmembershipFormula = SMTFactory.makeAtom(
				membershipPredSymbol, setAmembershipArgs, signature);
		final SMTFormula setBmembershipFormula = SMTFactory.makeAtom(
				membershipPredSymbol, setBmembershipArgs, signature);

		// creates the formula <code>c ∈ A ⇔ c ∈ B</code>
		final SMTFormula equivalence = SMTFactory.makeIff(new SMTFormula[] {
				setAmembershipFormula, setBmembershipFormula }, smtlibVersion);

		// creates the quantified formula
		final SMTFormula forall = SMTFactory.makeForAll(eltTerms, equivalence,
				smtlibVersion);

		// creates the equality <code>A = B</code>
		final SMTFormula equality = SMTFactory.makeEqual(new SMTTerm[] { termA,
				termB }, V1_2);

		// creates the implication
		final SMTFormula implies = SMTFactory.makeImplies(new SMTFormula[] {
				forall, equality }, smtlibVersion);

		// creates the axiom
		final SMTFormula axiom = SMTFactory.makeForAll(new SMTTerm[] { termA,
				termB }, implies, smtlibVersion);

		axiom.setComment("Extensionality axiom");
		return axiom;
	}

	/**
	 * Generates the SMT-LIB formula for the singleton part of elementary sets
	 * axiom event-B formula:
	 * <code>∀ x ⦂ ℤ · (∃ X ⦂ ℙ(ℤ) · (x ∈ X ∧ (∀ y ⦂ ℤ · (y ∈ X ⇒ x = y))))</code>
	 * 
	 * @return the SMTFormula representing the translated axiom
	 */
	private SMTFormula generateSingletonAxiom(
			final SMTPredicateSymbol membershipPredSymbol) {
		final SMTSortSymbol[] membershipArgSorts = membershipPredSymbol
				.getArgSorts();
		final int leftMembersNumber = membershipArgSorts.length - 1;
		final SMTSortSymbol setSort = membershipArgSorts[leftMembersNumber];
		// creates the quantified set variable with fresh name
		final String setX = signature.freshSymbolName("X");
		final SMTTerm termX = SMTFactory.makeVar(setX, setSort, smtlibVersion);

		// creates the quantified element variables with fresh names
		// and the equalities
		final SMTTerm[] xTerms = new SMTTerm[leftMembersNumber];
		final SMTTerm[] yTerms = new SMTTerm[leftMembersNumber];
		final SMTTerm[] xMembershipArgs = new SMTTerm[membershipArgSorts.length];
		final SMTTerm[] yMembershipArgs = new SMTTerm[membershipArgSorts.length];
		final SMTFormula[] equalities = new SMTFormula[leftMembersNumber];
		for (int i = 0; i < leftMembersNumber; i++) {
			final String xVar = signature.freshSymbolName("x");
			final SMTTerm xTerm = SMTFactory.makeVar(xVar,
					membershipArgSorts[i], smtlibVersion);
			xTerms[i] = xTerm;
			xMembershipArgs[i] = xTerm;

			final String yVar = signature.freshSymbolName("y");
			final SMTTerm yTerm = SMTFactory.makeVar(yVar,
					membershipArgSorts[i], smtlibVersion);
			yTerms[i] = yTerm;
			yMembershipArgs[i] = yTerm;

			equalities[i] = SMTFactory.makeEqual(
					new SMTTerm[] { yTerm, xTerm }, V1_2);
		}
		xMembershipArgs[leftMembersNumber] = termX;
		yMembershipArgs[leftMembersNumber] = termX;

		// creates the membership formulas
		final SMTFormula xMembershipFormula = SMTFactory.makeAtom(
				membershipPredSymbol, xMembershipArgs, signature);
		final SMTFormula yMembershipFormula = SMTFactory.makeAtom(
				membershipPredSymbol, yMembershipArgs, signature);

		// creates the conjunction of equalities
		final SMTFormula eqConjunction;
		if (equalities.length > 1) {
			eqConjunction = SMTFactory.makeAnd(equalities, smtlibVersion);
		} else {
			eqConjunction = equalities[0];
		}

		// creates the implication
		final SMTFormula implies = SMTFactory.makeImplies(new SMTFormula[] {
				yMembershipFormula, eqConjunction }, smtlibVersion);

		// creates the quantified formula
		final SMTFormula yForall = SMTFactory.makeForAll(yTerms, implies,
				smtlibVersion);

		// creates the first conjunction
		final SMTFormula conjonction = SMTFactory.makeAnd(new SMTFormula[] {
				xMembershipFormula, yForall }, smtlibVersion);

		// creates the set existential
		final SMTFormula existsX = SMTFactory.makeExists(
				new SMTTerm[] { termX }, conjonction, smtlibVersion);

		// creates the axiom
		final SMTFormula axiom = SMTFactory.makeForAll(xTerms, existsX,
				smtlibVersion);

		axiom.setComment("Elementary Sets axiom (Singleton part)");
		return axiom;
	}

	/**
	 * This method links some symbols of the logic to the main Event-B symbols.
	 */
	private void linkLogicSymbols() {
		final SMTLogicPP logic;
		if (smtlibVersion.equals(V1_2)) {
			logic = ((SMTSignatureV1_2PP) signature).getLogic();
		} else {
			logic = ((SMTSignatureV2_0PP) signature).getLogic();
		}
		final FormulaFactory ff = FormulaFactory.getDefault();

		final Type integerType = ff.makeIntegerType();
		final Type booleanType = ff.makeBooleanType();

		typeMap.put(integerType, logic.getIntegerSort());
		typeMap.put(ff.makePowerSetType(integerType),
				logic.getPowerSetIntegerSort());
		typeMap.put(booleanType, logic.getBooleanSort());
		typeMap.put(ff.makePowerSetType(booleanType),
				logic.getPowerSetBooleanSort());

		// TODO: a test could be added so that only the operators appearing in
		// the sequent are mapped)
		for (final SMTOperator operator : SMTOperator.values()) {
			putOperatorSymbol(operator);
		}
	}

	private void putOperatorSymbol(final SMTOperator operator) {
		SMTSymbol operatorSymbol = signature.getLogic().getOperator(operator);
		if (operatorSymbol == null) {
			final String symbolName = operator.toString();
			final SMTSortSymbol integerSort = signature.getLogic()
					.getIntegerSort();
			final SMTSortSymbol[] intTab = { integerSort };
			final SMTSortSymbol[] intIntTab = { integerSort, integerSort };

			switch (operator) {
			case DIV:
			case EXPN:
			case MINUS:
			case MOD:
			case UMINUS:
				operatorSymbol = signature.freshFunctionSymbol(symbolName,
						intIntTab, integerSort, !ASSOCIATIVE);
				break;
			case GE:
			case GT:
			case LE:
			case LT:
				operatorSymbol = signature.freshFunctionSymbol(symbolName,
						intIntTab, integerSort, !ASSOCIATIVE);
				break;
			case MUL:
			case PLUS:
				operatorSymbol = signature.freshFunctionSymbol(symbolName,
						intTab, integerSort, ASSOCIATIVE);
				break;
			}
		}
		operatorMap.put(operator, operatorSymbol);
	}

	/**
	 * This method translates the given actualPredicate into an SMT Formula.
	 * 
	 */
	private SMTFormula translate(final Predicate predicate, final boolean inGoal) {
		try {
			predicate.accept(this);
		} catch (IllegalArgumentException e) {
			if (inGoal) {
				if (DEBUG) {
					System.err.println("Catched IllegalArgumentException : '"
							+ e.getMessage() + "'.");
					System.err.println("Replacing with \u22a5 in goal.");
				}
				return SMTFactoryPP.makePFalse();
			} else {
				if (DEBUG) {
					System.err.println("Catched IllegalArgumentException : '"
							+ e.getMessage() + "'.");
					System.err.println("Replacing with \u22a4 in hypothesis.");
				}
				return SMTFactoryPP.makePTrue();
			}
		}
		return getSMTFormula();
	}

	/**
	 * @param lemmaName
	 *            the name of the lemma
	 * @param ppTranslatedHypotheses
	 *            the PP translated hypotheses
	 * @param ppTranslatedGoal
	 *            the PP translated formula
	 * @param logic
	 *            the used logic
	 * @return the SMTBenchmark of the translation
	 */
	private SMTBenchmark translate(final String lemmaName,
			final List<Predicate> ppTranslatedHypotheses,
			final Predicate ppTranslatedGoal, final SMTLogic logic) {
		translateSignature(logic, ppTranslatedHypotheses, ppTranslatedGoal);

		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();

		/**
		 * If the gatherer found an occurrence of the atomic expression
		 * <code>ℤ</code>, the translator adds the integer axiom to handle it.
		 */
		if (gatherer.foundAtomicIntegerExp()) {
			translatedAssumptions.add(generateIntegerAxiom());
		}

		/**
		 * If the gatherer found that the boolean theory was needed to discharge
		 * the sequent,
		 */
		for (final SMTTheory t : signature.getLogic().getTheories()) {
			if (t instanceof Booleans) {
				/**
				 * If the gatherer found an occurrence of the atomic expression
				 * <code>BOOL</code>, the translator adds the bool axiom to
				 * handle it.
				 */
				if (gatherer.foundAtomicBoolExp()) {
					translatedAssumptions.add(generateBoolAxiom());
				}
				/**
				 * If the gatherer found that the identifiers couldn't be
				 * translated themselves in boolean equalities, the translator
				 * adds the true axiom to handle them.
				 */
				if (gatherer.usesTruePredicate()) {
					translatedAssumptions.add(generateTrueEqualityAxiom());
					translatedAssumptions.add(generateTrueExistenceAxiom());
				}
			}
		}

		// translates each hypothesis
		for (final Predicate hypothesis : ppTranslatedHypotheses) {
			clearFormula();
			/**
			 * ignoring TRUE hypotheses generated by PP (
			 * <code>:assumption (true)</code>)
			 */
			if (hypothesis.getTag() != Formula.BTRUE) {
				translatedAssumptions.add(translate(hypothesis, !IN_GOAL));
			}
		}
		// translates the formula
		clearFormula();
		final SMTFormula smtFormula = translate(ppTranslatedGoal, IN_GOAL);

		/**
		 * The translator adds some set theory axioms for each defined
		 * membership predicate
		 */
		int i = 0;
		for (final Map.Entry<Type, SMTPredicateSymbol> entry : msTypeMap
				.entrySet()) {
			translatedAssumptions.add(i,
					generateExtensionalityAxiom(entry.getValue()));
			i++;
			translatedAssumptions.add(i,
					generateSingletonAxiom(entry.getValue()));
			i++;
		}

		final SMTBenchmarkPP benchmark = new SMTBenchmarkPP(lemmaName,
				signature, translatedAssumptions, smtFormula);
		benchmark.removeUnusedSymbols();
		return benchmark;
	}

	/**
	 * This method translates an Event-B type into an SMT-LIB sort. It gives the
	 * sort a fresh name if necessary, to avoid any name collision.
	 */
	@Override
	protected SMTSortSymbol translateTypeName(final Type type) {
		final boolean isAProductType = type instanceof ProductType;
		if (type.getBaseType() == null && type.getSource() == null
				&& type.getTarget() == null && !isAProductType) {
			return signature.freshSort(type.toString());
		} else {
			final StringBuilder basenameBuilder;
			if (isAProductType) {
				basenameBuilder = new StringBuilder();
			} else { // instance of PowerSetType
				basenameBuilder = new StringBuilder("P");
			}
			return signature.freshSort(buildBasenameString(basenameBuilder,
					type).toString());
		}
	}

	/**
	 * This method builds the SMT-LIB signature of a sequent given as its set of
	 * hypotheses and its goal. The method also fill the variables
	 * <code>typeMap</code>, <code>varMap</code> and <code>msTypeMap</code>.
	 * 
	 * @param logic
	 *            the SMT-LIB logic
	 * @param hypotheses
	 *            the set of hypotheses of the sequent
	 * @param goal
	 *            the goal of the sequent
	 */
	@Override
	protected void translateSignature(final SMTLogic logic,
			final List<Predicate> hypotheses, final Predicate goal) {
		if (logic instanceof SMTLogicPP) {
			if (smtlibVersion.equals(V1_2)) {
				signature = new SMTSignatureV1_2PP((SMTLogicPP) logic);
			} else {
				signature = new SMTSignatureV2_0PP(logic);
			}
		} else {
			throw new IllegalArgumentException("Wrong logic.");
		}

		linkLogicSymbols();

		final ITypeEnvironment typeEnvironment = extractTypeEnvironment(
				hypotheses, goal);

		translateTypeEnvironment(typeEnvironment);
		translateBoundIdentTypes(hypotheses, goal);
	}

	/**
	 * This is the translation method for the ppTrans approach of SMT
	 * translation.
	 * 
	 */
	@Override
	protected SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal) {

		/**
		 * PP translation
		 */
		final PPProof ppProof = ppTranslation(hypotheses, goal);
		@SuppressWarnings("deprecation")
		final List<Predicate> ppTranslatedHypotheses = ppProof
				.getTranslatedHypotheses();
		@SuppressWarnings("deprecation")
		final Predicate ppTranslatedGoal = ppProof.getTranslatedGoal();

		/**
		 * Logic auto-configuration
		 */
		final SMTLogic logic = determineLogic(ppTranslatedHypotheses,
				ppTranslatedGoal);

		/**
		 * SMT translation
		 */
		return translate(lemmaName, ppTranslatedHypotheses, ppTranslatedGoal,
				logic);
	}

	/**
	 * This is the public translation method
	 * 
	 * @param lemmaName
	 *            the name to be used in the SMT-LIB benchmark
	 * @param hypotheses
	 *            the hypotheses of the Event-B sequent
	 * @param goal
	 *            the goal of the Event-B sequent
	 * @return the SMT-LIB benchmark built over the translation of the given
	 *         Event-B sequent
	 */
	public static SMTBenchmark translateToSmtLibBenchmark(
			final String lemmaName, final List<Predicate> hypotheses,
			final Predicate goal, final String solver,
			final SMTLIBVersion smtlibVersion) {
		final SMTBenchmark smtB = new SMTThroughPP(solver, smtlibVersion)
				.translate(lemmaName, hypotheses, goal);
		return smtB;
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final Predicate predicate,
			final String solver, final SMTLIBVersion smtlibVersion) {
		final SMTThroughPP translator = new SMTThroughPP(solver, smtlibVersion);
		final List<Predicate> noHypothesis = new ArrayList<Predicate>(0);
		final SMTLogic logic = translator.determineLogic(noHypothesis,
				predicate);
		translator.translateSignature(logic, noHypothesis, predicate);
		return translator.translate(predicate, IN_GOAL);
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTSignature translateTE(final SMTLogic logic,
			final Predicate predicate, final String solver,
			final SMTLIBVersion smtlibVersion) {
		final SMTThroughPP translator = new SMTThroughPP(solver, smtlibVersion);
		final List<Predicate> noHypothesis = new ArrayList<Predicate>(0);
		translator.determineLogic(noHypothesis, predicate);
		translator.translateSignature(logic, noHypothesis, predicate);
		return translator.getSignature();
	}

	/**
	 * This method is used only to test the logic determination
	 */
	public static SMTLogic determineLogic(final Predicate goalPredicate,
			final SMTLIBVersion smtlibVersion) {
		final SMTThroughPP translator = new SMTThroughPP(null, smtlibVersion);
		return translator.determineLogic(new ArrayList<Predicate>(0),
				goalPredicate);
	}

	/**
	 * This method translates an Event-B associative expression into an SMT
	 * node.
	 */
	@Override
	public void visitAssociativeExpression(
			final AssociativeExpression expression) {
		final SMTTerm[] children = smtTerms(expression.getChildren());
		final int tag = expression.getTag();
		switch (tag) {
		case Formula.PLUS:
			final SMTFunctionSymbol plusSymbol = (SMTFunctionSymbol) operatorMap
					.get(SMTOperator.PLUS);
			smtNode = sf.makePlus(plusSymbol, children, signature);
			break;
		case Formula.MUL:
			final SMTFunctionSymbol mulSymbol = (SMTFunctionSymbol) operatorMap
					.get(SMTOperator.MUL);
			smtNode = sf.makeMul(mulSymbol, children, signature);
			break;
		default:
			/**
			 * BUNION, BINTER, BCOMP, FCOMP and OVR tags cannot be produced by
			 * ppTrans.
			 */
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B atomic expression into an SMT node.
	 */
	@Override
	public void visitAtomicExpression(final AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.INTEGER:
			smtNode = makeInteger(signature.getLogic().getIntsSet(), signature);
			break;
		case Formula.BOOL:
			smtNode = makeBool(signature.getLogic().getBoolsSet(), signature);
			break;
		case Formula.TRUE:
			/**
			 * This case is never reached because it is translated in its parent
			 * node.
			 */
			throw new IllegalTagException(expression.getTag());
		default:
			/**
			 * NATURAL, NATURAL1, EMPTYSET, KPRED, KSUCC, KPRJ1_GEN, KPRJ2_GEN,
			 * KID_GEN,FALSE tags cannot be produced by ppTrans.
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B binary expression into an SMT node.
	 */
	@Override
	public void visitBinaryExpression(final BinaryExpression expression) {
		final Expression left = expression.getLeft();
		final Expression right = expression.getRight();
		final SMTTerm[] children = smtTerms(left, right);
		switch (expression.getTag()) {
		case Formula.MINUS:
			final SMTFunctionSymbol minusSymbol = (SMTFunctionSymbol) operatorMap
					.get(SMTOperator.MINUS);
			smtNode = sf.makeMinus(minusSymbol, children, signature);
			break;
		case Formula.MAPSTO:

			if (left.getTag() != Formula.MAPSTO) {
				membershipPredicateTerms.add(0, children[0]);
			}

			if (right.getTag() != Formula.MAPSTO) {
				membershipPredicateTerms.add(children[1]);
			}
			break;
		case Formula.DIV:
			final SMTFunctionSymbol divSymbol = (SMTFunctionSymbol) operatorMap
					.get(SMTOperator.DIV);
			smtNode = sf.makeDiv(divSymbol, children, signature);
			break;
		case Formula.MOD:
			final SMTFunctionSymbol modSymbol = (SMTFunctionSymbol) operatorMap
					.get(SMTOperator.MOD);
			smtNode = sf.makeMod(modSymbol, children, signature);
			break;
		case Formula.EXPN:
			final SMTFunctionSymbol expnSymbol = (SMTFunctionSymbol) operatorMap
					.get(SMTOperator.EXPN);
			smtNode = sf.makeExpn(expnSymbol, children, signature);
			break;
		default:
			/**
			 * REL, TREL, SREL, STREL, PFUN, TFUN, PINJ, TINJ, PSUR, TSUR, TBIJ,
			 * SETMINUS, CPROD, DPROD, PPROD, DOMRES, DOMSUB, RANRES, RANSUB,
			 * UPTO, FUNIMAGE, RELIMAGE
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B bool expression into an SMT node.
	 */
	@Override
	public void visitBoolExpression(final BoolExpression expression) {
		/**
		 * KBOOL cannot be produced by ppTrans.
		 */
		throw new IllegalArgumentException(
				Messages.Incompatible_Formula_With_PPTrans_Production);
	}

	/**
	 * This method translates an Event-B relational actualPredicate into an SMT
	 * node.
	 */
	@Override
	public void visitRelationalPredicate(final RelationalPredicate predicate) {
		final int tag = predicate.getTag();
		Expression left;
		Expression right;
		switch (tag) {
		case Formula.EQUAL: {
			left = predicate.getLeft();
			right = predicate.getRight();

			if (left.getTag() == Formula.TRUE) {
				smtNode = translateTruePred(right);
			} else if (right.getTag() == Formula.TRUE) {
				smtNode = translateTruePred(left);
			} else if (left.getType() instanceof BooleanType) {
				smtNode = translateBoolIds(left, right);
			} else if (left instanceof Identifier
					&& right instanceof Identifier
					&& left.getType() instanceof PowerSetType
					&& right.getType() instanceof PowerSetType) {
				smtNode = translateSetsEquality((Identifier) left,
						(Identifier) right);
			} else {
				final SMTTerm[] children = smtTerms(left, right);
				smtNode = SMTFactory.makeEqual(children, smtlibVersion);
			}
			break;
		}
		case Formula.LT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			final SMTPredicateSymbol ltSymbol = (SMTPredicateSymbol) operatorMap
					.get(SMTOperator.LT);
			smtNode = sf.makeLessThan(ltSymbol, children, signature);
		}
			break;
		case Formula.LE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			final SMTPredicateSymbol leSymbol = (SMTPredicateSymbol) operatorMap
					.get(SMTOperator.LE);
			smtNode = sf.makeLessEqual(leSymbol, children, signature);
		}
			break;
		case Formula.GT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			final SMTPredicateSymbol gtSymbol = (SMTPredicateSymbol) operatorMap
					.get(SMTOperator.GT);
			smtNode = sf.makeGreaterThan(gtSymbol, children, signature);
		}
			break;
		case Formula.GE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			final SMTPredicateSymbol geSymbol = (SMTPredicateSymbol) operatorMap
					.get(SMTOperator.GE);
			smtNode = sf.makeGreaterEqual(geSymbol, children, signature);
		}
			break;
		case Formula.IN:
			smtNode = translateMemberShipPredicate(predicate);
			break;
		default:
			/**
			 * NOTEQUAL, NOTIN, SUBSET, SUBSETEQ, NOTSUBSET and NOTSUBSETEQ
			 * cannot be produced by ppTrans.
			 */
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B unary expression into an SMT node.
	 */
	@Override
	public void visitUnaryExpression(final UnaryExpression expression) {
		final SMTTerm[] children = new SMTTerm[] { smtTerm(expression
				.getChild()) };
		switch (expression.getTag()) {
		case Formula.UNMINUS:
			final SMTFunctionSymbol uminusSymbol = (SMTFunctionSymbol) operatorMap
					.get(SMTOperator.UMINUS);
			smtNode = sf.makeUMinus(uminusSymbol, children, signature);
			break;
		default:
			/**
			 * KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID,
			 * KMIN, KMAX, CONVERSE
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitSetExtension(final SetExtension expression) {
		throw new IllegalArgumentException(
				Messages.Incompatible_Formula_With_PPTrans_Production);
	}

	/**
	 * This method translates an Event-B bound identifier declaration into an
	 * SMT node.
	 */
	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final String varName = boundIdentDecl.getName();
		final SMTVar smtVar;

		final String smtVarName = signature.freshSymbolName(varName);
		final SMTSortSymbol sort = typeMap.get(boundIdentDecl.getType());
		smtVar = (SMTVar) SMTFactory.makeVar(smtVarName, sort, smtlibVersion);
		if (!qVarMap.containsKey(varName)) {
			qVarMap.put(varName, smtVar);
			boundIdentifiers.add(varName);
		} else {
			qVarMap.put(smtVarName, smtVar);
			boundIdentifiers.add(smtVarName);
		}
		smtNode = smtVar;
	}

	/**
	 * This method translates an Event-B free identifier into an SMT node.
	 */
	@Override
	public void visitFreeIdentifier(final FreeIdentifier expression) {
		final SMTSymbol symbol = varMap.get(expression.getName());

		if (symbol instanceof SMTFunctionSymbol) {
			smtNode = SMTFactory.makeConstant((SMTFunctionSymbol) symbol,
					signature);
			return;
		}
		smtNode = SMTFactory.makeAtom((SMTPredicateSymbol) symbol,
				new SMTTerm[] {}, signature);
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {
		throw new IllegalArgumentException(
				"'becomes equal to' assignments are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		throw new IllegalArgumentException(
				"'becomes member of' assignments are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesSuchThat(final BecomesSuchThat assignment) {
		throw new IllegalArgumentException(
				"'becomes such that' assignments are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		throw new IllegalArgumentException(
				"'Quantified expressions' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitSimplePredicate(final SimplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Simple predicates' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Multiple predicates' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		throw new IllegalArgumentException(
				"'Extended expressions' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
		throw new IllegalArgumentException(
				"'Extended predicates' are not compatible with the underlying logic used in this version of SMT-LIB."); //$NON-NLS-1$
	}
}
