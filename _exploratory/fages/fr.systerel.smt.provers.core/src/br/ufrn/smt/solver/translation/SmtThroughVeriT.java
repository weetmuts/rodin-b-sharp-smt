/**
 * 
 */
package br.ufrn.smt.solver.translation;

import java.util.ArrayList;

import org.eventb.core.ast.Predicate;

/**
 * @author guyot
 * 
 */
public class SmtThroughVeriT extends TranslatorV1_2 {


	/**
	 * This method translates the given predicate into an SMT Node.
	 */
	public static IdentifiersAndSMTStorage translate1(Predicate predicate,
			ArrayList<String> boundIdentifiers,
			ArrayList<String> freeIdentifiers) {
		final TranslatorV1_2 translator = new TranslatorV1_2(predicate,
				boundIdentifiers, freeIdentifiers);
		predicate.accept(translator);
		IdentifiersAndSMTStorage iSMT = new IdentifiersAndSMTStorage(
				translator.getSMTFormula(), translator.getBoundIdentifers(),
				translator.getFreeIdentifiers());
		return iSMT;
	}
	// TODO
	// if (this.smtUiPreferences.getUsingPrepro()) {
	// /**
	// * Launch preprocessing
	// */
	// // smtTranslationPreprocessing(args);
	// }
}
