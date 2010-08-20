package org.nuxeo.ecm.platform.faceted.search.jsf;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;

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

    @In(create = true)
    protected FacetedSearchService facetedSearchService;

    protected List<String> facetedSearchContentViewNames;

    protected String currentContentViewName;

    @Factory(value = "facetedSearchCurrentContentViewName", scope = EVENT)
    public String getFacetedSearchCurrentContentViewName()
            throws ClientException {
        if (currentContentViewName == null) {
            List<String> contentViewNames = getFacetedSearchContentViewNames();
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

    public List<String> getFacetedSearchContentViewNames()
            throws ClientException {
        if (facetedSearchContentViewNames == null) {
            facetedSearchContentViewNames = facetedSearchService.getFacetedSearchContentViewNames();
        }
        return facetedSearchContentViewNames;
    }

}
