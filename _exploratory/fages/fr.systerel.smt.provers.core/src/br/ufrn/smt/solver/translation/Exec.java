/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Creation
 *     Systerel (YFT) - Code simplification (unused methods etc ...)
 *******************************************************************************/

package br.ufrn.smt.solver.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * This class executes the given command with arguments and returns a String
 * containing both input and error streams produced content
 */
public class Exec {

	/**
	 * This method starts a process with the arguments and returns it. The error
	 * stream is redirect to the output stream in the returned process.
	 * 
	 * @param args
	 *            The arguments of the process
	 * @return a new process
	 * @throws IOException
	 */
	public static Process startProcess(final List<String> args)
			throws IOException {
		final ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);

		return pb.start();

	}

	/**
	 * This method reads and save in the stringbuilder the output of the
	 * process.
	 * 
	 * @param p
	 *            the process
	 * @param outputBuilder
	 *            the stringBuilder that will store the output of the process p
	 * @throws IOException
	 */
	public static void execProgram(final Process p,
			final StringBuilder outputBuilder) throws IOException {
		String ln;

		final BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		/**
		 * Reads input and error streams and writes content into the buffer to
		 * be returned
		 */
		while ((ln = br.readLine()) != null) {
			outputBuilder.append("\n");
			outputBuilder.append(ln);
		}

	}
}
