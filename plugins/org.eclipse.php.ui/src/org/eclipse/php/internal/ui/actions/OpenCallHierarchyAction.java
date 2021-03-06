/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corporation and others.
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
package org.eclipse.php.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.IDLTKStatusConstants;
import org.eclipse.dltk.internal.ui.actions.ActionUtil;
import org.eclipse.dltk.internal.ui.callhierarchy.CallHierarchyMessages;
import org.eclipse.dltk.internal.ui.callhierarchy.CallHierarchyUI;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.actions.SelectionDispatchAction;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.php.internal.core.documentModel.dom.ElementImplForPHP;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.ui.IWorkbenchSite;

/**
 * This action opens a call hierarchy on the selected method.
 * <p>
 * The action is applicable to selections containing elements of type
 * <code>IMethod</code>.
 */
public class OpenCallHierarchyAction extends SelectionDispatchAction {

	private PHPStructuredEditor fEditor;

	/**
	 * Creates a new <code>OpenCallHierarchyAction</code>. The action requires
	 * that the selection provided by the site's selection provider is of type
	 * <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site
	 *            the site providing context information for this action
	 */
	public OpenCallHierarchyAction(IWorkbenchSite site) {
		super(site);
		setText(CallHierarchyMessages.OpenCallHierarchyAction_label);
		setToolTipText(CallHierarchyMessages.OpenCallHierarchyAction_tooltip);
		setDescription(CallHierarchyMessages.OpenCallHierarchyAction_description);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// IJavaHelpContextIds.CALL_HIERARCHY_OPEN_ACTION);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call
	 * this constructor.
	 */
	public OpenCallHierarchyAction(PHPStructuredEditor editor) {
		this(editor.getEditorSite());
		fEditor = editor;
		setEnabled(SelectionConverter.canOperateOn(fEditor));
	}

	@Override
	public void selectionChanged(final ITextSelection selection) {
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(isEnabled(selection));
	}

	private boolean isEnabled(IStructuredSelection selection) {
		if (selection == null || selection.size() != 1) {
			return false;
		}
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof ElementImplForPHP) {
			return true;
		}
		if (!(firstElement instanceof IModelElement) && (firstElement instanceof IAdaptable)) {
			firstElement = ((IAdaptable) firstElement).getAdapter(IModelElement.class);
		}

		if (!(firstElement instanceof IModelElement)) {
			return false;
		}
		if (!(firstElement instanceof IModelElement))
			return false;
		switch (((IModelElement) firstElement).getElementType()) {
		// case IModelElement.INITIALIZER:
		case IModelElement.METHOD:
		case IModelElement.FIELD:
		case IModelElement.TYPE:
			return true;
		case IModelElement.PROJECT_FRAGMENT:
		case IModelElement.SOURCE_MODULE:
		case IModelElement.PACKAGE_DECLARATION:
		case IModelElement.IMPORT_DECLARATION:
			return true;
		// case IModelElement.LOCAL_VARIABLE:
		case IModelElement.SCRIPT_PROJECT:
		case IModelElement.SCRIPT_FOLDER:
		default:
			return false;
		}
	}

	@Override
	public void run(ITextSelection selection) {
		ISourceModule input = SelectionConverter.getInput(fEditor);
		if (!ActionUtil.isProcessable(getShell(), input)) {
			return;
		}

		try {
			IModelElement[] elements = SelectionConverter.codeResolveOrInputForked(fEditor);
			if (elements == null) {
				return;
			}
			List<IModelElement> candidates = new ArrayList<>(elements.length);
			for (int i = 0; i < elements.length; i++) {
				IModelElement[] resolvedElements = CallHierarchyUI.getCandidates(elements[i]);
				if (resolvedElements != null) {
					candidates.addAll(Arrays.asList(resolvedElements));
				}
			}
			if (candidates.isEmpty()) {
				IModelElement enclosingMethod = getEnclosingMethod(input, selection);
				if (enclosingMethod != null) {
					candidates.add(enclosingMethod);
				}
			}
			run(candidates.toArray(new IModelElement[candidates.size()]));
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, getShell(), getErrorDialogTitle(), ""); //$NON-NLS-1$
		} catch (InterruptedException e) {
			// cancelled
		}
	}

	private IModelElement getEnclosingMethod(IModelElement input, ITextSelection selection) {
		IModelElement enclosingElement = null;
		try {
			switch (input.getElementType()) {
			case IModelElement.SOURCE_MODULE:
				ISourceModule cu = (ISourceModule) input.getAncestor(IModelElement.SOURCE_MODULE);
				if (cu != null) {
					enclosingElement = cu.getElementAt(selection.getOffset());
				}
				break;
			}
			if (enclosingElement != null && enclosingElement.getElementType() == IModelElement.METHOD) {
				return enclosingElement;
			}
		} catch (ModelException e) {
			DLTKUIPlugin.log(e);
		}

		return null;
	}

	@Override
	public void run(IStructuredSelection selection) {
		if (selection instanceof ITextSelection) {
			run((ITextSelection) selection);
		} else {
			if (selection.size() != 1) {
				return;
			}
			Object input = selection.getFirstElement();
			if (!(input instanceof IModelElement)) {
				IStatus status = createStatus(Messages.OpenCallHierarchyAction_0);
				ErrorDialog.openError(getShell(), getErrorDialogTitle(), "", //$NON-NLS-1$
						status);
				return;
			}
			IModelElement element = (IModelElement) input;
			if (!ActionUtil.isProcessable(getShell(), element)) {
				return;
			}
			List<IModelElement> result = new ArrayList<>(1);
			IStatus status = compileCandidates(result, element);
			if (status.isOK()) {
				run(result.toArray(new IModelElement[result.size()]));
			} else {
				openErrorDialog(status);
			}
		}
	}

	private int openErrorDialog(IStatus status) {
		String message = ""; //$NON-NLS-1$
		String dialogTitle = getErrorDialogTitle();
		return ErrorDialog.openError(getShell(), dialogTitle, message, status);
	}

	private static String getErrorDialogTitle() {
		return ""; //$NON-NLS-1$
	}

	public void run(IModelElement[] elements) {
		if (elements.length == 0) {
			getShell().getDisplay().beep();
			return;
		}
		CallHierarchyUI.open(elements, getSite().getWorkbenchWindow(), getCallHierarchyID());
	}

	private static IStatus compileCandidates(List<IModelElement> result, IModelElement elem) {
		IStatus ok = new Status(IStatus.OK, DLTKUIPlugin.getPluginId(), 0, "", null); //$NON-NLS-1$
		switch (elem.getElementType()) {
		case IModelElement.METHOD:
			result.add(elem);
			return ok;
		}
		return createStatus(""); //$NON-NLS-1$
	}

	private static IStatus createStatus(String message) {
		return new Status(IStatus.INFO, DLTKUIPlugin.getPluginId(), IDLTKStatusConstants.INTERNAL_ERROR, message, null);
	}

	public String getCallHierarchyID() {
		return "org.eclipse.dltk.callhierarchy.view"; //$NON-NLS-1$
	}
}
