/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.php.internal.debug.ui.refactoring;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.php.internal.debug.core.model.PHPConditionalBreakpoint;
import org.eclipse.php.internal.debug.core.model.PHPLineBreakpoint;

import java.text.MessageFormat;

/**
 * @since 3.2
 * 
 */
public class LineBreakpointTypeChange extends LineBreakpointChange {

	private IResource originalResource;
	private IResource destResource;

	public LineBreakpointTypeChange(PHPLineBreakpoint breakpoint, IResource originalResource, IResource destResource)
			throws CoreException {
		super(breakpoint);
		this.originalResource = originalResource;
		this.destResource = destResource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	@Override
	public String getName() {
		String msg = MessageFormat.format("", //$NON-NLS-1$
				getBreakpointLabel(getOriginalBreakpoint()));
		if (!"".equals(destResource.getName())) { //$NON-NLS-1$
			msg = MessageFormat.format(RefactoringMessages.LineBreakpointTypeChange_0,
					getBreakpointLabel(getOriginalBreakpoint()), destResource.getName());
		}
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime
	 * .IProgressMonitor)
	 */
	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		Map<String, Comparable<?>> map = new HashMap<>();
		// addJavaBreakpointAttributes(map, fDestType);
		PHPLineBreakpoint breakpoint = new PHPConditionalBreakpoint(destResource, getLineNumber(), -1, -1, map);
		apply(breakpoint);
		getOriginalBreakpoint().delete();
		return new DeleteBreakpointChange(breakpoint);
	}

}
