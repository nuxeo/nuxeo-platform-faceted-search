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

import static org.nuxeo.ecm.platform.faceted.search.api.Constants.FACETED_SEARCH_FLAG;
import static org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.ConfigConstants.F_SEARCH_CONFIGURATION_FACET;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.localconfiguration.LocalConfigurationService;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;
import org.nuxeo.ecm.platform.contentview.jsf.ContentViewService;
import org.nuxeo.ecm.platform.faceted.search.api.Constants;
import org.nuxeo.ecm.platform.faceted.search.api.service.Configuration;
import org.nuxeo.ecm.platform.faceted.search.api.service.FacetedSearchService;
import org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.FacetedSearchConfiguration;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
public class FacetedSearchServiceImpl extends DefaultComponent implements FacetedSearchService {

    private static Log log = LogFactory.getLog(FacetedSearchServiceImpl.class);

    public static final String CONFIGURATION_EP = "configuration";

    protected Configuration configuration;

    protected ContentViewService contentViewService;

    protected PageProviderService pageProviderService;

    protected UserWorkspaceService userWorkspaceService;

    protected FacetedSearchConfiguration facetedSearchConfiguration;

    @Override
    public Set<String> getContentViewNames() throws ClientException {
        return getContentViewNames(null);
    }

    @Override
    public Set<String> getContentViewNames(DocumentModel currentDoc) throws ClientException {
        ContentViewService contentViewService = getContentViewService();
        return doFilterNames(contentViewService.getContentViewNames(FACETED_SEARCH_FLAG), currentDoc);
    }

    /**
     * Filter names depending of the local configuration, if none everything is returned
     *
     * @param contentViewNames
     * @return allowed contentviewNames or all if no local configuration
     */
    protected Set<String> doFilterNames(Set<String> contentViewNames, DocumentModel currentDoc) {
        FacetedSearchConfiguration fsConf = getFacetedConfiguration(currentDoc);
        return fsConf == null ? contentViewNames : fsConf.filterAllowedContentViewNames(contentViewNames);
    }

    /**
     * Try to get {@code localConfigurationService} and the associated {@code FacetedSearchConfiguration} to return the
     * instance
     *
     * @return null in case of any problem and the local configuration if everything goes well
     */
    protected FacetedSearchConfiguration getFacetedConfiguration(DocumentModel currentDoc) {
        LocalConfigurationService localConfigurationService = null;
        try {
            localConfigurationService = Framework.getService(LocalConfigurationService.class);
        } catch (Exception e) {
            final String errMsg = "Error connecting to LocalConfigurationService. " + e.getMessage();
            log.error(errMsg, e);
        }

        if (localConfigurationService == null) {
            log.warn("LocalConfigurationService service not bound");
            return null;
        } else {
            return localConfigurationService.getConfiguration(FacetedSearchConfiguration.class,
                    F_SEARCH_CONFIGURATION_FACET, currentDoc);
        }
    }

    protected ContentViewService getContentViewService() throws ClientException {
        if (contentViewService == null) {
            try {
                contentViewService = Framework.getService(ContentViewService.class);
            } catch (Exception e) {
                final String errMsg = "Error connecting to ContentViewService. " + e.getMessage();
                throw new ClientException(errMsg, e);
            }
            if (contentViewService == null) {
                throw new ClientException("ContentViewService service not bound");
            }
        }
        return contentViewService;
    }

    protected PageProviderService getPageProviderService() throws ClientException {
        if (pageProviderService == null) {
            try {
                pageProviderService = Framework.getService(PageProviderService.class);
            } catch (Exception e) {
                final String errMsg = "Error connecting to PageProviderService. " + e.getMessage();
                throw new ClientException(errMsg, e);
            }
            if (pageProviderService == null) {
                throw new ClientException("PageProviderService service not bound");
            }
        }
        return pageProviderService;
    }

    protected UserWorkspaceService getUserWorkspaceService() throws ClientException {
        if (userWorkspaceService == null) {
            try {
                userWorkspaceService = Framework.getService(UserWorkspaceService.class);
            } catch (Exception e) {
                final String errMsg = "Error connecting to UserWorkspaceService. " + e.getMessage();
                throw new ClientException(errMsg, e);
            }
            if (userWorkspaceService == null) {
                throw new ClientException("UserWorkspaceService service not bound");
            }
        }
        return userWorkspaceService;
    }

    @Override
    public DocumentModel saveSearch(CoreSession session, ContentView facetedSearchContentView, String title)
            throws ClientException {
        DocumentModel uws = getCurrentUserPersonalWorkspace(session);

        DocumentModel searchDoc = facetedSearchContentView.getSearchDocumentModel();
        searchDoc.setPropertyValue(Constants.FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY,
                facetedSearchContentView.getName());
        searchDoc.setPropertyValue("dc:title", title);
        PathSegmentService pathService = Framework.getLocalService(PathSegmentService.class);
        searchDoc.setPathInfo(uws.getPathAsString(), pathService.generatePathSegment(searchDoc));
        searchDoc = session.createDocument(searchDoc);
        session.save();
        return searchDoc;
    }

    @Override
    public List<DocumentModel> getCurrentUserSavedSearches(CoreSession session) throws ClientException {
        return getDocuments(Constants.CURRENT_USER_SAVED_SEARCHES_PAGE_PROVIDER_NAME, session,
                session.getPrincipal().getName());
    }

    protected DocumentModel getCurrentUserPersonalWorkspace(CoreSession session) throws ClientException {
        UserWorkspaceService userWorkspaceService = getUserWorkspaceService();
        return userWorkspaceService.getCurrentUserPersonalWorkspace(session, null);
    }

    @SuppressWarnings("unchecked")
    protected List<DocumentModel> getDocuments(String pageProviderName, CoreSession session, Object... parameters)
            throws ClientException {
        PageProviderService pageProviderService = getPageProviderService();
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("coreSession", (Serializable) session);
        return ((PageProvider<DocumentModel>) pageProviderService.getPageProvider(pageProviderName, null, null, null,
                properties, parameters)).getCurrentPage();

    }

    @Override
    public List<DocumentModel> getOtherUsersSavedSearches(CoreSession session) throws ClientException {
        return getDocuments(Constants.OTHER_USERS_SAVED_SEARCHES_PAGE_PROVIDER_NAME, session,
                session.getPrincipal().getName());
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (CONFIGURATION_EP.equals(extensionPoint)) {
            Configuration desc = (Configuration) contribution;
            if (configuration != null) {
                desc = mergeConfigurationDescriptor(configuration, desc);
            }
            configuration = desc;
        }
    }

    protected Configuration mergeConfigurationDescriptor(Configuration oldDesc, Configuration newDesc) {
        if (newDesc.getRootSavedSearchesTitle() != null) {
            oldDesc.setRootSavedSearchesTitle(newDesc.getRootSavedSearchesTitle());
        }
        return oldDesc;
    }

}
