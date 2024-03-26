package de.voomdoon.java.threaddump.adapter.file;

import java.io.IOException;
import java.lang.Thread.State;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.voomdoon.java.threaddump.model.Snapshot;
import de.voomdoon.java.threaddump.model.ThreadDump;
import de.voomdoon.java.threaddump.model.ThreadMetadata;
import de.voomdoon.java.threaddump.model.ThreadSnapshot;
import de.voomdoon.logging.LogManager;
import de.voomdoon.logging.Logger;

//FEATURE deadlocks

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since DOCME add inception version number
 */
public class ThreadDumpReader {

	/**
	 * @since DOCME add inception version number
	 */
	private final Logger logger = LogManager.getLogger(getClass());

	/**
	 * DOCME add JavaDoc for method read
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws EmptyThreadDumpException
	 * @since DOCME add inception version number
	 */
	public ThreadDump read(Path file) throws IOException, EmptyThreadDumpException {
		Queue<String> lines = new LinkedList<>(Files.readAllLines(file));

		if (lines.isEmpty()) {
			throw new EmptyThreadDumpException();
		}

		LocalDateTime time = LocalDateTime.parse(lines.poll().replace(' ', 'T'));

		consume(lines);

		List<ThreadSnapshot> threads = readThreads(lines);

		return new ThreadDump(time, threads);
	}

	/**
	 * DOCME add JavaDoc for method consume
	 * 
	 * @param lines
	 * @since DOCME add inception version number
	 */
	private void consume(Queue<String> lines) {
		lines.poll();// details
		lines.poll();// empty

		lines.poll();// list

		while (!lines.peek().equals("}")) {
			lines.poll();
		}

		lines.poll();// closing
		lines.poll();// empty
	}

	/**
	 * DOCME add JavaDoc for method getState
	 * 
	 * @param line
	 * @return
	 * @since DOCME add inception version number
	 */
	private State getState(String line) {
		if (line == null) {
			return State.RUNNABLE;// system thread
		}

		int start = line.indexOf(':') + 2;
		int end = line.indexOf(' ', start);

		if (end == -1) {
			end = line.length();
		}

		return State.valueOf(line.substring(start, end));
	}

	/**
	 * DOCME add JavaDoc for method getThreadLines
	 * 
	 * @param lines
	 * @return
	 * @since DOCME add inception version number
	 */
	private Queue<String> getThreadLines(Queue<String> lines) {
		Queue<String> result = new LinkedList<>();

		while (!lines.peek().isEmpty()) {
			result.add(lines.poll());
		}

		lines.poll();// empty

		return result;
	}

	/**
	 * DOCME add JavaDoc for method getValue
	 * 
	 * @param line
	 * @param key
	 * @return
	 * @since DOCME add inception version number
	 */
	private String getValue(String line, String key) {
		int begin = line.indexOf(key + "=");
		int end = line.indexOf(' ', begin);

		return line.substring(begin + 4, end);
	}

	/**
	 * DOCME add JavaDoc for method parseMetadata
	 * 
	 * @param poll
	 * @return
	 * @since DOCME add inception version number
	 */
	private ThreadMetadata parseMetadata(String line) {
		// TODO implement readThreadData
		String id = getValue(line, "tid");
		int end = line.indexOf('"', 1);

		if (end == -1) {
			// TESTME
			throw new IllegalArgumentException("Failed to parse name from '" + line + "'!");
		}

		String name = line.substring(1, end);

		return new ThreadMetadata(id, name);
	}

	/**
	 * DOCME add JavaDoc for method parseSnapshot
	 * 
	 * @param lines
	 * @return
	 * @since DOCME add inception version number
	 */
	private Snapshot parseSnapshot(Queue<String> lines) {
		Thread.State state = getState(lines.poll());
		// TODO implement parseSnapshot

		return new Snapshot(state);
	}

	/**
	 * DOCME add JavaDoc for method readThread
	 * 
	 * @param lines
	 * @param result
	 * @since DOCME add inception version number
	 */
	private void readThread(Queue<String> lines, List<ThreadSnapshot> result) {
		Queue<String> threadLines = getThreadLines(lines);

		ThreadSnapshot threadData = readThreadData(threadLines);
		logger.trace("thread: " + threadData.metadata().id());
		result.add(threadData);
	}

	/**
	 * DOCME add JavaDoc for method readThreadData
	 * 
	 * @param lines
	 * @return
	 * @since DOCME add inception version number
	 */
	private ThreadSnapshot readThreadData(Queue<String> lines) {
		ThreadMetadata metadata = parseMetadata(lines.poll());
		Snapshot snapshot = parseSnapshot(lines);

		return new ThreadSnapshot(metadata, snapshot);
	}

	/**
	 * DOCME add JavaDoc for method readThreads
	 * 
	 * @param lines
	 * @return
	 * @since DOCME add inception version number
	 */
	private List<ThreadSnapshot> readThreads(Queue<String> lines) {
		List<ThreadSnapshot> result = new ArrayList<>();

		while (!lines.isEmpty()) {
			if (!lines.peek().startsWith("JNI global refs")) {
				readThread(lines, result);
			} else {
				break;
			}
		}

		return result;
	}
}
