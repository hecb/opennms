/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.app.internal.operations;

import java.util.List;

import org.opennms.features.topology.api.GraphContainer;
import org.opennms.features.topology.api.Operation;
import org.opennms.features.topology.api.OperationContext;


public class DeleteGroupOperation implements Operation {

	@Override
	public Undoer execute(List<Object> targets, OperationContext operationContext) {
		if (targets == null || targets.isEmpty()) {
			return null;
		}

		GraphContainer graphContainer = operationContext.getGraphContainer();
		
		// TODO: Add a confirmation dialog before the group is deleted

		Object parentKey = targets.get(0);
		Object parentId = graphContainer.getVertexItemIdForVertexKey(parentKey);
		Object grandParentId = graphContainer.getVertexContainer().getParent(parentId);
		// Detach all children from the group
		for (Object childId : operationContext.getGraphContainer().getVertexContainer().getChildren(parentId)) {
			// Attach the children to their grandparent (which can be null)
			graphContainer.getDataSource().setParent(childId, grandParentId);
		}
		// Remove the group from the topology
		graphContainer.getVertexContainer().removeItem(parentId);

		graphContainer.redoLayout();
		return null;
	}

	@Override
	public boolean display(List<Object> targets, OperationContext operationContext) {
		return true;
	}

	@Override
	public boolean enabled(List<Object> targets, OperationContext operationContext) {
		return targets != null && targets.size() == 1 && targets.get(0) != null;
	}

	@Override
	public String getId() {
		return "DeleteGroup";
	}
}