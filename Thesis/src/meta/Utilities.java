package meta;

import java.util.Arrays;
import java.util.Collection;

import pddl.Requirement;

public abstract class Utilities
{
	public static String implode(Collection<?> col)
	{
		return implode(col, "");
	}
	public static String implode(Collection<?> col, String delimiter)
	{
		StringBuffer sb = new StringBuffer();
		for (Object obj : col)
			sb.append(obj + delimiter);
		if (sb.length() > delimiter.length())
			sb.delete(sb.length()-delimiter.length(), sb.length());
		return sb.toString();
	}
	public static String parImplode(Collection<?> col, String delimiter)
	{
		StringBuffer sb = new StringBuffer();
		for (Object obj : col)
			sb.append("(" + obj + ")" + delimiter);
		if (sb.length() > delimiter.length())
			sb.delete(sb.length()-delimiter.length(), sb.length());
		return sb.toString();
	}
	
	private static boolean printDebug, printStatus, printWarnings;
	public static boolean getPrintStatus() { return printStatus; }
	public static void setPrintDebug(boolean print) { printDebug = print; }
	public static void setPrintStatus(boolean print) { printStatus = print; }
	public static void setPrintWarnings(boolean print) { printWarnings = print; }
	public static void printDebug(Object s) { if (printDebug) System.out.println(s); }
	public static void print(Object s) { if (printStatus) System.out.print(s); }
	public static void println(Object s) { if (printStatus) System.out.println(s); }
	public static void printWarning(Object s) { if (printWarnings) System.out.println("WARNING: "+s); }
	
	public static void parserError(String problem)
	{
		System.err.println(problem);
		// TODO write 'System.exit(1);' instead of casting an exception
		throw new RuntimeException();
	}
	public static void parserError(String found, String... expected)
	{
		if (expected.length == 1)
			System.err.println("Found: '"+found+
					"' expected '"+expected[0]+"'");
		else
			System.err.println("Found: '"+found+
					"' expected one of "+Arrays.toString(expected));
		// TODO write 'System.exit(1);' instead of casting an exception
		throw new RuntimeException();
	}
	public static void supportError(Requirement req)
	{
		System.err.println("'"+req+"' not supported");
		// TODO write 'System.exit(1);' instead of casting an exception
		throw new RuntimeException();
	}
	public static void supportError(Requirement req, String problem)
	{
		System.err.println("The current domain does not support '"+req+"', however "+
				problem);
		// TODO write 'System.exit(1);' instead of casting an exception
		throw new RuntimeException();
	}
}
