/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *  ETH Zurich - initial API and implementation
 *  Systerel - separation of file and root element
 *  Systerel - added cleanup of attempted proofs
 *******************************************************************************/

package org.eventb.smt.core.performance.realprojects;

import static org.eventb.core.EventBPlugin.getProofManager;
import static org.eventb.core.EventBPlugin.getUserSupportManager;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.attempt;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.belongsToDomain;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.boundedGoalWithFiniteHypotheses;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.clarifyGoal;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.dataTypeDestructorWD;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.falseHypothesis;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.findContradictoryHypotheses;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.functionalGoal;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.functionalImage;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.functionalImageMembership;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.functionalOverridingInGoal;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.functionalOverridingInHypothesis;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.generalizedModusPonens;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.goalInHypothesis;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.importProjectFiles;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.lasso;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.loopOnAllPending;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.onAllPending;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.onePointRuleInGoal;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.onePointRuleInHypotheses;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.partitionRewriter;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.putInNegationNormalForm;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.sequence;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.shrinkImplicativeHypotheses;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.simplificationRewriter;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.trueGoal;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.typeRewriter;
import static org.eventb.smt.core.performance.realprojects.ResourceUtils.useEqualsHypotheses;
import static org.eventb.smt.internal.preferences.SMTPreferences.DEFAULT_SOLVER_INDEX;
import static org.eventb.smt.internal.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.VERIT_PATH_ID;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.smt.internal.provers.core.SMTProversCore;
import org.eventb.smt.internal.provers.ui.SmtProversUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.internal.core.debug.DebugHelpers;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BuilderTest extends TestCase {
	protected IRodinProject rodinProject;
	protected IEventBProject eventBProject;
	protected Map<String, Integer> results = new HashMap<String, Integer>();

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	protected static final IParameterizerDescriptor smtPpParamTacticDescriptor = SequentProver
			.getAutoTacticRegistry().getParameterizerDescriptor(
					SMTProversCore.PLUGIN_ID + ".SMTPPParam");
	protected static final IParameterizerDescriptor smtVeritParamTacticDescriptor = SequentProver
			.getAutoTacticRegistry().getParameterizerDescriptor(
					SMTProversCore.PLUGIN_ID + ".SMTVeriTParam");

	public static final String PLUGIN_ID = "org.eventb.smt.core.performance";

	public BuilderTest() {
		super();
	}

	public BuilderTest(String name) {
		super(name);
	}

	protected ITacticDescriptor makeSMTTactic(
			final IParameterizerDescriptor smtParamTacticDescriptor,
			final String configId) {
		SmtProversUIPlugin.getDefault();
		final IParameterSetting settings = smtParamTacticDescriptor
				.makeParameterSetting();
		settings.setBoolean("restricted", true);
		settings.setLong("timeout", (long) 4500);
		settings.setString("configId", configId);
		final ITacticDescriptor smtTacticDescriptor = smtParamTacticDescriptor
				.instantiate(settings, "autoSMT");
		return loopOnAllPending(
				(List<IAutoTacticRegistry.ITacticDescriptor>) Arrays.asList(
						trueGoal(),
						falseHypothesis(),
						goalInHypothesis(),
						functionalGoal(),
						boundedGoalWithFiniteHypotheses(),
						attempt(Collections.singletonList(sequence(
								Arrays.asList(
										lasso(),
										onAllPending(
												Collections
														.singletonList(smtTacticDescriptor),
												"onAllPendingId")),
								"sequenceId")), "attemptId"),
						partitionRewriter(), generalizedModusPonens(),
						simplificationRewriter(), putInNegationNormalForm(),
						typeRewriter(), findContradictoryHypotheses(),
						shrinkImplicativeHypotheses(),
						functionalOverridingInGoal(), clarifyGoal(),
						onePointRuleInGoal(),
						functionalOverridingInHypothesis(), functionalImage(),
						onePointRuleInHypotheses(), useEqualsHypotheses(),
						belongsToDomain(), functionalImageMembership(),
						dataTypeDestructorWD()), "loopOnAllPendingId");
	}

	protected IContextRoot createContext(String bareName)
			throws RodinDBException {
		final IContextRoot result = eventBProject.getContextRoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	protected IMachineRoot createMachine(String bareName)
			throws RodinDBException {
		final IMachineRoot result = eventBProject.getMachineRoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	protected IPORoot createPOFile(String bareName) throws RodinDBException {
		final IPORoot result = eventBProject.getPORoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	protected ISCContextRoot createSCContext(String bareName)
			throws RodinDBException {
		final ISCContextRoot result = eventBProject.getSCContextRoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	protected ISCMachineRoot createSCMachine(String bareName)
			throws RodinDBException {
		final ISCMachineRoot result = eventBProject.getSCMachineRoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	private void createRodinFileOf(IInternalElement result)
			throws RodinDBException {
		result.getRodinFile().create(true, null);
	}

	public static void saveRodinFileOf(IInternalElement elem)
			throws RodinDBException {
		elem.getRodinFile().save(null, false);
	}

	protected void runBuilder() throws CoreException {
		runBuilder(rodinProject);
	}

	protected void runBuilder(IRodinProject rp) throws CoreException {
		final IProject project = rp.getProject();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		IMarker[] buildPbs = project.findMarkers(
				RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER, true,
				IResource.DEPTH_INFINITE);
		if (buildPbs.length != 0) {
			for (IMarker marker : buildPbs) {
				System.out.println("Build problem for " + marker.getResource());
				System.out.println("  " + marker.getAttribute(IMarker.MESSAGE));
			}
			fail("Build produced build problems, see console");
		}
	}

	@SuppressWarnings("deprecation")
	private static void enableAutoTactics(IAutoTacticPreference pref,
			String[] tacticIds) {
		final List<ITacticDescriptor> descrs = new ArrayList<ITacticDescriptor>(
				tacticIds.length);
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		for (String id : tacticIds) {
			descrs.add(reg.getTacticDescriptor(id));
		}
		pref.setSelectedDescriptors(descrs);
		pref.setEnabled(true);
	}

	private static final String[] autoTacticIds = new String[] {
			"org.eventb.core.seqprover.trueGoalTac",
			"org.eventb.core.seqprover.falseHypTac",
			"org.eventb.core.seqprover.goalInHypTac",
			"org.eventb.core.seqprover.funGoalTac",
			"org.eventb.core.seqprover.autoRewriteTac",
			"org.eventb.core.seqprover.typeRewriteTac",
			"org.eventb.core.seqprover.findContrHypsTac",
			"org.eventb.core.seqprover.eqHypTac",
			"org.eventb.core.seqprover.shrinkImpHypTac",
			"org.eventb.core.seqprover.clarifyGoalTac", };

	protected static void enableAutoProver() {
		final IAutoTacticPreference autoPref = EventBPlugin
				.getAutoPostTacticManager().getAutoTacticPreference();
		enableAutoTactics(autoPref, autoTacticIds);
	}

	protected static void disableAutoProver() {
		EventBPlugin.getAutoPostTacticManager().getAutoTacticPreference()
				.setEnabled(false);
	}

	private static final String[] postTacticIds = new String[] {
			"org.eventb.core.seqprover.trueGoalTac",
			"org.eventb.core.seqprover.falseHypTac",
			"org.eventb.core.seqprover.goalInHypTac",
			"org.eventb.core.seqprover.autoRewriteTac",
			"org.eventb.core.seqprover.typeRewriteTac", };

	protected static void enablePostTactics() {
		final IAutoTacticPreference postPref = EventBPlugin
				.getAutoPostTacticManager().getPostTacticPreference();
		enableAutoTactics(postPref, postTacticIds);
	}

	protected static void disablePostTactics() {
		EventBPlugin.getAutoPostTacticManager().getPostTacticPreference()
				.setEnabled(false);
	}

	/**
	 * Deletes all user supports and proof attempts that where created and not
	 * cleaned up.
	 */
	protected static void deleteAllProofAttempts() {
		for (final IUserSupport us : getUserSupportManager().getUserSupports()) {
			us.dispose();
		}
		for (final IProofAttempt pa : getProofManager().getProofAttempts()) {
			pa.dispose();
		}
	}

	private void initializePreferences() {
		final StringBuilder preferencesBuilder = new StringBuilder();

		preferencesBuilder.append("veriT-dev-r2863,,");
		preferencesBuilder.append("verit,,");
		preferencesBuilder.append("/home/guyot/bin/veriT-dev-r2863,,");
		preferencesBuilder
				.append("-i smtlib2 --disable-print-success --disable-banner ");
		preferencesBuilder
				.append("--proof=- --proof-version=1 --proof-prune --disable-e,,");
		preferencesBuilder.append("V2.0;");

		preferencesBuilder.append("veriT+e-prover,,");
		preferencesBuilder.append("verit,,");
		preferencesBuilder.append("/home/guyot/bin/veriT-dev-r2863,,");
		preferencesBuilder
				.append("-i smtlib2 --disable-print-success --disable-banner ");
		preferencesBuilder
				.append("--proof=- --proof-version=1 --proof-prune --enable-e --max-time=3,,");
		preferencesBuilder.append("V2.0;");

		preferencesBuilder.append("cvc3-2011-11-21,,");
		preferencesBuilder.append("cvc3,,");
		preferencesBuilder.append("/home/guyot/bin/cvc3-2011-11-21,,");
		preferencesBuilder.append("-lang smt2 -timeout 3,,");
		preferencesBuilder.append("V2.0;");

		preferencesBuilder.append("alt-ergo-r217,,");
		preferencesBuilder.append("alt-ergo,,");
		preferencesBuilder.append("/home/guyot/bin/alt-ergo-nightly-r217,,");
		preferencesBuilder.append(",,");
		preferencesBuilder.append("V2.0;");

		preferencesBuilder.append("z3-3.2,,");
		preferencesBuilder.append("z3,,");
		preferencesBuilder.append("/home/guyot/bin/z3-3.2,,");
		preferencesBuilder.append("-smt2,,");
		preferencesBuilder.append("V2.0;");

		preferencesBuilder.append("veriT-dev-r2863-SMT1,,");
		preferencesBuilder.append("verit,,");
		preferencesBuilder.append("/home/guyot/bin/veriT-dev-r2863,,");
		preferencesBuilder
				.append("-i smtlib1 --disable-print-success --disable-banner --disable-e,,");
		preferencesBuilder.append("V1.2;");

		preferencesBuilder.append("veriT+e-prover-SMT1,,");
		preferencesBuilder.append("verit,,");
		preferencesBuilder.append("/home/guyot/bin/veriT-dev-r2863,,");
		preferencesBuilder
				.append("-i smtlib1 --disable-print-success --disable-banner --enable-e --max-time=3,,");
		preferencesBuilder.append("V1.2;");

		preferencesBuilder.append("cvc3-2011-11-21-SMT1,,");
		preferencesBuilder.append("cvc3,,");
		preferencesBuilder.append("/home/guyot/bin/cvc3-2011-11-21,,");
		preferencesBuilder.append("-lang smt -timeout 3,,");
		preferencesBuilder.append("V1.2;");

		preferencesBuilder.append("alt-ergo-r217-SMT1,,");
		preferencesBuilder.append("alt-ergo,,");
		preferencesBuilder.append("/home/guyot/bin/alt-ergo-nightly-r217,,");
		preferencesBuilder.append(",,");
		preferencesBuilder.append("V1.2;");

		preferencesBuilder.append("z3-3.2-SMT1,,");
		preferencesBuilder.append("z3,,");
		preferencesBuilder.append("/home/guyot/bin/z3-3.2,,");
		preferencesBuilder.append("-smt,,");
		preferencesBuilder.append("V1.2;");

		final IPreferenceStore store = SmtProversUIPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(SOLVER_PREFERENCES_ID, preferencesBuilder.toString());
		store.setValue(SOLVER_INDEX_ID, DEFAULT_SOLVER_INDEX);
		store.setValue(VERIT_PATH_ID, "/home/guyot/bin/verit");
		store.setValue(TRANSLATION_PATH_ID, DEFAULT_TRANSLATION_PATH);
	}

	protected void archiveFiles(String... params) throws Exception {
		final StringBuilder dirSuffixe = new StringBuilder();
		for (final String param : params) {
			dirSuffixe.append("/" + param);
		}
		final File dir = rodinProject.getProject().getLocation().toFile();
		final IPath destPath = new Path(
				"/home/guyot/c444/7/exploratory/real_projects/"
						+ rodinProject.getElementName() + dirSuffixe.toString());
		destPath.toFile().mkdirs();
		final String dest = destPath.toOSString() + "/";

		final String destination[] = new String[] { "/bin/sh", "-c",
				"/bin/mv *.bpr *.bps " + dest };

		final Process process = Runtime.getRuntime().exec(destination, null,
				dir);

		process.waitFor();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}

		rodinProject = createRodinProject("P");
		eventBProject = (IEventBProject) rodinProject
				.getAdapter(IEventBProject.class);

		disableAutoProver();
		disablePostTactics();

		DebugHelpers.disableIndexing();

		initializePreferences();
	}

	protected IRodinProject createRodinProject(String projectName)
			throws CoreException {
		IProject project = workspace.getRoot().getProject(projectName);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(pDescription, null);
		IRodinProject result = RodinCore.valueOf(project);
		return result;
	}

	protected void importProject(String prjName) throws Exception {
		importProjectFiles(rodinProject.getProject(), prjName);
	}

	@Override
	protected void tearDown() throws Exception {
		System.out.println();
		for (final Entry<String, Integer> entry : results.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		// Delete all Rodin projects
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		for (IRodinProject rp : rodinDB.getRodinProjects()) {
			rp.getProject().delete(true, true, null);
		}

		deleteAllProofAttempts();

		super.tearDown();
	}
}
