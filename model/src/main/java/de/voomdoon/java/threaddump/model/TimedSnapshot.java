package de.voomdoon.java.threaddump.model;

import java.time.LocalDateTime;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public record TimedSnapshot(LocalDateTime time, Snapshot snapshot) {

}
