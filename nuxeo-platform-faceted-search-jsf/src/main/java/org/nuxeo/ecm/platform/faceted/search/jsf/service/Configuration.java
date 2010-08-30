package org.nuxeo.ecm.platform.faceted.search.jsf.service;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Configuration object for the {@link FacetedSearchService}.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
@XObject("configuration")
public class Configuration {

    @XNode("rootSavedSearchesTitle")
    protected String rootSavedSearchesTitle;

    public String getRootSavedSearchesTitle() {
        return rootSavedSearchesTitle;
    }

}
