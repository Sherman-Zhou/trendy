package com.joinbe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Car Mng.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private TrendyConfig trendy;

    public TrendyConfig getTrendy() {
        return trendy;
    }

    public void setTrendy(TrendyConfig trendy) {
        this.trendy = trendy;
    }

    public static class TrendyConfig {

        private String url;

        private String vehiclePath;

        private String housekeepingJobCron;

        private String trajectoryBackupFolder;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVehiclePath() {
            return vehiclePath;
        }

        public void setVehiclePath(String vehiclePath) {
            this.vehiclePath = vehiclePath;
        }

        public String getHousekeepingJobCron() {
            return housekeepingJobCron;
        }

        public void setHousekeepingJobCron(String housekeepingJobCron) {
            this.housekeepingJobCron = housekeepingJobCron;
        }

        public String getTrajectoryBackupFolder() {
            return trajectoryBackupFolder;
        }

        public void setTrajectoryBackupFolder(String trajectoryBackupFolder) {
            this.trajectoryBackupFolder = trajectoryBackupFolder;
        }
    }
}
