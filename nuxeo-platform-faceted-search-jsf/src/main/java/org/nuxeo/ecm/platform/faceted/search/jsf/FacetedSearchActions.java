package org.nuxeo.ecm.platform.faceted.search.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.faceted.search.jsf.service.FacetedSearchService;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentView;
import org.nuxeo.ecm.webapp.contentbrowser.ContentViewActions;
import org.nuxeo.ecm.webapp.helpers.EventNames;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;

/**
 * Handles faceted search related web actions.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
@Name("facetedSearchActions")
@Scope(CONVERSATION)
public class FacetedSearchActions implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NONE_VALUE = "none";

    public static final String NONE_LABEL = "label.none";

    public static final String USER_SAVED_SEARCHES_LABEL = "label.user.saved.searches";

    public static final String ALL_SAVED_SEARCHES_LABEL = "label.all.saved.searches";

    public static final String FACETED_SEARCH_RESULTS_VIEW = "faceted_search_results";

    @In(create = true)
    protected FacetedSearchService facetedSearchService;

    @In(create = true)
    protected ContentViewActions contentViewActions;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    protected List<String> contentViewNames;

    protected String currentContentViewName;

    protected String currentSelectedSavedSearchId;

    protected String savedSearchTitle;

    @Factory(value = "facetedSearchCurrentContentViewName", scope = EVENT)
    public String getCurrentContentViewName() throws ClientException {
        if (currentContentViewName == null) {
            List<String> contentViewNames = getContentViewNames();
            if (!contentViewNames.isEmpty()) {
                currentContentViewName = contentViewNames.get(0);
            }
        }
        return currentContentViewName;
    }

    public void setFacetedSearchCurrentContentViewName(
            String facetedSearchCurrentContentViewName) {
        this.currentContentViewName = facetedSearchCurrentContentViewName;
    }

    public List<String> getContentViewNames() throws ClientException {
        if (contentViewNames == null) {
            contentViewNames = facetedSearchService.getContentViewNames();
        }
        return contentViewNames;
    }

    /*
     * ----- Retrieving user and all saved searches -----
     */

    public List<DocumentModel> getCurrentUserSavedSearches()
            throws ClientException {
        return facetedSearchService.getCurrentUserSavedSearches(documentManager);
    }

    public List<DocumentModel> getAllSavedSearches() throws ClientException {
        return facetedSearchService.getAllSavedSearches(documentManager);
    }

    @Factory(value = "savedSearchesSelectItems", scope = ScopeType.EVENT)
    public List<SelectItem> getSavedSearchesSelectItems()
            throws ClientException {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(NONE_VALUE, NONE_LABEL, "",
                currentSelectedSavedSearchId == null));

        SelectItemGroup userGroup = new SelectItemGroup(
                USER_SAVED_SEARCHES_LABEL);
        List<DocumentModel> userSavedSearches = getCurrentUserSavedSearches();
        List<SelectItem> userSavedSearchesItems = convertToSelectItems(userSavedSearches);
        userGroup.setSelectItems(userSavedSearchesItems.toArray(new SelectItem[userSavedSearchesItems.size()]));
        items.add(userGroup);

        List<DocumentModel> allSavedFacetedSearches = getAllSavedSearches();
        allSavedFacetedSearches.removeAll(userSavedSearches);
        List<SelectItem> allSavedSearchesItems = convertToSelectItems(allSavedFacetedSearches);
        SelectItemGroup allGroup = new SelectItemGroup(ALL_SAVED_SEARCHES_LABEL);
        allGroup.setSelectItems(allSavedSearchesItems.toArray(new SelectItem[allSavedSearchesItems.size()]));
        items.add(allGroup);

        return items;
    }

    protected List<SelectItem> convertToSelectItems(List<DocumentModel> docs)
            throws ClientException {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (DocumentModel doc : docs) {
            items.add(new SelectItem(doc.getId(), doc.getTitle(), "",
                    doc.equals(currentSelectedSavedSearchId)));
        }
        return items;
    }

    /*
     * ----- Saving and loading a search -----
     */

    public void setCurrentSelectedSavedSearchId(String savedSearchId)
            throws ClientException {
        this.currentSelectedSavedSearchId = savedSearchId;

    }

    public String getCurrentSelectedSavedSearchId() {
        return currentSelectedSavedSearchId;
    }

    public String loadSavedSearch() throws ClientException {
        if (currentSelectedSavedSearchId == null
                || currentSelectedSavedSearchId.isEmpty()
                || NONE_VALUE.equals(currentSelectedSavedSearchId)) {
            contentViewActions.reset(currentContentViewName);
        } else {
            loadSavedSearch(currentSelectedSavedSearchId);
        }
        return FACETED_SEARCH_RESULTS_VIEW;
    }

    public String loadSavedSearch(String savedSearchId) throws ClientException {
        DocumentModel savedSearch = documentManager.getDocument(new IdRef(
                savedSearchId));
        String contentViewName = (String) savedSearch.getPropertyValue(Constants.FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY);
        return loadSavedSearch(contentViewName, savedSearch);
    }

    public String loadSavedSearch(String contentViewName,
            DocumentModel searchDocument) throws ClientException {
        if (contentViewName != null) {
            ContentView contentView = contentViewActions.getContentView(
                    contentViewName, searchDocument);
            if (contentView != null) {
                this.currentContentViewName = contentViewName;
            }
        }
        return FACETED_SEARCH_RESULTS_VIEW;
    }

    public String getSavedSearchTitle() {
        return savedSearchTitle;
    }

    public void setSavedSearchTitle(String savedSearchTitle) {
        this.savedSearchTitle = savedSearchTitle;
    }

    public void saveSearch() throws ClientException {
        ContentView contentView = contentViewActions.getContentView(currentContentViewName);
        if (contentView != null) {
            DocumentModel savedSearch = facetedSearchService.saveSearch(documentManager, contentView,
                    savedSearchTitle);
            savedSearchTitle = null;
            Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
                    savedSearch);
        }
    }

}
