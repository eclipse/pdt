/*******************************************************************************
 * Copyright (c) 2012, 2016, 2017 PDT Extension Group and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     PDT Extension Group - initial API and implementation
 *     Kaloyan Raev - [501269] externalize strings
 *******************************************************************************/
package org.eclipse.php.composer.api.repositories;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.eclipse.php.composer.api.RepositoryPackage;
import org.eclipse.php.composer.api.annotation.Name;
import org.eclipse.php.composer.api.json.ParseException;

public class PackageRepository extends Repository {

	@Name("package")
	private RepositoryPackage repositoryPackage = new RepositoryPackage();

	public PackageRepository() {
		super("package"); //$NON-NLS-1$
		listen();
	}

	public PackageRepository(Object json) {
		this();
		fromJson(json);
	}

	public PackageRepository(String json) throws ParseException {
		this();
		fromJson(json);
	}

	public PackageRepository(File file) throws IOException, ParseException {
		this();
		fromJson(file);
	}

	public PackageRepository(Reader reader) throws IOException, ParseException {
		this();
		fromJson(reader);
	}

	public RepositoryPackage getPackage() {
		return repositoryPackage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PackageRepository clone() {
		PackageRepository clone = new PackageRepository();
		cloneProperties(clone);
		return clone;
	}
}
