package org.silentsoft.actlist.console;

import org.apache.log4j.spi.LoggingEvent;

public class ConsoleAppender extends org.apache.log4j.ConsoleAppender {
	
	@Override
	public void append(LoggingEvent event) {
		super.append(event);
		
		if (Console.getConsoleStream() != null) {
			Console.getConsoleStream().println(getLayout().format(event).trim());
		}
	}

}
