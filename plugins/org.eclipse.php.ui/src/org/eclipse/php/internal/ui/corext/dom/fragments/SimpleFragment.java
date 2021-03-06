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
package org.eclipse.php.internal.ui.corext.dom.fragments;

import org.eclipse.core.runtime.Assert;
import org.eclipse.php.core.ast.match.PHPASTMatcher;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.ParenthesisExpression;
import org.eclipse.php.internal.core.ast.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEditGroup;

class SimpleFragment extends ASTFragment {
	private final ASTNode fNode;

	SimpleFragment(ASTNode node) {
		Assert.isNotNull(node);
		fNode = node;
	}

	@Override
	public IASTFragment[] getMatchingFragmentsWithNode(ASTNode node) {
		if (!PHPASTMatcher.doNodesMatch(getAssociatedNode(), node)) {
			return new IASTFragment[0];
		}

		IASTFragment match = ASTFragmentFactory.createFragmentForFullSubtree(node, this);
		Assert.isTrue(match.matches(this) || this.matches(match));
		return new IASTFragment[] { match };
	}

	@Override
	public boolean matches(IASTFragment other) {
		return other.getClass().equals(getClass())
				&& PHPASTMatcher.doNodesMatch(other.getAssociatedNode(), getAssociatedNode());
	}

	@Override
	public IASTFragment[] getSubFragmentsMatching(IASTFragment toMatch) {
		return ASTMatchingFragmentFinder.findMatchingFragments(getAssociatedNode(), (ASTFragment) toMatch);

	}

	@Override
	public int getStartPosition() {
		return fNode.getStart();
	}

	@Override
	public int getLength() {
		return fNode.getLength();
	}

	@Override
	public ASTNode getAssociatedNode() {
		return fNode;
	}

	@Override
	public void replace(ASTRewrite rewrite, ASTNode replacement, TextEditGroup textEditGroup) {
		if (replacement instanceof Identifier && fNode.getParent() instanceof ParenthesisExpression) {
			// replace including the parenthesized expression around it
			rewrite.replace(fNode.getParent(), replacement, textEditGroup);
		} else {
			rewrite.replace(fNode, replacement, textEditGroup);
		}
	}

	@Override
	public int hashCode() {
		return fNode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimpleFragment other = (SimpleFragment) obj;
		return fNode.equals(other.fNode);
	}
}
