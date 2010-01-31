/*
 * de.dan_nrw.ftp
 *
 * Copyright (C) 2010, Daniel Czerwonk <d.czerwonk@googlemail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 */
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
