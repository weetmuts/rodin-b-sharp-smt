package br.ufrn.smt.solver.preferences;

import java.util.ArrayList;
import java.util.List;

public class SmtPreferencesStore{
	
	public static String CreatePreferences(List<SolverDetail> model){
		String preferences = new String();

		for (SolverDetail solverDetail : model) {
			preferences = preferences + solverDetail.getId() + "¤";
			preferences = preferences + solverDetail.getPath() + "¤";
			preferences = preferences + solverDetail.getArgs() + "¤";
			preferences = preferences + Boolean.toString(solverDetail.getsmtV1_2()) + "¤";
			preferences = preferences + Boolean.toString(solverDetail.getsmtV2_0()) + ";";
		}
		
		return preferences;
	}
	
	public static List<SolverDetail> CreateModel(String preferences){
		List<SolverDetail> model = new ArrayList<SolverDetail>();
		
		final String[] rows = preferences.split(";");
		for (String row : rows) {
			if ( row.length() > 0) {
				final String[] columns = row.split("¤");
				model.add(new SolverDetail(columns[0], columns[1], columns[2], Boolean.valueOf(columns[3]), Boolean.valueOf(columns[4])));
			}
		}
		return model;
	}
	
}