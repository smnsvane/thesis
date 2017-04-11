package pddl;

public enum ImageType
{
	actionImage("[a-z][a-z0-9_-]*"),
	unitImage("=|"+"[a-z][a-z0-9_-]*"),
	objectImage("[a-z][a-z0-9_-]*"),
	parameterImage("\\?"+"[a-z][a-z0-9_-]*");
	
	private String regex;
	public String getRegex() { return regex; }
	private ImageType(String regex) { this.regex = regex; }
}
