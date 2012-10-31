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
package erwins.jsample.database;

/** A convenient container for realying a checked Exception
 *  from a method that can't declare a throws clause to
 *  a calling method that can.  This doesn't happen very
 *  often, but occasionally you don't want to declare
 *  an interface method as throwing an exception that
 *  one of the methods called from the implementation
 *  actually throws. Use it like this:
 *
 *	<PRE>
 *	inteface X
 *	{	void interfaceMethod(); // throws nothing.
 *	}
 *
 *	void interfaceMethod()
 *	{	try
 *		{
 *			g();	// throws an IOException
 *		}
 *		catch( IOException e )
 *		{	throw new ThrowableContainer( e );
 *		}
 *	}
 *
 *	void caller(X implementation) thows IOException
 *	{	try
 *		{	implementation.interfaceMethod();
 *		}
 *		catch( ThrowableContainer e )
 *		{	throw (IOException)(e.contents());
 *		}
 *	}
 *	</PRE>
 *
 *	@include /etc/license.txt
 */

public class ThrowableContainer extends RuntimeException
{	private final Throwable contents;
	public ThrowableContainer( Throwable contents )
	{	this.contents = contents;
	}
	public Throwable contents()
	{	return contents;
	}
}
