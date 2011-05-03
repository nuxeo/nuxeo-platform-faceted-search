/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

package org.nuxeo.ecm.platform.faceted.search.jsf;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.nuxeo.ecm.platform.faceted.search.api.Constants.FACETED_SEARCH_FLAG;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.contentview.jsf.ContentViewHeader;
import org.nuxeo.ecm.platform.contentview.jsf.ContentViewService;
import org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.ConfigConstants;
import org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.FacetedSearchConfiguration;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

/**
 * Handle local configuration related actions.
 *
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @since 5.4.2
 */
@Name("facetedSearchConfigurationActions")
@Scope(CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class FacetedSearchConfigurationActions implements Serializable {

    protected Set<String> registeredContentView;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected transient ContentViewService contentViewService;

    public Set<ContentViewHeader> getNotSelectedContentViewsHeader() throws Exception {
        DocumentModel currentDoc = navigationContext.getCurrentDocument();
        if (!currentDoc.hasFacet(ConfigConstants.F_SEARCH_CONFIGURATION_FACET)) {
            return Collections.emptySet();
        }

        List<String> allowedContentView = getAllowedContentViewsName(currentDoc);
        Set<ContentViewHeader> notAllowedContentView = new HashSet<ContentViewHeader>();
        for (String cvName : getRegisteredContentViews()) {
            if (!allowedContentView.contains(cvName)) {
                notAllowedContentView.add(contentViewService.getContentViewHeader(
                        cvName));
            }
        }

        return notAllowedContentView;
    }

    public Set<ContentViewHeader> getSelectedContentViewsHeader() throws Exception {
        DocumentModel currentDoc = navigationContext.getCurrentDocument();
        if (!currentDoc.hasFacet(ConfigConstants.F_SEARCH_CONFIGURATION_FACET)) {
            return Collections.emptySet();
        }
        return getContentViewsHeader(getAllowedContentViewsName(currentDoc));
    }

    protected List<String> getAllowedContentViewsName(DocumentModel doc) {
        FacetedSearchConfiguration adapter = doc.getAdapter(FacetedSearchConfiguration.class);
        if (adapter == null) {
            return Collections.emptyList();
        }

        return adapter.getAllowedContentViewNames();
    }

    protected Set<ContentViewHeader> getContentViewsHeader(
            Collection<String> contentViewsNames) throws Exception {
        Set<ContentViewHeader> contentViews = new HashSet<ContentViewHeader>();
        for (String name : contentViewsNames) {
            contentViews.add(contentViewService.getContentViewHeader(name));
        }
        return contentViews;
    }

    protected Set<String> getRegisteredContentViews() throws Exception {
        if (registeredContentView == null) {
            registeredContentView = Framework.getService(
                    ContentViewService.class).getContentViewNames(
                    FACETED_SEARCH_FLAG);
        }
        return registeredContentView;
    }
}
