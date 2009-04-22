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
package fr.systerel.decert;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * The resources.
 */
public class Resources {
	// ============================================================================
	// External Resources
	// ============================================================================

	// Input Files
	/** The XML input file. */
	private static File XMLFile;

	/**
	 * Returns the XML input file.
	 * 
	 * @return the <tt>File</tt> object associated to this file
	 */
	final static File getXMLFile() {
		return XMLFile;
	}

	/**
	 * Builds the XML input file.
	 * 
	 * @param path
	 *            the path of the XML file to be built
	 */
	private static final void setXMLFile(String path) throws ResourceException {
		XMLFile = exists(path);
	}

	/** The DTD input file. */
	private static File DTDFile;

	/**
	 * Returns the DTD input file.
	 * 
	 * @return the <tt>File</tt> object associated to this file
	 */
	final static File getDTDFile() {
		return DTDFile;
	}

	/**
	 * Builds the DTD input file.
	 * 
	 * @param path
	 *            the path of the DTD file to be built
	 */
	private static void setDTDFile(String path) throws ResourceException {
		DTDFile = exists(path);
	}

	// ============================================================================
	// Command line options
	// ============================================================================
	/** The minimum verbose level. */
	private final static int NO_VERBOSE = 0;

	/** The maximum verbose level. */
	private final static int VERBOSE_ALL = 10;

	/** The verbose level. */
	private static int verboseLevel = VERBOSE_ALL;

	/**
	 * Returns the verbose level passed as option on the command line.
	 * 
	 * @return the verbose level
	 */
	final static int getVerboseLevel() {
		return verboseLevel;
	}

	// ============================================================================
	// Useful methods
	// ============================================================================

	/**
	 * Parses the options specified on the command line and loads the useful
	 * resources.
	 * 
	 * @param args
	 *            the command line options
	 * @throw <tt>ResourceException</tt> exception if some required resources
	 *        are missing
	 */
	static void parseOptions(String[] args) throws ResourceException {
		if ((args == null) || (args.length == 0)) {
			usage();
			System.exit(1);
		}

		if (args[0].equalsIgnoreCase("-help") || args[0].equalsIgnoreCase("-h")) {
			usage();
			System.exit(1);
		}

		int optionIndex = 0;
		while (optionIndex < args.length) {
			if (args[optionIndex].equalsIgnoreCase("-verbose")
					|| args[optionIndex].equalsIgnoreCase("-v")) {
				if (args[optionIndex + 1].equals("no"))
					verboseLevel = NO_VERBOSE;
				else if (args[optionIndex + 1].equals("all"))
					verboseLevel = VERBOSE_ALL;
				else {
					try {
						verboseLevel = Integer.parseInt(args[optionIndex + 1]);
					} catch (NumberFormatException e) {
						System.err
								.println("The -verbose option requires an integer value as argument.");
						usage();
						System.exit(1);
					}
				}
				optionIndex += 2;
			} else
				break;
		}

		// Input Files
		if ((optionIndex + 2) != args.length) {
			usage();
			System.exit(1);
		}
		setXMLFile(args[optionIndex++]);
		setDTDFile(args[optionIndex++]);
	}

	/**
	 * Prints the help message.
	 */
	private static void usage() {
		System.out.println("TypeCheck [options] <XMLFile><DTDFile>");
		System.out.println("where <XMLile> is the path of an XML input file "
				+ " and <DTDFile> is the path of the associated DTD file.");
		System.out
				.println("*************************************************************************************");
		System.out.println("Options are:");
		System.out.println("-help    (or -h) Prints this message and exit.");
		System.out
				.println("-verbose (or -v) Specifies the verbose level (NO/ALL/integer).");
		System.out
				.println("*************************************************************************************");
	}

	/**
	 * Checks that the specified file or directory exists.
	 * 
	 * @param name
	 *            the name of the file or directory
	 * @return the associated <tt>File</tt> object if the specified file or
	 *         directory exists
	 * @throw <tt>FileNotFoundException</tt> exception otherwise
	 */
	private static File exists(String name) throws ResourceException {
		File f = new File(name);
		if (!f.exists())
			throw new ResourceException(new FileNotFoundException(f
					.getAbsolutePath()));
		return f;
	}

	/**
	 * Prints the specified string or not, according to the verbose level. More
	 * precisely, the specified string is not printed if the specified level is
	 * greater than the verbose level; otherwise, it is printed.
	 * 
	 * @param s
	 *            the string to be printed
	 * @param level
	 *            the verbose level
	 */
	static void log(String s, int level) {
		if (verboseLevel >= level)
			System.out.println(s);
	}
}
