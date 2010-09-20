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

import java.util.List;
import java.util.Set;

import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PageProvider;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.faceted.search.api.Constants;
import org.nuxeo.ecm.platform.faceted.search.api.service.FacetedSearchService;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentView;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentViewService;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
public class FacetedSearchServiceImpl extends DefaultComponent implements
        FacetedSearchService {

    public static final String FACETED_SEARCH_FLAG = "FACETED_SEARCH";

    public static final String CONFIGURATION_EP = "configuration";

    protected Configuration configuration;

    protected ContentViewService contentViewService;

    protected UserWorkspaceService userWorkspaceService;

    public Set<String> getContentViewNames() throws ClientException {
        ContentViewService contentViewService = getContentViewService();
        return contentViewService.getContentViewNames(FACETED_SEARCH_FLAG);
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
        return getDocuments(
                Constants.CURRENT_USER_SAVED_SEARCHES_CONTENT_VIEW_NAME,
                session.getPrincipal().getName());
    }

    protected DocumentModel getCurrentUserPersonalWorkspace(CoreSession session)
            throws ClientException {
        UserWorkspaceService userWorkspaceService = getUserWorkspaceService();
        return userWorkspaceService.getCurrentUserPersonalWorkspace(session,
                null);
    }

    @SuppressWarnings("unchecked")
    protected List<DocumentModel> getDocuments(String contentViewName,
            Object... parameters) throws ClientException {
        ContentViewService contentViewService = getContentViewService();
        ContentView contentView = contentViewService.getContentView(contentViewName);
        contentView.resetPageProvider();
        return ((PageProvider<DocumentModel>) contentView.getPageProviderWithParams(parameters)).getCurrentPage();
    }

    public List<DocumentModel> getOtherUsersSavedSearches(CoreSession session)
            throws ClientException {
        return getDocuments(
                Constants.ALL_SAVED_SEARCHES_CONTENT_VIEW_NAME,
                session.getPrincipal().getName());
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
