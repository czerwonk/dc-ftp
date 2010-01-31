package de.dan_nrw.ftp;

import java.io.IOException;


/**
 * @author Daniel Czerwonk
 *
 */
public class FtpErrorException extends IOException {
    
    private static final long serialVersionUID = 7277173690615909944L;

    /**
     * Creates a new instance of FtpErrorException
     */
    public FtpErrorException() {
    }

    /**
     * Creates a new instance of FtpErrorException
     * @param message
     */
    public FtpErrorException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of FtpErrorException
     * @param cause
     */
    public FtpErrorException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of FtpErrorException
     * @param message
     * @param cause
     */
    public FtpErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
