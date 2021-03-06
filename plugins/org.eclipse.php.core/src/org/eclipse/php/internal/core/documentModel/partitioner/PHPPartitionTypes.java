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
package org.eclipse.php.internal.core.documentModel.partitioner;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.php.internal.core.documentModel.parser.regions.IPHPScriptRegion;
import org.eclipse.php.internal.core.documentModel.parser.regions.PHPRegionTypes;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public abstract class PHPPartitionTypes {

	public static final String PHP_DEFAULT = "org.eclipse.php.PHP_DEFAULT"; //$NON-NLS-1$
	public static final String PHP_SINGLE_LINE_COMMENT = "org.eclipse.php.PHP_SINGLE_LINE_COMMENT"; //$NON-NLS-1$
	public static final String PHP_MULTI_LINE_COMMENT = "org.eclipse.php.PHP_MULTI_LINE_COMMENT"; //$NON-NLS-1$
	public static final String PHP_DOC = "org.eclipse.php.PHP_DOC"; //$NON-NLS-1$
	public static final String PHP_QUOTED_STRING = "org.eclipse.php.PHP_QUOTED_STRING"; //$NON-NLS-1$

	public static String getPartitionType(final String type) {
		assert type != null;
		if (isPHPMultiLineCommentState(type)) {
			return PHP_MULTI_LINE_COMMENT;
		}
		if (isPHPLineCommentState(type)) {
			return PHP_SINGLE_LINE_COMMENT;
		}
		if (isPHPDocState(type)) {
			return PHP_DOC;
		}
		if (isPHPQuotesState(type)) {
			return PHP_QUOTED_STRING;
		}
		if (isPHPRegularState(type)) {
			return PHP_DEFAULT;
		}
		assert false;
		return null;
	}

	public static boolean isPHPCommentState(final String type) {
		return isPHPMultiLineCommentState(type) || isPHPLineCommentState(type) || isPHPDocState(type);
	}

	public static boolean isPHPDocState(final String type) {
		return type == null ? false : type.startsWith("PHPDOC"); //$NON-NLS-1$
	}

	public static boolean isPHPDocTagState(final String type) {
		return isPHPDocState(type) && !type.startsWith("PHPDOC_COMMENT"); //$NON-NLS-1$
	}

	public static boolean isPHPDocRegion(String type) {
		return type == PHPRegionTypes.PHPDOC_COMMENT || isPHPDocTagState(type);
	}

	public static boolean isPHPDocStartRegion(String type) {
		return type == PHPRegionTypes.PHPDOC_COMMENT_START;
	}

	public static boolean isPHPDocEndRegion(String type) {
		return type == PHPRegionTypes.PHPDOC_COMMENT_END;
	}

	public static boolean isPHPLineCommentState(final String type) {
		return type == PHPRegionTypes.PHP_LINE_COMMENT;
	}

	public static boolean isPHPMultiLineCommentStartRegion(String type) {
		return type == PHPRegionTypes.PHP_COMMENT_START;
	}

	public static boolean isPHPMultiLineCommentRegion(String type) {
		return type == PHPRegionTypes.PHP_COMMENT;
	}

	public static boolean isPHPMultiLineCommentEndRegion(String type) {
		return type == PHPRegionTypes.PHP_COMMENT_END;
	}

	public static boolean isPHPMultiLineCommentState(final String type) {
		return PHPPartitionTypes.isPHPMultiLineCommentStartRegion(type)
				|| PHPPartitionTypes.isPHPMultiLineCommentRegion(type)
				|| PHPPartitionTypes.isPHPMultiLineCommentEndRegion(type);
	}

	// NB: does not cover variables and special tokens that should be
	// "interpreted" by PHP inside back-quoted strings, double-quoted strings
	// and heredoc sections.
	// For full content coverage, use IPhpScriptRegion#isPhpQuotesState(int
	// relativeOffset).
	public static boolean isPHPQuotesState(final String type) {
		return type == PHPRegionTypes.PHP_CONSTANT_ENCAPSED_STRING || type == PHPRegionTypes.PHP_ENCAPSED_AND_WHITESPACE
				|| type == PHPRegionTypes.PHP_HEREDOC_START_TAG || type == PHPRegionTypes.PHP_HEREDOC_CLOSE_TAG
				|| type == PHPRegionTypes.PHP_NOWDOC_START_TAG || type == PHPRegionTypes.PHP_NOWDOC_CLOSE_TAG;
	}

	public static final boolean isPHPRegularState(final String type) {
		return type != null && !isPHPCommentState(type) && !isPHPQuotesState(type);
	}

	/**
	 * Returns starting region of the current partition
	 * 
	 * @param region
	 *            Region containing current offset
	 * @param relativeOffset
	 *            Current position relative to the containing region
	 * @return Starting region of the current partition
	 * @throws BadLocationException
	 */
	public static final ITextRegion getPartitionStartRegion(IPHPScriptRegion region, int relativeOffset)
			throws BadLocationException {
		String partitionType = region.getPartition(relativeOffset);
		ITextRegion internalRegion = region.getPHPToken(relativeOffset);
		ITextRegion startRegion = internalRegion;
		while (internalRegion.getStart() != 0) {
			internalRegion = region.getPHPToken(internalRegion.getStart() - 1);
			if (region.getPartition(internalRegion.getStart()) != partitionType) {
				break;
			}
			startRegion = internalRegion;
		}
		return startRegion;
	}

	/**
	 * Returns offset where the current partition starts
	 * 
	 * @param region
	 *            Region containing current offset
	 * @param relativeOffset
	 *            Current position relative to the containing region
	 * @return Starting offset of the current partition
	 * @throws BadLocationException
	 */
	public static final int getPartitionStart(IPHPScriptRegion region, int relativeOffset) throws BadLocationException {
		ITextRegion startRegion = getPartitionStartRegion(region, relativeOffset);
		return startRegion.getStart();
	}

	/**
	 * Returns region current partition ends on
	 * 
	 * @param region
	 *            Region containing current offset
	 * @param relativeOffset
	 *            Current position relative to the containing region
	 * @return Ending region of the current partition
	 * @throws BadLocationException
	 */
	public static final ITextRegion getPartitionEndRegion(IPHPScriptRegion region, int relativeOffset)
			throws BadLocationException {
		String partitionType = region.getPartition(relativeOffset);
		ITextRegion internalRegion = region.getPHPToken(relativeOffset);
		ITextRegion endRegion = internalRegion;
		while (internalRegion.getEnd() != region.getLength()) {
			internalRegion = region.getPHPToken(internalRegion.getEnd());
			if (region.getPartition(internalRegion.getStart()) != partitionType) {
				break;
			}
			endRegion = internalRegion;
		}
		return endRegion;
	}

	/**
	 * Returns offset where the current partition ends
	 * 
	 * @param region
	 *            Region containing current offset
	 * @param relativeOffset
	 *            Current position relative to the containing region
	 * @return Ending offset of the current partition
	 * @throws BadLocationException
	 */
	public static final int getPartitionEnd(IPHPScriptRegion region, int relativeOffset) throws BadLocationException {
		ITextRegion endRegion = getPartitionEndRegion(region, relativeOffset);
		return endRegion.getEnd();
	}

	/**
	 * Returns partition which corresponds to the provided offset
	 * 
	 * @param region
	 *            Region containing current offset
	 * @param relativeOffset
	 *            Current position relative to the containing region
	 * @return typed region containing partition
	 * @throws BadLocationException
	 */
	public static final ITypedRegion getPartition(IPHPScriptRegion region, int relativeOffset)
			throws BadLocationException {
		String partitionType = region.getPartition(relativeOffset);
		int startOffset = getPartitionStart(region, relativeOffset);
		int endOffset = getPartitionEnd(region, relativeOffset);
		return new TypedRegion(startOffset, endOffset - startOffset, partitionType);
	}

	public static boolean isPHPCondition(String type) {
		return (type == PHPRegionTypes.PHP_IF || type == PHPRegionTypes.PHP_FOR || type == PHPRegionTypes.PHP_FOREACH);

	}

	public static boolean isPHPLoop(String type) {
		return (type == PHPRegionTypes.PHP_WHILE || type == PHPRegionTypes.PHP_DO);

	}
}
