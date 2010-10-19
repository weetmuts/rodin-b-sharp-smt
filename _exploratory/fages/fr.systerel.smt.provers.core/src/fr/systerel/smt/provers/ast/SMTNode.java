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
package fr.systerel.smt.provers.ast;

import fr.systerel.smt.provers.ast.commands.SMTCommand;
import fr.systerel.smt.provers.ast.commands.SMTCommandOption;

/**
 * This is the base class for all nodes of an SMT-LIB AST (Abstract Syntax
 * Tree).
 */
public abstract class SMTNode<T extends SMTNode<T>> {

	// =========================================================================
	// Constants
	// =========================================================================
	/**
	 * <code>NO_TAG</code> is used to indicate that a tag value is invalid or
	 * absent. It is different from all valid tags.
	 */
	public final static int NO_TAG = 0;
	
	/**
	 * <code>ITE</code> represents an ITE term. 
	 * 
	 * @see SMTITETerm
	 */
	public final static int ITE = 3;

	/**
	 * First tag for a propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int FIRST_PROP_ATOM = 101;

	/**
	 * <code>TRUE</code> represents the <tt>true</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PTRUE = FIRST_PROP_ATOM + 0;

	/**
	 * <code>FALSE</code> represents the <tt>false</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PFALSE = FIRST_PROP_ATOM + 1;
	
	/**
	 * First tag for a connective formula.
	 * 
	 * @see SMTConnectiveFormula
	 */
	public final static int FIRST_CONNECTIVE_FORMULA = 201;
	
	/**
	 * <code>NOT</code> represents the <tt>not</tt> connective formula.
	 * 
	 * @see SMTConnectiveFormula
	 */
	public final static int NOT = FIRST_CONNECTIVE_FORMULA + 0;
	
	/**
	 * <code>IMPLIES</code> represents the <tt>implies</tt> connective formula.
	 * 
	 * @see SMTConnectiveFormula
	 */
	public final static int IMPLIES = FIRST_CONNECTIVE_FORMULA + 1;
	
	/**
	 * <code>IF_THEN_ELSE</code> represents the <tt>if_then_else</tt> connective formula.
	 * 
	 * @see SMTConnectiveFormula
	 */
	public final static int IF_THEN_ELSE = FIRST_CONNECTIVE_FORMULA + 2;
	
	/**
	 * <code>AND</code> represents the <tt>and</tt> connective formula.
	 * 
	 * @see SMTConnectiveFormula
	 */
	public final static int AND = FIRST_CONNECTIVE_FORMULA + 3;
	
	/**
	 * <code>OR</code> represents the <tt>or</tt> connective formula.
	 * 
	 * @see SMTConnectiveFormula
	 */
	public final static int OR = FIRST_CONNECTIVE_FORMULA + 4;
	
	/**
	 * <code>XOR</code> represents the <tt>xor</tt> connective formula.
	 * 
	 * @see SMTConnectiveFormula
	 */
	public final static int XOR = FIRST_CONNECTIVE_FORMULA + 5;
	
	/**
	 * <code>IFF</code> represents the <tt>iff</tt> connective formula.
	 * 
	 * @see SMTConnectiveFormula
	 */
	public final static int IFF = FIRST_CONNECTIVE_FORMULA + 6;
	
	/**
	 * First tag for an arithmetic formula.
	 * 
	 * @see SMTArithmeticFormula
	 */
	public final static int FIRST_ARITHMETIC_FORMULA = 301;
	
	/**
	 * <code>EQUAL</code> represents =.
	 * 
	 * @see SMTArithmeticFormula
	 */
	public final static int EQUAL = FIRST_ARITHMETIC_FORMULA + 0;
	
	/**
	 * <code>LT</code> represents <.
	 * 
	 * @see SMTArithmeticFormula
	 */
	public final static int LT = FIRST_ARITHMETIC_FORMULA + 1;
	
	/**
	 * <code>LE</code> represents <=.
	 * 
	 * @see SMTArithmeticFormula
	 */
	public final static int LE = FIRST_ARITHMETIC_FORMULA + 2;
	
	/**
	 * <code>GT</code> represents >.
	 * 
	 * @see SMTArithmeticFormula
	 */
	public final static int GT = FIRST_ARITHMETIC_FORMULA + 3;
	
	/**
	 * <code>GE</code> represents =.
	 * 
	 * @see SMTArithmeticFormula
	 */
	public final static int GE = FIRST_ARITHMETIC_FORMULA + 4;
	
