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


/**
 * This class represents a numeral in SMT-LIB grammar.
 */
public final class SMTMacroTerm extends SMTTerm {
	
	/** The children. */
	private final SMTTerm[] children;
	
	private final String macroId;
	
	private final boolean notClause;
	
	/**
	 * Creates a new macro.
	 * 
	 */
	SMTMacroTerm(int tag, String macroId, SMTTerm[] children, boolean not) {
		super(tag);
		if (children != null){
			this.children = children.clone();
		}
		else{
			this.children = null;
		}
			
		this.macroId = macroId;
		this.notClause = not;
	}

	@Override
	public void toString(StringBuilder builder) {
		// Add the not keyword if needed
		if (notClause){
			builder.append("(not");
		}
		
		if (children == null){
			builder.append(macroId);			
		}
		else{				
	        String sep = "(" + macroId + " " ;
			for (SMTTerm child: children) {
				builder.append(sep);
				sep = " ";
				child.toString(builder);
			}
			builder.append(")");
		}
		
		// Add the last parenthesis for the not keyword if needed
		if (notClause){
			builder.append(")");
		}
	}
}
