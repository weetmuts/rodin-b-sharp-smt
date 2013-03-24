/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.provers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

/**
 * Common implementation for creating and deleting intermediate files.
 * 
 * @author Laurent Voisin
 */
public class TemporaryFiles {

	// Directory where temporary files are created
	private final File tempDir;

	// Temporary files created so far
	private final List<File> files = new ArrayList<File>();

	public TemporaryFiles(IPath tempDirPath) {
		this.tempDir = new File(tempDirPath.toOSString());
	}

	/**
	 * Creates a temporary file with the given prefix and suffix.
	 * 
	 * @param prefix
	 *            the file name prefix, must be at least three characters long
	 * @param suffix
	 *            the file name suffix
	 * @return a fresh temporary file
	 * @throws IOException
	 *             If a file could not be created
	 */
	public File create(String prefix, String suffix) throws IOException {
		final File result = File.createTempFile(prefix, suffix, tempDir);
		files.add(result);
		result.deleteOnExit();
		return result;
	}

	/**
	 * Delete every temporary file.
	 */
	public void cleanup() {
		for (final File file : files) {
			file.delete();
		}
		files.clear();
	}

}
