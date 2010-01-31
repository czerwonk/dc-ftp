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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.net.ftp.FtpClientWithSkeySupport;


/**
 * @author Daniel Czerwonk
 */
public class FtpClient extends FtpClientWithSkeySupport {

    private static final Pattern LIST_PATTERN = Pattern.compile("^[d\\-](?:[r\\-][w\\-][x\\-]){3}(?:\\s+[^\\s]+){4}\\s+\\w{3}\\s+\\d+\\s+\\d{2}:\\d{2}\\s+(.+)$");
    private static final Pattern SIZE_PATTERN = Pattern.compile("(?<=213\\s)\\d+(?=\\r?\\n?)");
    
    
    /**
     * Creates a new instance of FtpClient
     */
    public FtpClient() {
        super();
    }

    /**
     * Creates a new instance of FtpClient
     * @param p
     */
    public FtpClient(Proxy p) {
        super(p);
    }

    /**
     * Creates a new instance of FtpClient
     * @param host
     * @param port
     * @throws IOException
     */
    public FtpClient(String host, int port) throws IOException {
        super(host, port);
    }

    /**
     * Creates a new instance of FtpClient
     * @param host
     * @throws IOException
     */
    public FtpClient(String host) throws IOException {
        super(host);
    }
    
    
    /**
     * Retrieves the names of all files in current remote working directory
     * @return
     * @throws IOException if request fails
     */
    public Iterable<String> getFileNames() throws IOException {
        return this.listWithFilter("-");
    }
    
    /**
     * Retrieves the names of all directories in current remote working directory
     * @return
     * @throws IOException if request fails
     */
    public Iterable<String> getDirNames() throws IOException {
        return this.listWithFilter("d");
    }

    private Iterable<String> listWithFilter(String startCharacter) throws IOException {
        List<String> result = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.list()));
        
        try {
            String line = null;
            
            while ((line = reader.readLine()) != null) {
                Matcher matcher = LIST_PATTERN.matcher(line);
                
                if (matcher.matches() && line.startsWith(startCharacter)) {
                    result.add(matcher.group(1));
                }
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();    
                }
                catch (IOException ex) {
                    // exception would hide exception thrown in outer try block
                    ex.printStackTrace(System.err);
                }
            }
        }
        
        return result;
    }

    /**
     * Determines the size of a remote file
     * @param remoteFile Path of remote file
     * @return Size of remote file
     * @throws IOException if request fails
     */
    public long getFileSize(String remoteFile) throws IOException {
        if (this.issueCommand("SIZE " + remoteFile) == FTP_ERROR) {
            throw new FtpErrorException(String.format("size %s: %s", remoteFile, this.getResponseString()));
        }
        
        String response = this.getResponseString();
        Matcher matcher = SIZE_PATTERN.matcher(response);
        
        if (!matcher.find()) {
            throw new FtpErrorException(String.format("size %s: %s", remoteFile, response));
        }
        
        return Long.parseLong(matcher.group(0));
    }
    
    /**
     * Downloads a remote file from server
     * @param remoteFile File to download
     * @param targetFile Path of target file
     * @param progressListeners Listeners observing download progress
     * @throws IOException if download fails
     */
    public void downloadFile(String remoteFile, File targetFile, IFtpProgressListener... progressListeners) throws IOException {
        this.binary();
        long fileSize = this.getFileSize(remoteFile);
        
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        
        try {
            inputStream = new BufferedInputStream(this.get(remoteFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));

            byte[] buffer = new byte[16384];
            int currentProgress = 0;
            long totalBytesRead = 0;
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                
                int newProgressRounded = (int)(100 * totalBytesRead / fileSize);
                
                if (newProgressRounded > currentProgress) {
                    for (IFtpProgressListener listener : progressListeners) {
                        listener.reportsProgress(newProgressRounded);
                    }
                    
                    currentProgress = newProgressRounded;
                }
            }   
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();   
                }
                catch (IOException ex) {
                    // exception would hide exception thrown in outer try block
                    ex.printStackTrace(System.err);                    
                }
            }
            
            if (inputStream != null) {
                try {
                    inputStream.close();    
                }
                catch (IOException ex) {
                    // exception would hide exception thrown in outer try block
                    ex.printStackTrace(System.err);                    
                }
            }
        }
    }
}