/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - minor modifications
 *******************************************************************************/

package org.eventb.smt.core.internal.ast;

import static org.eventb.smt.core.internal.ast.SMTFactory.CPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.OPAR;
import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;

import java.util.ArrayList;
import java.util.List;

import org.eventb.smt.core.internal.ast.attributes.Attribute;
import org.eventb.smt.core.internal.ast.commands.AssertCommand;
import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;

/**
 * This is the base class for all nodes of an SMT-LIB AST (Abstract Syntax
 * Tree).
 */
public abstract class SMTNode<T extends SMTNode<T>> {
	private static final String ANNOTATION_OPERATOR = "!";
	private final List<Attribute<?>> annotations = new ArrayList<Attribute<?>>();

	/**
	 * Checks the rank for associative symbol (predicate or function symbol).
	 * That is, given a sort and the arguments, this methods checks if all the
	 * terms has the same sort as the expected sort.
	 * 
	 * @param expectedSortArg
	 *            the expected sort.
	 * @param terms
	 *            the rank checked terms.
	 * @return true if all the terms are of the same sort of the expected sort,
	 *         false otherwise.
	 */
	protected static boolean checkAssociativeRank(
			final SMTSortSymbol expectedSortArg, final SMTTerm[] terms) {
		for (final SMTTerm term : terms) {
			if (!term.getSort().isCompatibleWith(expectedSortArg)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the rank for non-associative symbol (predicate or function
	 * symbol). That is, given the expected sorts and the arguments, it checks
	 * if each sort corresponds with each argument.
	 * 
	 * @param expectedSortArgs
	 *            the sexpected sorts
	 * @param terms
	 *            the arguments
	 * @return true if, for each argument, its sort is the same of the expected
	 *         sort for its parameter index
	 */
	public static boolean checkNonAssociativeRank(
			final SMTSortSymbol[] expectedSortArgs, final SMTTerm[] terms) {
		if (expectedSortArgs.length != terms.length) {
			return false;
		}
		for (int i = 0; i < terms.length; i++) {
			if (!expectedSortArgs[i].isCompatibleWith(terms[i].getSort())) {
				return false;
			}
		}
		return true;
	}

	public static void indent(final StringBuilder builder, final int offset) {
		for (int i = 0; i < offset; i++) {
			builder.append(" ");
		}
	}

	public void addAnnotation(final Attribute<?> attribute) {
		annotations.add(attribute);
	}

	public boolean isAnnotated() {
		return !annotations.isEmpty();
	}

	/**
	 * This method must be called only if isAnnotated() returned true.
	 * 
	 * @param builder
	 */
	public static void printAnnotationOperator(final StringBuilder builder) {
		builder.append(OPAR);
		builder.append(ANNOTATION_OPERATOR);
		builder.append(SPACE);
	}

	/**
	 * This method must be called only if isAnnotated() returned true.
	 * 
	 * @param builder
	 */
	public void printAnnotations(final StringBuilder builder) {
		builder.append("\n");
		indent(builder, AssertCommand.ASSERT_COMMAND_OFFSET);
		builder.append(SPACE);
		for (final Attribute<?> attribute : annotations) {
			attribute.toString(builder);
		}
		builder.append(CPAR);
	}
}
