package org.silentsoft.actlist.console;

import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ConsoleAppender extends ch.qos.logback.core.ConsoleAppender<ILoggingEvent> {

	@Override
	protected void append(ILoggingEvent event) {
		if (Console.getConsoleStream() != null) {
			try {
				Console.getConsoleStream().write(getEncoder().encode(event));
				Console.getConsoleStream().flush();
			} catch (IOException e) {
				
			}
		}
	}

}
