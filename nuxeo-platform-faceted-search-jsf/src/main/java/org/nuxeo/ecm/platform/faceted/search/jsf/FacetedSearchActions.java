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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentLocationImpl;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.TypeService;
import org.nuxeo.ecm.platform.faceted.search.api.Constants;
import org.nuxeo.ecm.platform.faceted.search.api.service.FacetedSearchService;
import org.nuxeo.ecm.platform.faceted.search.api.util.JSONMetadataExporter;
import org.nuxeo.ecm.platform.faceted.search.api.util.JSONMetadataHelper;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentView;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.platform.url.DocumentViewImpl;
import org.nuxeo.ecm.platform.url.api.DocumentView;
import org.nuxeo.ecm.platform.url.api.DocumentViewCodecManager;
import org.nuxeo.ecm.webapp.contentbrowser.ContentViewActions;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.api.Framework;

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

    public static final String DUBLINCORE_SCHEMA = "dublincore";

    public static final String NONE_VALUE = "none";

    public static final String NONE_LABEL = "label.none";

    public static final String USER_SAVED_SEARCHES_LABEL = "label.user.saved.searches";

    public static final String ALL_SAVED_SEARCHES_LABEL = "label.all.saved.searches";

    public static final String SEARCH_SAVED_LABEL = "label.search.saved";

    public static final String FACETED_SEARCH_CODEC = "facetedSearch";

    public static final String CONTENT_VIEW_NAME_PARAMETER = "contentViewName";

    public static final String FILTER_VALUES_PARAMETER = "values";

    public static final String ENCODED_VALUES_ENCODING = "UTF-8";

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

    public void setCurrentContentViewName(
            String facetedSearchCurrentContentViewName) {
        this.currentContentViewName = facetedSearchCurrentContentViewName;
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

    public List<DocumentModel> getOtherUsersSavedSearches()
            throws ClientException {
        return facetedSearchService.getOtherUsersSavedSearches(documentManager);
    }

    @Factory(value = "savedSearchesSelectItems", scope = ScopeType.EVENT)
    public List<SelectItem> getSavedSearchesSelectItems()
            throws ClientException {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(NONE_VALUE,
                resourcesAccessor.getMessages().get(NONE_LABEL)));

        SelectItemGroup userGroup = new SelectItemGroup(
                resourcesAccessor.getMessages().get(USER_SAVED_SEARCHES_LABEL));
        List<DocumentModel> userSavedSearches = getCurrentUserSavedSearches();
        List<SelectItem> userSavedSearchesItems = convertToSelectItems(userSavedSearches);
        userGroup.setSelectItems(userSavedSearchesItems.toArray(new SelectItem[userSavedSearchesItems.size()]));
        items.add(userGroup);

        List<DocumentModel> otherUsersSavedFacetedSearches = getOtherUsersSavedSearches();
        List<SelectItem> otherUsersSavedSearchesItems = convertToSelectItems(otherUsersSavedFacetedSearches);
        SelectItemGroup allGroup = new SelectItemGroup(
                resourcesAccessor.getMessages().get(ALL_SAVED_SEARCHES_LABEL));
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

    public String loadSelectedSavedSearch(String jsfView)
            throws ClientException {
        loadSelectedSavedSearch();
        return jsfView;
    }

    public void loadSelectedSavedSearch() throws ClientException {
        if (currentSelectedSavedSearchId == null
                || currentSelectedSavedSearchId.isEmpty()
                || NONE_VALUE.equals(currentSelectedSavedSearchId)) {
            contentViewActions.reset(currentContentViewName);
        } else {
            loadSavedSearch(currentSelectedSavedSearchId);
        }
    }

    public void loadSavedSearch(String savedSearchId) throws ClientException {
        DocumentModel savedSearch = documentManager.getDocument(new IdRef(
                savedSearchId));
        String contentViewName = (String) savedSearch.getPropertyValue(Constants.FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY);
        loadSavedSearch(contentViewName, savedSearch);
    }

    public void loadSavedSearch(String contentViewName,
            DocumentModel searchDocument) throws ClientException {
        // Do not reuse the existing document as it can be modified and saved
        // again
        DocumentModel newSearchDocument = createDocumentModelFrom(searchDocument);
        ContentView contentView = contentViewActions.getContentView(
                contentViewName, newSearchDocument);
        if (contentView != null) {
            this.currentContentViewName = contentViewName;
        }
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

            // Do not reuse the just saved document as it can be modified and
            // re-saved
            DocumentModel searchDocument = createDocumentModelFrom(savedSearch);
            contentView.setSearchDocumentModel(searchDocument);

            Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
                    savedSearch);
            facesMessages.add(FacesMessage.SEVERITY_INFO,
                    resourcesAccessor.getMessages().get(SEARCH_SAVED_LABEL));
        }
    }

    /**
     * Create a new {@code DocumentModel} with the same type as {@code
     * sourceDoc}. Copy all the {@code DataModel}s from {@code sourceDoc} to the
     * newly created document, except the {@code dublincore} schema.
     */
    protected DocumentModel createDocumentModelFrom(DocumentModel sourceDoc)
            throws ClientException {
        DocumentModel doc = documentManager.createDocumentModel(sourceDoc.getType());
        for (String schema : sourceDoc.getDeclaredSchemas()) {
            // Copy everything except dublincore schema, required values will be
            // created again on the next save, if any
            if (!DUBLINCORE_SCHEMA.equals(schema)) {
                DataModel dm = sourceDoc.getDataModel(schema);
                SchemaManager mgr = TypeService.getSchemaManager();
                DataModel newDM = DocumentModelImpl.cloneDataModel(
                        mgr.getSchema(dm.getSchema()), dm);
                doc.getDataModel(schema).setMap(newDM.getMap());
            }
        }
        return doc;
    }

    /*
     * ----- Permanent link generation and loading -----
     */

    protected String encodeValues(String values)
            throws UnsupportedEncodingException {
        String encodedValues = Base64.encodeBytes(values.getBytes(),
                Base64.GZIP | Base64.DONT_BREAK_LINES);
        encodedValues = URLEncoder.encode(encodedValues,
                ENCODED_VALUES_ENCODING);
        return encodedValues;
    }

    protected String decodeValues(String values)
            throws UnsupportedEncodingException {
        String decodedValues = URLDecoder.decode(values,
                ENCODED_VALUES_ENCODING);
        decodedValues = new String(Base64.decode(decodedValues));
        return decodedValues;
    }

    /**
     * Set the metadata of the SearchDocumentModel from an encoded JSON string.
     */
    public void setFilterValues(String filterValues) throws ClientException,
            JSONException, UnsupportedEncodingException {
        ContentView contentView = contentViewActions.getContentView(currentContentViewName);
        DocumentModel searchDocumentModel = contentView.getSearchDocumentModel();
        String decodedValues = decodeValues(filterValues);
        searchDocumentModel = JSONMetadataHelper.setPropertiesFromJson(
                searchDocumentModel, decodedValues);
        contentView.setSearchDocumentModel(searchDocumentModel);
    }

    /**
     * Compute a permanent link for the current search.
     */
    public String getPermanentLinkUrl() throws ClientException,
            UnsupportedEncodingException {
        DocumentView docView = new DocumentViewImpl(new DocumentLocationImpl(
                documentManager.getRepositoryName(), null));
        docView.addParameter(CONTENT_VIEW_NAME_PARAMETER,
                currentContentViewName);
        ContentView contentView = contentViewActions.getContentView(currentContentViewName);
        DocumentModel doc = contentView.getSearchDocumentModel();
        String values = getEncodedValuesFrom(doc);
        docView.addParameter(FILTER_VALUES_PARAMETER, values);
        DocumentViewCodecManager documentViewCodecManager = getDocumentViewCodecService();
        return documentViewCodecManager.getUrlFromDocumentView(
                FACETED_SEARCH_CODEC, docView, true, BaseURL.getBaseURL());
    }

    protected DocumentViewCodecManager getDocumentViewCodecService()
            throws ClientException {
        try {
            return Framework.getService(DocumentViewCodecManager.class);
        } catch (Exception e) {
            final String errMsg = "Could not retrieve the document view service. "
                    + e.getMessage();
            throw new ClientException(errMsg, e);
        }
    }

    /**
     * Returns an encoded JSON string computed from the {@code doc} metadata.
     */
    protected String getEncodedValuesFrom(DocumentModel doc)
            throws ClientException, UnsupportedEncodingException {
        JSONMetadataExporter exporter = new JSONMetadataExporter();
        String values = exporter.run(doc).toString();
        return encodeValues(values);
    }

}
