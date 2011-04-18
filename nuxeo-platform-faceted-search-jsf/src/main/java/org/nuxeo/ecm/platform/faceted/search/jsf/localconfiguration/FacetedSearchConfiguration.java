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
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration;

import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.localconfiguration.LocalConfiguration;

/**
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @since 5.4.2
 */
public interface FacetedSearchConfiguration extends
        LocalConfiguration<FacetedSearchConfiguration> {

    List<String> getAllowedContentViewNames();

    List<String> getDeniedContentViewNames();

    Set<String> filterAllowedContentViewNames(Set<String> names);
}