	/**
	 * First tag for an arithmetic term.
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int FIRST_ARITHMETIC_TERM = 401;
	
	/**
	 * <code>PLUS</code> represents +.
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int PLUS = FIRST_ARITHMETIC_TERM + 0;
	
	/**
	 * <code>MINUS</code> represents -.
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int MINUS = FIRST_ARITHMETIC_TERM + 1;
	
	/**
	 * <code>MUL</code> represents *.
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int MUL = FIRST_ARITHMETIC_TERM + 2;
	
	/**
	 * <code>DIV</code> represents /.
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int DIV = FIRST_ARITHMETIC_TERM + 3;
	
	/**
	 * <code>MODULO</code> represents %.
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int MODULO = FIRST_ARITHMETIC_TERM + 4;
	
	/**
	 * <code>UNARY_MINUS</code> represents ~.
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int UNARY_MINUS = FIRST_ARITHMETIC_TERM + 5;
	
	/**
	 * First tag for a base term.
	 * 
	 * @see SMTBoolean
	 */
	public final static int FIRST_BASE_TERM = 501;
	
	/**
	 * <code>IDENTIFIER</code> represents an identifier. 
	 * 
	 * @see SMTNumeral
	 */
	public final static int IDENTIFIER = FIRST_BASE_TERM  + 0;
	
	/**
	 * <code>NUMERAL</code> represents a numeral. 
	 * 
	 * @see SMTNumeral
	 */
	public final static int NUMERAL = FIRST_BASE_TERM  + 1;
	
	/**
	 * First tag for a boolean term.
	 * 
	 * @see SMTBoolean
	 */
	public final static int FIRST_BOOLEAN_TERM = FIRST_BASE_TERM + 2;

	/**
	 * <code>TRUE</code> represents TRUE
	 * 
	 * @see SMTBoolean
	 */
	public final static int TRUE = FIRST_BOOLEAN_TERM + 0;

	/**
	 * <code>FALSE</code> represents FALSE
	 * 
	 * @see SMTBoolean
	 */
	public final static int FALSE = FIRST_BOOLEAN_TERM + 1;
	
	/**
	 * Tag of Macro.
	 * 
	 * @see SMTMacroFormula & SMTMacroTerm
	 */
	public final static int MACRO = 601;
	
	/**
	 * Tag of Macro for terms.
	 * 
	 * @see SMTMacroTerm
	 */
	public final static int MACRO_TERM = MACRO + 1;
	
	/**
	 * Tag of Macro for formulas.
	 * 
	 * @see SMTMacroFormula
	 */
	public final static int MACRO_FORMULA = MACRO + 2;
	
	/**
	 * Tag of Bound Identifier.
	 * 
	 * @see SMTBoundIdentifier
	 */
	public final static int BOUND_IDENTIFIER = 701;
	
	/**
	 * Tag of Bound Identifier Declaration.
	 * 
	 * @see SMTBoundIdentifierDecl
	 */
	public final static int BOUND_IDENTIFIER_DECL = BOUND_IDENTIFIER + 1;
	
	/**
	 * Tag of Quantified Predicate Declaration.
	 * 
	 * @see SMTQuantifiedPred
	 */
	public final static int QUANTIFIED_PRED_DECL = BOUND_IDENTIFIER_DECL + 1;
	
	/**
	 * Tag of Quantified Predicate Exists Declaration.
	 * 
	 * @see SMTQuantifiedPred
	 */
	public final static int QUANTIFIED_PRED_EXISTS_DECL = QUANTIFIED_PRED_DECL;
	
	/**
	 * Tag of Quantified Predicate ForAll Declaration.
	 * 
	 * @see SMTQuantifiedPred
	 */
	public final static int QUANTIFIED_PRED_FORALL_DECL = QUANTIFIED_PRED_DECL + 1;
	
	/**
	 * First tag for a script command.
	 * 
	 * @see SMTCommand
	 */
	public final static int FIRST_COMMAND = 801;
	
	/**
	 * First tag for a script response.
	 * 
	 * @see SMTResponse
	 */
	public final static int FIRST_RESPONSE = 901;
	
	/**
	 * First tag for a script command option.
	 * 
	 * @see SMTCommandOption
	 */
	public final static int FIRST_COMMAND_OPTION = 1001;
	
	/**
	 * First tag for a token.
	 * 
	 * @see SMTToken
	 */
	public final static int FIRST_TOKEN = 1101;
	
	/**
	 * First tag for a term.
	 * 
	 * @see SMTTerm
	 */
	public final static int FIRST_TERM = 1201;

	
	// =========================================================================
	// Variables
	// =========================================================================
	/** The tag for this AST node. */
	private final int tag;

	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a new AST node.
	 * 
	 * @param tag
	 *            the tag
	 */
	protected SMTNode(int tag) {
		this.tag = tag;
	}

	// =========================================================================
	// Getters
	// =========================================================================

	/**
	 * Returns the tag of this AST node.
	 * <p>
	 * Each node has an attached tag that represents the operator associated to
	 * it.
	 * 
	 * @return the tag
	 */
	public final int getTag() {
		return tag;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================

	@Override
	public final String toString() {
		StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	/**
	 * Builds the string representation of the SMT noode.
	 * <p>
	 * 
	 * @param builder
	 *            the string builder containing the result
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract void toString(StringBuilder builder);
}
