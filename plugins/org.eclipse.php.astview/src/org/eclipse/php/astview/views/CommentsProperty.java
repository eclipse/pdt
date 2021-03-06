/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.php.astview.views;

import java.util.List;

import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.swt.graphics.Image;

/**
 *
 */
public class CommentsProperty extends ASTAttribute {

	private final Program fRoot;

	public CommentsProperty(Program root) {
		fRoot = root;
	}

	@Override
	public Object getParent() {
		return fRoot;
	}

	@Override
	public Object[] getChildren() {
		List<?> commentList = fRoot.comments();
		return (commentList == null ? EMPTY : commentList.toArray());
	}

	@Override
	public String getLabel() {
		List<?> commentList = fRoot.comments();
		return "> comments (" + (commentList == null ? 0 : commentList.size()) + ")"; //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return 17;
	}
}
