package org.nuxeo.ecm.platform.faceted.search.jsf;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.ui.web.contentview.ContentViewService;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
public class FacetedSearchServiceImpl implements FacetedSearchService {

    public static final String FACETED_SEARCH_PREFIX = "faceted_search";

    protected ContentViewService contentViewService;

    public List<String> getFacetedSearchContentViewNames() throws ClientException {
        ContentViewService contentViewService = getContentViewService();
        List<String> contentViewNames = new ArrayList<String>();
        for (String contentViewName : contentViewService.getContentViewNames()) {
            if (contentViewName.startsWith(FACETED_SEARCH_PREFIX)) {
                contentViewNames.add(contentViewName);
            }
        }
        return contentViewNames;
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

}
