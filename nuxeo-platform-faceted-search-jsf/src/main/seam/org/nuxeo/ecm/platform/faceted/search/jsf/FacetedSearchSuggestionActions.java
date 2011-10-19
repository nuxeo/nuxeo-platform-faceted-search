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
 *    Mariana Cedica
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.faceted.search.jsf;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.contentview.jsf.ContentViewService;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.faceted.search.api.service.FacetedSearchService;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.platform.types.adapter.TypeInfo;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.invalidations.AutomaticDocumentBasedInvalidation;
import org.nuxeo.ecm.platform.ui.web.invalidations.DocumentContextBoundActionBean;
import org.nuxeo.ecm.webapp.security.UserSuggestionActionsBean;
import org.nuxeo.runtime.api.Framework;

import com.google.inject.Inject;

@Name("facetedSearchSuggestionActions")
@Scope(CONVERSATION)
@AutomaticDocumentBasedInvalidation
public class FacetedSearchSuggestionActions extends
        DocumentContextBoundActionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    public static final String FACETED_SEARCH_SUGGESTION = "DEFAULT_DOCUMENT_SUGGESTION";

    public static final String FACETED_SEARCH_DEFAULT_CONTENT_VIEW_NAME = "faceted_search_default";

    public static final String FACETED_SEARCH_DEFAULT_DOCUMENT_TYPE = "FacetedSearchDefault";

    @In(create = true)
    protected FacetedSearchService facetedSearchService;

    @In(create = true)
    protected ContentViewActions contentViewActions;

    @Inject
    protected ContentViewService contentViewService;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true)
    protected transient UserSuggestionActionsBean userSuggestionActions;

    public DocumentModel getDocumentModel(String id) throws ClientException {
        return documentManager.getDocument(new IdRef(id));
    }

    @SuppressWarnings("unchecked")
    public List<SearchBoxSuggestion> getDocumentSuggestions(Object input)
            throws ClientException {
        List<SearchBoxSuggestion> suggestions = new ArrayList<SearchBoxSuggestion>();
        try {
            PageProviderService ppService = Framework.getService(PageProviderService.class);
            Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put(CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY,
                    (Serializable) documentManager);
            PageProvider<DocumentModel> pp = (PageProvider<DocumentModel>) ppService.getPageProvider(
                    FACETED_SEARCH_SUGGESTION, null, null, null, props,
                    new Object[] { input });
            for (DocumentModel doc : pp.getCurrentPage()) {
                suggestions.add(SearchBoxSuggestion.forDocument(doc));
            }
            List<SearchBoxSuggestion> searchBoxByAuthor= new ArrayList<SearchBoxSuggestion>();
            for(DocumentModel user : getUsersSuggestions(input)){
                suggestions.add(SearchBoxSuggestion.forUser(user));
                searchBoxByAuthor.add(SearchBoxSuggestion.forDocumentsByAuthor(user));
            }
            
            for(DocumentModel group : getGroupSuggestions(input)){
                suggestions.add(SearchBoxSuggestion.forGroup(group));
            }
            suggestions.addAll(searchBoxByAuthor);
        }catch(Exception e){
          throw new ClientException(e);
        }
        return suggestions;
    }

    public String handleSelection(String suggestionType, String suggestionValue)
            throws ClientException {
        return "";
    }

    public static class SearchBoxSuggestion {

        public static final String DOCUMENT_SUGGESTION = "document";

        public static final String USER_SUGGESTION = "user";

        public static final String GROUP_SUGGESTION = "group";

        public static final String DOCUMENTS_BY_AUTHOR_SUGGESTION = "documentsByAuthor";

        public static final String DOCUMENTS_BY_DATE_SUGGESTION = "documentsByDate";

        private final String type;

        private final String value;

        private final String label;

        private final String iconURL;

        public SearchBoxSuggestion(String type, String value, String label,
                String iconURL) {
            this.type = type;
            this.label = label;
            this.iconURL = iconURL;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        public String getIconURL() {
            return iconURL;
        }

        public static SearchBoxSuggestion forDocument(DocumentModel doc)
                throws ClientException {
            return new SearchBoxSuggestion(DOCUMENT_SUGGESTION,
                    doc.getRepositoryName() + ":" + doc.getId(),
                    doc.getTitle(), doc.getAdapter(TypeInfo.class).getIcon());
        }

        public static SearchBoxSuggestion forUser(DocumentModel user)
                throws ClientException {
            return new SearchBoxSuggestion(USER_SUGGESTION, user.getId(),
                    user.getTitle(), "/icons/user.gif");
        }

        public static SearchBoxSuggestion forGroup(DocumentModel group)
                throws ClientException {
            return new SearchBoxSuggestion(GROUP_SUGGESTION, group.getId(),
                    group.getName(), "/TODO");
        }
        
        public static SearchBoxSuggestion forDocumentsByAuthor(
                DocumentModel user) throws ClientException {
            // TODO handle i18n
            return new SearchBoxSuggestion(DOCUMENTS_BY_AUTHOR_SUGGESTION,
                    user.getId(), "Documents by " + user.getTitle(),
                    "/icons/file.gif");
        }

    }

    @Override
    protected void resetBeanCache(DocumentModel newCurrentDocumentModel) {
    }

    public List<DocumentModel> getUsersSuggestions(Object user)
            throws Exception, ClientException {
        return userSuggestionActions.getUserSuggestions(user);
    }

    public List<DocumentModel> getGroupSuggestions(Object user) throws Exception, ClientException{
        return userSuggestionActions.getGroupsSuggestions(user);
    }
}
