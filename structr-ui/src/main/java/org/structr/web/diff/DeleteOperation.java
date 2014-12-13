/**
 * Copyright (C) 2010-2014 Morgner UG (haftungsbeschränkt)
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.web.diff;

import java.util.Map;
import org.structr.common.error.FrameworkException;
import org.structr.core.app.App;
import org.structr.web.entity.dom.Content;
import org.structr.web.entity.dom.DOMElement;
import org.structr.web.entity.dom.DOMNode;
import org.w3c.dom.Node;

/**
 *
 * @author Christian Morgner
 */
public class DeleteOperation extends InvertibleModificationOperation {

	private DOMNode existingNode = null;

	public DeleteOperation(final Map<String, DOMNode> hashMappedExistingNodes, final DOMNode existingNode) {

		super(hashMappedExistingNodes);

		this.existingNode = existingNode;
	}

	@Override
	public String toString() {

		if (existingNode instanceof Content) {

			return "Delete Content(" + existingNode.getIdHash() + ")";

		} else {

			return "Delete " + existingNode.getProperty(DOMElement.tag) + "(" + existingNode.getIdHash();
		}
	}

	// ----- interface InvertibleModificationOperation -----
	@Override
	public void apply(final App app, final DOMNode sourceNode, final DOMNode newNode) throws FrameworkException {

		// do not delete synced nodes (nodes that are shared between multiple pages)
		if (!existingNode.isSynced()) {

			// remove node from parent, do not simply delete it
			final Node parent = existingNode.getParentNode();
			if (parent != null) {

				parent.removeChild(existingNode);
			}

			app.delete(existingNode);
		}
	}

	@Override
	public InvertibleModificationOperation revert() {
		return null;
	}

	@Override
	public Integer getPosition() {

		// delete operations should go first
		return 100;
	}
}
