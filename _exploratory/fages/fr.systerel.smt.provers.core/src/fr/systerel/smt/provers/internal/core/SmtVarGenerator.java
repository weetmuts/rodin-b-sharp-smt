/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Systerel (YFT) - Creation
 *     Vítor Alcântara de Almeida - First integration Smt solvers 
 *******************************************************************************/

package fr.systerel.smt.provers.internal.core;

import java.util.HashMap;

import org.eventb.core.ast.Expression;

/**
 * Class Implementation to generate smt var names.
 * 
 * @author Y. Fages-Tafanelli
 */
public class SmtVarGenerator {

	private Integer index=0;
	
	// Hashmap to link expression with its simplified string expression
	private HashMap<Expression, String> hm = new HashMap<Expression, String>();

	public String SmtVarName(Expression expr){
		if (hm.containsKey(expr)) {
			return hm.get(expr);
		}
		else {
			index++;
			String smtStr = "smt_"+index.toString();
			hm.put(expr, smtStr);
			return smtStr;
		}
	}

}