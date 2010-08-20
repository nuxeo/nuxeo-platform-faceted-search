package org.nuxeo.ecm.platform.faceted.search.jsf;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Service handling faceted searches and related saved searches.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
public interface FacetedSearchService {

    List<String> getFacetedSearchContentViewNames() throws ClientException;

}
