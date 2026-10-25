package ratismal.drivebackup.config.configSections;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ratismal.drivebackup.util.Logger;

import static ratismal.drivebackup.config.Localization.intl;

public class BackupStorage {
    public final long delay;
    public final int threadPriority;
    public final int keepCount;
    public final int localKeepCount;
    public final boolean backupsRequirePlayers;
    public final boolean shouldIgnoreNpcs;
    public final boolean disableSavingDuringBackups;
    public final String localDirectory;
    public final String remoteDirectory;

    public BackupStorage(
        long delay, 
        int threadPriority, 
        int keepCount, 
        int localKeepCount,
        boolean backupsRequirePlayers,
        boolean shouldIgnoreNpcs,
        boolean disableSavingDuringBackups,
        String localDirectory,
        String remoteDirectory
        ) {

        this.delay = delay;
        this.threadPriority = threadPriority;
        this.keepCount = keepCount;
        this.localKeepCount = localKeepCount;
        this.backupsRequirePlayers = backupsRequirePlayers;
        this.shouldIgnoreNpcs = shouldIgnoreNpcs;
        this.disableSavingDuringBackups = disableSavingDuringBackups;
        this.localDirectory = localDirectory;
        this.remoteDirectory = remoteDirectory;
    }

    @NotNull
    @Contract ("_, _ -> new")
    public static BackupStorage parse(@NotNull FileConfiguration config, Logger logger) {
        Configuration defaultConfig = config.getDefaults();
        long delay = config.getLong("delay");
        if (delay < 5 && delay != -1) {
            logger.log(intl("invalid-backup-delay"));
            delay = defaultConfig.getLong("delay");
        }
        int threadPriority = config.getInt("backup-thread-priority");
        if (threadPriority < Thread.MIN_PRIORITY) {
            logger.log(intl("thread-priority-too-low"));
            threadPriority = Thread.MIN_PRIORITY;
        } else if (threadPriority > Thread.MAX_PRIORITY) {
            logger.log(intl("thread-priority-too-high"));
            threadPriority = Thread.MAX_PRIORITY;
        }
        int keepCount = config.getInt("keep-count");
        if (keepCount < 1 && keepCount != -1) {
            logger.log(intl("keep-count-invalid"));
            keepCount = defaultConfig.getInt("keep-count");
        }
        int localKeepCount = config.getInt("local-keep-count");
        if (localKeepCount < -1) {
            logger.log(intl("local-keep-count-invalid"));
            localKeepCount = defaultConfig.getInt("local-keep-count");
        }
        boolean backupsRequirePlayers = config.getBoolean("backups-require-players");
        boolean shouldIgnoreNpcs = config.getBoolean("should-ignore-npcs");
        boolean disableSavingDuringBackups = config.getBoolean("disable-saving-during-backups");
        String localDirectory = config.getString("local-save-directory");
        if (localDirectory.startsWith("/")) {
            logger.log(intl("local-save-directory-not-relative"));
            localDirectory = localDirectory.substring(1);
        }
        String remoteDirectory = config.getString("remote-save-directory");
        return new BackupStorage(delay, threadPriority, keepCount, localKeepCount, backupsRequirePlayers, shouldIgnoreNpcs, disableSavingDuringBackups, localDirectory, remoteDirectory);
    }
}
