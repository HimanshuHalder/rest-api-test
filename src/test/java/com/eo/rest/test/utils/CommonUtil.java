package com.eo.rest.test.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

/**
 * This is the common util and it will hold commmon methods whi will support
 * and provide information to all tests
 */
public class CommonUtil {

    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public Properties readPropertiesFile(String fileName){
        InputStream fis = null;
        Properties prop = null;

        try {
            fis = this.getClass().getClassLoader().getResourceAsStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return prop;
    }

    /**
     * This method is to validate a string is numeric formatted
     * @param value
     * @return
     */
    public boolean isNumeric(String value){
        if (value == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Validate if a string is a timestamp
     * @param inputTimestamp
     * @return
     */
    public boolean isTimeStampValid(String inputTimestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            format.parse(inputTimestamp);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * This validates if a string is representing a valid url
     * @param url
     * @return
     */
    public boolean isValidUrl(String url)
    {
        try {
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
