package br.ufrn.smt.solver.preferences;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;

/**
 * This class provides the cell modifier for solver Table
 */
class SolversDetailsCellModifier implements ICellModifier {
	
	private Viewer viewer;
	 
	public SolversDetailsCellModifier(Viewer viewer) {
	    this.viewer = viewer;
	  }
	
	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		// TODO Auto-generated method stub
		if (element instanceof Item) element = ((Item) element).getData();

	    SolverDetail p = (SolverDetail) element;
	    if (SMTPreferencePage2.SOLVER_ID.equals(property))
	      p.setId((String)value);
	    else if (SMTPreferencePage2.SOLVER_PATH.equals(property))
	      p.setPath((String)value);
	    else if (SMTPreferencePage2.V1_2.equals(property))
	      p.setSmtV1_2(((Boolean) value).booleanValue());
	    else if (SMTPreferencePage2.V2_0.equals(property))
	      p.setSmtV2_0(((Boolean) value).booleanValue());

	    viewer.refresh();
	}
	
	
}

/**
 * This class provides the labels for PlayerTable
 */
/*class SolversDetailsLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element != null) {
			SolverDetail detail = (SolverDetail) element;
			String text = "";
			switch (columnIndex) {
			case 0:
				text = detail.getId();
				break;
			case 1:
				text = detail.getPath();
				break;
			case 2:
				text = detail.getPath();
				break;
			case 3:
				text = detail.getPath();
				break;
			}
			return text;
		}
		else{
			return null;
		}
			
	}
}*/
