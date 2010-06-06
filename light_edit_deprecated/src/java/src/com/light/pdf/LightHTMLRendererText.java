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
 * Main Description: Light is a simple text editor in clojure
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 *********************************************************************/
package com.light.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility interface for taking an input HTML document, formatting the content and then
 * ensuring that is properly formed for XHTMLRenderer and TagSoup.
 */
public interface LightHTMLRendererText {

    /**
     * Set the original input text document.
     * @param text
     */
    public void setText(final String text);

    /**
     * Return the original text document.
     *
     * @return
     */
    public String getText();

    /**
     * Parse the input document and return the formatted text.
     *
     * @return
     */
    public String parse();
    
    /**
     * Set Base Renderer.
     * @param base
     */
    public void setBaseRenderer(final XHTMLRendererBase base);
    
    /**
     * Return the base renderer instance.
     * @return
     */
    public XHTMLRendererBase getBaseRenderer();

    ///////////////////////////////////////////////////////////////////////////
    // XHtml Tuple Non-Static Inner Classes
    // GroupPosTuple and TextTuple.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Simple tuple data structure, <Group Start, End Positions>.
     */
    public final class GroupPosTuple {

        private final int start;
        private final int end;

        /**
         * 
         * @param s
         * @param e
         */
        public GroupPosTuple(final int s, final int e) {
            start = s;
            end = e;
        }
        
        /**
         * Get the start tag.
         * @return
         */
        public final int getStart() {
            return this.start;
        }
        
        /**
         * Return the end.
         * @return
         */
        public final int getEnd() {
            return this.end;
        }
        
        /**
         * To String.
         */
        public String toString() {
            return "<<GroupPosTuple start=" + start + " end=" + end + ">>";
        }
    } // End of Class

    /**
     * Simple text utilties used with PDF creation.
     * 
     * @author bbrown     
     */
    public final class TextUtils {

    	/**
    	 * Remove the HTML section.
    	 * 
    	 * @param maindoc
    	 * @param repl
    	 * @param startTag
    	 * @param endTag
    	 * @return
    	 */
        public final static String removeHtmlSection(final String maindoc,
                final String repl, final String startTag, final String endTag) {

            // Compile regular expression
            final StringBuffer track_pattern_buf = new StringBuffer();
            track_pattern_buf.append(startTag);
            track_pattern_buf.append("(.*?)");
            track_pattern_buf.append(endTag);

            //final Pattern pattern = Pattern.compile(track_pattern_buf.toString());
            final Pattern pattern = new_pattern(track_pattern_buf.toString());

            final int lorig = maindoc.length();
            // Replace all occurrences of pattern in input
            final Matcher matcher = pattern.matcher(maindoc);
            final String output = matcher.replaceAll(repl);
            final int lnew = output.length();
            if (lnew == lorig) {
                // Qualify when a text change has been made
                System.out.println("<Searching for tags for further processing, failed [[" +startTag+ "]]> diff:" + (lorig-lnew));
            }
            return output;
        }

        public static final Pattern new_pattern(final String pattern) {
            return Pattern.compile(pattern,
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        }

        /**
         * Find a simple match.
         */
        public static final TextTuple findMatch(final String text, final String pattern_str, int group, int total_groups) {

            final StringBuffer res = new StringBuffer();
            final Pattern pattern = new_pattern(pattern_str);
            final Matcher match = pattern.matcher(text);

            final TextTuple tuple = new TextTuple();
            while (match.find()) {
                if (match.groupCount() >= total_groups) {
                    res.append(match.group(group));
                    tuple.addPosTuple(match.start(group), match.end(group));
                }
            } // End of While
            tuple.setText(res.toString());
            return tuple;
        }
    }    
    
    /**
     * Simple tuple data structure, <text>,<List of Group Positions>
     */
    public final class TextTuple {

        private String text = "";
        private final List pos_tuples = new ArrayList();

        /**
         * Main constructor, set the doc and start and end positions.
         * 
         * @param doc
         * @param s
         * @param e
         */
        public TextTuple(final String doc, final int s, final int e) {
            this(doc);
            // Set the first group pos tuple.
            this.addPosTuple(s, e);
        }
        
        /**
         * Default constructor.
         */
        public TextTuple() { 

        }

        /**
         * Constructor with initial doc.
         * 
         * @param doc
         */
        public TextTuple(final String doc) {
            if (doc == null) {
                text = "";
            } else {
                text = doc;
            }
        }
        public String toString() {
            return this.text;
        }
        public List getTuples() { return pos_tuples; }

        /**
         * @return the text
         */
        public final String getText() {
            return text;
        }
        public final void setText(String txt) {
            this.text = txt;
        }
        
        public final GroupPosTuple addPosTuple(final int s, final int e) {
            final GroupPosTuple tuple = new GroupPosTuple(s, e);
            pos_tuples.add(tuple);
            return tuple;
        }

    } // End of Class

       
} // End of the Class

/////////////////////////////////////////////////
// End of File
/////////////////////////////////////////////////
