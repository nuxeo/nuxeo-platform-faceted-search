package org.nuxeo.ecm.platform.faceted.search.jsf.service;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PageProvider;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.faceted.search.jsf.Constants;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentView;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentViewService;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
public class FacetedSearchServiceImpl extends DefaultComponent implements
        FacetedSearchService {

    public static final String FACETED_SEARCH_PREFIX = "faceted_search";

    public static final String CONFIGURATION_EP = "configuration";

    protected Configuration configuration;

    protected ContentViewService contentViewService;

    protected UserWorkspaceService userWorkspaceService;

    public List<String> getContentViewNames()
            throws ClientException {
        ContentViewService contentViewService = getContentViewService();
        List<String> contentViewNames = new ArrayList<String>();
        for (String contentViewName : contentViewService.getContentViewNames()) {
            if (contentViewName.startsWith(FACETED_SEARCH_PREFIX)) {
                contentViewNames.add(contentViewName);
            }
        }
        return contentViewNames;
    }

    protected ContentViewService getContentViewService() throws ClientException {
        if (contentViewService == null) {
            try {
                contentViewService = Framework.getService(ContentViewService.class);
            } catch (Exception e) {
                final String errMsg = "Error connecting to ContentViewService. "
                        + e.getMessage();
                throw new ClientException(errMsg, e);
            }
            if (contentViewService == null) {
                throw new ClientException(
                        "ContentViewService service not bound");
            }
        }
        return contentViewService;
    }

    protected UserWorkspaceService getUserWorkspaceService()
            throws ClientException {
        if (userWorkspaceService == null) {
            try {
                userWorkspaceService = Framework.getService(UserWorkspaceService.class);
            } catch (Exception e) {
                final String errMsg = "Error connecting to UserWorkspaceService. "
                        + e.getMessage();
                throw new ClientException(errMsg, e);
            }
            if (userWorkspaceService == null) {
                throw new ClientException(
                        "UserWorkspaceService service not bound");
            }
        }
        return userWorkspaceService;
    }

    public DocumentModel saveSearch(CoreSession session,
            ContentView facetedSearchContentView, String title)
            throws ClientException {
        DocumentModel uws = getCurrentUserPersonalWorkspace(session);

        String rootSavedSearchesTitle = configuration.getRootSavedSearchesTitle();
        String rootSavedSearchesName = IdUtils.generateId(rootSavedSearchesTitle);
        Path rootSavedSearchesPath = new Path(uws.getPathAsString()).append(rootSavedSearchesName);
        if (!session.exists(new PathRef(rootSavedSearchesPath.toString()))) {
            DocumentModel rootSavedSearches = session.createDocumentModel(
                    uws.getPathAsString(), rootSavedSearchesName, "Folder");
            rootSavedSearches.setPropertyValue("dc:title",
                    rootSavedSearchesTitle);
            session.createDocument(rootSavedSearches);
            session.save();
        }

        DocumentModel searchDoc = facetedSearchContentView.getSearchDocumentModel();
        searchDoc.setPropertyValue(
                Constants.FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY,
                facetedSearchContentView.getName());
        searchDoc.setPropertyValue("dc:title", title);
        searchDoc.setPathInfo(rootSavedSearchesPath.toString(),
                IdUtils.generateId(title));
        searchDoc = session.createDocument(searchDoc);
        session.save();
        return searchDoc;
    }

    public List<DocumentModel> getCurrentUserSavedSearches(CoreSession session)
            throws ClientException {
        DocumentModel uws = getCurrentUserPersonalWorkspace(session);
        List<DocumentModel> userSavedSearches = getDocuments(
                Constants.CURRENT_USER_SAVED_SEARCHES_CONTENT_VIEW_NAME,
                uws.getPathAsString());
        return userSavedSearches;
    }

    protected DocumentModel getCurrentUserPersonalWorkspace(CoreSession session)
            throws ClientException {
        UserWorkspaceService userWorkspaceService = getUserWorkspaceService();
        return userWorkspaceService.getCurrentUserPersonalWorkspace(session,
                null);
    }

    protected List<DocumentModel> getDocuments(String contentViewName,
            Object... parameters) throws ClientException {
        ContentViewService contentViewService = getContentViewService();
        ContentView contentView = contentViewService.getContentView(contentViewName);
        contentView.resetPageProvider();
        return ((PageProvider<DocumentModel>) contentView.getPageProviderWithParams(parameters)).getCurrentPage();
    }

    public List<DocumentModel> getAllSavedSearches(CoreSession session)
            throws ClientException {
        List<DocumentModel> allSavedSearches = getDocuments(Constants.ALL_SAVED_SEARCHES_CONTENT_VIEW_NAME);
        return allSavedSearches;
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (CONFIGURATION_EP.equals(extensionPoint)) {
            Configuration desc = (Configuration) contribution;
            if (configuration != null) {
                desc = mergeConfigurationDescriptor(configuration, desc);
            }
            configuration = desc;
        }
    }

    protected Configuration mergeConfigurationDescriptor(Configuration oldDesc,
            Configuration newDesc) {
        if (newDesc.getRootSavedSearchesTitle() != null) {
            oldDesc.rootSavedSearchesTitle = newDesc.getRootSavedSearchesTitle();
        }
        return oldDesc;
    }

}
