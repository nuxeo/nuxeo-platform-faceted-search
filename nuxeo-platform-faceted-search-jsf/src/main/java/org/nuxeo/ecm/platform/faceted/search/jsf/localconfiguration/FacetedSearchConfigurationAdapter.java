/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

package org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration;

import static org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.ConfigConstants.F_SEARCH_CONFIGURATION_ALLOWED_CONTENT_VIEWS;
import static org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration.ConfigConstants.F_SEARCH_CONFIGURATION_DENIED_CONTENT_VIEWS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.localconfiguration.AbstractLocalConfiguration;

/**
 * Default implementation of {@code FacetedSearchConfiguration}
 *
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @since 5.4.2
 */
public class FacetedSearchConfigurationAdapter extends AbstractLocalConfiguration<FacetedSearchConfiguration> implements
        FacetedSearchConfiguration {

    protected List<String> allowedContentViews;

    protected List<String> deniedContentViews;

    protected DocumentRef docRef;

    public FacetedSearchConfigurationAdapter(DocumentModel doc) {
        docRef = doc.getRef();

        allowedContentViews = getList(doc, F_SEARCH_CONFIGURATION_ALLOWED_CONTENT_VIEWS);
        deniedContentViews = getList(doc, F_SEARCH_CONFIGURATION_DENIED_CONTENT_VIEWS);
    }

    protected List<String> getList(DocumentModel doc, String property) {
        String[] content;
        try {
            content = (String[]) doc.getPropertyValue(property);
        } catch (ClientException e) {
            return Collections.emptyList();
        }
        if (content != null) {
            return Collections.unmodifiableList(Arrays.asList(content));
        }
        return Collections.emptyList();
    }

    @Override
    public DocumentRef getDocumentRef() {
        return docRef;
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public FacetedSearchConfiguration merge(FacetedSearchConfiguration other) {
        if (other == null) {
            return this;
        }

        docRef = other.getDocumentRef();

        List<String> deniedCV = new ArrayList<String>(deniedContentViews);
        deniedCV.addAll(other.getDeniedContentViewNames());
        deniedContentViews = Collections.unmodifiableList(deniedCV);

        return this;
    }

    @Override
    public List<String> getAllowedContentViewNames() {
        return allowedContentViews;
    }

    @Override
    public List<String> getDeniedContentViewNames() {
        return deniedContentViews;
    }

    protected boolean isAllowedName(String name) {
        return !getDeniedContentViewNames().contains(name)
                && (getAllowedContentViewNames().contains(name) || getAllowedContentViewNames().isEmpty());
    }

    @Override
    public Set<String> filterAllowedContentViewNames(Set<String> names) {
        Set<String> filtered = new LinkedHashSet<String>();

        for (String name : names) {
            if (isAllowedName(name)) {
                filtered.add(name);
            }
        }

        return filtered;
    }
}
