package org.squirrelsql.session.graph;

import javafx.scene.control.TreeItem;
import org.squirrelsql.services.dndpositionmarker.RelativeNodePosition;

public interface WhereConfigColDragDroppedListener
{
   void dropped(String idToMove, TreeItem<WhereConfigColTreeNode> targetTreeItem, RelativeNodePosition movePosition);
}
