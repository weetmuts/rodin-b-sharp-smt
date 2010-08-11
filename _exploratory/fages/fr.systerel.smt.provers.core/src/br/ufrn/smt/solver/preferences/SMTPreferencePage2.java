/*******************************************************************************
 * Copyright (c) 2010 Systerel and Vítor Alcântara de Almeida .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vítor Alcântara de Almeida - Creation
 *     Systerel (YFT) - Preferences adaptation 
 *******************************************************************************/

package br.ufrn.smt.solver.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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

public class SMTPreferencePage2
	extends PreferencePage
	implements IWorkbenchPreferencePage {
	
	protected TableViewer fTable;
	
	protected Control fTableControl;
	
	protected SolverTableModel fModel;
	
	public SMTPreferencePage2() {
		setPreferenceStore(SmtProversCore.getDefault().getPreferenceStore());
		setDescription("SMT-Solver Plugin Preference Page YFT");
		fModel = new SolverTableModel();
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}


	@Override
	protected Control createContents(Composite parent) {
		return createTable(parent);
	}
	
	
	private Control createTable(Composite parent) {
		final Composite comp = new Composite(parent,SWT.NONE);
		// resize comp
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		fTable = createTableViewer(comp);
        
		final Table tableControl= fTable.getTable();
		tableControl.setHeaderVisible(true);
		tableControl.setLinesVisible(true);
		tableControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		update(fModel.solverDetails);
		
		// pack everything
		fTable.getTable().pack();
		parent.pack();
		
		final Button addButton = new Button(comp, SWT.PUSH);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
			}
		});
		

		
		/*final Button removeButton = new Button(parent, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			}
		});
		
		final Button newButton = new Button(parent, SWT.PUSH);
		newButton.setText("New");
		newButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			}
		});*/
		
		return comp;
	}
	


	

	
	protected TableViewer createTableViewer(Composite parent) {
		Table table= new Table(parent, getListStyle());
		table.setFont(parent.getFont());
		
		String[] columnNames = new String[]{"solver name","Path"};		
		CellEditor[] editors = new CellEditor[columnNames.length];
		
		TableViewer tv  = new TableViewer(table);
		tv.setColumnProperties(columnNames);
		tv.setContentProvider(new SolversDetailsContentProvider());	
		tv.setLabelProvider(new SolversDetailsLabelProvider());	
		
		TableColumn tc;
		tc = new TableColumn(tv.getTable(), SWT.LEFT);
		tc.setText("Solver ID");
		tc.setWidth(100);
		tc = new TableColumn(tv.getTable(), SWT.LEFT);
		tc.setText("Solver Path");
		tc.setWidth(200);
		
		return tv;
	}
	
	/**
	 * This class provides the labels for PlayerTable
	 */

	class SolversDetailsLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			SolverDetail detail = (SolverDetail) element;
		    String text = "";
		    switch (columnIndex) {
		    case 0:
		      text = detail.getId();
		      break;
		    case 1:
		      text = detail.getPath();
		      break;
		    }
		    return text;
		}
		
	}
	
	/**
	   * Updates the application with the selected team
	   * 
	   * @param team
	   *            the team
	   */
	  private void update(SolverDetails details) {
	    // Set the table viewer's input to the team
	    fTable.setInput(details);
	  }
	  
	/*
	 * Subclasses may override to specify a different style.
	 */
	protected int getListStyle(){
		int style=  SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL ;
		return style;
	}
	
	private class SolverDetail {
		private String id;
		private String path;
		
		public String getId(){
			return id;
		}
		
		public String getPath(){
			return path;
		}
		
		private SolverDetail(String id, String path) {
			this.id = id;
			this.path = path;
		}		
	}
	
	private class SolverDetails {
		final List <SolverDetail> details  = new ArrayList<SolverDetail>();;
 
		 /**
		   * Gets the details
		   * 
		   * @return List
		   */
		  public List getDetails() {
		    return Collections.unmodifiableList(details);
		  }		
	}
	
	
	
	class SolverTableModel {
		  final SolverDetails solverDetails;

		  
		  public SolverTableModel(){
			  solverDetails = new SolverDetails();
			  solverDetails.details.add(new SolverDetail("solver0", "path0"));
			  solverDetails.details.add(new SolverDetail("solver1", "path1"));
			  solverDetails.details.add(new SolverDetail("solver2", "path2"));
		  }
		  
	}
	
	/**
	 * This is a content provider for the statistics details viewer.
	 *
	 */
	public class SolversDetailsContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return ((SolverDetails)inputElement).getDetails().toArray();
		}

	}
	
}
