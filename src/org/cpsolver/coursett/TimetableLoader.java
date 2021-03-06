package org.cpsolver.coursett;

import org.apache.log4j.Logger;
import org.cpsolver.coursett.model.Lecture;
import org.cpsolver.coursett.model.Placement;
import org.cpsolver.coursett.model.TimetableModel;
import org.cpsolver.ifs.assignment.Assignment;
import org.cpsolver.ifs.util.Callback;


/**
 * Abstract timetable loader class.
 * 
 * @version CourseTT 1.3 (University Course Timetabling)<br>
 *          Copyright (C) 2006 - 2014 Tomas Muller<br>
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
 */

public abstract class TimetableLoader implements Runnable {
    private TimetableModel iModel = null;
    private Assignment<Lecture, Placement> iAssignment = null;
    private Callback iCallback = null;

    /**
     * Constructor
     * 
     * @param model
     *            an empty instance of timetable model
     * @param assignment current assignment
     */
    public TimetableLoader(TimetableModel model, Assignment<Lecture, Placement> assignment) {
        iModel = model;
        iAssignment = assignment;
    }

    /**
     * Returns provided model.
     * 
     * @return provided model
     */
    protected TimetableModel getModel() {
        return iModel;
    }
    
    /**
     * Returns provided assignment
     * @return provided assignment
     */
    protected Assignment<Lecture, Placement> getAssignment() {
        return iAssignment;
    }

    /**
     * Load the model.
     * @throws Exception thrown when the load fails
     */
    public abstract void load() throws Exception;

    /**
     * Sets callback class
     * 
     * @param callback
     *            method {@link Callback#execute()} is executed when load is
     *            done
     */
    public void setCallback(Callback callback) {
        iCallback = callback;
    }

    @Override
    public void run() {
        try {
            load();
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).error(e.getMessage(), e);
        } finally {
            if (iCallback != null)
                iCallback.execute();
        }
    }

}
