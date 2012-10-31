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

/** Thrown in the event of a Scanner (or parser) failure
 *
 *	@include /etc/license.txt
 */

public class ParseFailure extends Exception
{
	private final String inputLine;
	private final int    inputPosition;
	private final int	 inputLineNumber;

	public ParseFailure( String message,
						 String inputLine,
						 int inputPosition, 
						 int inputLineNumber )
	{
		super( message );
		this.inputPosition   = inputPosition;
		this.inputLine 		 = inputLine;
		this.inputLineNumber = inputLineNumber;
	}

	/** Returns a String that shows the current input line and a
	 *  pointer indicating the current input position.
	 *  In the following sample, the input is positioned at the
	 * 	&#64; sign on input line 17:
	 *  <PRE>
	 *  Line 17:
	 *  a = b + &#64; c;
	 *  ________^
	 *  </PRE>
	 *
	 *  Note that the official "message"  [returned from 
	 *  {@link Throwable#getMessage()}] is not included in the
	 *  error report.
	 */

	public String getErrorReport()
	{	
		StringBuffer b = new StringBuffer();
		b.append("Line ");
		b.append(inputLineNumber + ":\n");
		b.append(inputLine);
		b.append("\n");
		for( int i = 0; i < inputPosition; ++i)
			b.append("_");
		b.append("^\n");
		return b.toString();
	}
}
