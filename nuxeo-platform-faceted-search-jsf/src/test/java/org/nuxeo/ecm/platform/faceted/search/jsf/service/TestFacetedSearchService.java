/*
 * (C) Copyright 2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.platform.faceted.search.jsf.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.ConfigConstants.F_SEARCH_CONFIGURATION_ALLOWED_CONTENT_VIEWS;
import static org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.ConfigConstants.F_SEARCH_CONFIGURATION_DENIED_CONTENT_VIEWS;
import static org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.ConfigConstants.F_SEARCH_CONFIGURATION_FACET;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.localconfiguration.LocalConfigurationService;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.RepositorySettings;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;
import org.nuxeo.ecm.platform.contentview.jsf.ContentViewService;
import org.nuxeo.ecm.platform.faceted.search.api.Constants;
import org.nuxeo.ecm.platform.faceted.search.api.service.FacetedSearchService;
import org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.FacetedSearchConfiguration;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.platform.ui.web.jsf.MockFacesContext;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.ecm.platform.content.template", "org.nuxeo.ecm.platform.userworkspace.api",
        "org.nuxeo.ecm.platform.userworkspace.types", "org.nuxeo.ecm.platform.userworkspace.core",
        "org.nuxeo.ecm.platform.dublincore", "org.nuxeo.ecm.platform.faceted.search.jsf",
        "org.nuxeo.ecm.platform.query.api", "org.nuxeo.ecm.platform.contentview.jsf:OSGI-INF/contentview-framework.xml" })
@LocalDeploy({ "org.nuxeo.ecm.platform.faceted.search.jsf:test-faceted-search-contentviews-contrib.xml",
        "org.nuxeo.ecm.platform.faceted.search.jsf:test-faceted-search-core-types-contrib.xml" })
public class TestFacetedSearchService {

    public static final String FACETED_SEARCH_DEFAULT_CONTENT_VIEW_NAME = "faceted_search_default";

    public static final String FACETED_SEARCH_DEFAULT_DOCUMENT_TYPE = "FacetedSearchDefault";

    private static final Log log = LogFactory.getLog(TestFacetedSearchService.class);

    @Inject
    protected CoreSession session;

    @Inject
    protected FacetedSearchService facetedSearchService;

    @Inject
    protected ContentViewService contentViewService;

    @Inject
    protected UserWorkspaceService userWorkspaceService;

    @Inject
    protected LocalConfigurationService localConfigurationService;

    @Inject
    protected FeaturesRunner featuresRunner;

    protected MockFacesContext facesContext;

    @Before
    public void initializeFacesContext() {
        // set mock faces context for needed properties resolution
        facesContext = new MockFacesContext();
        facesContext.mapVariable("documentManager", session);
        facesContext.setCurrent();
        assertNotNull(FacesContext.getCurrentInstance());
    }

    @Test
    public void serviceRegistration() {
        assertNotNull(facetedSearchService);
        assertNotNull(localConfigurationService);
    }

    @Test
    public void retrieveFacetedSearchRelatedContentViews() throws ClientException {
        Set<String> contentViewNames = facetedSearchService.getContentViewNames();
        assertNotNull(contentViewNames);
        assertEquals(2, contentViewNames.size());

        assertTrue(contentViewNames.contains("faceted_search_default"));
        assertTrue(contentViewNames.contains("faceted_search_other"));
        assertFalse(contentViewNames.contains("not_a_faceted_search"));
    }

    @Test
    public void saveFacetedSearch() throws ClientException {
        DocumentModel savedSearch = createSavedSearch(session, "My saved search");
        assertNotNull(savedSearch);
        assertEquals("fulltext", savedSearch.getPropertyValue("fsd:ecm_fulltext"));
        assertEquals(FACETED_SEARCH_DEFAULT_CONTENT_VIEW_NAME,
                savedSearch.getPropertyValue(Constants.FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY));
        assertEquals("My saved search", savedSearch.getPropertyValue("dc:title"));

        // Check that the search is saved in the user workspace
        String savedSearchPath = savedSearch.getPathAsString();
        DocumentModel uws = userWorkspaceService.getCurrentUserPersonalWorkspace(session, null);
        assertTrue(savedSearchPath.startsWith(uws.getPathAsString()));

        // Check that the search is saved in the configured folder
        assertEquals(savedSearchPath, "/default-domain/UserWorkspaces/Administrator/My saved search");
    }

    protected DocumentModel createSavedSearch(CoreSession session, String title) throws ClientException {
        ContentView contentView = contentViewService.getContentView(FACETED_SEARCH_DEFAULT_CONTENT_VIEW_NAME);
        DocumentModel searchDocumentModel = session.createDocumentModel(FACETED_SEARCH_DEFAULT_DOCUMENT_TYPE);
        searchDocumentModel.setPropertyValue("fsd:ecm_fulltext", "fulltext");
        contentView.setSearchDocumentModel(searchDocumentModel);

        return facetedSearchService.saveSearch(session, contentView, title);
    }

    @Test
    public void getCurrentUserSavedSearches() throws ClientException {
        DocumentModel firstSavedSearch = createSavedSearch(session, "First saved search");
        DocumentModel secondSavedSearch = createSavedSearch(session, "Second saved search");

        List<DocumentModel> userSavedSearches = facetedSearchService.getCurrentUserSavedSearches(session);
        assertEquals(2, userSavedSearches.size());
        assertTrue(userSavedSearches.contains(firstSavedSearch));
        assertTrue(userSavedSearches.contains(secondSavedSearch));
    }

    @Test
    public void defineLocalConfiguration() throws ClientException {
        DocumentModel confWorkspace = session.createDocumentModel("Workspace");
        confWorkspace.addFacet(F_SEARCH_CONFIGURATION_FACET);
        confWorkspace.setPropertyValue(F_SEARCH_CONFIGURATION_ALLOWED_CONTENT_VIEWS,
                new String[] { "FOO_CV", "BAR_CV" });
        confWorkspace.setPropertyValue(F_SEARCH_CONFIGURATION_DENIED_CONTENT_VIEWS,
                new String[] { "JOHN_CV", "DOE_CV" });
        confWorkspace.setPathInfo("/", "confWorkspace");
        confWorkspace = session.createDocument(confWorkspace);

        DocumentModel fooFile = session.createDocumentModel(confWorkspace.getPathAsString(), "fooooo", "File");
        fooFile = session.createDocument(fooFile);

        FacetedSearchConfiguration conf = localConfigurationService.getConfiguration(FacetedSearchConfiguration.class,
                F_SEARCH_CONFIGURATION_FACET, fooFile);

        assertNotNull(conf);
        List<String> allowed = conf.getAllowedContentViewNames();
        assertTrue(allowed.contains("FOO_CV"));
        assertTrue(allowed.contains("BAR_CV"));

        List<String> denied = conf.getDeniedContentViewNames();
        assertTrue(denied.contains("JOHN_CV"));
        assertTrue(denied.contains("DOE_CV"));

        Set<String> test1 = new HashSet<String>(Arrays.asList(new String[] { "JOHN_CV", "DOE_CV", "GOOD", "BAR_CV" }));
        Set<String> filtered = conf.filterAllowedContentViewNames(test1);
        assertTrue(filtered.contains("BAR_CV"));
        assertFalse(filtered.contains("JOHN_CV"));
        assertFalse(filtered.contains("DOE_CV"));
        assertFalse(filtered.contains("GOOD"));
    }

    @Test
    public void getOtherUsersSavedSearches() throws ClientException {
        DocumentModel firstSavedSearch = createSavedSearch(session, "First saved search");
        DocumentModel secondSavedSearch = createSavedSearch(session, "Second saved search");
        DocumentModel thirdSavedSearch;
        try (CoreSession session = changeUser("user1")) {
            thirdSavedSearch = createSavedSearch(session, "Third saved search");

            // user1 should see no other saved searches
            List<DocumentModel> otherUsersSavedSearches = facetedSearchService.getOtherUsersSavedSearches(session);
            assertTrue(otherUsersSavedSearches.isEmpty());
        }

        // Administrator should see the user1 saved search
        try (CoreSession session = changeUser("Administrator")) {
            List<DocumentModel> otherUsersSavedSearches = facetedSearchService.getOtherUsersSavedSearches(session);
            assertEquals(1, otherUsersSavedSearches.size());
            assertFalse(otherUsersSavedSearches.contains(firstSavedSearch));
            assertFalse(otherUsersSavedSearches.contains(secondSavedSearch));
            assertTrue(otherUsersSavedSearches.contains(thirdSavedSearch));
        }
    }

    @Inject
    RepositorySettings repository;

    protected CoreSession changeUser(String username) throws ClientException {
        return repository.openSessionAs(username);
    }
}
