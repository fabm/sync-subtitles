package pt.fabm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class SyncSubtitles {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        File file = new File("/Volumes/NO NAME/The Blacklist S02E01 1080p/The.Blacklist.S02E06.1080p.srt");
        File file1 = new File("/Volumes/NO NAME/The Blacklist S02E01 1080p/The.Blacklist.S02E06.1080p_.srt");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");


        long initial = LocalDateTime
                .of(0, 1, 1, 0, 0, 00, 000000000)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long real = LocalDateTime
                .of(0, 1, 1, 0, 2, 30, 000000000)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long correspondente = LocalDateTime
                .of(0, 1, 1, 0, 2, 25, 000000000)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long diff = real - correspondente;
        long timePassed = real - initial;
        double ratio = (diff * 1d) / (timePassed * 1d);

        System.out.println(ratio);
        System.out.println(timePassed * ratio);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1))) {
                String line = null;

                while ((line = br.readLine()) != null) {

                    System.out.println(line);
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

                            System.out.printf("%02d:%02d:%02d,%03d --> %02d:%02d:%02d,%03d\n",
                                    nd1.getHour(),nd1.getMinute(),nd1.getSecond(),nd1.getNano()/1000000,
                                    nd2.getHour(),nd2.getMinute(),nd2.getSecond(),nd2.getNano()/1000000
                            );
                        } catch (NumberFormatException nfe) {
                            System.out.println("ex:" + line);
                        }
                    }
                }

                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
