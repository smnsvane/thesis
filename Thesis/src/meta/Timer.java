package meta;

import java.text.NumberFormat;
import java.util.HashMap;

public class Timer
{
	private HashMap<String, TimerInstance> timers =
		new HashMap<String, TimerInstance>();
	
	public void start(String name)
	{
		if (!timers.containsKey(name))
			timers.put(name, new TimerInstance(name));
		timers.get(name).start();
	}
	public void stop(String name) { timers.get(name).stop(); }
	
	private TimerInstance master = null;
	public void startMaster()
	{
		if (master == null)
			master = new TimerInstance("Master Timer");
		master.start();
	}
	public void stopMaster() { master.stop(); }
	
	public String getTime(String name)
	{
		long time = timers.get(name).getTime();
		String timeStr = NumberFormat.getNumberInstance().format(time);
		return timeStr+" ns";
	}
	public String getTimers()
	{
		StringBuilder sb = new StringBuilder();
		for (String s : timers.keySet())
			sb.append(s+" = "+timers.get(s)+"\n");
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		if (master == null)
			return "";
		
		// Determine the longest key for pretty print
		int keyStringLength = 5;
		for (String s : timers.keySet())
			keyStringLength = Math.max(keyStringLength, s.length() + 1);
		
		long totalMeasured = 0;
		String tTimeS = NumberFormat.getNumberInstance().format(master.getTime());
		
		StringBuilder result = new StringBuilder();
		// Print each measured function
		for (String s : timers.keySet())
		{
			long time = timers.get(s).getTime();
			String timeS = NumberFormat.getNumberInstance().format(time);
			result.append(s + ":");
			result.append(repeat(" ", keyStringLength - s.length() + tTimeS.length() - timeS.length()));
			result.append(timeS + " ns ");
			result.append("(" + NumberFormat.getNumberInstance().format(time * 100.0 / master.getTime()) + " %)\n");
			totalMeasured += time;
		}
		String totalLabel = "Total:";
		
		result.append(totalLabel);
		result.append(repeat(" ", keyStringLength - totalLabel.length() + tTimeS.length() - tTimeS.length() + 1));
		result.append(tTimeS + " ns (");
		result.append(NumberFormat.getNumberInstance().format(totalMeasured * 100.0 / master.getTime()) + " % measured)");
		return result.toString();
	}
	private static void repeat(StringBuilder sb, String s, int times)
	{
	    if (times > 0)
	        repeat(sb.append(s), s, times - 1);
	}
	private static String repeat(String s, int times)
	{
	    StringBuilder sb = new StringBuilder(s.length() * times);
	    repeat(sb, s, times);
	    return sb.toString();
	}
	
	static class TimerInstance
	{
		private String name;
		public TimerInstance(String name) { this.name = name; }
		
		private long time = 0;
		@Override
		public String toString() { return NumberFormat.getNumberInstance().format(getTime()); }
		public long getTime()
		{
			if (running)
				throw new RuntimeException(name+" not stopped");
			return time;
		}
		private boolean running = false;
		public void start()
		{
			if (running)
				throw new RuntimeException("already running");
			running = true;
			time -= System.nanoTime();
		}
		public void stop()
		{
			if (!running)
				throw new RuntimeException("not running");
			time += System.nanoTime();
			running = false;
		}
	}
}
