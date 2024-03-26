package de.voomdoon.java.threaddump.model;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public record ThreadSnapshot(ThreadMetadata metadata, Snapshot snapshot) {

	// FEATURE - locked <0x0000000700600178> (a java.lang.ref.ReferenceQueue$Lock)

	// FEATURE - parking to wait for <0x00000007006005e8> (a
	// java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
}
