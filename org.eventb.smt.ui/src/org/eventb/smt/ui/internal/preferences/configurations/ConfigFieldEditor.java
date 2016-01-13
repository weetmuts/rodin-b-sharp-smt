/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.configurations;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.ui.internal.preferences.AbstractTableFieldEditor;

/**
 * This class is used to build the solver configurations table printed in the
 * preferences page. This table contains all the information set by the user
 * when he added a new SMT solver configuration.
 * <p>
 * The data are contained in a <code>SMTPreferences</code> instance, of which
 * the <code>SolverConfiguration</code> list is given as input to the
 * <code>TableViewer</code>. As a consequence, it is necessary to update the
 * <code>tableViewer</code> each time the list <code>solverConfigs</code> is
 * modified, by calling the <code>refresh</code> method.
 * </p>
 *
 * @author Yoann Guyot
 */
public class ConfigFieldEditor extends
		AbstractTableFieldEditor<IConfigDescriptor, ConfigElement, ConfigModel> {

	private static final String SMT_CONFIGS_LABEL = "SMT solver configurations:";

	enum ConfigColumn implements ColumnDescriptor<ConfigElement> {

		NAME("Name", 100) {
			@Override
			public String getLabel(ConfigElement element) {
				return element.name;
			}
		},
		SOLVER("Solver", 105) {
			@Override
			public String getLabel(ConfigElement element) {
				return element.solverName;
			}
		},
		ARGS("Arguments", 203) {
			@Override
			public String getLabel(ConfigElement element) {
				return element.args;
			}
		};

		private final String title;
		private final int width;

		ConfigColumn(String title, int width) {
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

	public ConfigFieldEditor(ConfigModel configModel, Composite parent) {
		super("configs", SMT_CONFIGS_LABEL, parent, configModel);
	}

	@Override
	protected TableViewer createTableViewer(final Composite parent) {
		final Table table = new Table(parent, SWT.CHECK | SWT.FULL_SELECTION);
		final CheckboxTableViewer viewer = new CheckboxTableViewer(table);
		viewer.setCheckStateProvider(new ICheckStateProvider() {
			@Override
			public boolean isGrayed(Object element) {
				return false;
			}

			@Override
			public boolean isChecked(Object element) {
				final ConfigElement config = (ConfigElement) element;
				return config.enabled;
			}
		});
		viewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			@SuppressWarnings("synthetic-access")
			public void checkStateChanged(CheckStateChangedEvent event) {
				final ConfigElement config = (ConfigElement) event.getElement();
				config.enabled = event.getChecked();
				model.update(config);
			}
		});
		return viewer;
	}

	@Override
	protected ColumnDescriptor<ConfigElement>[] getColumnDescriptors() {
		return ConfigColumn.values();
	}

	@Override
	protected boolean canDuplicate() {
		return true;
	}

	@Override
	protected boolean canMove() {
		return true;
	}

	@Override
	protected void openEditor(final ConfigElement config) {
		new ConfigDialog(getShell(), model, config).open();
	}

	@Override
	protected boolean checkRemovePrecondition(final ConfigElement config) {
		return true;
	}

}