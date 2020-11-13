package com.sonymobile.miscta;

public final class MiscTaException extends RuntimeException {
    private miscta_status_t mStatus;

    public enum miscta_status_t {
        MT_SUCCESS,
        MT_ERROR,
        MT_NOMEM,
        MT_TAERR,
        MT_TOOBIG,
        MT_NOTFOUND,
        MT_INVALPARAM,
        MT_SAMEVAL
    }

    public MiscTaException(miscta_status_t status) {
        this.mStatus = status;
    }

    public MiscTaException(int status) {
        try {
            this.mStatus = miscta_status_t.values()[status];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.mStatus = miscta_status_t.MT_ERROR;
        }
    }

    public miscta_status_t getStatus() {
        return this.mStatus;
    }

    public String getMessage() {
        return "status=" + getStatus().name() + "(" + getStatus().ordinal() + ")";
    }
}
