package com.thoughtworks.inproctester.jetty;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UrlHelperTest {

    @Test
    public void shouldExtractPathWithQueryAndFragment() throws URISyntaxException {
        assertThat(UrlHelper.getRequestPath(new URI("http://localhost:8902/status?ln=test#help")), is("/status?ln=test#help"));
    }

    @Test
    public void shouldExtractPathWithQuery() throws URISyntaxException {
        assertThat(UrlHelper.getRequestPath(new URI("http://localhost:8902/status?ln=test")), is("/status?ln=test"));
    }

}
