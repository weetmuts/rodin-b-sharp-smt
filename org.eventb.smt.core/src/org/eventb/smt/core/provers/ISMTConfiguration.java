package org.eventb.smt.core.provers;

import org.eclipse.core.runtime.IPath;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.eventb.smt.core.translation.TranslationApproach;

public interface ISMTConfiguration {

	public abstract String getName();

	public abstract String getSolverName();

	public abstract SolverKind getKind();

	public abstract IPath getSolverPath();

	public abstract String getArgs();

	public abstract TranslationApproach getTranslationApproach();

	public abstract SMTLIBVersion getSmtlibVersion();

}