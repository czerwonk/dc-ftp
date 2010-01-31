package de.dan_nrw.ftp;

import java.io.BufferedReader;
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
}
