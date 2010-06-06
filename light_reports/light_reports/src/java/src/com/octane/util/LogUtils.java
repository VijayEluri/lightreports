/********************************************************************
 *
 * Copyright (c) 2006-2007 Berlin Brown and botnode.com  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php

 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Date: 1/5/2009
 *       7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
 *       
 * Main Description: Light Log Viewer is a tool for making it easier to search log files.  
 * Light Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs 
 * and charts for monitoring logs, file database to quickly navigate to files of interest, 
 * and HTML to PDF convert tool.  
 * Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, SWT 3.4, JFreeChart, iText. 
 * 
 * Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script light_logs.bat
 * (you may need to edit the Linux script for Unix/Linux environments).
 * Edit the win32 script to add more heap memory or other parameters.
 * 
 * The clojure source is contained in : HOME/src/octane
 * The java source is contained in :  HOME/src/java/src
 * 
 * To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml
 * 
 * Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and contains wrapper code
 *  around the Java source.  There are 2000+ lines of Java code in the Java library for Light Log Viewer.
 *  
 * Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  Clojure is also
 *   started from the same code, and reflection is used to dynamically initiate Clojure. See the 'start' package.  The binary
 *   code is contained in the octane_start.jar library.
 *   
 * Home Page: http://code.google.com/p/lighttexteditor/
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 *********************************************************************/

package com.octane.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.octane.util.beans.LogUtilsMatcherBean;

/**
 * @author Berlin
 * @version $Revision: 1.0 $
 */
public class LogUtils {
      
    /**
     * Attempt to match the following date patterns:
     * <pre>
     * [7/2/09 9:57:19:484 EDT] 0000000a SSLComponentI I   CWPKI0002I: SSL service initialization completed successfully
     * [7/2/09 10:16:30:019 EDT] 00000031 SystemErr     R java.net.ConnectException: A remote host refused an attempted connect operation.
     * </pre>
     */
    public static final Pattern PAT_SYS_OUT_LOG = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2,4} \\d{1,2}:\\d{1,2}:\\d{1,2}");
    
    public static final Pattern PAT_LOG4J = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{1,2}:\\d{1,2}");
        
    public static final Pattern PAT_QUERY_LOG = Pattern.compile("\\d{4}.\\d{2}.\\d{2}:\\d{2}:\\d{2}:\\d{2}");
    
    public static final Pattern PAT_ERROR_TERMS = Pattern.compile("ERROR|EXCEPTION", Pattern.CASE_INSENSITIVE);
           
    /**
     * Implementation Routine hasLogDatePatternMatcher.
     * @param line String
     * @return Matcher
     */
    public static final Matcher hasLogDatePatternMatcher(final String line) {
        
        final Matcher mSysOut = PAT_SYS_OUT_LOG.matcher(line);
        final boolean chk1 = mSysOut.find();
        if (chk1) {
            return mSysOut;
        }
        // Check the log4j type date
        
        final Matcher mLog4j = PAT_LOG4J.matcher(line);
        final boolean chk2 = mLog4j.find();
        if (chk2) {
            return mLog4j;
        }        
        
        final Matcher mQuery = PAT_QUERY_LOG.matcher(line);
        final boolean chk3 = mQuery.find();
        if (chk3) {
            return mQuery;
        }
        return null;
    }
    
    /**
     * Implementation Routine hasLogDatePattern.
     * @param line String
     * @return boolean
     */
    public static final boolean hasLogDatePattern(final String line) {
        final Matcher m = hasLogDatePatternMatcher(line);
        return (m != null);
    }
    
    /**
     * Implementation Routine getLogDatePattern.
     * @param line String
     * @return LogUtilsMatcherBean
     */
    public static final LogUtilsMatcherBean getLogDatePattern(final String line) {
        final Matcher m = hasLogDatePatternMatcher(line);
        if (m != null) {
            // Find has already been applied.
            final LogUtilsMatcherBean bean = new LogUtilsMatcherBean(m.start(), m.end(), m);
            return bean;
        } // End of the if //
        return null;
    }
    
    /**
     * Implementation Routine hasLogDatePatternMatcher.
     * @param line String
     * @return Matcher
     */
    public static final Matcher hasErrorPatternMatcher(final String line) {        
        final Matcher m = PAT_ERROR_TERMS.matcher(line);
        final boolean chk1 = m.find();
        if (chk1) {
            return m;
        }
        return null;
    }
    
    /**
     * Implementation Routine getErrorPattern.
     * @param line String
     * @return LogUtilsMatcherBean
     */
    public static final LogUtilsMatcherBean getErrorPattern(final String line) {
        final Matcher m = hasErrorPatternMatcher(line);
        if (m != null) {
            // Find has already been applied.
            final LogUtilsMatcherBean bean = new LogUtilsMatcherBean(m.start(), m.end(), m);
            return bean;
        } // End of the if //
        return null;
    }
    
} // End of the Class //
