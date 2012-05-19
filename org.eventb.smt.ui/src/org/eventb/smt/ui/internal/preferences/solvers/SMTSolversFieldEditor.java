/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences.solvers;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.NO_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.YES_LABEL;
import static org.eclipse.jface.dialogs.MessageDialog.QUESTION_WITH_CANCEL;
import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eventb.smt.core.preferences.PreferenceManager.SOLVERS_ID;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;

import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eventb.smt.core.preferences.ISMTSolver;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.ISolverConfigsPreferences;
import org.eventb.smt.ui.internal.preferences.AbstractTableFieldEditor;

/**
 * This class is used to build the solver table printed in the preferences page.
 * This table contains all the information set by the user when adding a new SMT
 * solver.
 * <p>
 * The data are contained in a <code>SMTPreferences</code> instance, of which
 * the <code>SMTSolver</code> list is given as input to the
 * <code>TableViewer</code>. As a consequence, it is necessary to update the
 * <code>tableViewer</code> each time the list <code>solvers</code> is modified,
 * by calling the <code>refresh</code> method.
 * </p>
 * 
 * @author guyot
 */
class SMTSolversFieldEditor extends
		AbstractTableFieldEditor<ISMTSolver, SolverElement, SolverModel> {

	private static final String SMT_SOLVERS_LABEL = "Known SMT solvers:";
	private static final String SMT_SOLVER_REMOVAL = "SMT Solver Removal";

	enum SolverColumn implements ColumnDescriptor<SolverElement> {

		NAME("Name", 100) {
			@Override
			public String getLabel(SolverElement element) {
				return element.name;
			}
		},
		KIND("Kind", 70) {
			@Override
			public String getLabel(SolverElement element) {
				return element.kind.toString();
			}
		},
		PATH("Path", 300) {
			@Override
			public String getLabel(SolverElement element) {
				return element.path.toOSString();
			}
		};

		private final String title;
		private final int width;

		SolverColumn(String title, int width) {
			this.title = title;
			this.width = width;
		}

		@Override
		public String title() {
			return title;
		}

		@Override
		public int width() {
			return width;
		}

	}

	/**
	 * Creates a new solvers field editor.
	 * 
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public SMTSolversFieldEditor(final Composite parent) {
		super(SOLVERS_ID, SMT_SOLVERS_LABEL, parent, new SolverModel());
	}

	@Override
	protected TableViewer createTableViewer(Composite parent) {
		return new TableViewer(parent, FULL_SELECTION);
	}

	@Override
	protected ColumnDescriptor<SolverElement>[] getColumnDescriptors() {
		return SolverColumn.values();
	}

	@Override
	protected boolean canDuplicate() {
		return false;
	}

	@Override
	protected void openEditor(final SolverElement solver) {
		new SolverDialog(getShell(), model, solver).open();
	}

	@Override
	protected boolean checkRemovePrecondition(final SolverElement solver) {
		final String solverId = solver.id;
		final ISolverConfigsPreferences configsPrefs = getPreferenceManager()
				.getSolverConfigsPrefs();
		final Set<ISolverConfig> relatedConfigs = configsPrefs
				.relatedConfigs(solverId);
		if (relatedConfigs.isEmpty()) {
			return true;
		}
		final StringBuilder msgBuilder = new StringBuilder();
		msgBuilder
				.append("One or several configurations are linked to the solver you intend to remove:\n");
		for (final ISolverConfig config : relatedConfigs) {
			msgBuilder.append(config.getName());
			msgBuilder.append('\n');
		}
		msgBuilder.append("Do you want to remove them as well?");
		final MessageDialog dialog = new MessageDialog(getShell(),
				SMT_SOLVER_REMOVAL, null, msgBuilder.toString(),
				QUESTION_WITH_CANCEL, new String[] { YES_LABEL, NO_LABEL,
						CANCEL_LABEL }, 0);
		final int choice = dialog.open();
		if (choice == 0) { // Yes
			for (final ISolverConfig config : relatedConfigs) {
				configsPrefs.removeSolverConfig(config.getID());
			}
		}
		return choice != 2; // Not Cancel
	}

}