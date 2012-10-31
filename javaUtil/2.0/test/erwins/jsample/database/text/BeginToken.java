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

package erwins.jsample.database.text;

/**
 * The Begin token is special in that it has no width, and doesn't match
 * anything in the input. It's used solely as a convenient initial value for the
 * current-token field of the {@link Scanner}, but is made public in case you
 * want to build your own scanner.
 * @include /etc/license.txt
 * @see Token
 */

public class BeginToken implements Token {
    public boolean match(String input, int offset) {
        return false;
    }

    public String lexeme() {
        return "";
    }

    @Override
    public String toString() {
        return "BeginToken";
    }
}
