package fr.systerel.smt.provers.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * Implementation of a class to generate smt var names.
 * 
 * @author Y. Fages-Tafanelli
 */
public class SmtVarGenerator {

	private Integer index=0;
	
	// Hashmap to link expression with its simplified string
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