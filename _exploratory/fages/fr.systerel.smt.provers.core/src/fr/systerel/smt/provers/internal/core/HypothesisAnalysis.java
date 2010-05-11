package fr.systerel.smt.provers.internal.core;

import static org.eventb.core.seqprover.eventbExtensions.Lib.ff;

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
import org.eventb.core.ast.Formula;
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
public class HypothesisAnalysis {

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
	 * 
	 * @author Y. Fages-Tafanelli
	 * 
	 */
	public void SimplifyHypothesis (List<Predicate> hypList,List<Predicate> hypListSimp){
		
		// Create a generator for smt variables 
		final SmtVarGenerator smtVarGen= new SmtVarGenerator();
		
		for (Predicate hyp : hypList) {
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
				};
			};
			// Add the simplified hypothesis in the list 
			hypListSimp.add(hyp.rewrite(rewriter));
		}
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

		@Override
		public boolean continueBCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean continueBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean continueBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean continueBINTER(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueBUNION(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueCPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueCSET(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueDIV(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueDOMRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueDOMSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueDPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueEXISTS(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueEXPN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueFCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueFORALL(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueFUNIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueGE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueGT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueKPARTITION(MultiplePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueLAND(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueLE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueLEQV(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueLIMP(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueLOR(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueLT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueMAPSTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueMOD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueMUL(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueNOTEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueNOTIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueNOTSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueNOTSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueOVR(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continuePFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continuePINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continuePLUS(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continuePPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continuePSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueQINTER(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueQUNION(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueRANRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueRANSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueRELIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueSETEXT(SetExtension set) {
			addTag(set);
			return true;
		}

		@Override
		public boolean continueSETMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueSREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueSTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean continueTBIJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueTFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueTINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueTSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean continueUPTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterBCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean enterBINTER(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterBUNION(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterCONVERSE(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterCPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterCSET(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterDIV(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterDOMRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterDOMSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterDPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterEXISTS(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterEXPN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterFCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterFORALL(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterFUNIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterGE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterGT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterKBOOL(BoolExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKCARD(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKDOM(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKFINITE(SimplePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterKID(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKINTER(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKMAX(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKMIN(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKPARTITION(MultiplePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterKPRJ1(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKPRJ2(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKRAN(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterKUNION(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterLAND(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterLE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterLEQV(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterLIMP(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterLOR(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterLT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterMAPSTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterMOD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterMUL(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterNOT(UnaryPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterNOTEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterNOTIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterNOTSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterOVR(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterPFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterPINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterPLUS(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterPOW(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterPOW1(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterPPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterPSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterQINTER(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterQUNION(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterRANRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterRANSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterRELIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterSETEXT(SetExtension set) {
			addTag(set);
			return true;
		}

		@Override
		public boolean enterSETMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterSREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterSTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean enterTBIJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterTFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterTINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterTSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterUNMINUS(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean enterUPTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitBCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			addTag(assign);
			return true;
		}

		@Override
		public boolean exitBINTER(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitBUNION(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitCONVERSE(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitCPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitCSET(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitDIV(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitDOMRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitDOMSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitDPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitEXISTS(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitEXPN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitFCOMP(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitFORALL(QuantifiedPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitFUNIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitGE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitGT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitKBOOL(BoolExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKCARD(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKDOM(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKFINITE(SimplePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitKID(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKINTER(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKMAX(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKMIN(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKPARTITION(MultiplePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitKPRJ1(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKPRJ2(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKRAN(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitKUNION(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitLAND(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitLE(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitLEQV(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitLIMP(BinaryPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitLOR(AssociativePredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitLT(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitMAPSTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitMOD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitMUL(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitNOT(UnaryPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitNOTEQUAL(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitNOTIN(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitNOTSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitOVR(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitPFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitPINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitPLUS(AssociativeExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitPOW(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitPOW1(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitPPROD(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitPSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitQINTER(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitQUNION(QuantifiedExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitRANRES(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitRANSUB(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitRELIMAGE(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitSETEXT(SetExtension set) {
			addTag(set);
			return true;
		}

		@Override
		public boolean exitSETMINUS(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitSREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitSTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitSUBSET(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitSUBSETEQ(RelationalPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean exitTBIJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitTFUN(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitTINJ(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitTREL(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitTSUR(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitUNMINUS(UnaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean exitUPTO(BinaryExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitBFALSE(LiteralPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean visitBOOL(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitBOUND_IDENT(BoundIdentifier ident) {
			addTag(ident);
			return true;
		}

		@Override
		public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
			addTag(ident);
			return true;
		}

		@Override
		public boolean visitBTRUE(LiteralPredicate pred) {
			addTag(pred);
			return true;
		}

		@Override
		public boolean visitEMPTYSET(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitFALSE(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitFREE_IDENT(FreeIdentifier ident) {
			addTag(ident);
			return true;
		}

		@Override
		public boolean visitINTEGER(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitINTLIT(IntegerLiteral lit) {
			addTag(lit);
			return true;
		}

		@Override
		public boolean visitKID_GEN(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitKPRED(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitKPRJ1_GEN(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitKPRJ2_GEN(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitKSUCC(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitNATURAL(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitNATURAL1(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

		@Override
		public boolean visitTRUE(AtomicExpression expr) {
			addTag(expr);
			return true;
		}

	}

}