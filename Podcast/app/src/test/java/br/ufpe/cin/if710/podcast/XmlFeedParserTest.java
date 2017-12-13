package br.ufpe.cin.if710.podcast;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;

/**
 * Created by Ricardo R Barioni on 08/12/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(XmlFeedParser.class)
public class XmlFeedParserTest {

    private static XmlPullParser mockParser() throws XmlPullParserException {
        XmlPullParserFactory factory = mock(XmlPullParserFactory.class);
        XmlPullParser xpp = mock(XmlPullParser.class);
        when(factory.newPullParser()).thenReturn(xpp);
        return xpp;
    }

    @Test
    public void readEnclosure_returnsCorrectUrl() throws XmlPullParserException, IOException {
        String expected = "https://hpbl.github.io/hub42_APS/audio/Oi%20Tudo%20Bom.mp3";

        XmlPullParser xpp = mockParser();
        when(xpp.getAttributeValue(null, "url")).thenReturn(expected);
        String actual = XmlFeedParser.readEnclosure(xpp);
        assertEquals(expected, actual);
    }

    @Test
    public void readText_returnsExpectedText() throws XmlPullParserException, IOException {
        String expected = "Texto teste";

        XmlPullParser xpp = mockParser();
        when(xpp.next()).thenReturn(XmlPullParser.TEXT);
        when(xpp.getText()).thenReturn(expected);

        String actual = XmlFeedParser.readText(xpp);
        assertEquals(expected, actual);
    }

    @Test
    public void readData_returnsCorrectTitle() throws XmlPullParserException, IOException {
        String expected = "Oi tudo bom com você";

        XmlPullParser xpp = mockParser();
        PowerMockito.stub(PowerMockito.method(XmlFeedParser.class, "readText")).toReturn(expected);

        String actual = XmlFeedParser.readData(xpp, "title");
        assertEquals(expected, actual);
    }

    @Test
    public void readData_returnsCorrectGuid() throws XmlPullParserException, IOException {
        String expected = "http://frontdaciencia.ufrgs.br/#1";

        XmlPullParser xpp = mockParser();
        PowerMockito.stub(PowerMockito.method(XmlFeedParser.class, "readText")).toReturn(expected);

        String actual = XmlFeedParser.readData(xpp, "guid");
        assertEquals(expected, actual);
    }

    @Test
    public void readData_returnsCorrectPubDate() throws XmlPullParserException, IOException {
        String expected = "Sun, 20 Jun 2010 10:40:05 GMT";

        XmlPullParser xpp = mockParser();
        PowerMockito.stub(PowerMockito.method(XmlFeedParser.class, "readText")).toReturn(expected);

        String actual = XmlFeedParser.readData(xpp, "pubDate");
        assertEquals(expected, actual);
    }

    @Test
    public void readData_returnsCorrectDescription() throws XmlPullParserException, IOException {
        String expected = "Programa 1";

        XmlPullParser xpp = mockParser();
        PowerMockito.stub(PowerMockito.method(XmlFeedParser.class, "readText")).toReturn(expected);

        String actual = XmlFeedParser.readData(xpp, "description");
        assertEquals(expected, actual);
    }

}
