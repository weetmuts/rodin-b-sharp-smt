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

import java.util.Arrays;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eventb.smt.ui.internal.preferences.AbstractTableFieldEditor.ColumnDescriptor;

/**
 * Label provider based on column descriptors for a table field editor.
 *
 * @author Laurent Voisin
 */
public class TableLabelProvider<C> extends LabelProvider implements
		ITableLabelProvider {

	private static String[] computeColumnProperties(
			ColumnDescriptor<?>[] columns) {
		final int length = columns.length;
		final String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = columns[i].name();
		}
		return result;
	}

	private final ColumnDescriptor<C>[] columnDescriptors;
	private final String[] columnProperties;

	public TableLabelProvider(ColumnDescriptor<C>[] columns) {
		this.columnDescriptors = columns;
		this.columnProperties = computeColumnProperties(columns);
	}

	public String[] getColumnProperties() {
		return columnProperties;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getColumnText(Object element, int columnIndex) {
		return columnDescriptors[columnIndex].getLabel((C) element);
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return Arrays.asList(columnProperties).contains(property);
	}

}
