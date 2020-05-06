package org.silentsoft.actlist.console;

import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class AdvancedConsoleAppender extends ch.qos.logback.core.ConsoleAppender<ILoggingEvent> {

	@Override
	protected void append(ILoggingEvent event) {
		if (Console.getAdvancedStream() != null) {
			try {
				Console.getAdvancedStream().write(getEncoder().encode(event));
				Console.getAdvancedStream().flush();
			} catch (IOException e) {
				
			}
		}
	}

}
