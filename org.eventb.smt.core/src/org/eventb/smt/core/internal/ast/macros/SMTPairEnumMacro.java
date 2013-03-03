/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.ast.macros;

import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.OPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;

import org.eventb.smt.core.internal.ast.SMTFunApplication;
import org.eventb.smt.core.internal.ast.SMTTerm;
import org.eventb.smt.core.internal.ast.symbols.SMTVarSymbol;

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
 * @author Vitor Alcantara de Almeida
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

	public SMTTerm[] getTerms() {
		return terms;
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
		toString(builder, -1);
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
			final SMTTerm valueTerm, final StringBuilder sb, final int offset) {
		sb.append(OPAR);
		sb.append("=");
		sb.append(SPACE);
		keyEl.getNameWithQMark(sb);
		sb.append(SPACE);
		sb.append(OPAR);
		sb.append("pair");
		sb.append(SPACE);
		keyTerm.toString(sb, offset);
		sb.append(SPACE);
		valueTerm.toString(sb, offset);
		sb.append(CPAR);
		sb.append(CPAR);
	}

	/**
	 * Returns the argument in the position <code>index</code> of the arg terms.
	 * The term must be an instanceof {@link SMTMacroTerm} or
	 * {@link SMTFunApplication}
	 * 
	 * @param term
	 *            the term.
	 * @param index
	 *            the index of the argument
	 * @return the term argument in the position index
	 */
	private SMTTerm getArgTerm(final SMTTerm term, final int index) {
		if (term instanceof SMTFunApplication) {
			final SMTFunApplication fA = (SMTFunApplication) term;
			return fA.getArgs()[index];
		} else {
			throw new IllegalArgumentException(
					"All the arguments of the pair enum macro must be a SMTFunApplication");
		}
	}

	@Override
	public void toString(final StringBuilder sb, final int offset) {
		sb.append(OPAR);
		sb.append(super.getMacroName());
		sb.append(" (lambda ");
		key.toString(sb);
		sb.append(" . ");
		if (terms.length == 1) {

			elemToString(key, getArgTerm(terms[0], 0), getArgTerm(terms[0], 1),
					sb, offset);
			sb.append(CPAR);
			sb.append(CPAR);
		} else {
			sb.append("(or");
			for (final SMTTerm term : terms) {
				sb.append("\n\t\t");
				elemToString(key, getArgTerm(term, 0), getArgTerm(term, 1), sb,
						offset);
			}
			sb.append("\n");
			sb.append(CPAR);
			sb.append(CPAR);
			sb.append(CPAR);
		}
	}
}
