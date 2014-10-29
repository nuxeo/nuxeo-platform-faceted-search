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
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.platform.faceted.search.dm;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.faceted.search.jsf.FacetedSearchActions;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.platform.ui.web.rest.RestHelper;
import org.nuxeo.ecm.platform.url.api.DocumentView;
import org.nuxeo.ecm.webapp.action.WebActionsBean;
import org.nuxeo.ecm.webapp.directory.DirectoryTreeDescriptor;
import org.nuxeo.ecm.webapp.tree.nav.MultiNavTreeManager;

/**
 * Handles faceted search permanent link loading.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
@Name("facetedSearchPermanentLinkActions")
@Scope(CONVERSATION)
@Install(precedence = FRAMEWORK)
public class FacetedSearchPermanentLinkActions implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create = true)
    protected RestHelper restHelper;

    @In(create = true)
    protected FacetedSearchActions facetedSearchActions;

    @In(create = true)
    protected WebActions webActions;

    @Begin(id = "#{conversationIdGenerator.currentOrNewMainConversationId}", join = true)
    public String loadPermanentLink(DocumentView docView)
            throws ClientException {
        restHelper.initContextFromRestRequest(docView);
        webActions.setCurrentTabId(DirectoryTreeDescriptor.NAV_ACTION_CATEGORY, "navtree_facetedSearch");
        return Constants.FACETED_SEARCH_RESULTS_VIEW;
    }

}
