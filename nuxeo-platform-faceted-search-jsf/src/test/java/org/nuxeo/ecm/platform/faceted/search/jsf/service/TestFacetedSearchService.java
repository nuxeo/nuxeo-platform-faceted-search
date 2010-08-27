package org.nuxeo.ecm.platform.faceted.search.jsf.service;

import java.util.List;
import java.util.Set;

import javax.el.ELException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.RepositorySettings;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.faceted.search.jsf.Constants;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentView;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentViewService;
import org.nuxeo.ecm.platform.ui.web.jsf.MockFacesContext;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
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
@RepositoryConfig(type = BackendType.H2, init = DefaultRepositoryInit.class, user = "Administrator", cleanup = Granularity.METHOD)
@Deploy( { "org.nuxeo.ecm.platform.content.template",
        "org.nuxeo.ecm.platform.userworkspace.api",
        "org.nuxeo.ecm.platform.userworkspace.types",
        "org.nuxeo.ecm.platform.userworkspace.core",
        "org.nuxeo.ecm.platform.faceted.search.jsf",
        "org.nuxeo.ecm.platform.ui:OSGI-INF/contentview-framework.xml" })
@LocalDeploy( {
        "org.nuxeo.ecm.platform.faceted.search.jsf:test-faceted-search-contentviews-contrib.xml",
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
    protected FeaturesRunner featuresRunner;

    protected MockFacesContext facesContext;

    @Before
    public void initializeFacesContext() {
        // set mock faces context for needed properties resolution
        facesContext = new MockFacesContext() {
            @Override
            public Object evaluateExpressionGet(FacesContext context,
                    String expression, Class expectedType) throws ELException {
                if ("#{documentManager}".equals(expression)) {
                    return session;
                } else {
                    log.error("Cannot evaluate expression: " + expression);
                }
                return null;
            }
        };
        facesContext.setCurrent();
        assertNotNull(FacesContext.getCurrentInstance());
    }

    @Test
    public void serviceRegistration() {
        assertNotNull(facetedSearchService);
    }

    @Test
    public void retrieveFacetedSearchRelatedContentViews()
            throws ClientException {
        Set<String> contentViewNames = facetedSearchService.getContentViewNames();
        assertNotNull(contentViewNames);
        assertEquals(2, contentViewNames.size());

        assertTrue(contentViewNames.contains("faceted_search_default"));
        assertTrue(contentViewNames.contains("faceted_search_other"));
        assertFalse(contentViewNames.contains("not_a_faceted_search"));
    }

    @Test
    public void saveFacetedSearch() throws ClientException {
        DocumentModel savedSearch = createSavedSearch("My saved search");
        assertNotNull(savedSearch);
        assertEquals("fulltext",
                savedSearch.getPropertyValue("fsd:ecm_fulltext"));
        assertEquals(
                FACETED_SEARCH_DEFAULT_CONTENT_VIEW_NAME,
                savedSearch.getPropertyValue(Constants.FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY));
        assertEquals("My saved search",
                savedSearch.getPropertyValue("dc:title"));

        // Check that the search is saved in the user workspace
        String savedSearchPath = savedSearch.getPathAsString();
        DocumentModel uws = userWorkspaceService.getCurrentUserPersonalWorkspace(
                session, null);
        assertTrue(savedSearchPath.startsWith(uws.getPathAsString()));

        // Check that the search is saved in the configured folder
        FacetedSearchServiceImpl serviceImpl = (FacetedSearchServiceImpl) facetedSearchService;
        String rootSavedSearchesPath = new Path(uws.getPathAsString()).append(
                IdUtils.generateId(serviceImpl.configuration.getRootSavedSearchesTitle())).toString();
        assertTrue(savedSearchPath.startsWith(rootSavedSearchesPath));
    }

    protected DocumentModel createSavedSearch(String title)
            throws ClientException {
        ContentView contentView = contentViewService.getContentView(FACETED_SEARCH_DEFAULT_CONTENT_VIEW_NAME);
        DocumentModel searchDocumentModel = session.createDocumentModel(FACETED_SEARCH_DEFAULT_DOCUMENT_TYPE);
        searchDocumentModel.setPropertyValue("fsd:ecm_fulltext", "fulltext");
        contentView.setSearchDocumentModel(searchDocumentModel);

        return facetedSearchService.saveSearch(session, contentView, title);
    }

    @Test
    public void getCurrentUserSavedSearches() throws ClientException {
        DocumentModel firstSavedSearch = createSavedSearch("First saved search");
        DocumentModel secondSavedSearch = createSavedSearch("Second saved search");

        List<DocumentModel> userSavedSearches = facetedSearchService.getCurrentUserSavedSearches(session);
        assertEquals(2, userSavedSearches.size());
        assertTrue(userSavedSearches.contains(firstSavedSearch));
        assertTrue(userSavedSearches.contains(secondSavedSearch));
    }

    @Test
    public void getAllSavedSearches() throws ClientException {
        DocumentModel firstSavedSearch = createSavedSearch("First saved search");
        DocumentModel secondSavedSearch = createSavedSearch("Second saved search");
        changeUser("user1");
        DocumentModel thirdSavedSearch = createSavedSearch("Third saved search");

        // user1 should see its saved search
        List<DocumentModel> allSavedSearches = facetedSearchService.getAllSavedSearches(session);
        assertEquals(1, allSavedSearches.size());
        assertTrue(allSavedSearches.contains(thirdSavedSearch));

        // Administrator should see 3 saved searches
        changeUser("Administrator");
        allSavedSearches = facetedSearchService.getAllSavedSearches(session);
        assertEquals(3, allSavedSearches.size());
        assertTrue(allSavedSearches.contains(firstSavedSearch));
        assertTrue(allSavedSearches.contains(secondSavedSearch));
        assertTrue(allSavedSearches.contains(thirdSavedSearch));
    }

    protected void changeUser(String username) {
        CoreFeature coreFeature = featuresRunner.getFeature(CoreFeature.class);
        RepositorySettings repository = coreFeature.getRepository();
        repository.shutdown();
        repository.setUsername(username);
        session = repository.get();
    }

}
