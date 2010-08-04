package fr.systerel.smt.provers.internal.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;

import br.ufrn.smt.solver.translation.Exec;
import br.ufrn.smt.solver.translation.PreProcessingException;
import br.ufrn.smt.solver.translation.RodinToSMTPredicateParser;
import br.ufrn.smt.solver.translation.TranslationException;

import fr.systerel.smt.provers.core.SmtProversCore;

public abstract class SmtProversCall extends XProverCall {
	
	protected final String proverName;

	private volatile boolean valid;
	
	// Access to these files must be synchronized
	protected File iFile;
	protected File oFile;
	
	//Variables created by Vitor
	String resultOfSolver;
	File smtFile;
	File firstTranslationFile;
	
	protected SmtProversCall(Iterable<Predicate> hypotheses, Predicate goal,
			IProofMonitor pm, String proverName) {
		super(hypotheses, goal, pm);
		this.proverName = proverName;
	}
	
	protected void callProver(ArrayList<String> args, String successMsg)
			throws IOException {
		String seeFileOrProofCommand = SmtProversCore.getDefault().getPreferenceStore().getString("executeTrans");
		if(seeFileOrProofCommand.equals("proofandshowfile") || seeFileOrProofCommand.equals("proofonly"))
		{
			for(int i = 0 ; i < args.size(); i++)
			{
				System.out.println(args.get(i));
			}
			if (SmtProversCore.getDefault().getPreferenceStore().getString("whichsolver").equals("cvc3"))
			{
				args.add("-lang");
				args.add("smt");
			}			
			String[] terms = new String[args.size()];
			for(int i = 0 ; i < terms.length ; i++)
			{
				terms[i] = args.get(i);
			}		
			resultOfSolver = Exec.execProgram(terms);
			
			checkResult(resultOfSolver);
			File resultFile = new File(iFile.getParent() + "/smTSolverString");
			if(!resultFile.exists())
			{
				resultFile.createNewFile();
			}			
			FileWriter fileWriter = new FileWriter(resultFile);
			fileWriter.write(resultOfSolver);
			fileWriter.close();
			oFile = resultFile;
			System.out.println(resultOfSolver);
		}		
		if(seeFileOrProofCommand.equals("proofandshowfile") || seeFileOrProofCommand.equals("showfileonly"))
		{
			boolean preprocess = SmtProversCore.getDefault().getPreferenceStore().getBoolean("usingprepro");
			String solver = SmtProversCore.getDefault().getPreferenceStore().getString("whichsolver");
			String preprocessorOptions = SmtProversCore.getDefault().getPreferenceStore().getString("preprocessingoptions");
			if((preprocess || !solver.equals("veriT")) && (preprocessorOptions.equals("aftersmt") || preprocessorOptions.equals("beforeandafter")) )
			{
				showFileInEditor(this.firstTranslationFile.getPath());
			}
			if(preprocessorOptions.equals("presmt") || preprocessorOptions.equals("beforeandafter"))
			{
				showFileInEditor(smtFile.getPath());
			}					
		}
	}
	
	private void showFileInEditor(String filePath)
	{
		String editor = SmtProversCore.getDefault().getPreferenceStore().getString("smteditor");
		String[] args = {editor,filePath};
		try {
			Exec.execProgram(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	private boolean checkResult(String expected) throws IOException {
		String typeOfSolver = SmtProversCore.getDefault().getPreferenceStore().getString("whichsolver");
		if( expected.trim().endsWith("unsat"))
		{
			valid = true;
		}
		else
		{
			valid = false;
		}
		return valid;		
	}

	private synchronized FileReader getOutputFileReader() {
		try {
			return new FileReader(oFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	protected static String preprocessSMTinVeriT(String smtFilePath) throws PreProcessingException
	{
		String pathOfSolver = SmtProversCore.getDefault().getPreferenceStore().getString("prepropath");
		if(pathOfSolver.isEmpty())
		{
			throw new PreProcessingException("The path of the pre-processor is not defined");
		}
		String[] args = new String[3];
		args[0] = pathOfSolver;
		args[1] = "--print-simp-and-exit";		
		args[2] = smtFilePath;
		try
		{
			String resultOfPreProcessing = Exec.execProgram(args);
			int benchmarkIndex = resultOfPreProcessing.indexOf("(benchmark") + 10;
			int i = 1;
			StringBuffer sb = new StringBuffer();
			sb.append("(benchmark");
			while(i > 0 || benchmarkIndex >= resultOfPreProcessing.length())
			{
				char c = resultOfPreProcessing.charAt(benchmarkIndex);
				if(c == '(')
				{
					++i;
				}
				else if(c == ')')
				{
					--i;
				}
				sb.append(c);
				++benchmarkIndex;
			}
			if(benchmarkIndex >= resultOfPreProcessing.length() && i != 0)
			{
				throw new PreProcessingException();
			}
			
			
			return sb.toString();			
		}
		
		catch(IOException io)
		{
			io.printStackTrace();
		} catch (PreProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	public void run() {
//Code by Vitor Alcantara de Almeida
		
		String solverPath = SmtProversCore.getDefault().getPreferenceStore().getString("solver_path");
		if(solverPath.isEmpty())
		{
			System.err.println("There is no defined path for solver");
			return;
		}		
		try {
			//Doing the translation:
			try
			{
				RodinToSMTPredicateParser rp = new RodinToSMTPredicateParser(hypotheses, goal);
				String pathOfSolver = SmtProversCore.getDefault().getPreferenceStore().getString("solver_path");
				String solverArgs = SmtProversCore.getDefault().getPreferenceStore().getString("solverarguments");

				smtFile = rp.getTranslatedFile();

				if(!smtFile.exists())
				{
					System.out.println("The translated file does not exist!!!!");
				}
				ArrayList<String> args = new ArrayList<String>();
				args.add(pathOfSolver);

				args.add(smtFile.getPath());
				if (!solverArgs.isEmpty())
				{
					args.add(solverArgs);
				}
						
				
				boolean preprocess = SmtProversCore.getDefault().getPreferenceStore().getBoolean("usingprepro");
				String solver = SmtProversCore.getDefault().getPreferenceStore().getString("whichsolver");
				if(preprocess || !solver.equals("veriT"))
				{
					try
					{
						String preprocessedSMT = preprocessSMTinVeriT(smtFile.getPath());//result.getThirdElement().getPath());
						File preprocessedFile = new File(/*result.getThirdElement().getParent()*/smtFile.getParent() + "/tempPreProcessed.smt");
						if(!preprocessedFile.exists())
						{
							preprocessedFile.createNewFile();
						}
						FileWriter fw = new FileWriter(preprocessedFile);
						fw.write(preprocessedSMT);
						fw.close();
						args.set(1, preprocessedFile.getPath());
						this.firstTranslationFile = smtFile;
						this.iFile = preprocessedFile;
						this.smtFile = preprocessedFile;
					}
					catch(PreProcessingException p)
					{
						System.err.println(p.getMessage());
						return;
					}					
					
				}
				iFile = smtFile;
				callProver(args, "Success");	
			}
			catch(TranslationException t)
			{
				System.err.println(t.getMessage());
				return;
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println(e.getMessage());
			return;
		}
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
