/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Common protocol for a button attached to a table field editor that allows to
 * add, edit, duplicate or remove an entry in the table.
 *
 * @author Laurent Voisin
 */
public abstract class TableButtonController<T extends AbstractElement<?>>
		implements ISelectionChangedListener, SelectionListener,
		IDoubleClickListener {

	// The associated control
	private final Button button;

	// Currently selected element in the table
	protected T selectedElement;

	public TableButtonController(Composite parent, String label,
			TableViewer tableViewer) {
		button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.addSelectionListener(this);
		tableViewer.addSelectionChangedListener(this);
		button.setEnabled(isEnabled());
	}

	// The table selection changed, we need to update
	@Override
	@SuppressWarnings("unchecked")
	public final void selectionChanged(SelectionChangedEvent event) {
		final IStructuredSelection ssel;
		ssel = (IStructuredSelection) event.getSelection();
		if (ssel == null) {
			selectedElement = null;
		} else {
			selectedElement = (T) ssel.getFirstElement();
		}
		button.setEnabled(isEnabled());
	}

	/**
	 * Tell whether the button should be enabled base on the currently selected
	 * element.
	 *
	 * @return whether the button should be enabled
	 * @see #selectedElement
	 */
	protected abstract boolean isEnabled();

	@Override
	public final void widgetDefaultSelected(SelectionEvent e) {
		// Do nothing and prevent overriding
	}

	@Override
	public final void widgetSelected(SelectionEvent e) {
		perform();
	}

	/**
	 * Perform the action attached to the button.
	 */
	protected abstract void perform();

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (isEnabled()) {
			perform();
		}
	}

}
