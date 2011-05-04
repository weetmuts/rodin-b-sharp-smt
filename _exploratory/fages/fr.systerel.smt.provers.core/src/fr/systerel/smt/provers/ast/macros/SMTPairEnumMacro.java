/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast.macros;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import fr.systerel.smt.provers.ast.SMTFunApplication;
import fr.systerel.smt.provers.ast.SMTMacroTerm;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTVarSymbol;

/**
 * <p>
 * This class is also used to create macros that are enumerations. But these
 * enumerations are of mapped values, and all elements are composed of the
 * function pair and the two arguments that form the maplet.
 * </p>
 * 
 * <p>
 * The translation is done as follows:
 * </p>
 * 
 * <p>
 * Given an Event-B set defined in extension:
 * </p>
 * 
 * <code>{x1 ↦ y1, x2 ↦ y2, … ,xn ↦ yn}</code>
 * 
 * <p>
 * where x1 … xn, y1...yn are predicate variables, and assuming that MP = {x1 ↦
 * y1, x2 ↦ y2, … ,xn ↦ yn}, they are translated to the macro:
 * </p>
 * 
 * <p>
 * <code>
 * (lambda ( ?f S) (?g T) . (or (= (pair ?f ?g) ( pair X1 Y1) (= (pair ?f ?g) (
 * pair X2 Y2) .. (= (pair ?f ?g) ( pair Xn Yn) )))
 * </code>
 * </p>
 * 
 * <p>
 * where:
 * </p>
 * 
 * <ul>
 * <li>S = dom(MP)</li>
 * <li>T = ran(MP)></li>
 * <li>?f is a fresh variable with type S</li>
 * <li>?g is a fresh variable with type T</li>
 * <li>X1 = smt(x1),X2 = smt(x2), … , Xn = smt(xn)</li>
 * <li>Y1 = smt(y1), Y2 = smt(y2) … , Yn = smt(yn)</li>
 * </ul>
 * 
 * @author vitor
 */
public class SMTPairEnumMacro extends SMTMacro {

	/**
	 * Initializes the class.
	 * 
	 * @param macroName
	 *            The name of the macro
	 * @param key
	 *            The ?f variable. See {@link SMTPairEnumMacro} for more
	 *            details.
	 * @param terms
	 *            The terms that contains the two values for each maplet. See
	 *            {@link SMTPairEnumMacro} for more details.
	 * 
	 * @param precedence
	 *            The precedence of the macro. See {@link SMTMacro} for more
	 *            details.
	 */
	SMTPairEnumMacro(final String macroName, final SMTVarSymbol key,
			final SMTTerm[] terms, final int precedence) {
		super(macroName, precedence);
		this.key = key;
		this.terms = terms;
	}

	/**
	 * return the ?f variable.
	 * 
	 * @see SMTPairEnumMacro
	 * 
	 * @return the ?f variable.
	 */
	public SMTVarSymbol getKey() {
		return key;
	}

	/**
	 * It's the ?f variable as described in {@link SMTPairEnumMacro}
	 */
	private final SMTVarSymbol key;

	/**
	 * The terms that contains the two values for each maplet. See
	 * {@link SMTPairEnumMacro} for more details.
	 */
	private final SMTTerm[] terms;

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	/**
	 * returns a string representation of each element of the enumeration. The
	 * string representation is:
	 * 
	 * (= (pair ?f ?g) ( pair Xn Yn) )
	 * 
	 * @see SMTPairEnumMacro
	 * 
	 * @param keyEl
	 *            the ?f identifier
	 * @param keyTerm
	 *            the Xn term
	 * @param valueTerm
	 *            the Yn term
	 */
	private void elemToString(final SMTVarSymbol keyEl, final SMTTerm keyTerm,
			final SMTTerm valueTerm, final StringBuilder sb) {
		sb.append(OPAR);
		sb.append("=");
		sb.append(SPACE);
		keyEl.getNameWithQMark(sb);
		sb.append(SPACE);
		sb.append(OPAR);
		sb.append("pair");
		sb.append(SPACE);
		keyTerm.toString(sb);
		sb.append(SPACE);
		valueTerm.toString(sb);
		sb.append(CPAR);
		sb.append(CPAR);
	}

	private SMTTerm getArgTerm(SMTTerm term, int index) {
		if (term instanceof SMTMacroTerm) {
			SMTMacroTerm mT = (SMTMacroTerm) term;
			return mT.getArgs()[index];
		} else if (term instanceof SMTFunApplication) {
			SMTFunApplication fA = (SMTFunApplication) term;
			return fA.getArgs()[index];
		} else {
			// FIXME: This statement should never be reached
			return null;
		}
	}

	@Override
	public void toString(final StringBuilder sb) {
		sb.append(OPAR);
		sb.append(super.getMacroName());
		sb.append(" (lambda ");
		// sb.append(key);
		key.toString(sb);
		sb.append(" . ");
		if (terms.length == 1) {

			elemToString(key, getArgTerm(terms[0], 0), getArgTerm(terms[0], 1),
					sb);
			sb.append(CPAR);
			sb.append(CPAR);
		} else {
			sb.append("(or");
			for (final SMTTerm term : terms) {
				sb.append("\n\t\t");
				elemToString(key, getArgTerm(term, 0), getArgTerm(term, 1), sb);
			}
			sb.append("\n");
			sb.append(CPAR);
			sb.append(CPAR);
			sb.append(CPAR);
		}
	}
}
