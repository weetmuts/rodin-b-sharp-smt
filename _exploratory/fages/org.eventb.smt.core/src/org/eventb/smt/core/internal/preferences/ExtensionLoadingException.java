package org.eventb.smt.core.internal.preferences;

public class ExtensionLoadingException extends IllegalArgumentException {
	public static final ExtensionLoadingException makeDotInIDException(
			final String id) {
		return new ExtensionLoadingException("Invalid id: " + id
				+ " (must not contain a dot).");
	}

	public static final ExtensionLoadingException makeInvalidJavaIDException(
			final String id) {
		return new ExtensionLoadingException("Invalid id: " + id
				+ " (must be a valid Java identifier).");
	}

	public static final ExtensionLoadingException makeNullIDException() {
		return new ExtensionLoadingException("Invalid id: null pointer.");
	}

	public static final ExtensionLoadingException makeNullBinaryNameException() {
		return new ExtensionLoadingException(
				"Invalid binary name: null pointer.");
	}

	public static final ExtensionLoadingException makeNoSuchBundleException(
			final String bundleName) {
		return new ExtensionLoadingException("Invalid bundle name: "
				+ bundleName + " (no such bundle was found installed).");
	}

	public static final ExtensionLoadingException makeNullPathException() {
		return new ExtensionLoadingException("Invalid path: null pointer.");
	}

	public static final ExtensionLoadingException makeNullRegistryException() {
		return new ExtensionLoadingException(
				"Invalid extension registry: null pointer.");
	}

	public static final ExtensionLoadingException makeNoExtensionException(
			final String id) {
		return new ExtensionLoadingException("Invalid extension point id: "
				+ id + " (no such extension point was found).");
	}

	public static final ExtensionLoadingException makeIllegalExtensionException(
			final String id) {
		return new ExtensionLoadingException("Duplicated extension " + id
				+ " ignored.");
	}

	private ExtensionLoadingException(String message) {
		super(message);
	}

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = -2787953160141168010L;
}