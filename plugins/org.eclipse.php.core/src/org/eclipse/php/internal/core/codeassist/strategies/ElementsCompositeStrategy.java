/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.php.internal.core.codeassist.strategies;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionReporter;
import org.eclipse.php.core.codeassist.ICompletionStrategy;

/**
 * This composite contains strategies that complete global elements
 * 
 * @author michael
 */
public class ElementsCompositeStrategy extends AbstractCompletionStrategy {

	private final Collection<ICompletionStrategy> strategies = new ArrayList<>();

	public ElementsCompositeStrategy(ICompletionContext context, boolean includeKeywords) {
		super(context);

		strategies.add(new TypesStrategy(context));
		strategies.add(new FunctionsStrategy(context));
		strategies.add(new VariablesStrategy(context));
		strategies.add(new ConstantsStrategy(context));
		if (includeKeywords) {
			strategies.add(new GlobalKeywordsStrategy(context));
		}
	}

	@Override
	public void apply(ICompletionReporter reporter) throws Exception {
		for (ICompletionStrategy strategy : strategies) {
			strategy.apply(reporter);
		}
	}
}