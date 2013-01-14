/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ide.common.resources;

import static com.android.ide.common.resources.ValueResourceParser.escapeResourceString;
import static com.android.ide.common.resources.ValueResourceParser.isEscaped;
import static com.android.ide.common.resources.ValueResourceParser.unescapeResourceString;

import junit.framework.TestCase;

public class ValueResourceParserTest extends TestCase {

    public void testEscapeStringShouldEscapeXmlSpecialCharacters() throws Exception {
        assertEquals("&lt;", escapeResourceString("<"));
        assertEquals("&amp;", escapeResourceString("&"));
    }

    public void testEscapeStringShouldEscapeQuotes() throws Exception {
        assertEquals("\\'", escapeResourceString("'"));
        assertEquals("\\\"", escapeResourceString("\""));
        assertEquals("\" ' \"", escapeResourceString(" ' "));
    }

    public void testEscapeStringShouldPreserveWhitespace() throws Exception {
        assertEquals("\"at end  \"", escapeResourceString("at end  "));
        assertEquals("\"  at begin\"", escapeResourceString("  at begin"));
    }

    public void testEscapeStringShouldEscapeAtSignAndQuestionMarkOnlyAtBeginning()
            throws Exception {
        assertEquals("\\@text", escapeResourceString("@text"));
        assertEquals("a@text", escapeResourceString("a@text"));
        assertEquals("\\?text", escapeResourceString("?text"));
        assertEquals("a?text", escapeResourceString("a?text"));
        assertEquals("\" ?text\"", escapeResourceString(" ?text"));
    }

    public void testEscapeStringShouldEscapeJavaEscapeSequences() throws Exception {
        assertEquals("\\n", escapeResourceString("\n"));
        assertEquals("\\t", escapeResourceString("\t"));
        assertEquals("\\\\", escapeResourceString("\\"));
    }

    public void testTrim() throws Exception {
        assertEquals("", unescapeResourceString("", false, true));
        assertEquals("", unescapeResourceString("  \n  ", false, true));
        assertEquals("test", unescapeResourceString("  test  ", false, true));
        assertEquals("  test  ", unescapeResourceString("\"  test  \"", false, true));
        assertEquals("test", unescapeResourceString("\n\t  test \t\n ", false, true));

        assertEquals("test\n", unescapeResourceString("  test\\n  ", false, true));
        assertEquals("  test\n  ", unescapeResourceString("\"  test\\n  \"", false, true));
        assertEquals("te\\st", unescapeResourceString("\n\t  te\\\\st \t\n ", false, true));
        assertEquals("te\\st", unescapeResourceString("  te\\\\st  ", false, true));
        assertEquals("test", unescapeResourceString("\"\"\"test\"\"  ", false, true));
        assertEquals("\"test\"", unescapeResourceString("\"\"\\\"test\\\"\"  ", false, true));
        assertEquals("test ", unescapeResourceString("test\\  ", false, true));
        assertEquals("\\\\\\", unescapeResourceString("\\\\\\\\\\\\ ", false, true));
        assertEquals("\\\\\\ ", unescapeResourceString("\\\\\\\\\\\\\\ ", false, true));
    }

    public void testNoTrim() throws Exception {
        assertEquals("", unescapeResourceString("", false, false));
        assertEquals("  \n  ", unescapeResourceString("  \n  ", false, false));
        assertEquals("  test  ", unescapeResourceString("  test  ", false, false));
        assertEquals("\"  test  \"", unescapeResourceString("\"  test  \"", false, false));
        assertEquals("\n\t  test \t\n ", unescapeResourceString("\n\t  test \t\n ", false, false));

        assertEquals("  test\n  ", unescapeResourceString("  test\\n  ", false, false));
        assertEquals("\"  test\n  \"", unescapeResourceString("\"  test\\n  \"", false, false));
        assertEquals("\n\t  te\\st \t\n ", unescapeResourceString("\n\t  te\\\\st \t\n ", false, false));
        assertEquals("  te\\st  ", unescapeResourceString("  te\\\\st  ", false, false));
        assertEquals("\"\"\"test\"\"  ", unescapeResourceString("\"\"\"test\"\"  ", false, false));
        assertEquals("\"\"\"test\"\"  ", unescapeResourceString("\"\"\\\"test\\\"\"  ", false, false));
        assertEquals("test  ", unescapeResourceString("test\\  ", false, false));
        assertEquals("\\\\\\ ", unescapeResourceString("\\\\\\\\\\\\ ", false, false));
        assertEquals("\\\\\\ ", unescapeResourceString("\\\\\\\\\\\\\\ ", false, false));
    }

    public void testUnescapeStringShouldUnescapeXmlSpecialCharacters() throws Exception {
        assertEquals("&lt;", unescapeResourceString("&lt;", false, true));
        assertEquals("<", unescapeResourceString("&lt;", true, true));
        assertEquals("<", unescapeResourceString("  &lt;  ", true, true));
        assertEquals("&amp;", unescapeResourceString("&amp;", false, true));
        assertEquals("&", unescapeResourceString("&amp;", true, true));
        assertEquals("&", unescapeResourceString("  &amp;  ", true, true));
        assertEquals("!<", unescapeResourceString("!&lt;", true, true));
    }

    public void testUnescapeStringShouldUnescapeQuotes() throws Exception {
        assertEquals("'", unescapeResourceString("\\'", false, true));
        assertEquals("\"", unescapeResourceString("\\\"", false, true));
        assertEquals(" ' ", unescapeResourceString("\" ' \"", false, true));
    }

    public void testUnescapeStringShouldPreserveWhitespace() throws Exception {
        assertEquals("at end  ", unescapeResourceString("\"at end  \"", false, true));
        assertEquals("  at begin", unescapeResourceString("\"  at begin\"", false, true));
    }

    public void testUnescapeStringShouldUnescapeAtSignAndQuestionMarkOnlyAtBeginning()
            throws Exception {
        assertEquals("@text", unescapeResourceString("\\@text", false, true));
        assertEquals("a@text", unescapeResourceString("a@text", false, true));
        assertEquals("?text", unescapeResourceString("\\?text", false, true));
        assertEquals("a?text", unescapeResourceString("a?text", false, true));
        assertEquals(" ?text", unescapeResourceString("\" ?text\"", false, true));
    }

    public void testUnescapeStringShouldUnescapeJavaUnescapeSequences() throws Exception {
        assertEquals("\n", unescapeResourceString("\\n", false, true));
        assertEquals("\t", unescapeResourceString("\\t", false, true));
        assertEquals("\\", unescapeResourceString("\\\\", false, true));
    }

    public void testIsEscaped() throws Exception {
        assertFalse(isEscaped("", 0));
        assertFalse(isEscaped(" ", 0));
        assertFalse(isEscaped(" ", 1));
        assertFalse(isEscaped("x\\y ", 0));
        assertFalse(isEscaped("x\\y ", 1));
        assertTrue(isEscaped("x\\y ", 2));
        assertFalse(isEscaped("x\\y ", 3));
        assertFalse(isEscaped("x\\\\y ", 0));
        assertFalse(isEscaped("x\\\\y ", 1));
        assertTrue(isEscaped("x\\\\y ", 2));
        assertFalse(isEscaped("x\\\\y ", 3));
        assertFalse(isEscaped("\\\\\\\\y ", 0));
        assertTrue(isEscaped( "\\\\\\\\y ", 1));
        assertFalse(isEscaped("\\\\\\\\y ", 2));
        assertTrue(isEscaped( "\\\\\\\\y ", 3));
        assertFalse(isEscaped("\\\\\\\\y ", 4));
    }
}
