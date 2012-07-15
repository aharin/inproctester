package com.thoughtworks.inproctester.jetty;

import com.thoughtworks.inproctester.core.UrlHelper;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UrlHelperTest {

    @Test
    public void shouldExtractPathWithQueryAndFragment() throws URISyntaxException {
        assertThat(UrlHelper.getRequestPath(new URI("http://localhost:8902/status?ln=test%20value#help%20page")), is("/status?ln=test%20value#help%20page"));
    }

    @Test
    public void shouldExtractPathWithQuery() throws URISyntaxException {
        assertThat(UrlHelper.getRequestPath(new URI("http://localhost:8902/status?ln=test%20value")), is("/status?ln=test%20value"));
    }

    @Test
    public void shouldCorrectlyEscapeSpacesInPath() throws Exception {
        assertThat(UrlHelper.getRequestPath(new URI("http://localhost:8902/some%20path?ln=test")), is("/some%20path?ln=test"));
    }
}
