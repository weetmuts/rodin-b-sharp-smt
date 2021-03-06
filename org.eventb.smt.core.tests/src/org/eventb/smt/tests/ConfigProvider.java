/*******************************************************************************
 * Copyright (c) 2013, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.tests;

import static org.eventb.smt.core.SMTCore.getConfigurations;
import static org.eventb.smt.core.SMTCore.getSolvers;
import static org.eventb.smt.core.SMTCore.newSolverDescriptor;
import static org.eventb.smt.core.SolverKind.ALT_ERGO;
import static org.eventb.smt.core.SolverKind.MATHSAT5;
import static org.eventb.smt.core.SolverKind.OPENSMT;
import static org.eventb.smt.tests.CommonSolverRunTests.makeConfig;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.core.ISolverDescriptor;
import org.eventb.smt.core.SolverKind;
import org.eventb.smt.core.internal.provers.SMTConfiguration;

/**
 * Common implementation for providing SMT configurations.
 * 
 * @author Laurent Voisin
 */
public class ConfigProvider {

	public static final ConfigProvider BUNDLED_CVC3 = new ConfigProvider(
			findSolverByName("CVC3 (bundled)"));

	public static final ConfigProvider BUNDLED_CVC4 = new ConfigProvider(
			findSolverByName("CVC4 (bundled)"));

	public static final ConfigProvider BUNDLED_Z3 = new ConfigProvider(
			findSolverByName("Z3 (bundled)"));

	public static final ConfigProvider BUNDLED_VERIT = new VeriTConfigProvider(
			findSolverByName("veriT (bundled)"));

	public static final ConfigProvider LAST_ALTERGO = new ConfigProvider(
			ALT_ERGO, "alt-ergo-nightly-r217");

	public static final ConfigProvider LAST_MATHSAT5 = new ConfigProvider(
			MATHSAT5, "mathsat5-smtcomp2011");

	public static final ConfigProvider LAST_OPENSMT = new ConfigProvider(
			OPENSMT, "opensmt-20101017");

	private static class VeriTConfigProvider extends ConfigProvider {

		private static final String COMMON_ARGS = "--enable-e --max-time=2.9";

		private static final String V2_ARGS = "--disable-print-success "
				+ COMMON_ARGS;

		VeriTConfigProvider(SolverKind kind, String binaryName) {
			super(kind, binaryName);
		}

		VeriTConfigProvider(ISolverDescriptor solver) {
			super(solver);
		}

		@Override
		protected String veritV1Args() {
			return COMMON_ARGS;
		}

		@Override
		protected String veritV2Args() {
			return V2_ARGS;
		}

		@Override
		protected String ppV1Args() {
			return COMMON_ARGS;
		}

		@Override
		protected String ppV2Args() {
			return V2_ARGS;
		}
	}

	private final ISolverDescriptor solver;

	protected ConfigProvider(SolverKind kind, String binaryName) {
		this(makeSolverDescriptor(kind, binaryName));
	}

	protected ConfigProvider(ISolverDescriptor solver) {
		this.solver = solver;
	}

	public SMTConfiguration config() {
		IConfigDescriptor config = findBundledConfiguration();
		if (config == null) {
			final String args = getArgs();
			config = makeConfig(solver, args);
		}
		return new SMTConfiguration(config, solver);
	}

	/*
	 * Returns the existing configuration with the same solver and the same
	 * options, if any.
	 */
	private IConfigDescriptor findBundledConfiguration() {
		final String solverName = solverName();
		for (final IConfigDescriptor config : getConfigurations()) {
			if (!config.isBundled())
				continue;
			if (!config.getSolverName().equals(solverName))
				continue;
			return config;
		}
		return null;
	}

	public String solverName() {
		return solver.getName();
	}

	private String getArgs() {
		return ppV2Args();
	}

	protected String veritV1Args() {
		return "";
	}

	protected String veritV2Args() {
		return "";
	}

	protected String ppV1Args() {
		return "";
	}

	protected String ppV2Args() {
		return "";
	}

	private static ISolverDescriptor makeSolverDescriptor(SolverKind kind,
			String binaryName) {
		return newSolverDescriptor(binaryName, kind, makeSolverPath(binaryName));
	}

	private static IPath makeSolverPath(String binaryName) {
		final IPath home = Path.fromOSString(System.getProperty("user.home"));
		final IPath solverPath = home.append("bin").append(binaryName);
		if (Platform.getOS() == Platform.OS_WIN32) {
			solverPath.addFileExtension("exe");
		}
		return solverPath;
	}

	private static ISolverDescriptor findSolverByName(String name) {
		final ISolverDescriptor[] solvers = getSolvers();
		for (ISolverDescriptor solver : solvers) {
			if (name.equals(solver.getName())) {
				return solver;
			}
		}
		throw new IllegalArgumentException("No solver named " + name);
	}

}
