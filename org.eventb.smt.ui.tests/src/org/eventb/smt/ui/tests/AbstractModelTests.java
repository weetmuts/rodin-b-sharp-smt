/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.tests;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.junit.Assert.assertEquals;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eventb.smt.ui.internal.preferences.AbstractElement;
import org.eventb.smt.ui.internal.preferences.AbstractModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for moving elements in the {@link AbstractModel} class.
 * 
 * @author Thomas Muller
 */
public class AbstractModelTests {

	private static final Display DISPLAY = getWorkbench().getDisplay();

	private static final String DEFAULT_VALUES = "a,b,c,d";

	private static final String SEP = ",";

	private Shell shell;

	private TestModel model;

	@Test
	public void moveUpNeg() {
		checkMoveUp(-1, -1, DEFAULT_VALUES);
	}

	@Test
	public void moveUp0() {
		checkMoveUp(0, 0, "a,b,c,d");
	}

	@Test
	public void moveUp1() {
		checkMoveUp(1, 0, "b,a,c,d");
	}

	@Test
	public void moveUp2() {
		checkMoveUp(2, 1, "a,c,b,d");
	}

	@Test
	public void moveUp3() {
		checkMoveUp(3, 2, "a,b,d,c");
	}

	@Test
	public void moveUp4() {
		checkMoveUp(4, 4, "a,b,c,d");
	}

	@Test
	public void moveDownNeg() {
		checkMoveDown(-1, -1, DEFAULT_VALUES);
	}

	@Test
	public void moveDown0() {
		checkMoveDown(0, 1, "b,a,c,d");
	}

	@Test
	public void moveDown1() {
		checkMoveDown(1, 2, "a,c,b,d");
	}

	@Test
	public void moveDown2() {
		checkMoveDown(2, 3, "a,b,d,c");
	}

	@Test
	public void moveDown3() {
		checkMoveDown(3, 3, "a,b,c,d");
	}

	@Test
	public void moveDown4() {
		checkMoveDown(4, 4, "a,b,c,d");
	}

	private void checkMoveUp(int idx, int newIdx, String expectedCoreElems) {
		assertCoreElements(DEFAULT_VALUES);
		final int actual = model.moveUp(idx);
		assertEquals(newIdx, actual);
		assertCoreElements(expectedCoreElems);
	}

	private void checkMoveDown(int idx, int newIdx, String expectedCoreElems) {
		assertCoreElements(DEFAULT_VALUES);
		final int actual = model.moveDown(idx);
		assertEquals(newIdx, actual);
		assertCoreElements(expectedCoreElems);
	}

	private void assertCoreElements(String expectedCoreElems) {
		model.store();
		assertEquals(expectedCoreElems, model.getStorage());
	}

	@Before
	public void setUp() {
		model = new TestModel();
		shell = new Shell(DISPLAY);
		model.setViewer(new TableViewer(shell));
		model.load();
	}

	@After
	public void tearDown() {
		shell.dispose();
	}

	/**
	 * A simple model containing strings as elements.
	 */
	private static class TestModel extends AbstractModel<String, TestElement> {

		private String storage;

		public TestModel() {
			this.storage = DEFAULT_VALUES;
		}

		@Override
		protected TestElement convert(String coreElement) {
			return new TestElement(coreElement);
		}

		@Override
		protected void doLoad() {
			addElements(storage.split(SEP));
		}

		@Override
		protected void doLoadDefaults() {
			// Nothing to do
		}

		@Override
		public TestElement newElement() {
			assert false;
			return null;
		}

		@Override
		protected String[] newArray(int length) {
			return new String[length];
		}

		@Override
		protected void doStore(String[] coreElements) {
			final StringBuilder sb = new StringBuilder();
			String sep = "";
			for (final String str : coreElements) {
				sb.append(sep);
				sep = SEP;
				sb.append(str);
			}
			storage = sb.toString();
		}

		public String getStorage() {
			return storage;
		}

	}

	private static class TestElement extends AbstractElement<String> {

		public TestElement(String name) {
			super(true, name);
		}

		@Override
		public String toCore() {
			return name;
		}

	}

}
