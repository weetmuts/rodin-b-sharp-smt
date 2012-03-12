package org.eventb.smt.core.preferences;

import static org.eventb.smt.core.internal.preferences.BundledSolverRegistry.BUNDLED_SOLVERS_ID;

public class BundledSolverLoadingException extends IllegalArgumentException {
	public static final BundledSolverLoadingException makeDotInIDException(
			final String id) {
		return new BundledSolverLoadingException("Invalid id: " + id
				+ " (must not contain a dot).");
	}

	public static final BundledSolverLoadingException makeWhitespaceOrColonInIDException(
			final String id) {
		return new BundledSolverLoadingException("Invalid id: " + id
				+ " (must not contain a whitespace or a colon).");
	}

	public static final BundledSolverLoadingException makeNullIDException() {
		return new BundledSolverLoadingException("Invalid id: null pointer.");
	}

	public static final BundledSolverLoadingException makeNullBinaryNameException() {
		return new BundledSolverLoadingException(
				"Invalid binary name: null pointer.");
	}

	public static final BundledSolverLoadingException makeNoSuchBundleException(
			final String bundleName) {
		return new BundledSolverLoadingException("Invalid bundle name: "
				+ bundleName + " (no such bundle was found installed).");
	}

	public static final BundledSolverLoadingException makeNullPathException() {
		return new BundledSolverLoadingException("Invalid path: null pointer.");
	}

	public static final BundledSolverLoadingException makeNoBundledSolversPointException() {
		return new BundledSolverLoadingException("Invalid extension point id: "
				+ BUNDLED_SOLVERS_ID + " (no such extension point was found).");
	}

	public static final BundledSolverLoadingException makeIllegalExtensionException(
			final String id) {
		return new BundledSolverLoadingException(
				"Duplicated bundled solver extension " + id + " ignored.");
	}

	private BundledSolverLoadingException(String message) {
		super(message);
	}

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = -2787953160141168010L;
}