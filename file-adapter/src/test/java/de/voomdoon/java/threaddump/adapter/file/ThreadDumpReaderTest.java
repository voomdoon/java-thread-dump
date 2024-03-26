package de.voomdoon.java.threaddump.adapter.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.voomdoon.java.threaddump.adapter.file.EmptyThreadDumpException;
import de.voomdoon.java.threaddump.adapter.file.ThreadDumpReader;
import de.voomdoon.java.threaddump.model.Snapshot;
import de.voomdoon.java.threaddump.model.ThreadDump;
import de.voomdoon.java.threaddump.model.ThreadMetadata;
import de.voomdoon.java.threaddump.model.ThreadSnapshot;
import de.voomdoon.testing.tests.TestBase;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since DOCME add inception version number
 */
class ThreadDumpReaderTest extends TestBase {

	/**
	 * @since DOCME add inception version number
	 */
	private ThreadDumpReader reader = new ThreadDumpReader();

	/**
	 * @since DOCME add inception version number
	 */
	@Test
	void testRead_error_EmptyThreadDumpException() throws Exception {
		logTestStart();

		Path file = Path.of("src/test/resources/empty.txt");

		assertThrows(EmptyThreadDumpException.class, () -> reader.read(file));
	}

	/**
	 * @since DOCME add inception version number
	 */
	@Test
	void testRead_multiple() throws Exception {
		logTestStart();

		ThreadDump actual = reader.read(Path.of("src/test/resources/multiple.txt"));
		List<ThreadSnapshot> actuals = actual.threads();

		assertThat(actuals).hasSize(2);
	}

	/**
	 * @since DOCME add inception version number
	 */
	@Test
	void testRead_systemThread() throws Exception {
		logTestStart();

		ThreadDump actual = reader.read(Path.of("src/test/resources/systemThread.txt"));
		List<ThreadSnapshot> actuals = actual.threads();

		assertThat(actuals).hasSize(2);
	}

	/**
	 * @since DOCME add inception version number
	 */
	@Test
	void testRead_thread_metadata_id() throws Exception {
		logTestStart();

		ThreadDump actual = reader.read(Path.of("src/test/resources/1.txt"));
		List<ThreadSnapshot> actuals = actual.threads();

		assertThat(actuals).satisfiesExactly(thread -> {
			assertThat(thread).extracting(ThreadSnapshot::metadata).extracting(ThreadMetadata::id)
					.isEqualTo("0x000001f93dd801b0");
		});
	}

	/**
	 * @since DOCME add inception version number
	 */
	@Test
	void testRead_thread_metadata_name() throws Exception {
		logTestStart();

		ThreadDump actual = reader.read(Path.of("src/test/resources/1.txt"));
		List<ThreadSnapshot> actuals = actual.threads();

		assertThat(actuals).satisfiesExactly(thread -> {
			assertThat(thread).extracting(ThreadSnapshot::metadata).extracting(ThreadMetadata::name).isEqualTo("main");
		});
	}

	/**
	 * @since DOCME add inception version number
	 */
	@Test
	void testRead_thread_snapshot_state() throws Exception {
		logTestStart();

		ThreadDump actual = reader.read(Path.of("src/test/resources/1.txt"));
		List<ThreadSnapshot> actuals = actual.threads();

		assertThat(actuals).satisfiesExactly(thread -> {
			assertThat(thread).extracting(ThreadSnapshot::snapshot).extracting(Snapshot::state)
					.isEqualTo(Thread.State.RUNNABLE);
		});
	}

	/**
	 * @since DOCME add inception version number
	 */
	@Test
	void testRead_thread_snapshot_state_WAITING_onObjectMonitor() throws Exception {
		logTestStart();

		ThreadDump actual = reader.read(Path.of("src/test/resources/waitingOnObjectMonitor.txt"));
		List<ThreadSnapshot> actuals = actual.threads();

		assertThat(actuals).satisfiesExactly(thread -> {
			assertThat(thread).extracting(ThreadSnapshot::snapshot).extracting(Snapshot::state)
					.isEqualTo(Thread.State.WAITING);
		});
	}

	/**
	 * @since DOCME add inception version number
	 */
	@Test
	void testRead_time() throws Exception {
		logTestStart();

		ThreadDump actual = reader.read(Path.of("src/test/resources/1.txt"));
		assertThat(actual).extracting(ThreadDump::time).isEqualTo(LocalDateTime.of(2024, 3, 7, 9, 32, 38));
	}
}
