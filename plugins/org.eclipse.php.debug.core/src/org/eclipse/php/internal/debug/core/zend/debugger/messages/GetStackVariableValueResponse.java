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
/*
 * GetStackVariableValueResponse.java
 *
 */

package org.eclipse.php.internal.debug.core.zend.debugger.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.php.debug.core.debugger.messages.IDebugResponseMessage;
import org.eclipse.php.internal.debug.core.zend.communication.CommunicationUtilities;

/**
 * @author guy
 */
public class GetStackVariableValueResponse extends DebugMessageResponseImpl implements IDebugResponseMessage {

	private byte[] varResult;

	/**
	 * Sets the DefaultExpression result.
	 */
	public void setVarResult(byte[] varResult) {
		this.varResult = varResult;
	}

	/**
	 * Returns the DefaultExpression result.
	 */
	public byte[] getVarResult() {
		return varResult;
	}

	@Override
	public void deserialize(DataInputStream in) throws IOException {
		setID(in.readInt());
		setStatus(in.readInt());
		setVarResult(CommunicationUtilities.readStringAsBytes(in));
	}

	@Override
	public int getType() {
		return 1035;
	}

	@Override
	public void serialize(DataOutputStream out) throws IOException {
		out.writeShort(getType());
		out.writeInt(getID());
		out.writeInt(getStatus());
		CommunicationUtilities.writeStringAsBytes(out, getVarResult());
	}
}