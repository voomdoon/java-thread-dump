package de.voomdoon.java.threaddump.model;

import java.util.List;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public record ThreadHistory(ThreadMetadata metadata, List<Snapshot> snapshots) {

}
