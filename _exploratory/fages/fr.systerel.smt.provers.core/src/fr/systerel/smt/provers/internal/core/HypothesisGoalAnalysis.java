package fr.systerel.smt.provers.internal.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IVisitor;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Implementation of a class to memorize operators encountered in hypothesis.
 * 
 * @author Y. Fages-Tafanelli
 */
public class HypothesisGoalAnalysis {

	// generator of smt variable generator
	final SmtVarGenerator smtVarGen= new SmtVarGenerator();

	private static final Set<Integer> ARITHMETIC_TAG_LIST = new HashSet<Integer>(
			Arrays.asList(Formula.BOUND_IDENT, Formula.DIV, Formula.EQUAL,
					Formula.EXPN, Formula.FREE_IDENT, Formula.FUNIMAGE,
					Formula.GE, Formula.GT, Formula.IN, Formula.INTEGER,
					Formula.INTLIT, Formula.LAND, Formula.LE, Formula.LEQV,
					Formula.LIMP, Formula.LOR, Formula.LT, Formula.MINUS,
					Formula.MOD, Formula.MUL, Formula.NATURAL,
					Formula.NATURAL1, Formula.NOTEQUAL, Formula.PLUS,
					Formula.POW, Formula.POW1, Formula.TFUN, Formula.UNMINUS,
					Formula.UPTO));

	// Check if the hypothesis is arithmetic or not
	public boolean IsArithmetic(Predicate hyp) {

		// Create a new visitor
		SmtVisitor hypVisitor = new SmtVisitor();
		hyp.accept(hypVisitor);

		System.out.println(hypVisitor.getTagList().toString());

		// Check if the tag list is arithmetic
		return ARITHMETIC_TAG_LIST.containsAll(hypVisitor.getTagList());

	}

	/**
	 * Simplify Hypothesis
	 * 	Parameter(s): List of predicates --> hypothesis to simplify
	 * 	return a list of predicates for simplified hypothesis 
	 * 
	 * @author Y. Fages-Tafanelli
	 * 
	 */
	public List<Predicate> SimplifyHypothesis (List<Predicate> hypList){

		// Create a generator for smt variables 
		List<Predicate> hypListSimp = new ArrayList<Predicate>();

		for (Predicate hyp : hypList) {
			final FormulaFactory ff = FormulaFactory.getDefault();
			IFormulaRewriter rewriter=new DefaultRewriter(true, ff){
				@Override
				public Predicate rewrite(RelationalPredicate predicate) {
					Predicate pred=predicate;
					switch (predicate.getTag())
					{
					case Formula.IN:
						// Handle IN 
						if (predicate.getRight().getTag()== Formula.NATURAL){
							// Natural
							pred=ff.makeRelationalPredicate(Formula.LE,ff.makeIntegerLiteral(new BigInteger("0"), null),predicate.getLeft(), null);
						}
						else if (predicate.getRight().getTag()== Formula.NATURAL1){
							// Natural 1
							pred=ff.makeRelationalPredicate(Formula.LT,ff.makeIntegerLiteral(new BigInteger("0"), null),predicate.getLeft(), null);
						}
						break;
					default:
						break;
					}
					return pred;
				};
				public Expression rewrite(BinaryExpression expression) {
					Expression expr=expression;
					switch(expression.getTag())
					{
					case Formula.FUNIMAGE:
						// Handle Function Image in rewriter
						expr = ff.makeFreeIdentifier(smtVarGen.SmtVarName(expression),null,expression.getType());
						break;
					default:
						break;
					}
					return expr;
				};
			};
			// Add the simplified hypothesis in the list 
			hypListSimp.add(hyp.rewrite(rewriter));
		}
		return hypListSimp;
	}

	/**
	 * Simplify Goal:
	 * 	Parameter(s): goal --> predicate to simplify
	 * 	return a predicate --> goal simplified
	 * 
	 * @author Y. Fages-Tafanelli
	 * 
	 */
	public Predicate SimplifyGoal (Predicate goal){
		final FormulaFactory ff = FormulaFactory.getDefault();
		IFormulaRewriter rewriter=new DefaultRewriter(true, ff){
			public Expression rewrite(BinaryExpression expression) {
				// Handle Function Image
				if (expression.getTag()== Formula.FUNIMAGE){
					Expression expr = ff.makeFreeIdentifier(smtVarGen.SmtVarName(expression),null,expression.getType());
					return expr;
				}
				else {
					return expression;	
				}
			}
		};
		return goal.rewrite(rewriter);
	}

	/**
	 * Expression/operators ... tags are memorized to be analyzed later
	 * 
	 * @author Y. Fages-Tafanelli
	 * 
	 */
	private static class SmtVisitor implements IVisitor {

		private final Set<Integer> tagList = new HashSet<Integer>();

		public Set<Integer> getTagList() {
			return tagList;
		}

		public void addTag(Formula<?> formula) {
			tagList.add(formula.getTag());
		}

		public boolean continueBCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			addTag(assign);
			return true;
		}

