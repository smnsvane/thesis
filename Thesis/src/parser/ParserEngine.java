package parser;


import java.io.FileNotFoundException;

import meta.Utilities;

import pddl.Data;

public class ParserEngine
{
	private PDDLFileReader fileReader;
	private Parser parser;
	
	public Data parse(String PDDLProblemFileURL, String PDDLDomainFileURL)
	{
		String problemString = readFile(PDDLProblemFileURL);
		
		String domainString = readDomainFile(PDDLDomainFileURL);
		
		Data data = parser.parse(domainString, problemString);
		
		return data;
	}
	public Data parse(String PDDLProblemFileURL)
	{
		int cutIndex = PDDLProblemFileURL.lastIndexOf("/");
		String path = PDDLProblemFileURL.substring(0, cutIndex + 1);
		
		if (fileReader == null)
			fileReader = new PDDLFileReader();
		
		String problemString = readFile(PDDLProblemFileURL);
		
		if (parser == null)
			parser = new Parser();
		
		String domainName = parser.getDomainNameFromProblem(problemString);
		
		String domainString = fetchDomainString(domainName, path);
		
		Data data = parser.parse(domainString, problemString);
		
		return data;
	}
	private String readDomainFile(String url)
	{
		Utilities.println("\tRead "+ url);
		String content = readFile(url);
		if (content == null)
			Utilities.printWarning(
					"No file found with URL: '"+url+"'");
		return content;
	}
	private String readFile(String url)
	{
		try { fileReader.initialize(url); }
		catch (FileNotFoundException e)
		{
			return null;
		}
		fileReader.fillBuffer();
		return fileReader.getContent();
	}
	private String fetchDomainString(String domainName, String path)
	{
		String domainFilename = domainName + ".pddl";
		String domainString = readDomainFile(path + domainFilename);
		
		if (domainString == null)
		{
			domainFilename = domainName + ".PDDL";
			domainString = readDomainFile(path + domainFilename);
		}
		if (domainString == null)
		{
			domainFilename = domainName.toUpperCase() + ".PDDL";
			domainString = readDomainFile(path + domainFilename);
		}
		if (domainString == null)
		{
			domainFilename = "domain.pddl";
			domainString = readDomainFile(path + domainFilename);
		}
		if (domainString == null)
		{
			domainFilename = "domain.PDDL";
			domainString = readDomainFile(path + domainFilename);
		}
		if (domainString == null)
		{
			domainFilename = "DOMAIN.PDDL";
			domainString = readDomainFile(path + domainFilename);
		}
		if (domainString == null)
		{
			System.err.println("Domain file not found");
			System.exit(1);
		}
		return domainString;
	}
}
