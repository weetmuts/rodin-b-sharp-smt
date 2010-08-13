package br.ufrn.smt.solver.preferences;


import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

class SolversDetailsLabelProvider implements ITableLabelProvider {
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		SolverDetail solver = (SolverDetail) element;
		switch (columnIndex) {
		case 0:
			return solver.getId();
		case 1:
			return solver.getPath();
		case 2:
			return Boolean.toString(solver.getsmtV1_2());
		case 3:
			return Boolean.toString(solver.getsmtV2_0());
		}
		return null;
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}
}