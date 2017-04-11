package pddl;

import java.util.regex.Pattern;

import meta.Utilities;

public class Image
{
	private String image;
	public String getImage() { return image; }
	
	public Image(String image, ImageType imgType)
	{
//		if (imgType != null)
//			if (!Pattern.matches(imgType.getRegex(), image))
//				Utilities.parserError("The word '"+image+"' contains illegal characters");
		this.image = image;
	}
	
	@Override
	public int hashCode() { return image.hashCode(); }
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Image))
			return false;
		Image other = (Image) obj;
		return image.equals(other.image);
	}
	
	@Override
	public String toString()
	{
		return "Class:"+getClass().getName()+" Image:"+image;
	}
}
