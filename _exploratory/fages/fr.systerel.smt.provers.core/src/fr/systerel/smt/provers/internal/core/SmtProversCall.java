package fr.systerel.smt.provers.internal.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;

public abstract class SmtProversCall extends XProverCall {
	
	protected final String proverName;

	private volatile boolean valid;
	
	// Access to these files must be synchronized
	protected File iFile;
	protected File oFile;
	
	protected SmtProversCall(Iterable<Predicate> hypotheses, Predicate goal,
			IProofMonitor pm, String proverName) {
		super(hypotheses, goal, pm);
		this.proverName = proverName;
	}
	
	protected void callProver(String[] cmdArray, String successMsg)
			throws IOException {

		
	}

	private boolean checkResult(String expected) throws IOException {
		return valid;
		
	}

	private synchronized FileReader getOutputFileReader() {
		return null;
	}

	protected boolean callPK(String[] cmdArray) throws IOException {
		return valid;		
	}

	@Override
	public synchronized void cleanup() {
		if (iFile != null) {
			iFile.delete();
			iFile = null;
		}
		if (oFile != null) {
			oFile.delete();
			oFile = null;
		}
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	protected synchronized void makeTempFileNames() throws IOException {

	}

	protected abstract void printInputFile() throws IOException;
	
	protected abstract String[] proverCommand();
	
	protected abstract String[] parserCommand();
	
	protected abstract String successString();
	
	@Override
	public void run() {
	}

	public boolean runWithPk() {
		return valid;
	}

	protected synchronized void showInputFile() {
		showFile(iFile);
	}
	
	protected synchronized void showOutputFile() {
		showFile(oFile);
	}
	
	private void showFile(File file) {
		if (file == null) {
			System.out.println("***File has been cleaned up***");
			return;
		}
		try {
			final BufferedReader rdr = new BufferedReader(new FileReader(file));
			String line;
			while ((line = rdr.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			System.out.println("***Exception when reading file: "
					+ e.getMessage() + "***");
		}
	}

	private void showProcessOutcome(ProcessMonitor monitor) {
		showProcessOutput(monitor, false);
		showProcessOutput(monitor, true);
		System.out.println("Exit code is: " + monitor.exitCode());

	}

	private void showProcessOutput(ProcessMonitor monitor, boolean error) {
		final String kind = error ? "error" : "output"; 
		System.out.println("-- Begin dump of process " + kind + " --");
		final byte[] bytes = error ? monitor.error() : monitor.output();
		if (bytes.length != 0) {
			final String output = new String(bytes);
			if (output.endsWith("\n")) {
				System.out.print(error);
			} else {
				System.out.println(error);
			}
		}
		System.out.println("-- End dump of process " + kind + " --");
	}

}
