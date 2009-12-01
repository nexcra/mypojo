/*  (c) 2004 Allen I. Holub. All rights reserved.
 *
 *  This code may be used freely by yourself with the following
 *  restrictions:
 *
 *  o Your splash screen, about box, or equivalent, must include
 *    Allen Holub's name, copyright, and URL. For example:
 *
 *      This program contains Allen Holub's SQL package.<br>
 *      (c) 2005 Allen I. Holub. All Rights Reserved.<br>
 *              http://www.holub.com<br>
 *
 *    If your program does not run interactively, then the foregoing
 *    notice must appear in your documentation.
 *
 *  o You may not redistribute (or mirror) the source code.
 *
 *  o You must report any bugs that you find to me. Use the form at
 *    http://www.holub.com/company/contact.html or send email to
 *    allen@Holub.com.
 *
 *  o The software is supplied <em>as is</em>. Neither Allen Holub nor
 *    Holub Associates are responsible for any bugs (or any problems
 *    caused by bugs, including lost productivity or data)
 *    in any of this code.
 */

package erwins.database.text;

import java.util.*;
import java.util.regex.*;

/***
 * A token set is a collection of tokens that define all possible lexical units
 * of some language. TokenSet objects serve as token factories, and all tokens
 * created by a particular TokenSet are in that set. (see {@link #create}).
 * {@link Scanner} ojbects use <code>TokenSet</code>s to recognize input tokens.
 * Each {@link Token} is responsible for deciding whether it comes next in the
 * input, and the tokens examine the input in the order that they were created.
 * <p> See the source code for {@link erwins.database.Database} (in the
 * distribution jar) for an example of how a token set is used in conjunction
 * with a Scanner.
 * 
 * @include /etc/license.txt
 */

public class TokenSet {
    private Collection<Token> members = new ArrayList<Token>();

    /**
     * Return an iterator across the Token pool. This iterator is guaranteed to
     * return the tokens in the order that {@link #create} was called. You can
     * use this iterator to list all the tokens in a given set.
     */

    public Iterator<Token> iterator() {
        return members.iterator();
    }

    /**********************************************************************
     * Create a Token based on a specification and add it to the current set.
     * <p> An appropriate token type is chosen by examining the input
     * specification. In particular, a {@link RegexToken} is created unless the
     * input string contains no regular-expression metacharacters ({i
     * \\[]{}()$^*+?|}) or starts with a single-quote mark ('). In this case, a
     * {@link WordToken} is created if the specification ends in any character
     * that could occur in a Java identifier; otherwise a {@link SimpleToken} is
     * created. If a string that starts with a single-quote mark also ends with
     * a single-quote mark, the end-quote mark is discarded. The end-quote mark
     * is optional. <p> Tokens are always extracted from the beginning of a
     * String, so the characters that precede the token are irrelevant.
     * 
     * @see WordToken
     * @see RegexToken
     * @see SimpleToken
     */

    public Token create(String spec) {
        Token token;
        int start = 1;

        if (!spec.startsWith("'")) {
            if (containsRegexMetacharacters(spec)) {
                token = new RegexToken(spec);
                members.add(token);
                return token;
            }

            --start; // don't compensate for leading quote

            // fall through to the "quoted-spec" case
        }

        int end = spec.length();

        if (start == 1 && spec.endsWith("'")) // saw leading '
        --end;

        token = Character.isJavaIdentifierPart(spec.charAt(end - 1)) ? (Token) new WordToken(spec.substring(start, end))
                : (Token) new SimpleToken(spec.substring(start, end));

        members.add(token);
        return token;
    }

    /**
     * Return true if the string argument contains any of the following
     * characters: \\[]{}$^*+?|()
     */
    private static final boolean containsRegexMetacharacters(String s) { // This method could be implemented more efficiently,
        // but its not called very often.
        Matcher m = metacharacters.matcher(s);
        return m.find();
    }

    private static final Pattern metacharacters = Pattern.compile("[\\\\\\[\\]{}$\\^*+?|()]");
}