		public boolean continueBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			addTag(assign);
			return true;
		}

		public boolean continueBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			addTag(assign);
			return true;
		}

		public boolean continueBINTER(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueBUNION(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueCPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueCSET(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueDIV(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueDOMRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueDOMSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueDPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueEXISTS(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueEXPN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueFCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueFORALL(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueFUNIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueGE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueGT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueKPARTITION(MultiplePredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueLAND(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueLE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueLEQV(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueLIMP(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueLOR(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueLT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueMAPSTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueMOD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueMUL(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueNOTEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueNOTIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueNOTSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueNOTSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueOVR(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continuePFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continuePINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continuePLUS(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continuePPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continuePSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueQINTER(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueQUNION(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueRANRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueRANSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueRELIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueSETEXT(SetExtension set) {
			addTag(set);
			return true;
		}

		public boolean continueSETMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueSREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueSTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		public boolean continueSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		public boolean continueSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean continueTBIJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean continueTFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean continueTINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean continueTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean continueTSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean continueUPTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterBCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			addTag(assign);
			return true;
		}

		
		public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			addTag(assign);
			return true;
		}

		
		public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			addTag(assign);
			return true;
		}

		
		public boolean enterBINTER(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterBUNION(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterCONVERSE(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterCPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterCSET(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterDIV(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterDOMRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterDOMSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterDPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterEXISTS(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterEXPN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterFCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterFORALL(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterFUNIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterGE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterGT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterKBOOL(BoolExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKCARD(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKDOM(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKFINITE(SimplePredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterKID(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKINTER(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKMAX(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKMIN(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKPARTITION(MultiplePredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterKPRJ1(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKPRJ2(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKRAN(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterKUNION(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterLAND(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterLE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterLEQV(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterLIMP(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterLOR(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterLT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterMAPSTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterMOD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterMUL(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterNOT(UnaryPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterNOTEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterNOTIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterNOTSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterOVR(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterPFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterPINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterPLUS(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterPOW(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterPOW1(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterPPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterPSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterQINTER(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterQUNION(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterRANRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterRANSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterRELIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterSETEXT(SetExtension set) {
			addTag(set);
			return true;
		}

		
		public boolean enterSETMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterSREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterSTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean enterTBIJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterTFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterTINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterTSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterUNMINUS(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean enterUPTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitBCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			addTag(assign);
			return true;
		}

		
		public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			addTag(assign);
			return true;
		}

		
		public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			addTag(assign);
			return true;
		}

		
		public boolean exitBINTER(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitBUNION(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitCONVERSE(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitCPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitCSET(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitDIV(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitDOMRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitDOMSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitDPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitEXISTS(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitEXPN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitFCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitFORALL(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitFUNIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitGE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitGT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitKBOOL(BoolExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKCARD(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKDOM(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKFINITE(SimplePredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitKID(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKINTER(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKMAX(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKMIN(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKPARTITION(MultiplePredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitKPRJ1(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKPRJ2(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKRAN(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitKUNION(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitLAND(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitLE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitLEQV(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitLIMP(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitLOR(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitLT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitMAPSTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitMOD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitMUL(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitNOT(UnaryPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitNOTEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitNOTIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitNOTSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitOVR(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitPFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitPINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitPLUS(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitPOW(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitPOW1(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitPPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitPSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitQINTER(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitQUNION(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitRANRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitRANSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitRELIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitSETEXT(SetExtension set) {
			addTag(set);
			return true;
		}

		
		public boolean exitSETMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitSREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitSTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean exitTBIJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitTFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitTINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitTSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitUNMINUS(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean exitUPTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitBFALSE(LiteralPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean visitBOOL(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitBOUND_IDENT(BoundIdentifier ident) {
			addTag(ident);
			return true;
		}

		
		public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
			addTag(ident);
			return true;
		}

		
		public boolean visitBTRUE(LiteralPredicate pred) {
			addTag(pred);
			return true;
		}

		
		public boolean visitEMPTYSET(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitFALSE(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitFREE_IDENT(FreeIdentifier ident) {
			addTag(ident);
			return true;
		}

		
		public boolean visitINTEGER(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitINTLIT(IntegerLiteral lit) {
			addTag(lit);
			return true;
		}

		
		public boolean visitKID_GEN(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitKPRED(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitKPRJ1_GEN(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitKPRJ2_GEN(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitKSUCC(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitNATURAL(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitNATURAL1(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean visitTRUE(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		
		public boolean continueExtendedExpression(ExtendedExpression expr) {
			addTag(expr);
			return false;
		}

		
		public boolean continueExtendedPredicate(ExtendedPredicate pred) {
			addTag(pred);
			return false;
		}

		
		public boolean enterExtendedExpression(ExtendedExpression expr) {
			addTag(expr);
			return false;
		}

		
		public boolean enterExtendedPredicate(ExtendedPredicate pred) {
			addTag(pred);
			return false;
		}

		
		public boolean exitExtendedExpression(ExtendedExpression expr) {
			addTag(expr);
			return false;
		}

		
		public boolean exitExtendedPredicate(ExtendedPredicate pred) {
			addTag(pred);
			return false;
		}

	}

}