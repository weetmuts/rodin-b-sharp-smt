package fr.systerel.smt.provers.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.IVisitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;

import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * Implementation of a call to the SMT Prover.
 * 
 * @author Y. Fages-Tafanelli
 */
public class ExternalSmt extends XProverReasoner {
	
	public static String REASONER_ID = SmtProversCore.PLUGIN_ID + ".externalSMT";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	public XProverCall newProverCall(IReasonerInput input,
			Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm) {
	
		
		
		final List<Predicate> smtHyps=new ArrayList<Predicate>();
		final List<Predicate> smtHypsSimp=new ArrayList<Predicate>();
		final HypothesisAnalysis smtHypOps= new HypothesisAnalysis();
		
		// Hypothesis selection
		for (Predicate hyp : hypotheses) {
			if (smtHypOps.IsArithmetic(hyp)){
				smtHyps.add(hyp);
			}
		}
		
		// Hypothesis simplification
		smtHypOps.SimplifyHypothesis(smtHyps,smtHypsSimp);
		
		System.out.println(smtHypsSimp.toString());
		
		return new SmtCall(smtHyps, goal, pm);
	}
}
