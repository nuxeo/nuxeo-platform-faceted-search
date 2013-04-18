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

package org.nuxeo.ecm.platform.faceted.search.api.service;

import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;

/**
 * Service handling faceted searches and related saved searches.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
public interface FacetedSearchService {

    /**
     * Returns the list of Content view names associated to a Faceted Search.
     *
     * @throws ClientException in case of any error
     */
    Set<String> getContentViewNames() throws ClientException;

    /**
     * Returns the list of Content view names associated to a Faceted Search and
     * depending of a local configuration.
     *
     * @param doc corresponds to current navigation context, to try to get some
     *            local configuration of a parent.
     * @throws ClientException in case of any error
     */
    Set<String> getContentViewNames(DocumentModel doc) throws ClientException;

    /**
     * Save the current Faceted search in the user workspace with the given
     * title.
     *
     * @param session the {@code CoreSession} to use
     * @param facetedSearchContentView the Faceted Search to save
     * @param title the title of the being saved Faceted Search
     * @return the saved Faceted Search DocumentModel
     * @throws ClientException in case of any error during the save
     */
    DocumentModel saveSearch(CoreSession session,
            ContentView facetedSearchContentView, String title)
            throws ClientException;

    /**
     * Returns the current user saved Faceted Searches, located into its own
     * user workspace.
     *
     * @param session the {@code CoreSession} to use
     * @throws ClientException in case of any error
     */
    List<DocumentModel> getCurrentUserSavedSearches(CoreSession session)
            throws ClientException;

    /**
     * Returns all the accessible saved Faceted Searches except the ones for the
     * current user.
     *
     * @param session the {@code CoreSession} to use
     * @throws ClientException in case of any error
     */
    List<DocumentModel> getOtherUsersSavedSearches(CoreSession session)
            throws ClientException;

    /**
     *
     *
     * @since 5.7
     */
     Configuration getConfiguration();

}
