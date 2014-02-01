/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.decert.smt;

import java.io.File;

import fr.systerel.decert.ResourceException;

/**
 * The resources.
 */
public class Resources extends fr.systerel.decert.Resources {
	// ============================================================================
	// External Resources
	// ============================================================================

	// Output Files
	/** The SMT output folder. */
	private File SMTFolder;

	/**
	 * Returns the SMT output folder.
	 * 
	 * @return the <tt>File</tt> object associated to this file
	 */
	final File getSMTFolder() {
		return SMTFolder;
	}

	/**
	 * Builds the SMT output folder.
	 * 
	 * @param path
	 *            the path of the folder to be built
	 */
	private final void setSMTFolder(String path) throws ResourceException {
		File f = new File(path);
		if (!f.exists())
			f.mkdir();
		SMTFolder = f;
	}

	// ============================================================================
	// Useful methods
	// ============================================================================

	/**
	 * Parses the input files specified on the command line.
	 * 
	 * @param args
	 *            the command line parameters
	 * @param index
	 *            the starting index for the input files
	 * @throw <tt>ResourceException</tt> exception if some required resources
	 *        are missing
	 */
	@Override
	protected void parseInputFiles(String[] args, int index)
			throws ResourceException {
		// Input Files
		if ((index + 3) != args.length) {
			usage();
			System.exit(1);
		}
		setXMLFile(args[index++]);
		setDTDFile(args[index++]);
		setSMTFolder(args[index++]);
	}
	
	/**
	 * Prints the command line.
	 */
	@Override
	protected void printCommand() {
		System.out.println("SMTBench [options] <XMLFile><DTDFile><SMTFolder>");
		System.out.println("where <XMLFile> is the path of an XML input file "
				+ "<DTDFile> is the path of the associated DTD file "
				+ "and <SMTFolder> is the output directory.");
	}
}
