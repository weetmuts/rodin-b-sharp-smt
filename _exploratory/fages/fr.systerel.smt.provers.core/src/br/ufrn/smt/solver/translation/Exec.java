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

	public static String execProgram(List<String> args) throws IOException {
		String ln;
		/**
		 * Executes the command with args in a new process
		 */
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);

		final Process p = pb.start();

		final BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		/**
		 * Reads input and error streams and writes content into the buffer to
		 * be returned
		 */
		final StringBuilder outputBuilder = new StringBuilder();
		while ((ln = br.readLine()) != null) {
			outputBuilder.append("\n");
			outputBuilder.append(ln);
		}

		return outputBuilder.toString();
	}
}
