package com.brainydroid.daydreaming.db;

import com.brainydroid.daydreaming.background.Logger;
import com.fasterxml.jackson.annotation.JsonView;

// FIXME: adapt doc imported from LocationPoint
public abstract class StatusModel<M extends StatusModel<M,S,F>,
        S extends StatusModelStorage<M,S,F>, F extends ModelJsonFactory<M,S,F>>
        extends Model<M,S,F> {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "StatusModel";

    @JsonView(Views.Public.class)
    protected String status;

    /**
     * Set the status of the {@code LocationPoint}, and persist to database
     * if necessary.
     * <p/>
     * A value of {@code LocationPoint.STATUS_COLLECTING} means the {@code
     * LocationPoint} is either waiting for its timestamp or still has a
     * listener registered on a location provider,
     * receiving location updates. A value of {@code
     * LocationPoint.STATUS_COMPLETED} means the instance has finished
     * collecting its relevant data and has been closed,
     * but not necessarily persisted to the database (that is an independent
     * property).
     *
     * @param status Status to set, should be one of {@code
     *               LocationPoint.STATUS_COLLECTING} or {@code
     *               LocationPoint.STATUS_COMPLETED}
     */
    public synchronized void setStatus(String status) {
        Logger.v(TAG, "Setting status");
        this.status = status;
        saveIfSync();
    }

    /**
     * Get the status of the {@code LocationPoint}.
     * <p/>
     * See {@code setStatus()} for details on the meaning of this status.
     *
     * @return Current status of the {@code LocationPoint}
     */
    public synchronized String getStatus() {
        return status;
    }

}
