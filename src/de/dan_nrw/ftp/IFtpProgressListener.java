package de.dan_nrw.ftp;


/**
 * @author Daniel Czerwonk
 */
public interface IFtpProgressListener {

    /**
     * Reports the current operation progress (e.g. file download) to all listeners 
     * @param progressInPercent
     */
    void reportsProgress(int progressInPercent);
}
