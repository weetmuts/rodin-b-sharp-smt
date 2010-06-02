package fr.systerel.smt.provers.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
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
		final List<Predicate> smtHypsSimp;
		final Predicate goalSimp;
		final HypothesisGoalAnalysis smtHypGoalOps= new HypothesisGoalAnalysis();
		
		// Hypothesis selection
		for (Predicate hyp : hypotheses) {
			if (smtHypGoalOps.IsArithmetic(hyp)){
				smtHyps.add(hyp);
			}
		}
		
		// Hypothesis & Goal simplification
		smtHypsSimp=smtHypGoalOps.SimplifyHypothesis(smtHyps);
		goalSimp=smtHypGoalOps.SimplifyGoal(goal);
		
		System.out.println("Hyps simplified " +smtHypsSimp.toString());
		System.out.println("Goal simplified " +goalSimp.toString());
		
		return new SmtCall(smtHyps, goalSimp, pm);
	}
}
