package org.nuxeo.ecm.platform.faceted.search.jsf.service;

import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentView;

/**
 * Service handling faceted searches and related saved searches.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
public interface FacetedSearchService {

    /**
     * Returns the list of Content view names associated to a Faceted Search.
     * @throws ClientException in case of any error
     */
    Set<String> getContentViewNames() throws ClientException;

    /**
     * Save the current Faceted search in the user workspace with the given title.
     * @param session the {@code CoreSession} to use
     * @param facetedSearchContentView the Faceted Search to save
     * @param title the title of the being saved Faceted Search
     * @return the saved Faceted Search DocumentModel
     * @throws ClientException in case of any error during the save
     */
    public DocumentModel saveSearch(CoreSession session,
            ContentView facetedSearchContentView, String title)
            throws ClientException;

    /**
     * Returns the current user saved Faceted Searches, located into its own user workspace.
     * @param session the {@code CoreSession} to use
     * @throws ClientException in case of any error
     */
    public List<DocumentModel> getCurrentUserSavedSearches(CoreSession session) throws ClientException;

    /**
     * Returns all the accessible saved Faceted Searches
     * @param session the {@code CoreSession} to use
     * @throws ClientException in case of any error
     */
    public List<DocumentModel> getAllSavedSearches(CoreSession session) throws ClientException;

}
