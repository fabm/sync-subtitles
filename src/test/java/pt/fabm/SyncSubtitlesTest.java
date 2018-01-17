package pt.fabm;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;

public class SyncSubtitlesTest {
    @Test
    public void testSubtitlesSync() throws IOException, URISyntaxException {
        InputStream subtitle = SyncSubtitles.class.getResourceAsStream("/subtitle.srt");
        BufferedReader br = new BufferedReader(new InputStreamReader(subtitle, StandardCharsets.ISO_8859_1));

        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);

        SyncSubtitles syncSubtitles = SyncSubtitles
                .builder()
                .withReader(br)
                .withPrintWriter(pw)
                .withCorrespondentTime(0,0,25,0)
                .withRealTime(0,0,30,0)
                .build();

        syncSubtitles.sync();

        br.close();
        pw.close();

    }


}
