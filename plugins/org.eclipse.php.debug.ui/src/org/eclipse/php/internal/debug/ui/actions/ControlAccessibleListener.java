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
package org.eclipse.php.internal.debug.ui.actions;

import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.widgets.Control;

public class ControlAccessibleListener extends AccessibleAdapter {
	private String controlName;

	public ControlAccessibleListener(String name) {
		controlName = name;
	}

	@Override
	public void getName(AccessibleEvent e) {
		e.result = controlName;
	}

	public static void addListener(Control comp, String name) {
		// strip mnemonic
		String[] strs = name.split("&"); //$NON-NLS-1$
		StringBuilder stripped = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			stripped.append(strs[i]);
		}
		comp.getAccessible().addAccessibleListener(new ControlAccessibleListener(stripped.toString()));
	}
}
