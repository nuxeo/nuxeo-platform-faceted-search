package org.nuxeo.ecm.platform.faceted.search.jsf;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@RepositoryConfig(type = BackendType.H2, user = "Administrator")
@Deploy( { "org.nuxeo.ecm.platform.faceted.search.jsf",
        "org.nuxeo.ecm.platform.ui:OSGI-INF/contentview-framework.xml" })
@LocalDeploy( { "org.nuxeo.ecm.platform.faceted.search.jsf:test-faceted-search-contentviews-contrib.xml" })
public class TestFacetedSearchService {

    @Inject
    FacetedSearchService facetedSearchService;

    @Test
    public void serviceRegistration() {
        assertNotNull(facetedSearchService);
    }

    @Test
    public void retrieveFacetedSearchRelatedContentViews()
            throws ClientException {
        List<String> contentViewNames = facetedSearchService.getFacetedSearchContentViewNames();
        assertNotNull(contentViewNames);
        assertEquals(2, contentViewNames.size());

        assertTrue(contentViewNames.contains("faceted_search_default"));
        assertTrue(contentViewNames.contains("faceted_search_other"));
        assertFalse(contentViewNames.contains("not_a_faceted_search"));
    }

}
