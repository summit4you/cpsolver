package org.cpsolver.ifs.heuristics;

import java.util.ArrayList;
import java.util.List;


import org.apache.log4j.Logger;
import org.cpsolver.ifs.model.Neighbour;
import org.cpsolver.ifs.model.Value;
import org.cpsolver.ifs.model.Variable;
import org.cpsolver.ifs.solution.Solution;
import org.cpsolver.ifs.solver.Solver;
import org.cpsolver.ifs.util.DataProperties;

/**
 * A round robin neighbour selection. Two or more {@link NeighbourSelection}
 * needs to be registered within the selection. This selection criterion takes
 * the registered neighbour selections one by one and performs
 * {@link NeighbourSelection#init(Solver)} and then it is using
 * {@link NeighbourSelection#selectNeighbour(Solution)} to select a neighbour.
 * When null is returned by the underlaying selection, next registered neighbour
 * selection is initialized and used for the following selection(s). If the last
 * registered selection returns null, the selection is returned to the first
 * registered neighbour selection (it is initialized before used again).
 * 
 * <br>
 * <br>
 * 
 * @version StudentSct 1.3 (Student Sectioning)<br>
 *          Copyright (C) 2007 - 2014 Tomas Muller<br>
 *          <a href="mailto:muller@unitime.org">muller@unitime.org</a><br>
 *          <a href="http://muller.unitime.org">http://muller.unitime.org</a><br>
 * <br>
 *          This library is free software; you can redistribute it and/or modify
 *          it under the terms of the GNU Lesser General Public License as
 *          published by the Free Software Foundation; either version 3 of the
 *          License, or (at your option) any later version. <br>
 * <br>
 *          This library is distributed in the hope that it will be useful, but
 *          WITHOUT ANY WARRANTY; without even the implied warranty of
 *          MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *          Lesser General Public License for more details. <br>
 * <br>
 *          You should have received a copy of the GNU Lesser General Public
 *          License along with this library; if not see
 *          <a href='http://www.gnu.org/licenses/'>http://www.gnu.org/licenses/</a>.
 *
 * @param <V> Variable
 * @param <T> Value
 */
public class RoundRobinNeighbourSelection<V extends Variable<V, T>, T extends Value<V, T>> extends StandardNeighbourSelection<V, T> {
    protected static Logger sLogger = Logger.getLogger(RoundRobinNeighbourSelection.class);
    protected int iSelectionIdx = -1;
    protected List<NeighbourSelection<V, T>> iSelections = new ArrayList<NeighbourSelection<V, T>>();
    protected Solver<V, T> iSolver = null;

    /**
     * Constructor
     * 
     * @param properties
     *            configuration
     * @throws Exception thrown when initialization fails
     */
    public RoundRobinNeighbourSelection(DataProperties properties) throws Exception {
        super(properties);
    }

    /** Register a neighbour selection 
     * @param selection a neighbour selection to include in the selection
     **/
    public void registerSelection(NeighbourSelection<V, T> selection) {
        iSelections.add(selection);
    }

    /** Initialization */
    @Override
    public void init(Solver<V, T> solver) {
        super.init(solver);
        iSolver = solver;
    }

    /**
     * Select neighbour. A first registered selections is initialized and used
     * until it returns null, then the second registered selections is
     * initialized and used and vice versa.
     */
    @Override
    public Neighbour<V, T> selectNeighbour(Solution<V, T> solution) {
        while (true) {
            int selectionIndex = getSelectionIndex();
            NeighbourSelection<V, T> selection = iSelections.get(selectionIndex);
            Neighbour<V, T> neighbour = selection.selectNeighbour(solution);
            if (neighbour != null)
                return neighbour;
            changeSelection(selectionIndex);
        }
    }
    
    public synchronized int getSelectionIndex() {
        if (iSelectionIdx == -1) {
            iSelectionIdx = 0;
            iSelections.get(iSelectionIdx).init(iSolver);
        }
        return iSelectionIdx;
    }

    /** Change selection 
     * @param selectionIndex current selection index 
     **/
    public synchronized void changeSelection(int selectionIndex) {
        int newSelectionIndex = (1 + selectionIndex) % iSelections.size();
        if (newSelectionIndex == iSelectionIdx) return; // already changed
        iSelectionIdx = newSelectionIndex;
        sLogger.debug("Phase changed to " + (newSelectionIndex + 1));
        if (iSolver.currentSolution().getBestInfo() == null || iSolver.getSolutionComparator().isBetterThanBestSolution(iSolver.currentSolution()))
            iSolver.currentSolution().saveBest();
        iSelections.get(iSelectionIdx).init(iSolver);
    }
}
