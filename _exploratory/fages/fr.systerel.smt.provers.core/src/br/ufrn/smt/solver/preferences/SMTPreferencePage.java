package br.ufrn.smt.solver.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class SMTPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public SMTPreferencePage() {
		super(GRID);
		setPreferenceStore(SmtProversCore.getDefault().getPreferenceStore());
		setDescription("SMT-Solver Plugin Preference Page");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		String[][] solvers = {{"veriT","veriT"},{"z3","z3"},{"cvc3","cvc3"},{"alt-ergo","alt-ergo"}};
		addField(new ComboFieldEditor("whichsolver", "SMT-Solver",solvers,getFieldEditorParent()));

		addField(new FileFieldEditor("solver_path", 
				"SMT-Solver Path:", getFieldEditorParent()));
		
		addField(new StringFieldEditor("solverarguments", "Arguments to Solver:", getFieldEditorParent()));
		
		
		BooleanFieldEditor preprocessing = new BooleanFieldEditor("usingprepro","Pre-Process SMT with veriT-Solver",getFieldEditorParent());
		
		
		RadioGroupFieldEditor editor= new RadioGroupFieldEditor(
				"executeTrans","How to execute plugin", 1,
				new String[][] {
					{"Only prove with solver", "proofonly"},
					{"Prove with solver and show SMT file", "proofandshowfile"},
					{"Only show SMT-File", "showfileonly"}
				},   getFieldEditorParent() );
		addField(editor);
		
		
		RadioGroupFieldEditor preProcessEditor= new RadioGroupFieldEditor(
				"preprocessingoptions","Preprocessed SMT File options", 1,
				new String[][] {
					{"Only show SMT file before pre-processing", "presmt"},
					{"Only show SMT file after pre-processing", "aftersmt"},
					{"Show SMT-File before and after pre-processing", "beforeandafter"}
				}, editor.getRadioBoxControl(getFieldEditorParent())); 
		
		addField(preprocessing);
			
		addField(new FileFieldEditor("prepropath", 
				"Pre-Processor Path:", getFieldEditorParent()));
			
		addField(new FileFieldEditor("smteditor", 
				"Editor to see SMT File", getFieldEditorParent()));
				
		addField(preProcessEditor);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}