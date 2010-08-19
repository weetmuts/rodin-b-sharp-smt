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
package fr.systerel.smt.provers.astV1_2;

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
	 * <code>LAMBDA</code> represents an lambda term. 
	 * 
	 * @see SMTLambda
	 */
	public final static int LAMBDA = 4;
	
	/**
	 * <code>BECOMES_FORMULA</code> represents a Becomes Formula 
	 * 
	 * @see SMTBecomesFormula
	 */
	public final static int BECOMES_FORMULA = 5;

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
	 * <code>PINT</code> represents the <tt>Int</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PINT = FIRST_PROP_ATOM + 2;
	
	/**
	 * <code>PNAT</code> represents the <tt>Nat</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PNAT = FIRST_PROP_ATOM + 3;
	
	/**
	 * <code>PNAT1</code> represents the <tt>Nat1</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PNAT1 = FIRST_PROP_ATOM + 4;
	
	/**
	 * <code>PBOOL</code> represents the <tt>Bool</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PBOOL = FIRST_PROP_ATOM + 5;
	
	/**
	 * <code>PEMPTYSET</code> represents the <tt>EmptySet</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PEMPTYSET = FIRST_PROP_ATOM + 6;
	
	/**
	 * <code>PKPRED</code> represents the <tt>Kpred</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PKPRED = FIRST_PROP_ATOM + 7;
	
	/**
	 * <code>PKSUCC</code> represents the <tt>KSucc</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PKSUCC = FIRST_PROP_ATOM + 8;
	
	/**
	 * <code>PKPRJ1_GEN</code> represents the <tt>KPrj1_Gen</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PKPRJ1_GEN = FIRST_PROP_ATOM + 9;
	
	/**
	 * <code>PKPRJ2_GEN</code> represents the <tt>Kprj2_Gen</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PKPRJ2_GEN = FIRST_PROP_ATOM + 10;
	
	/**
	 * <code>PKID_GEN</code> represents the <tt>Kid_Gen</tt> propositional atom.
	 * 
	 * @see SMTPropAtom
	 */
	public final static int PKID_GEN = FIRST_PROP_ATOM + 11;
	
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
	 * <code>MAPSTO</code> represents "pair".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int MAPSTO = FIRST_ARITHMETIC_TERM + 6;
	
	/**
	 * <code>REL</code> represents "rel".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int REL = FIRST_ARITHMETIC_TERM + 7;
	
	/**
	 * <code>PFUN</code> represents "pfun".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int PFUN = FIRST_ARITHMETIC_TERM + 8;
	
	/**
	 * <code>TFUN</code> represents "tfun".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int TFUN = FIRST_ARITHMETIC_TERM + 9;
	
	/**
	 * <code>PINJ</code> represents "pinj".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int PINJ = FIRST_ARITHMETIC_TERM + 10;
	
	/**
	 * <code>TINJ</code> represents "tinj".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int TINJ = FIRST_ARITHMETIC_TERM + 11;
	
	/**
	 * <code>PSUR</code> represents "psur".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int PSUR = FIRST_ARITHMETIC_TERM + 12;
	
	/**
	 * <code>TSUR</code> represents "tsur".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int TSUR = FIRST_ARITHMETIC_TERM + 13;
	
	/**
	 * <code>TBIJ</code> represents "bij".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int TBIJ = FIRST_ARITHMETIC_TERM + 14;
	
	/**
	 * <code>SETMINUS</code> represents "setminus".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int SETMINUS = FIRST_ARITHMETIC_TERM + 15;
	
	/**
	 * <code>CPROD</code> represents "cartesianproduct".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int CPROD = FIRST_ARITHMETIC_TERM + 16;
	
	/**
	 * <code>DOMRES</code> represents "domr".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int DOMRES = FIRST_ARITHMETIC_TERM + 17;
	
	/**
	 * <code>DOMSUB</code> represents "doms".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int DOMSUB = FIRST_ARITHMETIC_TERM + 18;
	
	/**
	 * <code>RANRES</code> represents "ranres".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int RANRES = FIRST_ARITHMETIC_TERM + 19;
	
	/**
	 * <code>RANSUB</code> represents "rans".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int RANSUB = FIRST_ARITHMETIC_TERM + 20;
	
	/**
	 * <code>UPTO</code> represents "range".
	 * 
	 * @see SMTArithmeticTerm
	 */
	public final static int UPTO = FIRST_ARITHMETIC_TERM + 21;
		
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
	SMTNode(int tag) {
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
	abstract void toString(StringBuilder builder);
}
