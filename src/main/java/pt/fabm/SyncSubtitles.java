package pt.fabm;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SyncSubtitles {

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private Integer correspondentHours;
    private Integer correspondentMinutes;
    private Integer correspondentSeconds;
    private Integer correspondentMillis;
    private Integer realHours;
    private Integer realMinutes;
    private Integer realSeconds;
    private Integer realMillis;

    public static class Builder {
        private PrintWriter printWriter;
        private BufferedReader bufferedReader;
        private Integer correspondentHours;
        private Integer correspondentMinutes;
        private Integer correspondentSeconds;
        private Integer correspondentMillis;
        private Integer realHours;
        private Integer realMinutes;
        private Integer realSeconds;
        private Integer realMillis;

        public Builder withCorrespondentTime(Integer hours, Integer minutes, Integer seconds, Integer millis){
            this.correspondentHours = hours;
            this.correspondentMinutes = minutes;
            this.correspondentSeconds = seconds;
            this.correspondentMillis = millis;
            return this;
        }

        public Builder withRealTime(Integer hours, Integer minutes, Integer seconds, Integer millis) {
            this.realHours = hours;
            this.realMinutes = minutes;
            this.realSeconds = seconds;
            this.realMillis = millis;
            return this;
        }


        public Builder withReader(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
            return this;
        }

        public Builder withPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
            return this;
        }

        private static void check(String name, Object value) {
            if (Objects.isNull(value)) {
                throw new NoSuchElementException("No value present in " + name + " field");
            }
        }

        public SyncSubtitles build() {

            check("printWriter", printWriter);
            check("bufferedReader", bufferedReader);
            check("correspondentHours", correspondentHours);
            check("correspondentMinutes", correspondentMinutes);
            check("correspondentSeconds", correspondentSeconds);
            check("correspondentMillis", correspondentMillis);
            check("realHours", realHours);
            check("realMinutes", realMinutes);
            check("realSeconds", realSeconds);
            check("realMillis", realMillis);

            SyncSubtitles syncSubtitles = new SyncSubtitles();
            syncSubtitles.printWriter = printWriter;
            syncSubtitles.bufferedReader = bufferedReader;

            syncSubtitles.correspondentHours = correspondentHours;
            syncSubtitles.correspondentMinutes = correspondentMinutes;
            syncSubtitles.correspondentSeconds = correspondentSeconds;
            syncSubtitles.correspondentMillis = correspondentMillis;

            syncSubtitles.realHours = realHours;
            syncSubtitles.realMinutes = realMinutes;
            syncSubtitles.realSeconds = realSeconds;
            syncSubtitles.realMillis = realMillis;

            return syncSubtitles;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public void sync() throws IOException {

        long initial = LocalDateTime
                .of(0, 1, 1, 0, 0, 00, 000000000)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long real = LocalDateTime
                .of(0, 1, 1, realHours, realMinutes, realSeconds, realMillis * 1000000)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long correspondente = LocalDateTime
                .of(0, 1, 1, correspondentHours, correspondentMinutes, correspondentSeconds, correspondentMillis * 1000000)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long diff = real - correspondente;
        long timePassed = real - initial;
        double ratio = (diff * 1d) / (timePassed * 1d);

        System.out.println(ratio);
        System.out.println(timePassed * ratio);

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {

            if (line.contains("-->")) {
                try {
                    int h = Integer.parseInt(line.substring(0, 2));
                    int m = Integer.parseInt(line.substring(3, 5));
                    int s = Integer.parseInt(line.substring(6, 8));
                    int ms = Integer.parseInt(line.substring(9, 12));

                    correspondente = LocalDateTime
                            .of(0, 1, 1, h, m, s, ms * 1000000)
                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                    long toAdd = Math.round((correspondente - initial) * ratio);

                    h = Integer.parseInt(line.substring(17, 19));
                    m = Integer.parseInt(line.substring(20, 22));
                    s = Integer.parseInt(line.substring(23, 25));
                    ms = Integer.parseInt(line.substring(26, 29));

                    LocalDateTime nd1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(correspondente + toAdd), ZoneId.systemDefault());

                    LocalDateTime nd2 = LocalDateTime.of(0, 1, 1, h, m, s, ms * 1000000)
                            .plus(toAdd, ChronoUnit.MILLIS);


                    printWriter.printf("%02d:%02d:%02d,%03d --> %02d:%02d:%02d,%03d\n",
                            nd1.getHour(), nd1.getMinute(), nd1.getSecond(), nd1.getNano() / 1000000,
                            nd2.getHour(), nd2.getMinute(), nd2.getSecond(), nd2.getNano() / 1000000
                    );


                } catch (NumberFormatException nfe) {
                    throw new IllegalStateException("Problem to parse line:" + line);
                }
            } else {
                printWriter.println(line);
            }
        }
    }
}
