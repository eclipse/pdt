/*******************************************************************************
 * Copyright (c) 2018 Michał Niewrzał and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Michał Niewrzał - initial API and implementation
 *******************************************************************************/
package org.eclipse.php.phpunit.model.connection;

public class MessageElement {

	private String file;

	private Integer line;

	private String filtered;

	public String getFile() {
		return file;
	}

	public Integer getLine() {
		return line;
	}

	public String getFiltered() {
		return filtered;
	}
}
