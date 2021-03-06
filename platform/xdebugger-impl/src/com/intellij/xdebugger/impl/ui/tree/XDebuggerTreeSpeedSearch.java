/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.xdebugger.impl.ui.tree;

import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.LoadingNode;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.Convertor;
import com.intellij.util.ui.tree.TreeUtil;
import com.intellij.xdebugger.impl.ui.tree.nodes.XDebuggerTreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

class XDebuggerTreeSpeedSearch extends TreeSpeedSearch {

  public final int SEARCH_DEPTH = Registry.intValue("debugger.variablesView.rss.depth");

  public XDebuggerTreeSpeedSearch(XDebuggerTree tree, Convertor<TreePath, String> toStringConvertor) {
    super(tree, toStringConvertor, true);
  }

  @NotNull
  @Override
  protected Object[] getAllElements() {
    XDebuggerTreeNode root = ObjectUtils.tryCast(myComponent.getModel().getRoot(), XDebuggerTreeNode.class);
    int initialLevel = root != null ? root.getPath().getPathCount() : 0;

    return TreeUtil.treePathTraverser(myComponent)
        .expand(n -> myComponent.isExpanded(n) || n.getPathCount() - initialLevel < SEARCH_DEPTH)
        .traverse()
        .filter(o -> !(o.getLastPathComponent() instanceof LoadingNode))
        .toList()
        .toArray(new TreePath[0]);
  }
}