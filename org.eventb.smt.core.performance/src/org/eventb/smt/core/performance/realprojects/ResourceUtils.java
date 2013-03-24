/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.realprojects;

import static org.eventb.smt.core.performance.realprojects.BuilderTest.PLUGIN_ID;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class ResourceUtils {
	private static final String BELONGS_TO_DOMAIN = "org.eventb.core.seqprover.InDomGoalTac";
	private static final String BOUNDED_GOAL_WITH_FINITE_HYPOTHESIS = "org.eventb.core.seqprover.finiteHypBoundedGoalTac";
	private static final String CLARIFY_GOAL = "org.eventb.core.seqprover.clarifyGoalTac";
	private static final String DATATYPE_DESTRUCTOR_WD = "org.eventb.core.seqprover.dtDestrWDTac";
	private static final String FALSE_HYPOTHESIS = "org.eventb.core.seqprover.falseHypTac";
	private static final String FIND_CONTRADICTORY_HYPOTHESES = "org.eventb.core.seqprover.findContrHypsTac";
	private static final String FUNCTIONAL_GOAL = "org.eventb.core.seqprover.funGoalTac";
	private static final String FUNCTIONAL_IMAGE = "org.eventb.core.seqprover.FunImgInGoalTac";
	private static final String FUNCTIONAL_IMAGE_MEMBERSHIP = "org.eventb.core.seqprover.FunImgInGoalTac";
	private static final String FUNCTIONAL_OVERRIDING_IN_GOAL = "org.eventb.core.seqprover.funOvrGoalTac";
	private static final String FUNCTIONAL_OVERRIDING_IN_HYPOTHESIS = "org.eventb.core.seqprover.funOvrHypTac";
	private static final String GENERALIZED_MODUS_PONENS = "org.eventb.core.seqprover.genMPTac";
	private static final String GOAL_IN_HYPOTHESIS = "org.eventb.core.seqprover.goalInHypTac";
	private static final String LASSO = "org.eventb.core.seqprover.lasso";
	private static final String ONE_POINT_RULE_IN_GOAL = "org.eventb.core.seqprover.onePointGoalTac";
	private static final String ONE_POINT_RULE_IN_HYPOTHESES = "org.eventb.core.seqprover.onePointHypTac";
	private static final String PARTITION_REWRITER = "org.eventb.core.seqprover.partitionRewriteTac";
	private static final String PUT_IN_NEGATION_NORMAL_FORM = "org.eventb.core.seqprover.NNFTac";
	private static final String SHRINK_IMPLICATIVE_HYPOTHESES = "org.eventb.core.seqprover.shrinkImpHypTac";
	private static final String SIMPLIFICATION_REWRITER = "org.eventb.core.seqprover.autoRewriteTac";
	private static final String TRUE_GOAL = "org.eventb.core.seqprover.trueGoalTac";
	private static final String TYPE_REWRITER = "org.eventb.core.seqprover.typeRewriteTac";
	private static final String USE_EQUALS_HYPOTHESES = "org.eventb.core.seqprover.eqHypTac";

	private static void setContents(IFile file, String contents)
			throws Exception {
		final InputStream input = new ByteArrayInputStream(
				contents.getBytes("utf-8"));
		file.setContents(input, IResource.NONE, null);
	}

	private static IRodinFile createRodinFile(IRodinProject project,
			String fileName) throws RodinDBException {
		IRodinFile file = project.getRodinFile(fileName);
		assert file != null;
		file.create(true, null);
		return file;
	}

	private static void initFile(IRodinFile rodinFile, String contents)
			throws Exception {
		final IFile resource = rodinFile.getResource();
		setContents(resource, contents);
	}

	public static ITacticDescriptor belongsToDomain() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(BELONGS_TO_DOMAIN);
	}

	public static ITacticDescriptor boundedGoalWithFiniteHypotheses() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(BOUNDED_GOAL_WITH_FINITE_HYPOTHESIS);
	}

	public static ITacticDescriptor clarifyGoal() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(CLARIFY_GOAL);
	}

	public static ITacticDescriptor dataTypeDestructorWD() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(DATATYPE_DESTRUCTOR_WD);
	}

	public static ITacticDescriptor falseHypothesis() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(FALSE_HYPOTHESIS);
	}

	public static ITacticDescriptor findContradictoryHypotheses() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(FIND_CONTRADICTORY_HYPOTHESES);
	}

	public static ITacticDescriptor functionalGoal() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(FUNCTIONAL_GOAL);
	}

	public static ITacticDescriptor functionalImage() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(FUNCTIONAL_IMAGE);
	}

	public static ITacticDescriptor functionalImageMembership() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(FUNCTIONAL_IMAGE_MEMBERSHIP);
	}

	public static ITacticDescriptor functionalOverridingInGoal() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(FUNCTIONAL_OVERRIDING_IN_GOAL);
	}

	public static ITacticDescriptor functionalOverridingInHypothesis() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(FUNCTIONAL_OVERRIDING_IN_HYPOTHESIS);
	}

	public static ITacticDescriptor generalizedModusPonens() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(GENERALIZED_MODUS_PONENS);
	}

	public static ITacticDescriptor goalInHypothesis() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(GOAL_IN_HYPOTHESIS);
	}

	public static ITacticDescriptor lasso() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(LASSO);
	}

	public static ITacticDescriptor onePointRuleInGoal() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(ONE_POINT_RULE_IN_GOAL);
	}

	public static ITacticDescriptor onePointRuleInHypotheses() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(ONE_POINT_RULE_IN_HYPOTHESES);
	}

	public static ITacticDescriptor partitionRewriter() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(PARTITION_REWRITER);
	}

	public static ITacticDescriptor putInNegationNormalForm() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(PUT_IN_NEGATION_NORMAL_FORM);
	}

	public static ITacticDescriptor shrinkImplicativeHypotheses() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(SHRINK_IMPLICATIVE_HYPOTHESES);
	}

	public static ITacticDescriptor simplificationRewriter() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(SIMPLIFICATION_REWRITER);
	}

	public static ITacticDescriptor trueGoal() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(TRUE_GOAL);
	}

	public static ITacticDescriptor typeRewriter() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(TYPE_REWRITER);
	}

	public static ITacticDescriptor useEqualsHypotheses() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		return reg.getTacticDescriptor(USE_EQUALS_HYPOTHESES);
	}

	public static ITacticDescriptor loopOnAllPending(
			List<ITacticDescriptor> descs, String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(TacticCombinators.LoopOnAllPending.COMBINATOR_ID);
		return comb.combine(descs, id);
	}

	public static ITacticDescriptor attempt(List<ITacticDescriptor> descs,
			String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(TacticCombinators.Attempt.COMBINATOR_ID);
		return comb.combine(descs, id);
	}

	public static ITacticDescriptor sequence(List<ITacticDescriptor> descs,
			String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(TacticCombinators.Sequence.COMBINATOR_ID);
		return comb.combine(descs, id);
	}

	public static ITacticDescriptor onAllPending(List<ITacticDescriptor> descs,
			String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(TacticCombinators.OnAllPending.COMBINATOR_ID);
		return comb.combine(descs, id);
	}

	public static IContextRoot createContext(IRodinProject project,
			String bareName, String contents) throws Exception {
		final String contextName = EventBPlugin.getContextFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, contextName);
		initFile(rFile, contents);
		return (IContextRoot) rFile.getRoot();
	}

	public static IMachineRoot createMachine(IRodinProject project,
			String bareName, String contents) throws Exception {
		final String machineName = EventBPlugin.getMachineFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, machineName);
		initFile(rFile, contents);
		return (IMachineRoot) rFile.getRoot();
	}

	public static IPRRoot createPRFile(IRodinProject project, String bareName,
			String contents) throws Exception {
		final String prFileName = EventBPlugin.getPRFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, prFileName);
		initFile(rFile, contents);
		return (IPRRoot) rFile.getRoot();

	}

	public static IPSRoot createPSFile(IRodinProject project, String bareName,
			String contents) throws Exception {
		final String psFileName = EventBPlugin.getPSFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, psFileName);
		initFile(rFile, contents);
		return (IPSRoot) rFile.getRoot();

	}

	public static final String MCH_BARE_NAME = "machine";
	public static final List<IDeclaration> EMPTY_DECL = Collections.emptyList();
	public static final String INTERNAL_ELEMENT1 = "internal_element1";
	public static final String INTERNAL_ELEMENT2 = "internal_element2";
	public static final String CTX_BARE_NAME = "context";

	/**
	 * Imports files of a template project into an already existing project.
	 * 
	 * @param dest
	 *            destination project. Must already exist and be configured
	 * @param srcName
	 *            name of the source project which lies in the
	 *            <code>projects</code> folder of this plug-in
	 * @throws Exception
	 *             in case of error
	 */
	public static void importProjectFiles(IProject dest, String srcName)
			throws Exception {
		final URL entry = getProjectsURL();
		final URL projectsURL = FileLocator.toFileURL(entry);
		final File projectsDir = new File(projectsURL.toURI());
		for (final File project : projectsDir.listFiles()) {
			if (project.isDirectory() && project.getName().equals(srcName))
				importFiles(dest, project, true);
		}
	}

	static URL getProjectsURL() {
		return Platform.getBundle(PLUGIN_ID).getEntry("projects");
	}

	private static void importFiles(IProject project, File root, boolean isRoot)
			throws IOException, CoreException {
		for (final File file : root.listFiles()) {
			final String filename = file.getName();
			if (!filename.equals(".project")) {
				if (file.isFile()) {
					final InputStream is = new FileInputStream(file);
					final String name = (isRoot) ? filename : root.getName()
							+ "/" + filename;
					final IFile target = project.getFile(name);
					target.create(is, false, null);
				} else if (file.isDirectory() && !filename.equals(".svn")) {
					final IFolder folder = project.getFolder(filename);
					folder.create(true, false, null);
					importFiles(project, file, false);
				}
			}
		}
	}
}
