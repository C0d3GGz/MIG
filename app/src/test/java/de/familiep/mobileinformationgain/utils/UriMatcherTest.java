package de.familiep.mobileinformationgain.utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class UriMatcherTest {

    public Object[] noUri() {
        return new Object[]{
            new Object[]{" "},
            new Object[]{""},
            new Object[]{"hello"},
            new Object[]{"check out this link: www.hello.com/hi"}
        };
    }

    @Test
    @Parameters(method = "noUri")
    public void shouldReturnStringIfNoUri(String notAUri){
        String result = UriMatcher.getShortenedDomainIfUri(notAUri);
        assertEquals(notAUri, result);
    }

    public Object[] validUris() {
        return new Object[]{
            new Object[]{"http://www.mydomain.ltd.uk/blah/some/page.html", "www.mydomain.ltd.uk"},
            new Object[]{"https://google.com", "google.com"},
            new Object[]{"ftp://hello-world.cool/image.jpg", "hello-world.cool"},
            new Object[]{"http://www.hello.com/hi", "www.hello.com"},
            new Object[]{"regexr.com/foo.html?q=bar", "regexr.com"},
            new Object[]{"mediatemple.net", "mediatemple.net"},
            new Object[]{"www.google.de/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=Information", "www.google.de"},
            new Object[]{"www.google.com?search=hello", "www.google.com"},
            new Object[]{"www.google.de#webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=Information", "www.google.de"},
            new Object[]{"www.google.de?webhpsourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=Information", "www.google.de"}
        };
    }

    @Test
    @Parameters(method = "validUris")
    public void shouldReturnShortenedDomain(String uri, String shortenedUri){
        assertEquals(shortenedUri, UriMatcher.getShortenedDomainIfUri(uri));
    }
}