package fr.systerel.smt.provers.internal.ui;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

import fr.systerel.smt.provers.ui.SmtProversUIPlugin;

public class SMTTacticProvider extends DefaultTacticProvider implements
		ITacticProvider {

	public static class SMTApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return SmtProversUIPlugin.ExternalSmtTac(true);
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && node.isOpen()) {
			final ITacticApplication appli = new SMTApplication();
			return singletonList(appli);
		}
		return emptyList();
	}
}
