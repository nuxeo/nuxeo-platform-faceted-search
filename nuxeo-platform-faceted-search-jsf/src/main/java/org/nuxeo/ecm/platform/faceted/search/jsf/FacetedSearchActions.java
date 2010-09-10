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

package org.nuxeo.ecm.platform.faceted.search.jsf;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.json.JSONException;
import org.nuxeo.common.utils.Base64;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.faceted.search.jsf.service.FacetedSearchService;
import org.nuxeo.ecm.platform.faceted.search.jsf.util.JSONMetadataExporter;
import org.nuxeo.ecm.platform.faceted.search.jsf.util.JSONMetadataHelper;
import org.nuxeo.ecm.platform.forms.layout.api.FieldDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.Widget;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentView;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.virtualnavigation.action.MultiNavTreeManager;
import org.nuxeo.ecm.webapp.contentbrowser.ContentViewActions;
import org.nuxeo.ecm.webapp.directory.DirectoryTreeNode;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

/**
 * Handles faceted search related web actions.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
@Name("facetedSearchActions")
@Scope(CONVERSATION)
@Install(precedence = FRAMEWORK)
public class FacetedSearchActions implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NONE_VALUE = "none";

    public static final String NONE_LABEL = "label.none";

    public static final String USER_SAVED_SEARCHES_LABEL = "label.user.saved.searches";

    public static final String ALL_SAVED_SEARCHES_LABEL = "label.all.saved.searches";

    public static final String  SEARCH_SAVED_LABEL = "label.search.saved";

    @In(create = true)
    protected FacetedSearchService facetedSearchService;

    @In(create = true)
    protected ContentViewActions contentViewActions;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected ResourcesAccessor resourcesAccessor;

    @In(create = true)
    protected MultiNavTreeManager multiNavTreeManager;

    protected List<String> contentViewNames;

    protected String currentContentViewName;

    protected String currentSelectedSavedSearchId;

    protected String savedSearchTitle;

    protected Map<String, List<String>> directoriesValues = new HashMap<String, List<String>>();

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

    public void setCurrentContentViewName(
            String facetedSearchCurrentContentViewName) {
        this.currentContentViewName = facetedSearchCurrentContentViewName;
        multiNavTreeManager.setSelectedNavigationTree(Constants.FACETED_SEARCH_NAV_TREE_ID);
    }

    public List<String> getContentViewNames() throws ClientException {
        if (contentViewNames == null) {
            contentViewNames = new ArrayList<String>(
                    facetedSearchService.getContentViewNames());
        }
        return contentViewNames;
    }

    public void clearSearch() {
        contentViewActions.reset(currentContentViewName);
        this.currentSelectedSavedSearchId = null;
    }

    /*
     * ----- Retrieving user and all saved searches -----
     */

    public List<DocumentModel> getCurrentUserSavedSearches()
            throws ClientException {
        return facetedSearchService.getCurrentUserSavedSearches(documentManager);
    }

    public List<DocumentModel> getOtherUsersSavedSearches() throws ClientException {
        return facetedSearchService.getOtherUsersSavedSearches(documentManager);
    }

    @Factory(value = "savedSearchesSelectItems", scope = ScopeType.EVENT)
    public List<SelectItem> getSavedSearchesSelectItems()
            throws ClientException {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(NONE_VALUE, resourcesAccessor.getMessages().get(NONE_LABEL)));

        SelectItemGroup userGroup = new SelectItemGroup(
                resourcesAccessor.getMessages().get(USER_SAVED_SEARCHES_LABEL));
        List<DocumentModel> userSavedSearches = getCurrentUserSavedSearches();
        List<SelectItem> userSavedSearchesItems = convertToSelectItems(userSavedSearches);
        userGroup.setSelectItems(userSavedSearchesItems.toArray(new SelectItem[userSavedSearchesItems.size()]));
        items.add(userGroup);

        List<DocumentModel> otherUsersSavedFacetedSearches = getOtherUsersSavedSearches();
        List<SelectItem> otherUsersSavedSearchesItems = convertToSelectItems(otherUsersSavedFacetedSearches);
        SelectItemGroup allGroup = new SelectItemGroup(resourcesAccessor.getMessages().get(ALL_SAVED_SEARCHES_LABEL));
        allGroup.setSelectItems(otherUsersSavedSearchesItems.toArray(new SelectItem[otherUsersSavedSearchesItems.size()]));
        items.add(allGroup);

        return items;
    }

    protected List<SelectItem> convertToSelectItems(List<DocumentModel> docs)
            throws ClientException {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (DocumentModel doc : docs) {
            items.add(new SelectItem(doc.getId(), doc.getTitle(), ""));
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
        return Constants.FACETED_SEARCH_RESULTS_VIEW;
    }

    public String loadSavedSearch(String savedSearchId) throws ClientException {
        DocumentModel savedSearch = documentManager.getDocument(new IdRef(
                savedSearchId));
        String contentViewName = (String) savedSearch.getPropertyValue(Constants.FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY);
        return loadSavedSearch(contentViewName, savedSearch);
    }

    public String loadSavedSearch(String contentViewName,
        DocumentModel searchDocument) throws ClientException {

        // Do not reuse the existing document as it can be modified ans re-saved
        DocumentModel newSearchDocument = createDocumentModelFrom(searchDocument);
        ContentView contentView = contentViewActions.getContentView(
                contentViewName, newSearchDocument);
        if (contentView != null) {
            this.currentContentViewName = contentViewName;
        }
        return Constants.FACETED_SEARCH_RESULTS_VIEW;
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
            DocumentModel savedSearch = facetedSearchService.saveSearch(
                    documentManager, contentView, savedSearchTitle);
            currentSelectedSavedSearchId = savedSearch.getId();
            savedSearchTitle = null;

            // Do not reuse the just saved document as it can be modified ans re-saved
            DocumentModel searchDocument = createDocumentModelFrom(savedSearch);
            contentView.setSearchDocumentModel(searchDocument);

            Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
                    savedSearch);
            facesMessages.add(FacesMessage.SEVERITY_INFO,
                    resourcesAccessor.getMessages().get(SEARCH_SAVED_LABEL));
        }
    }

    protected DocumentModel createDocumentModelFrom(DocumentModel sourceDoc) throws ClientException {
        DocumentModel blankDoc = documentManager.createDocumentModel(sourceDoc.getType());
        blankDoc.copyContent(sourceDoc);
        return blankDoc;
    }

    public void setFilterValues(String filterValues) throws ClientException, JSONException {
        String decodedValues = new String(Base64.decode(filterValues));
        ContentView contentView = contentViewActions.getContentView(currentContentViewName);
        DocumentModel doc = contentView.getSearchDocumentModel();
        doc = JSONMetadataHelper.setPropertiesFromJson(doc, decodedValues);
        contentView.setSearchDocumentModel(doc);
    }

    public String getSearchPermlinkUrl() throws ClientException, UnsupportedEncodingException {
        ContentView contentView = contentViewActions.getContentView(currentContentViewName);
        DocumentModel doc = contentView.getSearchDocumentModel();
        String url = BaseURL.getBaseURL();
        // TODO : replace "nxsrch" by viewcodec prefix constant after move in -dm
        url += "nxsrch" + "/" + documentManager.getRepositoryName() + "/?view=" + currentContentViewName + "&values=";
        JSONMetadataExporter jSonMetadataExporter = new JSONMetadataExporter();
        String jsonString = jSonMetadataExporter.run(doc).toString();
        jsonString = Base64.encodeBytes(jsonString.getBytes(), Base64.GZIP | Base64.DONT_BREAK_LINES);
        jsonString = URLEncoder.encode(jsonString, "UTF-8");
        url += jsonString;
        return url;
    }

    public void addValue(Widget widget, DirectoryTreeNode node) throws ClientException {
        List<String> values = directoriesValues.get(widget.getName());
        if (values == null) {
            values = new ArrayList<String>();
            directoriesValues.put(widget.getName(), values);
        }
        values.add(node.getPath());

        ContentView cView = contentViewActions.getContentView(currentContentViewName);
        DocumentModel searchDocument = cView.getSearchDocumentModel();
        FieldDefinition field = widget.getFieldDefinitions()[0];
        searchDocument.setPropertyValue(field.getPropertyName(), (Serializable) values);
    }

    public void removeValue(String widgetName, String value) {
        directoriesValues.get(widgetName).remove(value);
    }


    public List<String> getValuesFor(String widgetName) {
        return directoriesValues.get(widgetName);
    }

}
